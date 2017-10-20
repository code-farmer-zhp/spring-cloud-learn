package com.feiniu.score.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.CacheUtils;
import com.feiniu.score.common.ConstantGrowth;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dao.growth.*;
import com.feiniu.score.dao.score.ScoreOrderDetailDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.growth.GrowthDetail;
import com.feiniu.score.entity.growth.GrowthOrderInfo;
import com.feiniu.score.exception.BizException;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.ApplicationContextHolder;
import com.feiniu.score.util.ShardUtils;
import com.feiniu.score.vo.OrderJsonVo;
import com.feiniu.score.vo.OrderJsonVo.OrderDetail;
import com.feiniu.score.vo.ReturnJsonVo;
import com.feiniu.score.vo.ReturnJsonVo.ReturnDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import scala.actors.threadpool.Arrays;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


@Service
public class GrowthOrderServiceImpl implements GrowthOrderService {

    private static final CustomLog log = CustomLog.getLogger(GrowthOrderServiceImpl.class);
    @Autowired
    private GrowthDetailDao growthDetailDao;

    @Autowired
    private GrowthMainDao growthMainDao;

    @Autowired
    private GrowthOrderInfoDao growthOrderInfoDao;

    @Autowired
    private GrowthMemService growthMemService;

    @Autowired
    private GrowthCommonDao growthCommonDao;
//	@Autowired
//	private OrderClient orderClient;

    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private SearchMemberDao searchMemberDao;
    @Autowired
    private ScoreOrderDetailDao scoreOrderDetailDao;

    @Autowired
    private GrowthBaseServiceWithCache growthBaseServiceWithCache;
    /**
     * 1订单：提交订单
     * 在提交订单时，查询订单信息并计算成长值存储到缓存
     *
     * @param memGuid
     * @return
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore", readOnly = true)
    public void orderInput(String memGuid, OrderJsonVo vo) {
        // 计算订单金额
        int growthValue = 0;
        if (cacheUtils.isCompanyOrPartner(memGuid)) {
            log.info("订单计算成长值：因为用户是企业用户或合伙人，获得成长值为0。memGuid=" + memGuid,"orderInput");
            growthValue = 0;
        } else {
            Map<String, Object> map = this.computeGrowthValueByOrderJson(vo, cacheUtils.isEmployee(memGuid));
            growthValue = (int) map.get("growthValue");
        }
        cacheUtils.putCache(ConstantGrowth.CACHE_ORDER_GROWTH_KEY + vo.getOgSeq(), CacheUtils.TEN_DAY, growthValue);

//		return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }


    /**
     * 2订单：支付订单
     * 1、订单在支付的时候记录到本地（成长值明细和订单信息）
     * 2、成长值的数据状态为0(待生效)
     *
     * @param memGuid
     * @return
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void orderPay(String memGuid, OrderJsonVo vo) {
        // 判断 整个订单是否可以获得成长值
        Map<String, Object> map = this.computeGrowthValueByOrderJson(vo, cacheUtils.isEmployee(memGuid));
        int orderGrowthValueAll = (int) map.get("growthValue");
        BigDecimal orderPay = null;
        if (map.get("orderPay") != null) {
            orderPay = (BigDecimal) map.get("orderPay");
        }
        int orderGrowthValueSum = 0;

        // BUILD GOI GrowthOrderInfo
        List<GrowthOrderInfo> goiList = this.buildGrowthOrderInfo(vo);
        if (goiList != null) {
            Collections.sort(goiList);  //按realPay升序排序,最后一个即金额最大的，补足分摊的差额。
            for (int i = 0; i < goiList.size(); i++) {
                GrowthOrderInfo goi = goiList.get(i);
                // 保存订单信息
                growthOrderInfoDao.saveOrderInfo(memGuid, goi);

                /**
                 * 如果整个订单满足获得成长值的要求 ， 那么每个商品才可以获得成长值
                 * 例如：订单有效金额小于10块  每个订单最大成长值500
                 */
                //if(orderGrowthValueAll >= 0){

                // 构建GrowthDetail
                Integer growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_GW;
                Integer dataFlag = ConstantGrowth.DATA_FLAG_DSX;

                Long orderInfoId = goi.getGoiSeq();
                int growthValue = 0;
                if (cacheUtils.isCompanyOrPartner(memGuid)) {
                    log.info("支付订单计算成长值：因为用户是企业用户，获得成长值为0。memGuid=" + memGuid,"orderPay");
                } else if (orderGrowthValueAll == 0) {
                    log.info("订单总成长值为0，获得成长值为0。memGuid=" + memGuid+",ogSeq="+vo.getOgSeq(),"orderPay");
                } else {
                    if (i < goiList.size() - 1) {
                        growthValue = this.computeGrowthValueByDetail(goi.getOgQty(), goi.getRealPay(), goi.getKind(), orderGrowthValueAll, orderPay);
                        orderGrowthValueSum += growthValue;
                    } else {
                        growthValue = orderGrowthValueAll - orderGrowthValueSum;
                        orderGrowthValueSum += growthValue;
                    }
                }
                GrowthDetail detail = this.buildDetail(memGuid, orderInfoId, growthValue, growthChannel, dataFlag);
                detail.setGroupKey(growthChannel + "_" + vo.getOgSeq());
                growthDetailDao.saveGrowthDetail(memGuid, detail);

                //}

            }
        }

//		return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }


    /**
     * 3订单：收货确认
     * <p>
     * 1、更新成长值明细表的数据为有效（根据ogSeq查找订单，根据订单和购物获得查找唯一成长值）
     * 2、更新订单表 pay_status = 3(到货签收)
     * 3、更新或新增growthMain表GUID的成长值
     * <p>
     * /**
     * 确认订单已经收货
     * 自营：
     * {"kind":"1",  // 自营
     * "fdlSeq": "出货者编号",
     * "ogSeq": "订单编号",
     * "memGuid": "用户ID"}
     * 商城：
     * {"kind":"2",  // 商城
     * "ogSeq": "子订单编号",
     * "packNo": "包裹编号",
     * "memGuid": "用户ID"}
     *
     * @param memGuid
     * @return
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void receiveOrder(String memGuid, String message, Map<String, Date> keyDate) {


        JSONObject jsonObj = JSONObject.parseObject(message);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        //必须传
        paramMap.put("memGuid", memGuid);
//		  king：1(自营)   kind：2(商城)
        String kind = jsonObj.getString("kind");
        List<GrowthOrderInfo> orderInfoList = null;
        switch (kind) {
            case "1":

                paramMap.put("ogSeq", jsonObj.getString("ogSeq"));

                //改为按订单显示后，确认收货也按订单。只要任意包裹收货，整比订单的成长值全送
                //paramMap.put("fdlSeq" , jsonObj.getString("fdlSeq"));
                orderInfoList = growthOrderInfoDao.findOrderListByMap(memGuid, paramMap);
                break;
            case "2":

                //改为按订单显示后，确认收货也按订单。只要任意包裹收货，整比订单的成长值全送
                //商城传的是子订单号。还要查询出父订单。
                paramMap.put("ogsSeq", jsonObj.getString("ogSeq"));
                //paramMap.put("packageNo" , jsonObj.getString("packNo"));
                orderInfoList = growthOrderInfoDao.findOrderListByOgsSeq(memGuid, paramMap);
                break;
            default:
                break;
        }

        if (orderInfoList == null || orderInfoList.size() == 0) {
            String msg = "根据入参找不到相应的订单 memGuid = " + memGuid + "  message=" + message;
            log.error(msg,"receiveOrder");
            throw new BizException(msg);
        } else if (orderInfoList.size() != 1) {
            log.error("根据入参找到" + orderInfoList.size() + "个相应的订单  memGuid= " + memGuid + "  message=" + message,"receiveOrder");
        }
        int incrementValue = 0;

        for (GrowthOrderInfo orderInfo : orderInfoList) {
            if (orderInfo == null) {
                throw new BizException(
                        "receiveOrder must not null , memGuid = " + memGuid);
            }
            if (ConstantGrowth.ORDER_PAY_STATUS_YQS.equals(orderInfo.getPayStatus())) {
                log.error("该商品已经签收  memGuid = " + memGuid
                        + " message= " + message,"receiveOrder");
                continue;
            }
            // 更新订单表
            orderInfo.setPayStatus(ConstantGrowth.ORDER_PAY_STATUS_YQS); // 更新订单状态为已签收
            //设置时间。洗数据专用。
            if (keyDate != null) {
                Date payDate = keyDate.get("payDate");
                Date insDate = keyDate.get("insDate");
                Date updateDate = keyDate.get("updateDate");
                orderInfo.setPayDate(payDate);
                orderInfo.setInsDate(insDate);
                orderInfo.setUpdDate(updateDate);
            }
            growthOrderInfoDao.updateGrowthOrderInfo(memGuid, orderInfo);

            String adrId = orderInfo.getAdrId();
            // 如果adrId 为空或"_"，说明不是CPS订单， CPS订单不能给予成长值 如果不是CPS订单 才要更新 成长值明细表
            if (StringUtils.isBlank(adrId)
                    || ConstantGrowth.DEFAULT_STRING_VALUE.equals(adrId)) {

                // 更新 detail
                List<Integer> growthChannelList = new ArrayList<Integer>();
                growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_GW);
                List<GrowthDetail> detailList = growthDetailDao
                        .findDetailByOrder(memGuid, orderInfo.getGoiSeq(),
                                growthChannelList);

                if (detailList != null) {
                    for (GrowthDetail detail : detailList) {
                        detail.setDataFlag(ConstantGrowth.DATA_FLAG_YX);
                        if (cacheUtils.isCompanyOrPartner(memGuid)) {
                            log.info("确认收货获得成长值：因为用户是企业用户或合伙人，获得成长值为0。memGuid="
                                    + memGuid,"receiveOrder");
                            incrementValue = 0;
                        } else {
                            incrementValue += detail.getGrowthValue();
                        }
                        if (keyDate != null) {
                            Date updateDate = keyDate.get("updateDate");
                            detail.setUpdDate(updateDate);
                        } else {
                            detail.setUpdDate(new Date());
                        }
                        growthDetailDao.updateGrowthDetail(memGuid, detail);
                    }
                }
            }

        }
        // 更新 growthMain
        if (incrementValue > 0) {
            this.addOrUpdateGrowthValueByGuid(memGuid, incrementValue);
        }
        if (incrementValue == 0) {
            growthBaseServiceWithCache.saveGrowthValueWithValueZero(memGuid, true);
        }
        if (incrementValue < 0) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "确认收货获得成长值小于0");
        }

//		return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }


    /**
     * 4、自动第10天给支付的订单成长值
     * 定时任务自动为支付了 10 天 还未确认收货的 订单赠送成长值
     * 需求：客人未进行收货确认时，到支付后的第10天赠送成长值（包含支付当日）
     *
     * @return
     */
    public void autoConfirmOrder() {
        try {
            log.info("开始执行自动第10天给支付的订单成长值","autoConfirmOrder");

            for (int i = 0; i < ShardUtils.getDbCount(); i++) {

                GrowthOrderServiceImpl service = (GrowthOrderServiceImpl) ApplicationContextHolder.getBean("growthOrderServiceImpl");

                String dataSource = DataSourceUtils.DATASOURCE_BASE_NAME + i;

                try {
                    for (int j = 0; j < ShardUtils.getTableCount(); j++) {
                        service.everyTable(dataSource, j);
                    }
                } catch (Exception e) {
                    log.error("数据源" + dataSource + "出错", "autoConfirmOrder",e);
                }
            }

            log.info("结束自动第10天给支付的订单成长值","autoConfirmOrder");
        } catch (Exception e) {
            log.error("自动第10天给支付的订单成长值异常","autoConfirmOrder", e);
        }
    }


    @DynamicDataSource(dataSourceNameIndex = 0, index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void everyTable(String dataSourceIndex, int tableNo) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, ConstantGrowth.DAY_AGO_RECEIVE);
        Date tenDaysAgo = calendar.getTime();

        paramMap.put("growthPayDate", tenDaysAgo);
        paramMap.put("payStatus", ConstantGrowth.ORDER_PAY_STATUS_YZF); // 已经支付
        List<GrowthOrderInfo> orderList = growthOrderInfoDao.findOrderListByMap(paramMap, tableNo);
        if (orderList != null) {
            for (GrowthOrderInfo orderInfo : orderList) {

                String memGuid = orderInfo.getMemGuid();
                // 更新订单表
                orderInfo.setPayStatus(ConstantGrowth.ORDER_PAY_STATUS_YQS); // 更新订单状态为已签收
                orderInfo.setUpdDate(new Date());
                orderInfo.setUpdMan("system job");
                growthOrderInfoDao.updateGrowthOrderInfo(orderInfo.getMemGuid(), orderInfo);


                String adrId = orderInfo.getAdrId();
                //如果adrId 为空，说明不是CPS订单， CPS订单不能给予成长值  如果不是CPS订单 才要更新 成长值明细表
                if (StringUtils.isBlank(adrId) || ConstantGrowth.DEFAULT_STRING_VALUE.equals(adrId)) {

                    // 更新 detail
                    List<Integer> growthChannelList = new ArrayList<Integer>();
                    growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_GW);
                    growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_GW);
                    growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_PL);
                    List<GrowthDetail> detailList = growthDetailDao.findDetailByOrder(memGuid, orderInfo.getGoiSeq(), growthChannelList);

                    int incrementValue = 0;
                    if (detailList != null) {
                        for (GrowthDetail detail : detailList) {

                            detail.setDataFlag(ConstantGrowth.DATA_FLAG_YX);
                            if ("+".equals(detail.getOperate())) {
                                incrementValue += detail.getGrowthValue();
                            } else {
                                incrementValue += -detail.getGrowthValue();
                            }
                            detail.setUpdDate(new Date());
                            detail.setUpdMan("system job");
                            growthDetailDao.updateGrowthDetail(memGuid, detail);
                        }
                    }

                    // 更新 growthMain
                    if (incrementValue != 0) {
                        this.addOrUpdateGrowthValueByGuid(memGuid, incrementValue);
                    } else {
                        growthBaseServiceWithCache.saveGrowthValueWithValueZero(memGuid,true);
                    }
                    log.info("goiSeq=" + orderInfo.getGoiSeq() + ", memGuid=" + memGuid + ", 获得成长值=" + incrementValue + ", dbNo=" + dataSourceIndex + ", tableNo=" + tableNo,"everyTable");
                }
            }
        }

    }


    /**
     * 成长值：取消整个订单
     * @param memGuid
     * @param ogSeq
     * @return
     */
//	@DynamicDataSource(index=0)
//	@Transactional(propagation=Propagation.REQUIRED)
//	public Result orderCancel(String memGuid , String ogSeq){
//		
//		
//		
//		return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success"); 
//	}


    /**
     * 5 退货
     * 1、（商城）如果传递的是订单号，那么整单都退货
     * 2、（自营）如果传递的是退货单号，那么按退货单处理
     *
     * @param memGuid
     * @return
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void orderReturn(String memGuid, ReturnJsonVo vo, Map<String, Date> keyDate) {

        int memGrowthValue = 0;
        String ogSeq = vo.getOgSeq();
        String rgSeq = vo.getRgSeq();

        List<ReturnDetail> mallList = vo.getMallList();
        List<ReturnDetail> selfList = vo.getSelfList();

        // olSeq  计算 itNo 的 退货数量
//		Map<String,BigDecimal> returnMap = this.getOrderReturnInfoMap(mallList, selfList);


        // 查询出 要退货的明细
        List<GrowthOrderInfo> orderList = new ArrayList<GrowthOrderInfo>();
        if (mallList != null && mallList.size() > 0) {
            orderList.addAll(this.queryReturnOrderList(memGuid, ogSeq, rgSeq, mallList));
        }
        if (selfList != null && selfList.size() > 0) {
            orderList.addAll(this.queryReturnOrderList(memGuid, ogSeq, rgSeq, selfList));
        }

        if (orderList == null || orderList.size() == 0) {
            //log.info("退订未找到订单数据ogSeq = "+ogSeq+"  rgSeq="+rgSeq +"  memGuid="+memGuid);
            throw new ScoreException(ResultCode.RESULT_GROWTH_SUBMIT_ORDER_BUT_NO_CONSUME_LOG, "退订未找到订单数据ogSeq = " + ogSeq + "  rgSeq=" + rgSeq + "  memGuid=" + memGuid);
        }

        // 退货回收部分 的成长值明细
        for (GrowthOrderInfo order : orderList) {
            int growthValue = 0;
            ReturnDetail returnDetail = this.getOrderReturnInfoDetail(order.getOlSeq(), mallList, selfList);
            if (returnDetail == null) {
                continue;
            }
            String rlSeq = returnDetail.getRlSeq();
            //BigDecimal returnPay = returnDetail.getReturnMoney();
            int returnQty = returnDetail.getQuantity();
            BigDecimal returnAllMoney = returnDetail.getRealReturn();


            List<Integer> growthChannelList = new ArrayList<Integer>();
            growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_GW);
            growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_GW);
            growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PL);
            growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLZD);
            growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLJH);
            growthChannelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_PL);

            List<GrowthDetail> detailList_all = growthDetailDao.findDetailByOrder(memGuid, order.getGoiSeq(), growthChannelList);

            // 计算退货购物可回收成长值
            growthValue = this.computeGrowthValueReturn(memGuid, order, returnQty, returnAllMoney, detailList_all);
            // 如果有可回收成长值 ， 则回收
            if (growthValue > 0) {
                GrowthDetail detail = null;
                if (ConstantGrowth.ORDER_PAY_STATUS_YQS.equals(order.getPayStatus())) {
                    memGrowthValue = memGrowthValue - growthValue; // 记录当前会员的成长值的变化
                    detail = this.buildDetail(memGuid, order.getGoiSeq(), growthValue, ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_GW, ConstantGrowth.DATA_FLAG_YX);
                }else if (ConstantGrowth.ORDER_PAY_STATUS_YZF.equals(order.getPayStatus())) {
                    detail = this.buildDetail(memGuid, order.getGoiSeq(), growthValue, ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_GW, ConstantGrowth.DATA_FLAG_DSX);
                }else{
                    throw new ScoreException("订单没有支付或签收,memGuid="+memGuid+", GoiSeq="+ order.getGoiSeq());
                }
                // 增加退货单信息
                detail.setRgSeq(rgSeq);
                detail.setRlSeq(rlSeq);
                detail.setReturnQty(returnQty);
                detail.setReturnPay(returnAllMoney);
                if (keyDate != null) {
                    Date insDate = keyDate.get("insDate");
                    if (insDate != null) {
                        detail.setInsDate(insDate);
                    }
                    Date updDate = keyDate.get("updDate");
                    if (updDate != null) {
                        detail.setUpdDate(updDate);
                    }
                }
                detail.setGroupKey(detail.getGrowthChannel() + "_" + rgSeq);
                growthDetailDao.saveGrowthDetail(memGuid, detail);

            }

            // 计算评论、置顶、精华可回收成长值

            growthValue = this.computeGrowthValueReturnComment(returnQty, detailList_all);
            if (growthValue > 0) {
                GrowthDetail detail = null;
                if ( ConstantGrowth.ORDER_PAY_STATUS_YQS.equals(order.getPayStatus())) {
                    memGrowthValue = memGrowthValue - growthValue; // 记录当前会员的成长值的变化
                    detail = this.buildDetail(memGuid, order.getGoiSeq(), growthValue, ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_PL, ConstantGrowth.DATA_FLAG_YX);
                }else if (ConstantGrowth.ORDER_PAY_STATUS_YZF.equals(order.getPayStatus())) {
                    detail = this.buildDetail(memGuid, order.getGoiSeq(), growthValue, ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_PL, ConstantGrowth.DATA_FLAG_DSX);
                }else{
                    throw new ScoreException("订单没有支付或签收,memGuid="+memGuid+", GoiSeq="+ order.getGoiSeq());
                }

                detail.setGroupKey(detail.getGrowthChannel() + "_" + order.getCommentSeq());
                growthDetailDao.saveGrowthDetail(memGuid, detail);
            }

            // 更新订单
            order.setReturnQty(returnQty + order.getReturnQty());
            order.setReturnPay(returnAllMoney);
            order.setRgSeq(rgSeq);
            order.setRlSeq(rlSeq);
            growthOrderInfoDao.updateGrowthOrderInfo(memGuid, order);
        }
        // 如果 会员成长值回收了 则更新 会员成长值统计表
        if (memGrowthValue < 0) {
            this.addOrUpdateGrowthValueByGuid(memGuid, memGrowthValue);
        }
        if (memGrowthValue == 0) {
            growthBaseServiceWithCache.saveGrowthValueWithValueZero(memGuid,true);
        }
        if (memGrowthValue > 0) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "退货回收成长值小于0");
        }

//		return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    /**
     * 从查询出来的 detailList中找不不同 channle 的 detail
     *
     * @param detailList
     * @param channels
     * @return
     */
    private List<GrowthDetail> getDetailListByChannels(List<GrowthDetail> detailList, Integer[] channels) {
        List<GrowthDetail> list = new ArrayList<GrowthDetail>();
        if (detailList != null) {
            @SuppressWarnings("unchecked")
            List<Integer> channelList = Arrays.asList(channels);
            for (GrowthDetail d : detailList) {

                if (channelList.contains(d.getGrowthChannel())) {
                    list.add(d);
                }
            }
        }

        return list;
    }

    /**
     * 查询退货退应的 订单信息
     * 修改后，未收货也退，但设为未生效
     *
     * @param memGuid
     * @param ogSeq
     * @param rgSeq
     * @param returnDetaiList
     * @return
     */
    private List<GrowthOrderInfo> queryReturnOrderList(String memGuid, String ogSeq, String rgSeq, List<ReturnDetail> returnDetaiList) {

        List<GrowthOrderInfo> orderList = new ArrayList<GrowthOrderInfo>();
        Set<String> olSeqSet = new HashSet<String>();
        if (returnDetaiList != null) {
            // 如果 退货单 不为空 则 按退货单处理
            if (StringUtils.isNotBlank(rgSeq)) {

                for (ReturnDetail d : returnDetaiList) {

//					itNoSet.add(d.getItNo());
                    olSeqSet.add(d.getOlSeq());
                }

            }
            //修改后，未收货也退，但设为未生效
            orderList = growthOrderInfoDao.findOrderListByOlSeqList(memGuid, ogSeq, olSeqSet);
        }
        return orderList;
    }


    /**
     * @param mallList
     * @param selfList
     * @return
     */
    private ReturnDetail getOrderReturnInfoDetail(String olSeq, List<ReturnDetail> mallList, List<ReturnDetail> selfList) {

        if (mallList != null) {

            for (ReturnDetail d : mallList) {
//				paramMap.put(d.getOlSeq(), d.getCard().add(d.getReturnMoney()));
                if (d.getOlSeq().equals(olSeq)) {
                    return d;
                }

            }
        }

        if (selfList != null) {
            for (ReturnDetail d : selfList) {
//				paramMap.put(d.getOlSeq(), d.getCard().add(d.getReturnMoney()));
                if (d.getOlSeq().equals(olSeq)) {
                    return d;
                }

            }
        }

        return null;
    }


    /**
     * BUILD DETAIL
     *
     * @param memGuid
     * @param orderInfoId
     * @param growthValue
     * @param growthChannel
     * @param dataFlag
     * @return
     */
    private GrowthDetail buildDetail(String memGuid, Long orderInfoId, Integer growthValue, Integer growthChannel, Integer dataFlag) {


        GrowthDetail detail = new GrowthDetail();
        detail.setMemGuid(memGuid);
        detail.setOrderInfoId(orderInfoId);

        if (Arrays.asList(ConstantGrowth.DETAIL_GROWTH_CHANNEL_OPERATE_LOSS).contains(growthChannel)) {
            detail.setOperate("-");
        } else {
            detail.setOperate("+");
        }
        detail.setGrowthValue(Math.abs(growthValue));
        detail.setGrowthChannel(growthChannel);
        detail.setDataFlag(dataFlag);

        return detail;
    }


    /**
     * 新增或更新 会员的总的成长值
     *
     * @param memGuid
     * @param incrementValue
     */
    private void addOrUpdateGrowthValueByGuid(String memGuid, int incrementValue) {
        // 更新 growthMain
        growthBaseServiceWithCache.changeGrowthValue(memGuid,incrementValue,true); // 更新的是增量
        // 通知 CRM memGuid 的成长值有变化
        growthMemService.saveGrowthChangeTokafkaForCRM(memGuid);
    }


    private List<GrowthOrderInfo> buildGrowthOrderInfo(OrderJsonVo vo) {

        List<GrowthOrderInfo> orderList = new ArrayList<GrowthOrderInfo>();

        List<OrderDetail> mallList = vo.getMallList();
        List<OrderDetail> selfList = vo.getSelfList();

        if (mallList != null) {
            for (OrderDetail d : mallList) {
                orderList.add(this.buildOrderFromVoList(vo, d));
            }
        }

        if (selfList != null) {
            for (OrderDetail d : selfList) {
                orderList.add(this.buildOrderFromVoList(vo, d));
            }
        }


        return orderList;
    }

    /**
     * @param vo
     * @param d
     * @return
     */
    private GrowthOrderInfo buildOrderFromVoList(OrderJsonVo vo, OrderDetail d) {

        GrowthOrderInfo order = new GrowthOrderInfo();
        Date now = new Date();
        order.setMemGuid(vo.getMemGuid());
        order.setOgSeq(vo.getOgSeq());
        order.setOgNo(vo.getOgNo());
        if (StringUtils.isNotBlank(vo.getAdrId())) {
            order.setAdrId(vo.getAdrId());
        } else {
            order.setAdrId(ConstantGrowth.DEFAULT_STRING_VALUE);
        }


        order.setPackageNo(d.getPackageNo());
        order.setItNo(d.getItNo());
        order.setOlSeq(d.getOlSeq());
        order.setOgQty(d.getQuantity());
        order.setReturnQty(0);
        order.setRealPay(d.computeRealPay());
        order.setPrice(d.getPrice());
        order.setSmSeq(d.getSmSeq());
        String fdlSeq = StringUtils.isNotBlank(d.getFdlSeq()) ? d.getFdlSeq() : ConstantGrowth.DEFAULT_STRING_VALUE;
        order.setFdlSeq(fdlSeq);

        String kind = StringUtils.isNotBlank(d.getKind()) ? d.getKind() : ConstantGrowth.DEFAULT_STRING_VALUE;
        order.setKind(kind);

        String ogsSeq = StringUtils.isNotBlank(d.getOgsSeq()) ? d.getOgsSeq() : ConstantGrowth.DEFAULT_STRING_VALUE;
        order.setOgsSeq(ogsSeq);

        order.setCommentSeq(ConstantGrowth.DEFAULT_STRING_VALUE);
        order.setRemark(ConstantGrowth.DEFAULT_STRING_VALUE);
        order.setPayStatus(ConstantGrowth.ORDER_PAY_STATUS_YZF);
        order.setPayDate(now);
        order.setInsMan(ConstantGrowth.DEFAULT_STRING_VALUE);
        order.setUpdMan(ConstantGrowth.DEFAULT_STRING_VALUE);

        return order;
    }

    /**
     * 计算一个订单总的（成长值 ）
     *
     * @param vo
     * @return
     */
    @Override
    public Map<String, Object> computeGrowthValueByOrderJson(OrderJsonVo vo, boolean isEmployee) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        // 如果是CPS订单，组团订单  虚拟充值订单 电子屏订单 分销平台不能获得成长值
        if (!growthBaseServiceWithCache.canGetGrowth(vo)){
            returnMap.put("growthValue", 0);
        } else {
            List<OrderDetail> mallList = vo.getMallList();
            List<OrderDetail> selfList = vo.getSelfList();
            int growthValue = 0;
            BigDecimal orderPay = BigDecimal.ZERO;
            BigDecimal cardPayTotal = BigDecimal.ZERO;
            BigDecimal bonusPayTotal = BigDecimal.ZERO;
            BigDecimal growthValueBigDec = BigDecimal.ZERO;
            BigDecimal couponsPayPrice = BigDecimal.ZERO;  //飞牛券支付门槛，满XX元才能使用
            Set<String> actSeqsSet = new HashSet<>();
            if (mallList != null) {
                for (OrderDetail d : mallList) {
                    BigDecimal realPay = d.computeRealPay();
                    orderPay = orderPay.add(realPay);
                    /*if((d.getCoupons()!=null&&d.getCoupons().compareTo(BigDecimal.ZERO) > 0)
							||(d.getVouchers()!=null&&d.getVouchers().compareTo(BigDecimal.ZERO) > 0)
							||(d.getBonus()!=null&&d.getBonus().compareTo(BigDecimal.ZERO) > 0)){
						log.info("订单使用了飞牛券或购物金或抵用券，获得的成长值为0,vo="+vo);
						canGetGrowth=false;
					}*/
                    if ((d.getCard() != null && d.getCard().compareTo(BigDecimal.ZERO) > 0)) {
                        cardPayTotal = cardPayTotal.add(d.getCard());
                    }
                    if ((d.getBonus() != null && d.getBonus().compareTo(BigDecimal.ZERO) > 0)) {
                        bonusPayTotal = bonusPayTotal.add(d.getCard());
                    }
                    if (StringUtils.isNotBlank(d.getActSeq())) {
                        actSeqsSet.add(d.getActSeq());
                    }
                }
            }
            if (selfList != null) {
                for (OrderDetail d : selfList) {
                    BigDecimal realPay = d.computeRealPay();
                    orderPay = orderPay.add(realPay);
					/*if((d.getCoupons()!=null&&d.getCoupons().compareTo(BigDecimal.ZERO) > 0)
							||(d.getVouchers()!=null&&d.getVouchers().compareTo(BigDecimal.ZERO) > 0)
							||(d.getBonus()!=null&&d.getBonus().compareTo(BigDecimal.ZERO) > 0)){
						log.info("订单使用了飞牛券或购物金或抵用券，获得的成长值为0,vo="+vo);
						canGetGrowth=false;
					}*/
                    if ((d.getCard() != null && d.getCard().compareTo(BigDecimal.ZERO) > 0)) {
                        cardPayTotal = cardPayTotal.add(d.getCard());
                    }
                    if ((d.getBonus() != null && d.getBonus().compareTo(BigDecimal.ZERO) > 0)) {
                        bonusPayTotal = bonusPayTotal.add(d.getBonus());
                    }
                    if (StringUtils.isNotBlank(d.getActSeq())) {
                        actSeqsSet.add(d.getActSeq());
                    }
                }

            }
            if (actSeqsSet != null && !actSeqsSet.isEmpty()) {
                String[] actSeqs = new String[actSeqsSet.size()];
                actSeqsSet.toArray(actSeqs);
                if (actSeqs != null & actSeqs.length > 0) {
                    for (String actSeq : actSeqs) {
                        Double usePriceDou = growthCommonDao.getUsePriceByActSeq(actSeq);
                        if (usePriceDou != null) {
                            couponsPayPrice = couponsPayPrice.add(BigDecimal.valueOf(usePriceDou));
                        }
                    }
                }
            }
			/*if(canGetGrowth==false){
				growthValue = 0;
			}*/
            // 订单实际支付小于10元的不能获得成长值
            if (orderPay.compareTo(new BigDecimal(ConstantGrowth.ORDER_MIN_LIMIT)) < 0) {
                growthValue = 0;
            } else {
                growthValueBigDec = orderPay;
                //员工用户订单获得的成长值要实付金额再减去购物卡支付的金额
                if (isEmployee) {
                    growthValueBigDec = growthValueBigDec.subtract(cardPayTotal);
                }
                //订单获得的成长值要实付金额再减去 抵用券支付的金额除以0.2得到的支付门槛
                growthValueBigDec = growthValueBigDec.subtract(bonusPayTotal.divide(ConstantGrowth.BONUS_DIV_COEFF_FOR_GROWTH_CAL,3,BigDecimal.ROUND_HALF_UP));

                //订单获得的成长值要实付金额再减去飞牛券的支付门槛
                growthValueBigDec = growthValueBigDec.subtract(couponsPayPrice);

                growthValue = growthValueBigDec.intValue();
                if (growthValue > ConstantGrowth.ORDER_MAX_LIMIT) {
                    growthValue = ConstantGrowth.ORDER_MAX_LIMIT;
                } else if (growthValue < 0) {
                    growthValue = 0;
                }
            }
            returnMap.put("growthValue", growthValue);
            String growthKey = ConstantGrowth.CACHE_ORDER_GROWTH_KEY + vo.getOgSeq();
            cacheUtils.putCache(growthKey, CacheUtils.TEN_DAY, growthValue);
            returnMap.put("orderPay", orderPay);
        }
        return returnMap;
    }

    /**
     * @param quantity 单种商品的 购物数量
     * @param realPay  (有效金额) = 现金+购物卡+余额
     * @return
     */
    private int computeGrowthValueByDetail(int quantity, BigDecimal realPay, String kind, int orderGrowthValueAll, BigDecimal orderPay) {

        // 单个商品最大 可获得 500 成长值,  注意!!已作废
//		int maxLimit = ConstantGrowth.ORDER_ITEM_MAX_LIMIT;
//		int value = realPay.intValue();
//		//单品多件视为同件商品，最多500,  注意!!已作废
//		/*
//		int growthValue;
//		if(StringUtils.equals(kind, Constant.KIND_OF_DPDJ)){
//			growthValue = value > maxLimit ? maxLimit : value;
//		}else{
//			growthValue = value > maxLimit*quantity ? maxLimit*quantity : value;
//		}
//		*/
//		int growthValue = value > maxLimit*quantity ? maxLimit*quantity : value;

        int growthValue;
        int growthAll = orderGrowthValueAll < ConstantGrowth.ORDER_MAX_LIMIT ? orderGrowthValueAll : ConstantGrowth.ORDER_MAX_LIMIT;
        if (orderPay == null) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "订单总金额无数据");
        }
        if (BigDecimal.ZERO.equals(orderPay)) {
            growthValue = 0;
        } else {
            growthValue = (realPay.divide(orderPay, 4, RoundingMode.DOWN).multiply(new BigDecimal(growthAll))).intValue();
        }
        return growthValue;
    }


    private List<OrderDetail> covertOrderDetailWithGroup(List<OrderDetail> allList) {
        Set<String> parentIds = new HashSet<>();
        Map<String, OrderDetail> parentOrderDetailMap = new HashMap<String, OrderDetail>();
        //得到顶级商品 将parentId设置为它们的id
        for (OrderDetail orderDetail : allList) {
            if (StringUtils.equals("0", orderDetail.getParentId())) {
                String id = orderDetail.getId();
                orderDetail.setParentId(id);

                OrderDetail returnOrderDetail = new OrderDetail();
                BeanUtils.copyProperties(orderDetail, returnOrderDetail);
                parentOrderDetailMap.put(id, returnOrderDetail);
            }
            parentIds.add(orderDetail.getParentId());
        }
        //对商品分组
        Map<String, List<OrderDetail>> groupOrderDetailMap = new HashMap<>();
        for (OrderDetail orderDetail : allList) {
            String parentId = orderDetail.getParentId();
            List<OrderDetail> details = groupOrderDetailMap.get(parentId);
            if (details == null) {
                details = new ArrayList<>();
                details.add(orderDetail);
                groupOrderDetailMap.put(parentId, details);
            } else {
                details.add(orderDetail);
            }
        }
        //将组合商品、优惠套餐、固定搭配的多条记录合并为一条
        for (String parentId : parentIds) {
            List<OrderDetail> details = groupOrderDetailMap.get(parentId);
            OrderDetail parentOrderDetail = parentOrderDetailMap.get(parentId);
            if (parentOrderDetail == null && details != null && !details.isEmpty()) {
                parentOrderDetail = new OrderDetail();
                BeanUtils.copyProperties(details.get(0), parentOrderDetail);
            }
            BigDecimal groupRealPay = new BigDecimal(0);
            BigDecimal groupCard = new BigDecimal(0);
            BigDecimal groupCoupons = new BigDecimal(0);
            BigDecimal groupVouchers = new BigDecimal(0);
            BigDecimal groupBonus = new BigDecimal(0);
            BigDecimal groupSellActivity = new BigDecimal(0);
            BigDecimal groupScore = new BigDecimal(0);
            BigDecimal groupPrice = new BigDecimal(0);
            for (OrderDetail detail : details) {
                groupRealPay = groupRealPay.add(detail.getRealPay());
                groupCard = groupCard.add(detail.getCard());
                groupCoupons = groupCoupons.add(detail.getCoupons());
                if (detail.getVouchers() != null) {
                    groupVouchers = groupVouchers.add(detail.getVouchers());
                }
                if (detail.getBonus() != null) {
                    groupBonus = groupBonus.add(detail.getBonus());
                }
                groupSellActivity = groupSellActivity.add(detail.getSellActivity());
                groupScore = groupScore.add(detail.getScore());
                groupPrice = groupPrice.add(detail.getPrice());
            }
            if (parentOrderDetail != null) {
                parentOrderDetail.setRealPay(groupRealPay);
                parentOrderDetail.setCard(groupCard);
                parentOrderDetail.setCoupons(groupCoupons);
                parentOrderDetail.setVouchers(groupVouchers);
                parentOrderDetail.setBonus(groupBonus);
                parentOrderDetail.setSellActivity(groupSellActivity);
                parentOrderDetail.setScore(groupScore);
                parentOrderDetail.setPrice(groupPrice);
                parentOrderDetailMap.put(parentId, parentOrderDetail);
            }
        }
        List<OrderDetail> returnOrderDetailList = new ArrayList<OrderDetail>();
        for (OrderDetail returnOrderDetail : parentOrderDetailMap.values()) {
            returnOrderDetailList.add(returnOrderDetail);
        }
        return returnOrderDetailList;
    }

	/*
	@Override
	public OrderJsonVo covertOrderJsonWithGroup(OrderJsonVo vo) {
		OrderJsonVo returnVo = new OrderJsonVo();
		BeanUtils.copyProperties(vo, returnVo);
		List<OrderDetail> mallList = vo.getMallList();
		List<OrderDetail> selfList = vo.getSelfList();
		
		if(mallList != null){
			returnVo.setMallList(covertOrderDetailWithGroup(mallList));
		}
		
		if(selfList != null){
			returnVo.setSelfList(covertOrderDetailWithGroup(selfList));
		}
		return returnVo;
	}
	*/

    /**
     * 计算  根据订单和退货数量 ， 计算应该回收的成长值（注意和获得成长值逻辑一致）
     *
     * @param order
     * @param returnQty
     * @return
     */
    private int computeGrowthValueReturn(String memGuid, GrowthOrderInfo order, int returnQty, BigDecimal returnAllMoney, List<GrowthDetail> detailList_all) {

        List<GrowthDetail> detailList = this.getDetailListByChannels(detailList_all, new Integer[]{ConstantGrowth.DETAIL_GROWTH_CHANNEL_GW});

        // 购物没有获得成长值 的 也不能回收成长值
        if (detailList == null || detailList.size() < 1) {
            return 0;
        }

        if (detailList.size() > 1) {
            log.error("同一商品出现了多个成长值明细 goiSeq = " + order.getGoiSeq() + " memGuid = " + memGuid,"computeGrowthValueReturn");
        }

        GrowthDetail d = detailList.get(0);

        // 购物获得的 成长值
        int value_gw = d.getGrowthValue();
        if (value_gw == 0) {
            return 0;
        }
        int growthValue = 0;

        // 当前商品的  购物数量 - 已经退的数量
        int orderNowQty = order.getOgQty() - order.getReturnQty();

        // 如果全部退完
        if (returnQty == orderNowQty) {

            if (order.getReturnPay().add(returnAllMoney).compareTo(order.getRealPay()) >= 0) {

                List<GrowthDetail> returnList = this.getDetailListByChannels(detailList_all, new Integer[]{ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_GW});
                int returnValue = 0; // 已经退货了的成长值
                if (returnList != null) {
                    for (GrowthDetail gd : returnList) {
                        returnValue += gd.getGrowthValue();
                    }
                }
                growthValue = value_gw - returnValue;
            } else if (order.getReturnPay().add(returnAllMoney).compareTo(order.getRealPay()) < 0) {
                // 按退货的钱比例求出应回收成长值
                growthValue = returnAllMoney.divide(order.getRealPay(), 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(value_gw)).intValue();
            }

            // 如果是部分退货
        } else if (returnQty < orderNowQty) {
            // 按退货的钱比例求出应回收成长值
            growthValue = returnAllMoney.divide(order.getRealPay(), 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal(value_gw)).intValue();
        } else {

            String errorMsg = "退货数量大于购物数量 或 已经剩余的数量 ： ogSeq=" + order.getOgSeq() + "itNo=" + order.getItNo() + "returnQty=" + returnQty + "ogQty=" + order.getOgQty() + "orderNowQty=" + orderNowQty;
            log.error(errorMsg,"computeGrowthValueReturn");
            //throw new BizException(errorMsg);
        }

        return growthValue;

    }

    /**
     * 计算退货评论可回收成长值
     *
     * @param returnQty      当前退货数量
     * @param detailList_all
     * @return
     */
    private int computeGrowthValueReturnComment(final int returnQty, final List<GrowthDetail> detailList_all) {

        // 当前退货数量
        if (returnQty < 1) {
            return 0;
        }

        if (detailList_all == null || detailList_all.size() < 1) {
            return 0;
        }

        // 评论获得成长值类型
        List<Integer> channelList = new ArrayList<Integer>();
        channelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PL);
        channelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLZD);
        channelList.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLJH);

        // 评论回收类型
        List<Integer> channelListReturn =  new ArrayList<Integer>();
        channelListReturn.add(ConstantGrowth.DETAIL_GROWTH_CHANNEL_TH_PL);

        int value = 0;
        for (GrowthDetail d : detailList_all) {

            if (channelList.contains(d.getGrowthChannel())) {
                value = value + d.getGrowthValue();
            }
            if (channelListReturn.contains(d.getGrowthChannel())) {

                value = value - d.getGrowthValue();
            }
        }

        return value;

    }

}






