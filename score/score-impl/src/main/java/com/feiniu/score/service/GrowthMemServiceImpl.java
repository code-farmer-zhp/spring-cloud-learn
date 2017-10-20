package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.ProducerClient;
import com.feiniu.score.common.*;
import com.feiniu.score.dao.growth.GrowthDetailDao;
import com.feiniu.score.dao.growth.GrowthMainDao;
import com.feiniu.score.dao.growth.GrowthOrderInfoDao;
import com.feiniu.score.dao.growth.GrowthValueNumDao;
import com.feiniu.score.dao.score.ScoreGetLastValidOrder;
import com.feiniu.score.dao.score.ScoreGetOrderDetail;
import com.feiniu.score.dao.score.ScoreOrderDetailDao;
import com.feiniu.score.dao.score.ScoreYearDao;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.dto.PartnerInfo;
import com.feiniu.score.dto.Result;
import com.feiniu.score.entity.growth.GrowthDetail;
import com.feiniu.score.entity.growth.GrowthMain;
import com.feiniu.score.entity.growth.GrowthOrderInfo;
import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.exception.BizException;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.HttpRequestUtils;
import com.feiniu.score.vo.GrowthOrderDetail;
import com.feiniu.score.vo.GrowthOrderDetailByOg;
import com.feiniu.score.vo.OrderJsonVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@Service
public class GrowthMemServiceImpl implements GrowthMemService {

    public static final CustomLog log = CustomLog.getLogger(GrowthMemServiceImpl.class);

    @Autowired
    private GrowthDetailDao growthDetailDao;
    @Autowired
    private GrowthMainDao growthMainDao;
    @Autowired
    private GrowthOrderInfoDao growthOrderInfoDao;
    @Autowired
    private GrowthValueNumDao growthValueNumDao;
    @Autowired
    private ScoreOrderDetailDao scoreOrderDetailDao;
    @Autowired
    private ScoreYearDao scoreYearDao;
    @Autowired
    private PkadBaseServiceWithCache pkadDaoWithCache;
    @Autowired
    private ScoreGetLastValidOrder scoreGetLastValidOrder;
    @Autowired
    private ScoreGetOrderDetail scoreGetOrderDetail;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    @Qualifier("producerGrowthClient")
    private ProducerClient<Object, String> producerClient;
    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;
    //成长值消息变动topic
    @Value("${fn.topic.growth}")
    private String growthChangeTopic;

    @Autowired
    private GrowthBaseServiceWithCache growthBaseServiceWithCache;

    /**
     * 判断是否是第一次登录
     *
     * @param memGuid
     * @param date
     * @param cacheKey
     * @return
     */
    private boolean isFirstLogin(String memGuid, Date date, String cacheKey) {
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为 null");
        }
        String cacheValue = cacheUtils.getCacheData(cacheKey);
        if (cacheValue != null && "true".equals(cacheValue)) {
            return false;
        } else {
            return true;
            //用缓存存当天是否第一次登录。数据一致性由数据库的唯一键保障。
            /*SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
            String loginDate = sf.format(date);
            String uniqueKey = ConstantGrowth.DETAIL_GROWTH_CHANNEL_DL + "_" + memGuid + "_" + loginDate;
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("memGuid", memGuid);
            paramMap.put("uniqueKey", uniqueKey);
            paramMap.put("showZero", 1);
            paramMap.put("withOrder", 0);
            List<GrowthDetail> returnGd = growthDetailDao.getGrowthDetailListBySelective(memGuid, paramMap);
            return returnGd == null || returnGd.isEmpty();*/
        }
    }


    /**
     * 评论获得成长值。
     * 新增growth_detail记录
     * 修改growth_main中的成长值
     *
     * @param memGuid
     * @param data
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result saveGrowthByCommentProduct(String memGuid, String data) {
        Integer growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_PL;
        String remark = "评论商品";
        changeGrowthBecauseComment(data, growthChannel, remark);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    /**
     * 评论设定精华或置顶获得额外积分
     * 新增growth_detail记录
     * 修改growth_main中的成长值
     *
     * @param memGuid
     * @param data
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result saveGrowthBySetEssenceOrTop(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        String commentSeq = jsonObj.getString("commentSeq");
        if (StringUtils.isEmpty(commentSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "commentSeq 不能为空");
        }
        Integer comType = jsonObj.getInteger("dirType");
        if (comType == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "dirType 不能为空");
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("commentSeq", commentSeq);
        paramMap.put("memGuid", memGuid);
        paramMap.put("start", 0);
        paramMap.put("pageSize", 50);
        List<GrowthOrderInfo> growthOrderInfoList = growthOrderInfoDao.getGrowthOrderInfoListBySelective(memGuid, paramMap);

        if (growthOrderInfoList == null || growthOrderInfoList.size() == 0) {
            throw new ScoreException(ResultCode.RESULT_GROWTH_Set_Essence_Or_Top_BUT_NO_COMMENT_RECORD, "数据库中没有该评论的记录");
        } else if (Objects.equals(comType, ConstantGrowth.GROWTH_COMTYPE_COMMENT_SET_TOP)) {
            Integer growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLZD;
            String remark = "评论置顶";
            changeGrowthBecauseComment(data, growthChannel, remark);
        } else if (Objects.equals(comType, ConstantGrowth.GROWTH_COMTYPE_COMMENT_SET_ESSENCE)) {
            Integer growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLJH;
            String remark = "评论设置精华";
            changeGrowthBecauseComment(data, growthChannel, remark);
        } else if (Objects.equals(comType, ConstantGrowth.GROWTH_COMTYPE_COMMENT_BOTH_SET_ESSENCE_AND_TOP)) {
            Integer growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLZD;
            String remark = "评论置顶";
            changeGrowthBecauseComment(data, growthChannel, remark);

            Integer growthChannel2 = ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLJH;
            String remark2 = "评论设置精华";
            changeGrowthBecauseComment(data, growthChannel2, remark2);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }


    private void changeGrowthBecauseComment(String data, Integer growthChannel, String remark) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        //会员ID
        String memGuid = jsonObject.getString("memGuid");
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        //订单流水号
        String ogSeq = jsonObject.getString("ogSeq");
        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }
        //订单号
        String ogNo = jsonObject.getString("ogNo");
        if (StringUtils.isEmpty(ogNo)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogNo 不能为空");
        }
        //订单明细流水号
        String olSeq = jsonObject.getString("olSeq");
        if (StringUtils.isEmpty(olSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "olSeq 不能为空");
        }
        //商品ID
        String itNo = jsonObject.getString("itNo");
        if (StringUtils.isEmpty(itNo)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "itNo 不能为空");
        }

        //评论ID
        String commentSeq = jsonObject.getString("commentSeq");
        if (StringUtils.isEmpty(commentSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "commentSeq 不能为空");
        }
        Integer isWithPic;
        Integer picCount = jsonObject.getInteger("picCount");
        if (picCount == null || picCount < Constant.PIC_COUNT) {
            isWithPic = 0;
        } else {
            isWithPic = 1;
        }
        //growth_order_info是否有数据
        Map<String, Object> paramMapOrder = new HashMap<String, Object>();
        paramMapOrder.put("start", 0);
        paramMapOrder.put("pageSize", 2);
        paramMapOrder.put("memGuid", memGuid);
        paramMapOrder.put("olSeq", olSeq);
        paramMapOrder.put("ogSeq", ogSeq);
        List<GrowthOrderInfo> growthOrderInfolist = growthOrderInfoDao.getGrowthOrderInfoListBySelective(memGuid, paramMapOrder);
        if (growthOrderInfolist == null || growthOrderInfolist.size() == 0) {
            throw new ScoreException(ResultCode.RESULT_GROWTH_SUBMIT_ORDER_BUT_NO_CONSUME_LOG, "成长值数据表中无此单商品数据");
        }

        GrowthOrderInfo goi0 = growthOrderInfolist.get(0);

        if (goi0 != null) {
            if (goi0.getRealPay() == null || goi0.getOgQty() == 0) {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "商品实付金额和数量无数据");
            }
            if ((goi0.getReturnQty() == goi0.getOgQty())) {
                log.error("此单商品已全部退货，对其评论不获得成长值   data=" + data, "changeGrowthBecauseComment");
                return;
            }
            //按商品售价算，而非实付金额 ,大于0是为了区分老数据
            if (goi0.getPrice().doubleValue() < 5.0 && goi0.getPrice().doubleValue() > 0.0) {
                log.info("此件商品金额过低，对其评论不获得成长值   data=" + data, "changeGrowthBecauseComment");
                return;
            }
            //老数据没记price，默认为0.0，这种仍按实付金额的单价算。新数据price为0则实付必为0，也不会送成长值
            else if (goi0.getPrice() == null || (goi0.getPrice().doubleValue() == 0.0 && (goi0.getRealPay().doubleValue() / goi0.getOgQty()) < 5.0)) {
                log.info("此件商品金额过低，对其评论不获得成长值   data=" + data, "changeGrowthBecauseComment");
                return;
            }
        } else {
            throw new ScoreException(ResultCode.RESULT_GROWTH_SUBMIT_ORDER_BUT_NO_CONSUME_LOG, "成长值数据表中无此单商品数据");
        }

        //是否已对该单商品进行评论/精华/置顶
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("olSeq", olSeq);
        paramMap.put("ogSeq", ogSeq);
        paramMap.put("itNo", itNo);
        if (Objects.equals(growthChannel, ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLJH) || Objects.equals(growthChannel, ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLZD)) {
            paramMap.put("inChannels", ConstantGrowth.ZDJJCHANNELS);
        } else {
            paramMap.put("growthChannel", growthChannel);
        }
        paramMap.put("memGuid", memGuid);
        List<GrowthOrderDetail> growthOrderDetail = growthDetailDao.getGrowthOrderDetailBySelective(memGuid, paramMap);
        if (growthOrderDetail != null && growthOrderDetail.size() != 0) {
            //throw new ScoreException(ResultCode.RESULT_REPEAT_SUBMIT,"该商品的评论已经获得成长值，不能重复提交。");
            log.error("该商品的评论已经获得成长值  不能重复提交 data=" + data, "changeGrowthBecauseComment");
            return;
        }

        int getGrowth = 0;
        if (Objects.equals(growthChannel, ConstantGrowth.DETAIL_GROWTH_CHANNEL_PL)) {
            getGrowth = ConstantGrowth.GROWTH_GAIN_COMMENT_PRODUCT;
        }
        if (Objects.equals(growthChannel, ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLJH)) {
            getGrowth = ConstantGrowth.GROWTH_GAIN_COMMENT_SET_ESSENCE;
        }
        if (Objects.equals(growthChannel, ConstantGrowth.DETAIL_GROWTH_CHANNEL_PLZD)) {
            getGrowth = ConstantGrowth.GROWTH_GAIN_COMMENT_SET_TOP;
        }
        OrderJsonVo vo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
        //评论只看价格
        //如果是企业用户
        if (cacheUtils.isCompanyOrPartner(memGuid)) {
            log.info("增加评论成长值：因为用户是企业用户或合伙人，获得成长值为0。memGuid=" + memGuid, "changeGrowthBecauseComment");
            getGrowth = 0;
        } else if (StringUtils.equals(vo.getSiteMode(), Constant.ELECTRONIC_SCREEN)
                || Objects.equals(vo.getSourceMode(), Constant.DISTRIBUTION_PLATFORM)) {
            log.info("增加评论成长值：因为是分销商户或电子屏订单,获得成长值为0。memGuid=" + vo.getMemGuid() + ",ogSeq=" + vo.getOgSeq(), "changeGrowthBecauseComment");
            getGrowth = 0;
        }
        Long oiSeq = goi0.getGoiSeq();
        //若是评论则更新orderinfo表，置顶和加精不必
        if (Objects.equals(growthChannel, ConstantGrowth.DETAIL_GROWTH_CHANNEL_PL)) {
            goi0.setCommentSeq(commentSeq);
            goi0.setCommentWithPic(isWithPic);
            growthOrderInfoDao.updateGrowthOrderInfo(memGuid, goi0);
        }

        GrowthDetail growthDetailOfOrder = new GrowthDetail();
        growthDetailOfOrder.setMemGuid(memGuid);
        growthDetailOfOrder.setOrderInfoId(oiSeq);
        growthDetailOfOrder.setOperate("+");
        growthDetailOfOrder.setGrowthValue(getGrowth);
        growthDetailOfOrder.setGrowthChannel(growthChannel);
        growthDetailOfOrder.setDataFlag(ConstantGrowth.DATA_FLAG_YX);
        growthDetailOfOrder.setRemark(remark);
        growthDetailOfOrder.setGroupKey(growthChannel + "_" + commentSeq);
        growthDetailDao.saveGrowthDetail(memGuid, growthDetailOfOrder);

        if (getGrowth != 0) {
            int mainRows = growthBaseServiceWithCache.changeGrowthValue(memGuid, getGrowth, true);
            if (mainRows > 0) {
                saveGrowthChangeTokafkaForCRM(memGuid);
            }
        }

    }


    /**
     * 查询会员的等级
     *
     * @param memGuid
     * @return
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getMemLevel(String memGuid) {
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            data.putAll(getMemGrowthInfo(memGuid));
        } catch (ScoreException e) {
            if (ResultCode.GET_IS_PARTNER_ERROR == e.getCode()) {
                return new Result(ResultCode.GET_IS_PARTNER_ERROR, null, "查询是否为合伙人异常");
            }
            if (ResultCode.GET_IS_TRADE_UNIONIST_ERROR == e.getCode()) {
                return new Result(ResultCode.GET_IS_TRADE_UNIONIST_ERROR, null, "查询是否为工会会员异常");
            }
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }

    /**
     * 查询会员的成长值有效期内超过的会员百分比
     *
     * @param memGuid
     * @return
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getMemOverPercent(String memGuid) {
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }


        Map<String, Object> data = new HashMap<String, Object>();

        data.put("memGuid", memGuid);
        DecimalFormat decif = new DecimalFormat("0%");
        PartnerInfo partnerInfo = cacheUtils.getIsPartnerInfo(memGuid);
        if (partnerInfo.getIsPartner()) {
            data.put("growthValue", 0);
            if (cacheUtils.isTradeUnionist(memGuid)) {
                data.put("memLevel", ConstantGrowth.LEVEL_OF_TRADE_UNIONIST);
                data.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_TRADE_UNIONIST));
            } else {
                data.put("memLevel", ConstantGrowth.LEVEL_OF_PARTNER);
                data.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_PARTNER));
            }
            String time = partnerInfo.getBecomePartnerTime();
            if (StringUtils.isNotBlank(time)) {
                data.put("levelChangeTime", DateUtil.getFormatDateFromStr(time, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"));
            } else {
                data.put("levelChangeTime", null);
            }
            data.put("expiryDate", null);
            data.put("overPercent", decif.format(0));
        } else {
            GrowthMain gm = growthBaseServiceWithCache.getGrowthMainByMemGuid(memGuid, true);
            if (gm == null) {
                //throw new ScoreException(ResultCode.RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD,"数据库中无此用户数据");
                // 为空默认为新用户，普通会员
                data.put("memLevel", ConstantGrowth.LEVEL_OF_0);
                data.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_0));
                data.put("growthValue", 0);
                data.put("levelChangeTime", null);
                data.put("expiryDate", null);
                data.put("overPercent", decif.format(0));
            } else {
                data.put("memLevel", gm.getMemLevel());
                data.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(gm.getMemLevel()));
                data.put("growthValue", gm.getGrowthValue());
                data.put("levelChangeTime", DateUtil.getFormatDate(gm.getLevelChangeDate(), "yyyy/MM/dd HH:mm:ss"));
                data.put("expiryDate", DateUtil.getFormatDate(gm.getExpiryDate(), "yyyy/MM/dd"));

                Double percent;
                final String percentKey = gm.getGrowthValue() + ConstantCache.LESS_PERCENT;
                String percentStr = cacheUtils.getCacheData(percentKey);
                if (StringUtils.isNotBlank(percentStr)) {
                    BigDecimal bg = new BigDecimal(Double.parseDouble(percentStr));
                    percent = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                } else {
                    percent = growthValueNumDao.getPercentLessThanMyGrowthValue(gm.getGrowthValue());
                    cacheUtils.putCache(percentKey, 3600, percent);
                }
                data.put("overPercent", decif.format(percent));
            }
        }
        data.put("levelList", ConstantGrowth.GET_LEVEL_VALUE_NEED_DESC);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }


    /**
     * 查询成长值明细，带描述
     *
     * @param memGuid
     * @param data
     * @return
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getGrowthDetail(String memGuid, String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        JSONObject jsonObj = JSONObject.parseObject(data);
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        Date startDate = jsonObj.getDate("startDate");
//		if(startDate==null) {
//			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "startDate 不能为空");
//		}
        Date endDate = jsonObj.getDate("endDate");
//		if(endDate==null) {
//			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "endDate 不能为空");
//		}
        Integer pageNo = jsonObj.getInteger("PageIndex");
        if (pageNo == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "PageIndex 不合法");
        }
        Integer pageSize = jsonObj.getInteger("RowCount");
        if (pageSize == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "RowCount 不能为空");
        }
        Integer showCRMdel = jsonObj.getInteger("NoShowCrmDel");
        if (showCRMdel == null) {
            showCRMdel = 0;
        }
        //查询detail表的信息。
        Map<String, Object> paramMapForSel = new HashMap<String, Object>();
        paramMapForSel.put("start", (pageNo - 1) * pageSize);
        paramMapForSel.put("pageSize", pageSize);
        paramMapForSel.put("memGuid", memGuid);
        paramMapForSel.put("dataFlag", ConstantGrowth.DATA_FLAG_YX);
        paramMapForSel.put("startDate", startDate);
        paramMapForSel.put("endDate", endDate);
        if (showCRMdel != 0) {
            paramMapForSel.put("displayChannels", ConstantGrowth.NOSHOWCHANNELS);
        }
        List<GrowthOrderDetail> growthOrderDetailList = growthDetailDao.getGrowthOrderDetailBySelective(memGuid, paramMapForSel);
        int growthDetailCount = growthDetailDao.getGrowthOrderDetailCountBySelective(memGuid, paramMapForSel);

        if (growthOrderDetailList == null || growthOrderDetailList.size() == 0) {
            Map<String, Object> retuanData = new HashMap<String, Object>();
            retuanData.put("PageIndex", pageNo);
            retuanData.put("TotalItems", 0);
            retuanData.put("totalPage", 0);
            retuanData.put("growthDetailList", new JSONArray());
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, retuanData, "success");
            //return new Result(ResultCode.RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD, "数据库中无此用户数据");
            //throw new ScoreException(ResultCode.RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD,"数据库中无此用户数据");
        }

        Map<String, Object> retuanData = new HashMap<String, Object>();
        retuanData.put("PageIndex", pageNo);
        retuanData.put("TotalItems", growthDetailCount);
        retuanData.put("totalPage", (int) Math.ceil((double) growthDetailCount / (double) pageSize));
        JSONArray jmap = new JSONArray();

        Map<Integer, String> channelDescs = ConstantGrowth.GET_DETAIL_CHANNEL_DESC;
        Map<Integer, String> loginFromDescs = ConstantGrowth.GET_LOGIN_FROM_DESC;

        for (GrowthOrderDetail god : growthOrderDetailList) {
            Map<String, Object> retuanDetailData = new HashMap<String, Object>();
            String upDate = DateUtil.getFormatDate(god.getDetailUpdDate(), "yyyy-MM-dd HH:mm:ss");
            retuanDetailData.put("growthChangeDate", upDate);
            retuanDetailData.put("growthValue", god.getOperate() + god.getGrowthValue());
            retuanDetailData.put("growthType", channelDescs.get(god.getGrowthChannel()));
            if (ConstantGrowth.DETAIL_GROWTH_CHANNEL_DL.equals(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc",
                        loginFromDescs.get(god.getLoginFrom()) + " 登录时间 : "
                                + upDate);
            }
            if (ConstantGrowth.GIFTCHANNELS.contains(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc", "飞牛赠送成长值");
            }
            if (ConstantGrowth.NOSHOWCHANNELS.contains(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc", "飞牛回收成长值");
            }
            if (ConstantGrowth.OG_SM_CHANNELS.contains(god.getGrowthChannel()) || ConstantGrowth.OG_PL_CHANNELS.contains(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc", "订单号 : " + god.getOgNo()
                        + " 商品ID : " + god.getItNo());
                retuanDetailData.put("orderNo", god.getOgNo());
                retuanDetailData.put("itNo", god.getItNo());
                retuanDetailData.put("smSeq", god.getSmSeq());
                retuanDetailData.put("ogsSeq", god.getOgsSeq());
            }
            jmap.add(retuanDetailData);
        }
        retuanData.put("growthDetailList", jmap);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, retuanData, "success");

    }


    /**
     * 查询符合条件的成长值明细记录数量
     *
     * @param memGuid
     * @param data
     * @return
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Integer getGrowthDetailCount(String memGuid, String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        JSONObject jsonObj = JSONObject.parseObject(data);
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        Date startDate = jsonObj.getDate("startDate");
//		if(startDate==null) {
//			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "startDate 不能为空");
//		}
        Date endDate = jsonObj.getDate("endDate");
//		if(endDate==null) {
//			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "endDate 不能为空");
//		}
        //查询detail表的信息。
        Map<String, Object> paramMapForSel = new HashMap<String, Object>();
        paramMapForSel.put("memGuid", memGuid);
        paramMapForSel.put("startDate", startDate);
        paramMapForSel.put("endDate", endDate);
        paramMapForSel.put("dataFlag", ConstantGrowth.DATA_FLAG_YX);
        Integer showCRMdel = jsonObj.getInteger("NoShowCrmDel");
        if (showCRMdel == null) {
            showCRMdel = 0;
        }
        if (showCRMdel != 0) {
            paramMapForSel.put("displayChannels", ConstantGrowth.NOSHOWCHANNELS);
        }
        int growthDetailCount = 0;
        growthDetailCount = growthDetailDao.getGrowthDetailCountBySelective(memGuid, paramMapForSel);
        return growthDetailCount;
    }


    /**
     * 监听CRM赠送成长值
     *
     * @param memGuid
     * @return json
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void saveGrowthkafkafromCRM(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        String gtadId = jsonObj.getString("gtad_id");//主键编号(去重)
        String membGradeF = jsonObj.getString("memb_grade_f");//等级生效时间（去重）
        String gtadType = jsonObj.getString("gtad_type");//来源类型  1是权益发放,2权益取消
        String mrdfType = jsonObj.getString("mrdf_type");//权益类型 A1：是
        Integer mrdfGrowth = jsonObj.getInteger("mrdf_growth");//成长值
        //String  dGtad   =  jsonObj.getString("d_gtad");//发放日期
        //String  dTake =  jsonObj.getString("d_take");//领取有效日期
        String dEffF = jsonObj.getString("d_eff_f");//权益生效日
        String dEffT = jsonObj.getString("d_eff_t");//权益失效日
        Date effectiveDate = null;
        Date expiryDate = null;
        Date now = new Date();
        if (memGuid == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为 null");
        }
        if (gtadId == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "gtad_id 不能为 null");
        }
        if (membGradeF == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memb_grade_f 不能为 null");
        }
        if (!"1".equals(gtadType) && !"2".equals(gtadType) && !"3".equals(gtadType) && !"4".equals(gtadType)) {
            throw new BizException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "gtad_type 参数 错误");
        }
        if (!"A1".equals(mrdfType)) {
            throw new BizException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "mrdf_type 参数 错误");
        }
        if (mrdfGrowth == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "mrdf_growth 不能为 null");
        }
        if (dEffF == null || StringUtils.equals(dEffF, "null")) {
            effectiveDate = now;
        }
        if (dEffT == null || StringUtils.equals(dEffT, "null")) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_eff_t 不能为 null");
        }

        try {
            if (dEffF != null && !StringUtils.equals(dEffF, "null")) {
                effectiveDate = DateUtils.parseDate(dEffF, "yyyyMMdd");
            }
            if (dEffT != null && !StringUtils.equals(dEffT, "null")) {
                expiryDate = DateUtils.parseDate(dEffT, "yyyyMMdd");
            }
        } catch (ParseException e) {
            log.error("error：日期格式转换失败", "saveGrowthkafkafromCRM", e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "日期格式转换失败");
        }


        GrowthMain growthMainSel = growthMainDao.getGrowthMainByMemGuid(memGuid);

        //检查是否重复
        Map<String, Object> uniqueparamMap = new HashMap<String, Object>();
        uniqueparamMap.put("uniqueKey", ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS + "_" + gtadId + memGuid + membGradeF);
        List<GrowthDetail> unionGd = this.growthDetailDao.getGrowthDetailListByMemGuid(memGuid, uniqueparamMap);
        if (unionGd != null && !unionGd.isEmpty()) {
            throw new BizException(ResultCode.RESULT_REPEAT_SUBMIT, "重复提交的CRM的kafka数据");
        }

        Integer growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS;
        GrowthDetail growthDetail = new GrowthDetail();
        growthDetail.setMemGuid(memGuid);
        if ("1".equals(gtadType)) {//权益发放
            growthDetail.setOperate("+");
            growthDetail.setGrowthChannel(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS);
            growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS;
        } else if ("3".equals(gtadType)) {//权益调整发放
            growthDetail.setOperate("+");
            growthDetail.setGrowthChannel(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZFF);
            growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZFF;
        } else if ("2".equals(gtadType)) {//权益取消
            growthDetail.setOperate("-");
            growthDetail.setGrowthChannel(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNQX);
            growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNQX;
        } else if ("4".equals(gtadType)) {//权益调整取消
            growthDetail.setOperate("-");
            growthDetail.setGrowthChannel(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZQX);
            growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZQX;
        }
        if (("2".equals(gtadType)) || ("4".equals(gtadType))) {
            if (growthMainSel == null) {
                throw new ScoreException(ResultCode.RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD, "CRM扣除成长值，但数据库中无此用户数据");
            } else if (growthMainSel.getGrowthValue() - Math.abs(mrdfGrowth) < 0) {
                log.error("变更后成长值小于0,growth_value=" + growthMainSel.getGrowthValue() + ",mrdfGrowth=" + mrdfGrowth, "saveGrowthkafkafromCRM");
                growthDetail.setGrowthValue(growthMainSel.getGrowthValue());
            } else {
                growthDetail.setGrowthValue(Math.abs(mrdfGrowth));
            }
        } else {
            growthDetail.setGrowthValue(Math.abs(mrdfGrowth));
        }
        growthDetail.setDataFlag(ConstantGrowth.DATA_FLAG_YX);
        growthDetail.setUniqueKey(growthChannel + "_" + gtadId + memGuid + membGradeF);
        growthDetail.setGroupKey(growthChannel + "_" + gtadId + memGuid + membGradeF);
        growthDetail.setInsDate(now);
        growthDetail.setUpdDate(growthDetail.getInsDate());
        growthDetailDao.saveGrowthDetail(memGuid, growthDetail);

        GrowthMain gm = new GrowthMain();
        if (growthMainSel == null) {
            gm.setMemGuid(memGuid);
            if ("1".equals(gtadType) || "3".equals(gtadType)) {
                gm.setGrowthValue(Math.abs(mrdfGrowth));
            } else {
                throw new BizException(ResultCode.RESULT_STATUS_EXCEPTION, "变更后成长值小于0");
            }
            gm.setEffectiveDate(effectiveDate);
            gm.setInsDate(now);
            gm.setUpdDate(gm.getInsDate());
            growthBaseServiceWithCache.saveGrowthMain(memGuid, gm, true);
            //成长值变动通知CRM
            this.saveGrowthChangeTokafkaForCRM(memGuid);
        } else {
            gm.setGmSeq(growthMainSel.getGmSeq());
            if ("1".equals(gtadType) || "3".equals(gtadType)) {
                gm.setChangedGrowthValue(Math.abs(mrdfGrowth));
            } else {
                if (growthMainSel.getGrowthValue() - Math.abs(mrdfGrowth) < 0) {
                    log.error("变更后成长值小于0,growth_value=" + growthMainSel.getGrowthValue() + ",mrdfGrowth=" + mrdfGrowth, "saveGrowthkafkafromCRM");
                    gm.setGrowthValue(0);
                } else {
                    gm.setChangedGrowthValue(-Math.abs(mrdfGrowth));
                }
            }
            gm.setEffectiveDate(effectiveDate);
            gm.setUpdDate(now);
            growthBaseServiceWithCache.updateGrowthMain(memGuid, gm, true);
            //成长值变动通知CRM
            this.saveGrowthChangeTokafkaForCRM(memGuid);
        }
    }

    /**
     * 监听kafka 获取 成长值、有效期、会员等级变化
     *
     * @param memGuid
     * @return json
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void getkafkafromCRM(String memGuid, String data) {

        JSONObject jsonObj = JSONObject.parseObject(data);
        String membGrade = jsonObj.getString("memb_grade");//会员等级
        String gradeUpdDate = jsonObj.getString("d_grade");//等级更新日
        String gradeEffDate = jsonObj.getString("d_grade_f");//等级生效日
        String gradeEfflessDate = jsonObj.getString("d_grade_t");//等级失效日
        String gradeEffTime = jsonObj.getString("t_grade_f");//等级生效时
        String gradeEfflessTime = jsonObj.getString("t_grade_t");//等级失效时

        if (StringUtils.isEmpty(memGuid)) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为 null");
        }
        if (membGrade == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memb_grade 不能为 null");
        }
        if (gradeUpdDate == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_grade 不能为 null");
        }
        if (gradeEffDate == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_grade_f 不能为 null");
        }
        if (gradeEffTime == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "t_grade_f 不能为 null");
        }
        if (gradeEfflessDate == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "d_grade_t 不能为 null");
        }
        if (gradeEfflessTime == null) {
            throw new BizException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "t_grade_t 不能为 null");
        }

        Date effectiveDate;
        Date expiryDate;
        try {
            effectiveDate = DateUtils.parseDate(gradeEffDate + gradeEffTime, "yyyyMMddHHmmss");
            expiryDate = DateUtils.parseDate(gradeEfflessDate + gradeEfflessTime, "yyyyMMddHHmmss");
        } catch (ParseException e) {
            log.error("error：日期格式转换失败", "getkafkafromCRM", e);
            throw new BizException(ResultCode.RESULT_STATUS_EXCEPTION, "日期格式转换失败");
        }

        int growthMain = growthMainDao.getGrowthMainListCount(memGuid);
        Date now = new Date();
        if (growthMain == 0) {
            GrowthMain gm = new GrowthMain();
            gm.setMemGuid(memGuid);
            gm.setGrowthValue(0);
            gm.setMemLevel(membGrade);
            gm.setEffectiveDate(effectiveDate);
            gm.setExpiryDate(expiryDate);
            gm.setLevelChangeDate(now);
            growthBaseServiceWithCache.saveGrowthMain(memGuid, gm, true);
        } else {
            GrowthMain gmainFromDb = growthMainDao.getGrowthMainByMemGuid(memGuid);
            GrowthMain gmain = new GrowthMain();
            gmain.setGmSeq(gmainFromDb.getGmSeq());
            gmain.setMemLevel(membGrade);
            gmain.setEffectiveDate(effectiveDate);
            gmain.setExpiryDate(expiryDate);
            gmain.setLevelChangeDate(now);
            growthBaseServiceWithCache.updateGrowthMain(memGuid, gmain, true);
        }

    }

    /**
     * 成长值变动通知CRM
     *
     * @param memGuid
     * @return dataJson
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void saveGrowthChangeTokafkaForCRM(String memGuid) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("memGuid", memGuid);
        List<GrowthMain> growthMains = this.growthMainDao.getGrowthMainList(memGuid, paramMap);
        if (growthMains != null && growthMains.size() > 0) {
            GrowthMain selfGrowth = growthMains.get(0);
            Integer growthvalue = selfGrowth.getGrowthValue();//当下成长值
            String dGrowth;//成长值生效 日期
            String tGrowth;//成长值生效时间

            try {
                String growthEfftTimeStr = DateUtil.getFormatDate(selfGrowth.getUpdDate(), "yyyyMMddHHmmss");
                dGrowth = growthEfftTimeStr.substring(0, 8);
                tGrowth = growthEfftTimeStr.substring(8, 14);
            } catch (Exception e) {
                log.error("error：日期格式转换失败", "saveGrowthChangeTokafkaForCRM", e);
                throw new BizException(ResultCode.RESULT_STATUS_EXCEPTION, "日期格式错误或者日期格式转换失败");
            }

            Map<String, Object> info = new HashMap<>();
            info.put("memb_id", memGuid);
            info.put("memb_growth", growthvalue);
            info.put("d_growth", dGrowth);
            info.put("t_growth", tGrowth);
            String message = JSONObject.toJSONString(info);
            try {
                producerClient.sendMessage(growthChangeTopic, "saveGrowthChange", message);
                log.info("成长值变动消息发送:" + message, "saveGrowthChangeTokafkaForCRM");
            } catch (Exception e) {
                log.error("发送成长值变动消息失败。message=" + message, "saveGrowthChangeTokafkaForCRM", e);
                scoreExceptionHandler.handlerBizException(e, memGuid, message, Constant.GROWTH_PRODUCE_GROWTH);
            }
        } else {
            throw new BizException(ResultCode.RESULT_STATUS_EXCEPTION, memGuid + ": 用户没有成长值记录");
        }

    }

    /**
     * 礼包赠送成长值,目前版本未被使用
     *
     * @param memGuid
     * @return json
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void saveGrowthfromPkad(String memGuid, String data, Pkad pkad) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        String mrstId = jsonObj.getString("mrst_id");//主键编号(去重)
        String membId = jsonObj.getString("memb_id");//会员id（去重）
        String membGradeF = jsonObj.getString("memb_grade_f");//等级生效时间（去重）
        Integer mrdfGrowth = jsonObj.getInteger("mrdf_growth");//成长值
        if (memGuid == null || !membId.equals(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "memb_id 参数错误");
        }
        if (mrstId == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "mrst_id 不能为 null");
        }
        if (membGradeF == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memb_grade_f 不能为 null");
        }
        String pkadType = pkad.getPkadType();
        if (!"1".equals(pkadType) && !"2".equals(pkadType) && !"3".equals(pkadType) && !"4".equals(pkadType)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "pkad_type 参数 错误");
        }
        if (mrdfGrowth == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "mrdf_growth 不能为 null");
        }

        Map<String, Object> uniqueparamMap = new HashMap<String, Object>();
        uniqueparamMap.put("uniqueKey", ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS + "_" + mrstId + memGuid + membGradeF);
        List<GrowthDetail> unionGd = this.growthDetailDao.getGrowthDetailListByMemGuid(memGuid, uniqueparamMap);
        if (unionGd != null && !unionGd.isEmpty()) {
            log.info("RequestNo:"+ HttpRequestUtils.getRequestNo()+" , 领取礼包时，尝试重复领取成长值，unionGd ： "+unionGd);
            return ;
        }

        Integer growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS;
        GrowthDetail growthDetail = new GrowthDetail();
        growthDetail.setMemGuid(memGuid);
        if ("1".equals(pkadType)) {//飞牛赠送
            growthDetail.setOperate("+");
            growthDetail.setGrowthChannel(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS);
            growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNZS;
        } else if ("3".equals(pkadType)) {//权益调整发放
            growthDetail.setOperate("+");
            growthDetail.setGrowthChannel(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZFF);
            growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZFF;
        } else if ("2".equals(pkadType)) {//权益取消
            growthDetail.setOperate("-");
            growthDetail.setGrowthChannel(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNQX);
            growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNQX;
        } else if ("4".equals(pkadType)) {//权益调整取消
            growthDetail.setOperate("-");
            growthDetail.setGrowthChannel(ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZQX);
            growthChannel = ConstantGrowth.DETAIL_GROWTH_CHANNEL_FNTZQX;
        }
        growthDetail.setGrowthValue(Math.abs(mrdfGrowth));
        growthDetail.setDataFlag(ConstantGrowth.DATA_FLAG_YX);
        growthDetail.setUniqueKey(growthChannel + "_" + mrstId + memGuid + membGradeF);
        growthDetail.setGroupKey(growthChannel + "_" + mrstId + memGuid + membGradeF);
        growthDetail.setInsDate(new Date());
        growthDetail.setUpdDate(growthDetail.getInsDate());
        if (!StringUtils.isBlank(pkad.getMrstUi()) && ConstantMrst.MRSTUIList.contains(pkad.getMrstUi())) {
            growthDetail.setRemark(ConstantMrst.GET_MRSTUI_DESC.get(pkad.getMrstUi()));
        }
        growthDetailDao.saveGrowthDetail(memGuid, growthDetail);

        int growthMain = growthMainDao.getGrowthMainListCount(memGuid);
        if (growthMain == 0) {
            GrowthMain gm = new GrowthMain();
            gm.setMemGuid(memGuid);
            if ("1".equals(pkadType) || "3".equals(pkadType)) {
                gm.setGrowthValue(Math.abs(mrdfGrowth));
            } else {
                gm.setGrowthValue(0 - Math.abs(mrdfGrowth));
            }
            growthBaseServiceWithCache.saveGrowthMain(memGuid, gm, true);
            //成长值变动通知CRM
            this.saveGrowthChangeTokafkaForCRM(memGuid);
        } else {
            if ("1".equals(pkadType) || "3".equals(pkadType)) {
                growthBaseServiceWithCache.changeGrowthValue(memGuid, Math.abs(mrdfGrowth), true);
            } else {
                growthBaseServiceWithCache.changeGrowthValue(memGuid, -Math.abs(mrdfGrowth), true);
            }
            //成长值变动通知CRM
            this.saveGrowthChangeTokafkaForCRM(memGuid);
        }
    }

    @Override
    public Result queryMemLevelList() {
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, ConstantGrowth.GET_LEVEL_VALUE_NEED_DESC, "success");
    }


    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getMemScoreAndGrowthInfo(String memGuid) {
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.putAll(getMemGrowthInfo(memGuid));
        //权益
        if (cacheUtils.isPartner(memGuid)) {
            data.put("mrstUiList", new JSONArray());
        } else {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("membId", memGuid);
            String today = DateUtil.getFormatDate(new Date(), "yyyyMMdd");
            paramMap.put("today", today);
            paramMap.put("isRecharge", ConstantMrst.IS_F_DB);
            paramMap.put("isCancel", ConstantMrst.IS_F_DB);
            Set<String> mrstUis = pkadDaoWithCache.getMrstUisBySelective(memGuid, paramMap, true);

            paramMap.put("isCancel", ConstantMrst.IS_T_DB);
            paramMap.put("isTake", ConstantMrst.IS_T_DB);
            Set<String> mrstUisCancelButTaken = pkadDaoWithCache.getMrstUisBySelective(memGuid, paramMap, true);

            mrstUis.addAll(mrstUisCancelButTaken);
            data.put("mrstUiList", mrstUis);
        }

        Integer availableScore = scoreYearDao.getAvaliableScore(memGuid, "true");

        data.put("availableScore", availableScore == null ? 0 : availableScore);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }


    /**
     * 查询成长值明细，带描述。按订单显示
     *
     * @param memGuid
     * @param data
     * @return
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getGrowthDetailGroupByOg(String memGuid, String data, Integer isWithGroupKey) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        JSONObject jsonObj = JSONObject.parseObject(data);
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        Date startDate = jsonObj.getDate("startDate");
//		if(startDate==null) {
//			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "startDate 不能为空");
//		}
        Date endDate = jsonObj.getDate("endDate");
//		if(endDate==null) {
//			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "endDate 不能为空");
//		}
        Integer pageNo = jsonObj.getInteger("PageIndex");
        if (pageNo == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "PageIndex 不合法");
        }
        Integer pageSize = jsonObj.getInteger("RowCount");
        if (pageSize == null || pageSize == 0) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "RowCount 不能为空或0");
        }

        Map<String, Object> returnData = new HashMap<String, Object>();
        PartnerInfo partnerInfo = cacheUtils.getIsPartnerInfo(memGuid);
        if (partnerInfo.getIsPartner()) {
            returnData.put("PageIndex", pageNo);
            returnData.put("growthDetailList", new JSONArray());
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "success");
        }

        //查询detail表的信息。
        Map<String, Object> paramMapForSel = new HashMap<String, Object>();
        paramMapForSel.put("start", (pageNo - 1) * pageSize);
        paramMapForSel.put("pageSize", pageSize);
        paramMapForSel.put("memGuid", memGuid);
        paramMapForSel.put("dataFlag", ConstantGrowth.DATA_FLAG_YX);
        paramMapForSel.put("startDate", startDate);
        paramMapForSel.put("endDate", endDate);
        List<GrowthOrderDetailByOg> growthOrderDetailGroupByOgList = growthDetailDao.getGrowthDetailGroupByOgWithKey(memGuid, paramMapForSel);
        /*int growthDetailGroupByOgCount;*/
        /*int growthDetailGroupByOgCount=growthDetailDao.getGrowthDetailCountGroupByOg(memGuid, paramMapForSel);*/

        if (growthOrderDetailGroupByOgList == null || growthOrderDetailGroupByOgList.size() == 0) {
            returnData.put("PageIndex", pageNo);
            returnData.put("growthDetailList", new JSONArray());
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "success");
            //return new Result(ResultCode.RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD, "数据库中无此用户数据");
            //throw new ScoreException(ResultCode.RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD,"数据库中无此用户数据");
        }

        returnData.put("PageIndex", pageNo);
        /*retuanData.put("TotalItems",growthDetailGroupByOgCount);
        retuanData.put("totalPage",(int) Math.ceil((double)growthDetailGroupByOgCount/(double)pageSize));*/
        JSONArray jmap = new JSONArray();

        Map<Integer, String> channelDescs = ConstantGrowth.GET_DETAIL_CHANNEL_DESC;
        Map<Integer, String> loginFromDescs = ConstantGrowth.GET_LOGIN_FROM_DESC;

        for (GrowthOrderDetailByOg god : growthOrderDetailGroupByOgList) {
            Map<String, Object> retuanDetailData = new HashMap<String, Object>();
            String upDate = DateUtil.getFormatDate(god.getDetailUpdDate(), "yyyy-MM-dd HH:mm:ss");
            retuanDetailData.put("growthChangeDate", upDate);
            retuanDetailData.put("growthValue", god.getOperate() + god.getGrowthValue());
            retuanDetailData.put("growthType", channelDescs.get(god.getGrowthChannel()));
            if (ConstantGrowth.DETAIL_GROWTH_CHANNEL_DL.equals(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc",
                        loginFromDescs.get(god.getLoginFrom()) + " 登录时间 : "
                                + upDate);
            }
            if (ConstantGrowth.GIFTCHANNELS.contains(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc", "飞牛赠送成长值");
            }
            if (ConstantGrowth.NOSHOWCHANNELS.contains(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc", "飞牛回收成长值");
            }
            if (ConstantGrowth.OG_SM_CHANNELS.contains(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc", "订单号 : " + god.getOgNo());
                retuanDetailData.put("orderNo", god.getOgNo());
                retuanDetailData.put("ogsSeq", god.getOgsSeq());
            }
            if (ConstantGrowth.OG_PL_CHANNELS.contains(god.getGrowthChannel())) {
                retuanDetailData.put("growthDesc", "订单号 : " + god.getOgNo() + " 商品ID : " + god.getItNo());
                retuanDetailData.put("orderNo", god.getOgNo());
                retuanDetailData.put("ogsSeq", god.getOgsSeq());
                retuanDetailData.put("smSeq", god.getSmSeq());
            }
            jmap.add(retuanDetailData);
        }
        returnData.put("growthDetailList", jmap);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "success");

    }

    /**
     * 查询成长值明细，带描述。按订单显示
     *
     * @param memGuid
     * @param data
     * @return
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getGrowthDetailCountGroupByOg(String memGuid, String data, Integer isWithGroupKey) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        JSONObject jsonObj = JSONObject.parseObject(data);
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        Map<String, Object> retuanData = new HashMap<String, Object>();

        PartnerInfo partnerInfo = cacheUtils.getIsPartnerInfo(memGuid);
        if (partnerInfo.getIsPartner()) {
            retuanData.put("TotalItems", 0);
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, retuanData, "success");
        }
        Date startDate = jsonObj.getDate("startDate");
        Date endDate = jsonObj.getDate("endDate");

        //查询detail表的信息。
        Map<String, Object> paramMapForSel = new HashMap<String, Object>();
        paramMapForSel.put("memGuid", memGuid);
        paramMapForSel.put("dataFlag", ConstantGrowth.DATA_FLAG_YX);
        paramMapForSel.put("startDate", startDate);
        paramMapForSel.put("endDate", endDate);
        int growthDetailGroupByOgCount = growthDetailDao.getGrowthDetailCountGroupByOgWithKey(memGuid, paramMapForSel);

        retuanData.put("TotalItems", growthDetailGroupByOgCount);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, retuanData, "success");

    }


    @Override
    public Result clearCacheValue(String key) {
        boolean flag1 = cacheUtils.removeCacheData(key);
        boolean flag2 = cacheUtils.hDel(key);
        if (flag1 && flag2) {
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
        } else {
            return new Result(ResultCode.RESULT_RUN_TIME_EXCEPTION, "移除缓存失败");
        }
    }


    @Override
    public String showCacheValue(String key, String field) {
        if (StringUtils.isBlank(field)) {
            return cacheUtils.getCacheData(key);
        } else {
            return cacheUtils.hGet(key, field);
        }
    }

    @Override
    public Result putCacheValue(String key, String value) {
        cacheUtils.putCache(key, 60 * 60 * 24 * 7, value);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    private Map<String, Object> getMemGrowthInfo(String memGuid) {
        Map<String, Object> data = new HashMap<>();
        PartnerInfo partnerInfo = cacheUtils.getIsPartnerInfo(memGuid);
        if (partnerInfo.getIsPartner()) {
            data.put("growthValue", 0);
            if (cacheUtils.isTradeUnionist(memGuid)) {
                data.put("memLevel", ConstantGrowth.LEVEL_OF_TRADE_UNIONIST);
                data.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_TRADE_UNIONIST));
            } else {
                data.put("memLevel", ConstantGrowth.LEVEL_OF_PARTNER);
                data.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_PARTNER));
            }
            String time = partnerInfo.getBecomePartnerTime();
            if (StringUtils.isNotBlank(time)) {
                data.put("levelChangeTime", DateUtil.getFormatDateFromStr(time, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"));
            } else {
                data.put("levelChangeTime", null);
            }
            data.put("nextLevel", "");
            data.put("nextLevelDesc", "");
            data.put("nextLevelNeed", 0);
            data.put("isPartner", 1);
        } else {
            GrowthMain gm = growthBaseServiceWithCache.getGrowthMainByMemGuid(memGuid, true);
            if (gm == null) {

                //return new Result(ResultCode.RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD,"数据库中无此用户数据");
                //throw new ScoreException(ResultCode.RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD,"数据库中无此用户数据");
                // 为空默认为新用户，普通会员
                data.put("memLevel", ConstantGrowth.LEVEL_OF_0);
                data.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_0));
                data.put("growthValue", 0);
                data.put("levelChangeTime", null);
                data.put("isPartner", 0);
                data.putAll(ConstantGrowth.getNextLevelInfo(0));
            } else {
                data.put("memLevel", gm.getMemLevel());
                data.put("growthValue", gm.getGrowthValue());
                data.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(gm.getMemLevel()));
                data.put("isPartner", 0);
                data.put("levelChangeTime", DateUtil.getFormatDate(gm.getLevelChangeDate(), "yyyy/MM/dd HH:mm:ss"));
                data.putAll(ConstantGrowth.getNextLevelInfo(gm.getGrowthValue()));
            }
            data.put("levelList", ConstantGrowth.GET_LEVEL_VALUE_NEED_DESC);
        }
        return data;
    }

    @Override
    public void growthValueNumChange(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        Integer growthValueDuce = jsonObject.getInteger("growthValueDuce");
        Integer growthValueAdd = jsonObject.getInteger("growthValueAdd");
        if (!Objects.equals(growthValueDuce, growthValueAdd)) {
            growthValueNumDao.changeTableGrowthValueNum(growthValueDuce, growthValueAdd);
        }
    }
}
