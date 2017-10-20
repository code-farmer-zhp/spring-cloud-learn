package com.feiniu.score.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.ProducerClient;
import com.feiniu.score.common.*;
import com.feiniu.score.common.Constant.ScoreStatus;
import com.feiniu.score.dao.growth.GrowthMainDao;
import com.feiniu.score.dao.growth.SearchMemberDao;
import com.feiniu.score.dao.score.*;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.datasource.ScoreSlaveDataSource;
import com.feiniu.score.dto.Result;
import com.feiniu.score.entity.growth.GrowthMain;
import com.feiniu.score.entity.score.*;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.ExceptionMsgUtil;
import com.feiniu.score.util.HttpRequestUtils;
import com.feiniu.score.util.MD5Util;
import com.feiniu.score.vo.*;
import com.feiniu.score.vo.ProductInfo.OrderScoreInfo;
import com.feiniu.score.vo.ReturnJsonVo.ReturnDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ScoreServiceImpl implements ScoreService {

    public static final CustomLog log = CustomLog.getLogger(ScoreServiceImpl.class);

    @Autowired
    private ScoreMemberDao scoreMemberDao;

    @Autowired
    private ScoreMainLogDao scoreMainLogDao;

    @Autowired
    private ScoreOrderDetailDao scoreOrderDetailDao;

    @Autowired
    private ScoreYearDao scoreYearDao;

    @Autowired
    private GrowthMainDao growthMainDao;

    @Autowired
    private ScoreCommonDao scoreCommonDao;

    @Autowired
    private ScoreGetBillDao scoreGetBillDao;

    @Autowired
    private ScoreGetOrderDetailDao scoreGetOrderDetailDao;

    @Autowired
    private ScoreGetCommmentDetailDao scoreGetCommmentDetailDao;

    @Autowired
    private ScoreDefalutTableDao scoreDefalutTableDao;

    @Autowired
    private SearchMemberDao searchMemberDao;

    @Autowired
    private ScoreGetOrderDetail scoreGetOrderDetail;

    @Autowired
    private ScoreGetReturnDetail scoreGetReturnDetail;

    @Autowired
    private ScoreGetTotalScoreBySmSeqDao scoreGetTotalScoreBySmSeqDao;

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private ProducerClient<Object, String> producerClient;

    @Value("${fn.topic.order.score}")
    private String scoreTopic;

    @Autowired
    private ScoreFilter scoreFilter;

    @Autowired
    private GrowthOrderService growthOrderService;

    @Autowired
    private ScoreGetStoreInfoDao scoreGetStoreInfoDao;

    @Autowired
    private ScoreLoadOlService scoreLoadOlService;

    @Value("${scoreUseSection}")
    private Long scoreUseSection;

    /**
     * 显示订单可获积分信息
     */
    @Override
    public Result getOrderScore(String memGuid, String productInfoJson) {
        ProductInfo productInfo = ProductInfo.parseJson(productInfoJson);
        Map<String, Object> data = new HashMap<>();
        List<Long> useScoreList = new ArrayList<>();
        if (cacheUtils.isCompanyUser(memGuid)) {
            data.put("totalScore", 0);
            data.put("maxUseScorePoints", 0);
            data.put("availableScore", 0);
            data.put("useScoreList", useScoreList);
            data.put("scoreUseSection", scoreUseSection);
        } else {
            //Integer avscore = scoreYearDao.getAvaliableScore(memGuid);
            //int availableScore = avscore == null ? 0 : avscore;
            List<OrderScoreInfo> selfList = productInfo.getSelfList();
            List<OrderScoreInfo> mallList = productInfo.getMallList();

            int totalScore;
            //long maxUseScorePoints;

            HashMap<String, String> itNoMapToCpSeq = new HashMap<>();
            for (OrderScoreInfo orderScoreInfo : selfList) {
                itNoMapToCpSeq.put(orderScoreInfo.getItNo(), orderScoreInfo.getCpSeq());
            }
            for (OrderScoreInfo orderScoreInfo : mallList) {
                itNoMapToCpSeq.put(orderScoreInfo.getItNo(), orderScoreInfo.getCpSeq());
            }
            //计算可获得多少积分
            totalScore = computeObtainScore(selfList, mallList, itNoMapToCpSeq);
            //计算最大使用积分
            //maxUseScorePoints = computeMaxUseScore(selfList, mallList) * 100;
            //maxUseScorePoints = maxUseScorePoints > availableScore ? availableScore : maxUseScorePoints;

           /* long sectionCount = maxUseScorePoints / scoreUseSection;
            for (long i = sectionCount; i >= 0; i--) {
                useScoreList.add(scoreUseSection * i);
            }*/
            data.put("totalScore", totalScore);
            data.put("maxUseScorePoints", 0);
            data.put("availableScore", 0);
            data.put("useScoreList", useScoreList);
            data.put("scoreUseSection", scoreUseSection);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }

    private long computeMaxUseScore(List<OrderScoreInfo> selfList, List<OrderScoreInfo> mallList) {
        BigDecimal totalPay = new BigDecimal(0);
        if (selfList != null && selfList.size() > 0) {
            // 不可以使用积分的parentId标识
            Map<String, Boolean> notCanUserParentIdMap = scoreFilter.getCanUseScoreInfo(selfList);
            for (OrderScoreInfo info : selfList) {
                String parentId = info.getParentId();
                String ssmGrade = info.getSsmGrade();
                String ssmType = info.getSsmType();
                Boolean parentIdFlag = notCanUserParentIdMap.get(parentId);
                if (parentIdFlag == null
                        && !StringUtils.equals(ssmGrade, Constant.PROMOTION_GRADE_OF_1)
                        && !StringUtils.equals(ssmGrade, Constant.PROMOTION_GRADE_OF_3)
                        && !StringUtils.equals(ssmType, Constant.GROUP_TYPE)) {
                    totalPay = totalPay.add(info.getRealPay());
                }
            }
        }
        if (mallList != null && mallList.size() > 0) {
            for (OrderScoreInfo info : mallList) {
                totalPay = totalPay.add(info.getRealPay());
            }
        }
        return totalPay.divide(BigDecimal.valueOf(2.00), BigDecimal.ROUND_DOWN).toBigInteger().longValue();
    }


    private int computeObtainScore(List<OrderScoreInfo> selfList, List<OrderScoreInfo> mallList, HashMap<String, String> itNoMapToCpSeq) {
        int totalScore = 0;
        if (itNoMapToCpSeq.size() > 0) {
            //请求对应的比例
            JSONObject billsByItNos;
            try {
                billsByItNos = scoreGetBillDao.getBillsInfo(itNoMapToCpSeq, true);
            } catch (Exception e) {
                log.error("查询积分比例出错降级。", "computeObtainScore", e);
                return 0;
            }
            if (selfList != null && selfList.size() > 0) {
                //计算积分
                for (OrderScoreInfo orderScoreInfo : selfList) {
                    String itNo = orderScoreInfo.getItNo();
                    String ssmGrade = orderScoreInfo.getSsmGrade();
                    String ssmType = orderScoreInfo.getSsmType();

                    BigDecimal bill;
                    if (billsByItNos.get(itNo) != null) {
                        bill = billsByItNos.getJSONObject(itNo).getBigDecimal(ScoreGetBillDao.BILL_KEY);
                    } else {
                        bill = new BigDecimal(0);
                    }
                    //判断是否可以获得积分（不是团购商品，且符合促销等级）
                    if (bill != null
                            && !StringUtils.equals(ssmGrade, Constant.PROMOTION_GRADE_OF_1)
                            && !StringUtils.equals(ssmGrade, Constant.PROMOTION_GRADE_OF_3)
                            && !StringUtils.equals(ssmGrade, Constant.PROMOTION_GRADE_OF_4)
                            && !StringUtils.equals(ssmType, Constant.GROUP_TYPE)) {
                        totalScore += Math.floor(orderScoreInfo.getRealPay().multiply(bill).doubleValue());
                    }
                }
            }
            if (mallList != null && mallList.size() > 0) {
                for (OrderScoreInfo orderScoreInfo : mallList) {
                    String itNo = orderScoreInfo.getItNo();
                    BigDecimal bill;
                    if (billsByItNos.get(itNo) != null) {
                        bill = billsByItNos.getJSONObject(itNo).getBigDecimal(ScoreGetBillDao.BILL_KEY);
                    } else {
                        bill = new BigDecimal(0);
                    }
                    if (bill != null) {
                        totalScore += Math.floor(orderScoreInfo.getRealPay().multiply(bill).doubleValue());
                    }
                }
            }
        }
        return totalScore;
    }

    /**
     * 订单提交后从用户账户中扣除订单中抵扣的积分，同时异步执行生成此订单可获积分的功能
     */
    @Override
    public Result submitOrderScore(String memGuid, String orderInfo) {
        JSONObject jsonObj = JSONObject.parseObject(orderInfo);

        Integer consumeScore = jsonObj.getInteger("consumeScore");
        if (consumeScore == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "consumeScore 不能为空");
        }
        if (consumeScore < 0) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "consumeScore 不能为负值");
        }
        //订单流水号
        String ogSeq = jsonObj.getString("ogSeq");
        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }

        //订单流号
        String ogNo = jsonObj.getString("ogNo");
        if (StringUtils.isEmpty(ogNo)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogNo 不能为空");
        }
        //如果是企业用户就为1
        Integer memType = jsonObj.getInteger("memType");
        if (memType == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memType 不能为空");
        }
        //省份
        String provinceId = jsonObj.getString("provinceId");
        if (StringUtils.isEmpty(provinceId)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "provinceId 不能为空");
        }

        //如果是企业用户且消费积分。报错
        if (Objects.equals(memType, Constant.COMPANY_USER) && consumeScore > 0) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "企业用户不能使用积分。");
        }

        scoreCommonDao.deductOrderComsumeScore(memGuid, consumeScore, ogSeq, ogNo, provinceId);

        //放入缓存
        cacheUtils.putConsumeScore(ogSeq, consumeScore);
        //发送消息进行订单详细信息入库
        Map<String, Object> data = new HashMap<>();
        data.put("memGuid", memGuid);
        data.put("ogSeq", ogSeq);
        data.put("consumeScore", consumeScore);
        data.put("ogNo", ogNo);
        data.put("provinceId", provinceId);
        Map<String, Object> info = new HashMap<>();
        info.put("type", Constant.DIRECT_TYPE_SUBMIT_ORDER);
        info.put("data", data);
        String message = JSONObject.toJSONString(info);
        try {
            producerClient.sendMessage(scoreTopic, System.currentTimeMillis() + "", message);
        } catch (Exception e) {
            log.error("提交订单后，发送kafka消息失败。", "submitOrderScore", e);
            String errorMsg = ExceptionMsgUtil.getMsg(e);
            scoreDefalutTableDao.handleFailMessage(memGuid, message, Constant.DIRECT_TYPE_SUBMIT_ORDER, errorMsg);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }


    /**
     * 查询订单获得积分信息
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getOrderScoreInfo(String memGuid, String order) {
        JSONObject jsonObj = JSONObject.parseObject(order);
        String ogSeq = jsonObj.getString("ogSeq");

        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }
        //从缓存中取
        Integer getScore = null;
        String value = cacheUtils.getCacheData(cacheUtils.getGetScoreKey(ogSeq));
        if (StringUtils.isNotBlank(value)) {
            getScore = Integer.parseInt(value);
        }

        OrderJsonVo orderJsonVo = null;

        //如果缓存中没有
        if (getScore == null) {
            //重新计算
            try {
                orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
            } catch (Exception e) {
                log.error("获取订单详细错误服务降级。", "getOrderScoreInfo", e);
            }
            if (orderJsonVo != null) {
                try {
                    getScore = scoreCommonDao.computeScore(orderJsonVo);
                    cacheUtils.putGetScore(ogSeq, getScore);
                } catch (Exception e) {
                    log.error("计算积分错误服务降级。", "getOrderScoreInfo", e);
                    getScore = 0;
                }
            } else {
                getScore = 0;
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("getScore", getScore);

        // 计算订单的成长值
        Integer getGrowthValue = 0;
        String growthKey = ConstantGrowth.CACHE_ORDER_GROWTH_KEY + ogSeq;
        String obj = cacheUtils.getCacheData(growthKey);
        if (StringUtils.isBlank(obj)) {
            if (cacheUtils.isCompanyOrPartner(memGuid)) {
                cacheUtils.putCache(growthKey, CacheUtils.TEN_DAY, 0);
                data.put("getGrowth", 0);
                data.put("nextLevel", "");
                data.put("nextLevelDesc", "");
                data.put("nextLevelNeed", 0);
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
            } else {
                if (orderJsonVo != null) {
                    //computeGrowthValueByOrderJson将计算结果加缓存
                    Map<String, Object> map = growthOrderService.computeGrowthValueByOrderJson(orderJsonVo, cacheUtils.isEmployee(memGuid));
                    getGrowthValue = (int) map.get("growthValue");
                } else {
                    getGrowthValue = 0;
                }
            }
        } else {
            getGrowthValue = Integer.parseInt(obj);
        }
        data.put("getGrowth", getGrowthValue);
        GrowthMain growthMain = growthMainDao.getGrowthMainByMemGuid(memGuid);
        Integer myGrowthValue;
        if (growthMain == null) {
            myGrowthValue = 0;
        } else {
            myGrowthValue = growthMain.getGrowthValue();
        }
        Integer totalGrowthValue = getGrowthValue + myGrowthValue;
        data.putAll(ConstantGrowth.getNextLevelInfo(totalGrowthValue));

        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }

    /**
     * 查询订单消费积分信息
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getOrderConsumeScoreInfo(String memGuid, String order) {
        JSONObject jsonObj = JSONObject.parseObject(order);
        String ogSeq = jsonObj.getString("ogSeq");

        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }
        //从缓存中取
        Integer consumeScore = null;
        String value = cacheUtils.getCacheData(cacheUtils.getConsumeScoreKey(ogSeq));
        if (StringUtils.isNotBlank(value)) {
            consumeScore = Integer.parseInt(value);
        }
        //如果缓存中没有从数据库里取
        if (consumeScore == null) {
            ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_CONSUME);
            if (scoreMainLog != null) {
                consumeScore = Math.abs(scoreMainLog.getScoreNumber());
            } else {
                consumeScore = 0;
            }
            cacheUtils.putConsumeScore(ogSeq, consumeScore);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("consumeScore", consumeScore);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }

    /**
     * 订单付款
     */
    @Override
    public Result addScore(String memGuid, String order) {
        JSONObject jsonObj = JSONObject.parseObject(order);

        String ogSeq = jsonObj.getString("ogSeq");
        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }
        String payDate = jsonObj.getString("payDate");
        //支付完成的订单，生产消息加入信息队列服务器，后台消费消息计算真正积分并入库
        //srcType:0（包括自营和商城）
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        info.put("type", Constant.DIRECT_TYPE_ORDER_BUY);

        data.put("ogSeq", ogSeq);
        data.put("memGuid", memGuid);
        if (StringUtils.isNotBlank(payDate)) {
            data.put("payDate", payDate);
        }
        info.put("data", data);
        String message = JSONObject.toJSONString(info);
        try {
            producerClient.sendMessage(scoreTopic, System.currentTimeMillis() + "", message);
            log.info("订单付款 message:" + message, "addScore");
        } catch (Exception e) {
            log.error("付款请求发送kafka消息失败。" + message, "addScore", e);
            String errorMsg = ExceptionMsgUtil.getMsg(e);
            scoreDefalutTableDao.handleFailMessage(memGuid, message, Constant.DIRECT_TYPE_ORDER_BUY, errorMsg);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }


    /**
     * 查询退货单可返还的消费积分
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getReturnConsumeScore(String memGuid, String inputData) {
        JSONObject jsonObj = JSONObject.parseObject(inputData);

        String rgSeq = jsonObj.getString("rgSeq");
        if (StringUtils.isEmpty(rgSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "rgSeq 不能为空");
        }
        //因退货可返回的积分
        Integer getScoreTotal = null;
        String value = cacheUtils.getCacheData(cacheUtils.getGetScoreKey(rgSeq));
        if (StringUtils.isNotBlank(value)) {
            getScoreTotal = Integer.parseInt(value);
        }
        if (getScoreTotal == null) {

            ScoreMainLog scoreMainLogBack = scoreMainLogDao.getScoreMainLogBack(memGuid, rgSeq, Constant.SCORE_CHANNEL_RETURN_PRODUCT_ROCKBACK);
            if (scoreMainLogBack != null) {
                getScoreTotal = scoreMainLogBack.getScoreNumber();
            } else {
                //查询退货信息
                ReturnJsonVo returnJsonVo = scoreGetReturnDetail.getReturnDetail(memGuid, rgSeq, "");
                String ogSeq = returnJsonVo.getOgSeq();
                ScoreMainLog scoreMainLogBuy = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_BUY);
                if (scoreMainLogBuy == null) {
                    getScoreTotal = 0;
                } else {
                    //商城退单列表
                    List<ReturnDetail> mallList = returnJsonVo.getMallList();
                    //自已退单列表
                    List<ReturnDetail> selfList = returnJsonVo.getSelfList();
                    //退货商品信息总列表
                    //是否是商城退货
                    boolean isMall = false;
                    List<ReturnDetail> returnList = new ArrayList<>();
                    if (mallList != null && mallList.size() > 0) {
                        returnList.addAll(mallList);
                        isMall = true;
                    }
                    if (selfList != null && selfList.size() > 0) {
                        returnList.addAll(selfList);
                    }
                    //用户订单购买获得积分的详细信息
                    List<ScoreOrderDetail> orderBuyGetScoreDetailList = scoreOrderDetailDao.getScoreOrderDetailList(memGuid, ogSeq, Constant.SCORE_ORDER_DETAIL_TYPE_BUY);
                    for (ReturnDetail returnDetail : returnList) {
                        //订单明细
                        String olSeq = returnDetail.getOlSeq();
                        //查找订单购买时对应的商品
                        ScoreOrderDetail scoreOrderDetailBuy = null;
                        for (ScoreOrderDetail sodBuy : orderBuyGetScoreDetailList) {
                            String olSeqBuy = sodBuy.getOlSeq();
                            //找到对应的商品
                            if (StringUtils.equals(olSeq, olSeqBuy)) {
                                scoreOrderDetailBuy = sodBuy;
                                break;
                            }
                        }
                        if (scoreOrderDetailBuy != null) {
                            //查询此商品已经退货了多少个。
                            Integer countHaveReturn = scoreOrderDetailDao.getItHaveReturnCount(scoreOrderDetailBuy.getMemGuid(), returnDetail, scoreOrderDetailBuy.getOgSeq());
                            countHaveReturn = NumberUtils.getIntValue(countHaveReturn, 0);
                            //退货商品可以获得返还的消费积分
                            int scoreGet = scoreCommonDao.computeScoreGet(returnDetail, scoreOrderDetailBuy, isMall, countHaveReturn);
                            getScoreTotal += scoreGet;
                        }
                    }
                }
            }
            cacheUtils.putGetScore(rgSeq, getScoreTotal);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("returnConsumeScore", getScoreTotal);

        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }


    /**
     * 取消订单，只处理商城的订单，返回消费积分
     */
    @Override
    public Result cancelMallOrderConsumeScore(String memGuid, String dataJson) {
        JSONObject jsonObj = JSONObject.parseObject(dataJson);

        //订单流水号
        String ogSeq = jsonObj.getString("ogSeq");
        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }

        Integer packageNo = jsonObj.getInteger("packageNo");

        //取消订单，生产消息加入信息队列服务器，后台消费消息计算真正积分并入库
        //type:1  订单确认加积分;2,退货退款确认减积分;3,取消订单（未付款）以后扩展使用
        //srcType:1 自营;2, 商城;退货使用，退货分商城和自营
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        info.put("type", Constant.DIRECT_TYPE__CANCLE_MALL_ORDER);
        data.put("ogSeq", ogSeq);
        data.put("memGuid", memGuid);
        data.put("packageNo", packageNo);
        info.put("data", data);
        String message = JSONObject.toJSONString(info);
        try {
            producerClient.sendMessage(scoreTopic, System.currentTimeMillis() + "", message);
            log.info("取消商城订单 message:" + message, "cancelMallOrderConsumeScore");
        } catch (Exception e) {
            log.error("取消商城订单，发送kafka消息失败。message=" + message, "cancelMallOrderConsumeScore", e);
            String errorMsg = ExceptionMsgUtil.getMsg(e);
            scoreDefalutTableDao.handleFailMessage(memGuid, message, Constant.DIRECT_TYPE__CANCLE_MALL_ORDER, errorMsg);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }


    /**
     * 退货退款确认后回收发放的积分和返还消费的积分
     */
    @Override
    public Result confirmReturnScore(String memGuid, String dataJson) {
        JSONObject jsonObj = JSONObject.parseObject(dataJson);
        //退货单号
        String rgSeq = jsonObj.getString("rgSeq");
        if (StringUtils.isEmpty(rgSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "rgSeq 不能为空");
        }
        //付款状态
        String pay = jsonObj.getString("pay");
        if (StringUtils.isEmpty(pay)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pay 不能为空");
        }

        String rssSeq = jsonObj.getString("rssSeq");

        //退货确认，生产消息加入信息队列服务器，后台消费消息计算真正积分退回
        //type:1  订单确认加积分;2,退货退款确认减积分;以后扩展使用
        //srcType:1 自营;2, 商城;退货使用，退货分商城和自营
        Map<String, Object> info = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        info.put("type", Constant.DIRECT_TYPE_RETURN_PRODUCT);
        data.put("rgSeq", rgSeq);
        data.put("memGuid", memGuid);
        data.put("pay", pay);
        data.put("rssSeq", rssSeq);
        info.put("data", data);
        String message = JSONObject.toJSONString(info);
        try {
            producerClient.sendMessage(scoreTopic, System.currentTimeMillis() + "", message);
            log.info("退货确认 message:" + message, "confirmReturnScore");
        } catch (Exception e) {
            log.error("退货确认发送kafka消息失败。message=" + message, "confirmReturnScore", e);
            String errorMsg = ExceptionMsgUtil.getMsg(e);
            scoreDefalutTableDao.handleFailMessage(memGuid, message, Constant.DIRECT_TYPE_RETURN_PRODUCT, errorMsg);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    /**
     * 显示用户可用积分信息
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getUserAvaliableScore(String memGuid, String cache) {
        if (StringUtils.isEmpty(cache)) {
            cache = "true";
        }
        Integer availableScore = scoreYearDao.getAvaliableScore(memGuid, cache);
        Map<String, Object> data = new HashMap<>();
        data.put("availableScore", availableScore == null ? 0 : availableScore);

        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }


    /**
     * 查询用户可用积分，待生效积分，即将过期积分
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getUserScoreInfo(String memGuid) {
        //积分汇总:用户可用积分，用户即将生效积分
        int lockedScore = 0;
        int expiredScore = 0;
        ScoreMember scoreMember = scoreMemberDao.getLockedAndExpired(memGuid);
        if (scoreMember != null) {
            lockedScore = scoreMember.getLockedScore();
            expiredScore = scoreMember.getExpiredScore();
        }

        Integer avaliableScore = scoreYearDao.getAvaliableScore(memGuid, "true");
        Calendar calendar = Calendar.getInstance();
        //今年年底
        String dueTime = calendar.get(Calendar.YEAR) + "/12/31";
        //积分过期时间为今年年底的积分信息
        Integer expiringScore = scoreYearDao.getExpiringScore(memGuid, dueTime);

        Map<String, Object> data = new HashMap<>();
        //用户可用积分
        data.put("availabeScore", avaliableScore == null ? 0 : avaliableScore);
        //用户即将生效积分
        data.put("lockedScore", lockedScore);
        //用户即将过期积分
        data.put("expiringScore", expiringScore == null ? 0 : expiringScore);
        //过期时间
        data.put("expiringTime", dueTime);

        data.put("expiredScore", expiredScore);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }


    /**
     * 查询用户积分详细信息
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getUserScoreDetailList(String memGuid, String queryData) {
        JSONObject jsonObj = JSONObject.parseObject(queryData);
        //订单流水号
        String ogSeq = jsonObj.getString("ogSeq");
        if (StringUtils.isEmpty(ogSeq)) {
            ogSeq = null;
        }
        String ogNo = jsonObj.getString("ogNo");
        if (StringUtils.isEmpty(ogNo)) {
            ogNo = null;
        }

        //积分来源：0:全部;1:购物；2：评论;3.绑定手机;4.绑定邮箱;5.手机签到获得
        Integer srcType = jsonObj.getInteger("srcType");
        if (srcType == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "srcType 不能为空");
        }
        //积分消费获取类型：0:全部；1，获取；2，消费
        Integer directType = jsonObj.getInteger("directType");
        if (directType == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "directType 不能为空");
        }
        //开始时间
        String startTime = jsonObj.getString("startTime");
        if (StringUtils.isEmpty(startTime)) {
            startTime = null;
        }
        //结束时间
        String endTime = jsonObj.getString("endTime");
        if (StringUtils.isEmpty(endTime)) {
            endTime = null;
        } else {
            endTime += " 23:59:59";
        }
        //第几页
        Integer pageNo = jsonObj.getInteger("pageNo");
        if (pageNo == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pageNo 不能为空");
        }
        //每页显示条数
        Integer pageSize = jsonObj.getInteger("pageSize");
        if (pageSize == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pageSize 不能为空");
        }

        //是否查询订单或评论的详细信息
        Boolean isDetail = jsonObj.getBoolean("isDetail");
        if (isDetail == null) {
            isDetail = false;
        }
        //{srcType:\"0\",directType:\"0\",pageNo:\"1\",pageSize:\"15\"}
        //只缓存默认第一页
        boolean isDefault = false;
        if (srcType == 0 && directType == 0 && pageNo == 1 && pageSize == 15
                && startTime == null && endTime == null && ogNo == null && ogSeq == null) {
            isDefault = true;
            Map data = cacheUtils.getUserScoreDetailList(memGuid);
            if (data != null) {
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
            }
        }
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("ogSeq", ogSeq);
        mapParam.put("ogNo", ogNo);
        mapParam.put("memGuid", memGuid);
        mapParam.put("startTime", startTime);
        mapParam.put("endTime", endTime);
        mapParam.put("srcType", srcType);
        mapParam.put("directType", directType);
        mapParam.put("pageSize", pageSize);
        //分页起始位置
        int start = Math.max(pageSize * (pageNo - 1), 0);
        mapParam.put("start", start);

        List<Map<String, Object>> userScoreList = scoreMainLogDao.getUserScoreDetailList(mapParam, memGuid);
        if (userScoreList == null) {
            userScoreList = new ArrayList<>();
        }
        Map<Long, Object> batchQueryComments = new HashMap<>();
        //查询评论详细信息
        if (isDetail && userScoreList.size() > 0) {
            batchQueryComments = batchQueryComments(userScoreList);
        }
        //订单信息缓存
        Map<String, Object> cacheOrder = new HashMap<>();
        for (Map<String, Object> map : userScoreList) {
            Integer channel = (Integer) map.get("channel");
            //评论类型的。
            if (isCommentChannle(channel)) {
                map.put("srcType", Constant.SRC_TYPE_COMMENT);
                map.put("type", "评论");
                if (isDetail) {
                    Long commentSeq = (Long) map.get("commentSeq");
                    //拼装成和购物一样的格式数据
                    List<Object> list = new ArrayList<>();
                    list.add(batchQueryComments.get(commentSeq));
                    Map<String, Object> mapDetail = new HashMap<>();
                    mapDetail.put("dataList", list);
                    mapDetail.put("packNo", 0);
                    List<Map<String, Object>> detail = new ArrayList<>();
                    detail.add(mapDetail);
                    map.put("detail", detail);
                } else {
                    map.put("detail", "评论");
                }
                map.put("remark", "");

            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_BIND_PHONE)) {
                //绑定手机
                map.put("srcType", Constant.SRC_TYPE_BIND_PHONE);
                map.put("type", "绑定手机");
                map.put("detail", "绑定手机");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_BIND_EMAIL)) {
                //绑定邮箱
                map.put("srcType", Constant.SRC_TYPE_BIND_EMAIL);
                map.put("type", "绑定邮箱");
                map.put("detail", "绑定邮箱");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_SCORE_EXPIRED)) {
                map.put("srcType", Constant.SRC_TYPE_BUY);
                map.put("type", "积分过期");
                map.put("detail", "积分过期");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_PHONE_SIGN)) {
                map.put("srcType", Constant.SRC_TYPE_PHONE_SIGN);
                map.put("type", "签到");
                map.put("detail", "签到获得积分");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_CRM_GIVE)) {
                map.put("srcType", Constant.SRC_TYPE_CRM_GIVE);
                map.put("type", "飞牛赠送");
                map.put("detail", "飞牛赠送积分");
                map.put("remark", map.get("remark"));
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_CRM_RECOVER)) {
                map.put("srcType", Constant.SRC_TYPE_CRM_RECOVER);
                map.put("type", "飞牛回收");
                map.put("detail", "飞牛回收积分");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_RAFFLE_GIVE)) {
                map.put("srcType", Constant.SRC_TYPE_RAFFLE);
                map.put("type", "积分抽奖");
                map.put("detail", "抽奖获得积分");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_RAFFLE_COST)) {
                map.put("srcType", Constant.SRC_TYPE_RAFFLE);
                map.put("type", "积分抽奖");
                map.put("detail", "抽奖使用积分");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_EXCHANGE_CARD_COST)) {
                map.put("srcType", Constant.SRC_TYPE_EXCHANGE);
                map.put("type", "积分兑换");
                map.put("detail", "兑换单号：" + map.get("remark"));
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_EXCHANGE_VOUCHER_COST)) {
                String remark = map.get("remark").toString();
                if (StringUtils.isNotEmpty(remark) && remark.contains(Constant.VOUCHER_EXCHANGE_REMARK_PREFIX)) {
                    map.put("detail", "兑换飞牛优惠券\"" + remark.substring(Constant.VOUCHER_EXCHANGE_REMARK_PREFIX.length()) + "\"");
                } else {
                    map.put("detail", "积分兑换优惠券");
                }
                map.put("srcType", Constant.SRC_TYPE_EXCHANGE);
                map.put("type", "积分兑换");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_FILL_IN_INTEREST)) {
                map.put("srcType", Constant.SRC_TYPE_OTHER);
                map.put("type", "其他");
                map.put("detail", "完善兴趣爱好");
                map.put("remark", "");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_APP_UPGRADE_GIVE)) {
                map.put("srcType", Constant.SRC_TYPE_OTHER);
                map.put("type", "其他");
                map.put("detail", "升级飞牛网APP获得积分");
                map.put("remark", "升级飞牛网APP");
            } else {
                //订单类型
                if (isDetail) {
                    String ogSeqBack = (String) map.get("ogSeq");
                    Object orderDetail = cacheOrder.get(memGuid + ogSeqBack);
                    if (orderDetail == null) {
                        orderDetail = scoreGetOrderDetailDao.getOrderDetailByOgSeq(memGuid, ogSeqBack);
                        cacheOrder.put(memGuid + ogSeqBack, orderDetail);
                    }
                    map.put("detail", orderDetail);
                } else {
                    map.put("detail", "购物");
                }
                map.put("srcType", Constant.SRC_TYPE_BUY);
                map.put("type", "购物");
                //如果不是客服赠送的不保留remark。
                if (!Objects.equals(channel, Constant.SCORE_CHANNEL_APPROVAL)) {
                    map.put("remark", "");
                }

            }
            Date limitTime = (Date) map.get("limitTime");
            map.put("scoreStatus", ScoreStatus.getDes(channel, limitTime));
            map.put("description", Constant.DESCRIPTION[channel]);
        }

        Integer totalNum = scoreMainLogDao.getUserScoreDetailListCount(mapParam, memGuid);
        if (totalNum == null) {
            totalNum = 0;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("userScoreList", userScoreList);
        data.put("totalNum", totalNum);
        if (isDefault) {
            cacheUtils.putUserScoreDetailList(memGuid, data);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, data, "success");
    }

    /**
     * 绑定手机
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result saveScoreByBindPhone(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);

        //手机号
        String phoneNo = jsonObj.getString("phoneNo");
        if (StringUtils.isEmpty(phoneNo)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "phoneNo 不能为空");
        }
        Integer channel = Constant.SCORE_CHANNEL_BIND_PHONE;
        Integer count = scoreMainLogDao.getScoreMainLogCountByChannel(memGuid, channel);
        if (count != null && count > 0) {
            throw new ScoreException(ResultCode.RESULT_REPEAT_SUBMIT, "已经获得绑定手机的积分，不能重复提交。");
        }
        Integer scoreGet = Constant.BIND_PHONE_SCORE;
        addScoreEffectiveImmediately(memGuid, phoneNo, channel, scoreGet, "", "");
        String phoneKey = memGuid + "_bind_phone";
        cacheUtils.removeCacheData(phoneKey);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    /**
     * 绑定邮箱
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result saveScoreByBindEmail(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);

        //邮箱
        String email = jsonObj.getString("email");
        if (StringUtils.isEmpty(email)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "email 不能为空");
        }
        Integer channel = Constant.SCORE_CHANNEL_BIND_EMAIL;
        Integer count = scoreMainLogDao.getScoreMainLogCountByChannel(memGuid, channel);
        if (count != null && count > 0) {
            throw new ScoreException(ResultCode.RESULT_REPEAT_SUBMIT, "已经获得绑定邮箱的积分，不能重复提交。");
        }
        Integer scoreGet = Constant.BIND_EMAIL_SCORE;
        addScoreEffectiveImmediately(memGuid, email, channel, scoreGet, "", "");
        String emailKey = memGuid + "_bind_email";
        cacheUtils.removeCacheData(emailKey);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result haveSign(String memGuid) {
        String scoreTodayStr = cacheUtils.getCacheData(memGuid + ConstantCache.SCORE_SIGN_TODAY);
        if (StringUtils.isNotBlank(scoreTodayStr)) {
            JSONObject scoreTodayJson = JSONObject.parseObject(scoreTodayStr);
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, scoreTodayJson.getBoolean("flag"), "success");
        } else {
            Integer channel = Constant.SCORE_CHANNEL_PHONE_SIGN;
            //查询是否有今天签到获得积分记录并返回
            Boolean flag;
            Integer scoreNumber = scoreMainLogDao.getTodayScoreBySign(memGuid, channel);

            Map<String, Object> resultMap = new HashMap<>();
            if (scoreNumber == null) {
                flag = Boolean.FALSE;
                resultMap.put("flag", false);
                resultMap.put("getScore", null);
            } else {
                flag = Boolean.TRUE;
                resultMap.put("flag", true);
                resultMap.put("getScore", scoreNumber);
            }
            cacheUtils.putCache(memGuid + ConstantCache.SCORE_SIGN_TODAY, DateUtil.getSecondsUntilTomorrowZero().intValue(), JSONObject.toJSONString(resultMap));
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, flag, "success");
        }
    }

    /**
     * 评论获得积分。立即生效(异步处理）
     */
    @Override
    public Result saveScoreByCommentProduct(String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        String memGuid = checkParam(jsonObject);
        //发送消息
        Map<String, Object> kafkaMsg = new HashMap<>();
        kafkaMsg.put("type", Constant.DIRECT_TYPE_COMMENT);
        kafkaMsg.put("data", jsonObject);
        String message = JSONObject.toJSONString(kafkaMsg);
        try {
            producerClient.sendMessage(scoreTopic, System.currentTimeMillis() + "", message);
            log.info("提交评论message:" + message, "saveScoreByCommentProduct");
        } catch (Exception e) {
            log.error("提交评论后，发送kafka消息失败。", "saveScoreByCommentProduct", e);
            String errorMsg = ExceptionMsgUtil.getMsg(e);
            scoreDefalutTableDao.handleFailMessage(memGuid, message, Constant.DIRECT_TYPE_COMMENT, errorMsg);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    private String checkParam(JSONObject jsonObject) {
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

        //卖场编号
        String smSeq = jsonObject.getString("smSeq");
        if (StringUtils.isEmpty(smSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "smSeq 不能为空");
        }
        //评论商品ID
        Integer commentSeq = jsonObject.getInteger("commentSeq");
        if (commentSeq == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "commentSeq 不能为空");
        }
        //评论获得的积分
        Integer getScore = jsonObject.getInteger("getScore");
        if (getScore == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "getScore 不能为空");
        }
        return memGuid;
    }

    /**
     * 评论设定精华或置顶获得额外积分
     */
    @Override
    public Result saveScoreBySetEssenceOrTop(String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer dirType = jsonObj.getInteger("dirType");
        if (dirType == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "dirType 不能为空");
        }
        String memGuid = checkParam(jsonObj);
        //发送消息
        Map<String, Object> kafkaMsg = new HashMap<>();
        kafkaMsg.put("type", Constant.DIRECT_TYPE_SETESSENCEORTOP);
        kafkaMsg.put("data", jsonObj);
        String message = JSONObject.toJSONString(kafkaMsg);
        try {
            producerClient.sendMessage(scoreTopic, System.currentTimeMillis() + "", message);
            log.info("评论设定精华或置顶message:" + message, "saveScoreBySetEssenceOrTop");
        } catch (Exception e) {
            log.error("评论设定精华或置顶，发送kafka消息失败。", "saveScoreBySetEssenceOrTop", e);
            String errorMsg = ExceptionMsgUtil.getMsg(e);
            scoreDefalutTableDao.handleFailMessage(memGuid, message, Constant.DIRECT_TYPE_SETESSENCEORTOP, errorMsg);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    /**
     * 审核通过送积分
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result saveScoreByCustomerGive(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);

        String ogSeq = jsonObj.getString("ogSeq");
        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }
        String ogNo = jsonObj.getString("ogNo");
        if (StringUtils.isEmpty(ogNo)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogNo 不能为空");
        }
        Integer scoreNumber = jsonObj.getInteger("scoreNumber");
        if (scoreNumber == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "scoreNumber 不能为空");
        }
        String remark = jsonObj.getString("remark");
        if (StringUtils.isEmpty(remark)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "remark 不能为空");
        }

        Integer channel = Constant.SCORE_CHANNEL_APPROVAL;


        addScoreEffectiveImmediately(memGuid, remark, channel, scoreNumber, ogSeq, ogNo);

        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    //积分立即生效
    private Integer addScoreEffectiveImmediately(String memGuid, String remark, Integer channel, Integer scoreGet, String ogSeq, String ogNo) {
        //如果是企业用户就为0积分
        if (cacheUtils.isCompanyUser(memGuid)) {
            log.info("增加积分（立即生效）：因为用户是企业用户名，获得0积分。memGuid=" + memGuid, "addScoreEffectiveImmediately");
            scoreGet = 0;
        }
        //记录ScoreMainLog日志
        ScoreMainLog scoreMainLogNew = new ScoreMainLog();
        scoreMainLogNew.setChannel(channel);
        scoreMainLogNew.setRemark(remark);
        scoreMainLogNew.setMemGuid(memGuid);
        scoreMainLogNew.setOgSeq(ogSeq);
        scoreMainLogNew.setOgNo(ogNo);
        scoreMainLogNew.setRgSeq("");
        Calendar calendar = Calendar.getInstance();
        //立即生效
        Date nowDate = DateUtil.getNowDate();
        // 统计用， 加减可用积分不用limitTime
        scoreMainLogNew.setLimitTime(null);
        scoreMainLogNew.setActualTime(nowDate);
        calendar.add(Calendar.YEAR, 1);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, 31);
        //明年12-31失效
        scoreMainLogNew.setEndTime(calendar.getTime());
        scoreMainLogNew.setScoreNumber(scoreGet);
        scoreMainLogNew.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLogNew.setCommentSeq(0);
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogNew);
        Integer smlSeq = scoreMainLogNew.getSmlSeq();
        scoreCommonDao.addSelfAvailableScore(memGuid, nowDate, scoreGet, scoreMainLogNew.getSmlSeq());
        return smlSeq;
    }


    /**
     * 按照日期对积分收支进行统计
     * 默认，查询前31条记录
     */
    @Override
    @ScoreSlaveDataSource
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result loadScoreSum(String data) {
        Map<String, Object> mapParam = new HashMap<>();
        JSONObject jsonObj = JSONObject.parseObject(data);
        if (jsonObj != null) {
            String startTime = jsonObj.getString("startTime");
            String endTime = jsonObj.getString("endTime");
            Integer webSite = jsonObj.getInteger("webSite");
            Integer pageNo = jsonObj.getInteger("pageNo");
            Integer pageSize = jsonObj.getInteger("pageSize");
            if (StringUtils.isEmpty(startTime)) {
                startTime = null;
            }
            if (StringUtils.isEmpty(endTime)) {
                endTime = null;
            }

            if (webSite == null) {
                webSite = Constant.LOAD_SCORE_DEFAULT_WEBSIT;
            }
            if (pageNo == null) {
                pageNo = Constant.LOAD_SCORE_DEFAULT_PAGENO;
            }
            if (pageSize == null) {
                pageSize = Constant.LOAD_SCORE_DEFAULT_PAGESIZE;
            }
            int start = Math.max(pageSize * (pageNo - 1), 0);

            mapParam.put("startTime", startTime);
            mapParam.put("endTime", endTime);
            mapParam.put("webSite", webSite);
            mapParam.put("start", start);
            mapParam.put("size", pageSize);
        } else {
            mapParam.put("start", Constant.LOAD_SCORE_DEFAULT_PAGENO);
            mapParam.put("size", Constant.LOAD_SCORE_DEFAULT_PAGESIZE);
            mapParam.put("webSite", Constant.LOAD_SCORE_DEFAULT_WEBSIT);
        }
        List<Map<String, Object>> dataList = scoreDefalutTableDao.loadScoreSum(mapParam);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, dataList, "success");
    }

    /**
     * 依据商品出货号获取商品 赚取/消费 积分
     */
    @Override
    public Result loadOlScore(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        JSONArray infos = jsonObj.getJSONArray("infos");
        if (infos == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "infos 不能为空");
        }

        Map<Integer, List<Map<String, Object>>> typeMap = new HashMap<>();
        for (int i = 0; i < infos.size(); i++) {
            JSONObject infoObj = infos.getJSONObject(i);
            String ogNo = infoObj.getString("ogNo");
            if (StringUtils.isEmpty(ogNo)) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogNo 不能为空");
            }
            String olSeq = infoObj.getString("olSeq");
            if (StringUtils.isEmpty(olSeq)) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "olSeq 不能为空");
            }
            Integer type = infoObj.getInteger("type");
            if (type == null) {
                //默认是赚取积分
                type = Constant.LOADOL_SCORE_TYPE_GET;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("ogNo", ogNo);
            map.put("olSeq", olSeq);
            map.put("type", type);
            List<Map<String, Object>> listMap = typeMap.get(type);
            if (listMap == null) {
                listMap = new ArrayList<>();
                listMap.add(map);
                typeMap.put(type, listMap);
            } else {
                listMap.add(map);
            }
        }

        List<Map<String, Object>> dataList = scoreLoadOlService.loadOlScoreByType(memGuid, typeMap);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, dataList, "success");
    }


    /**
     * ERP积分流水查询
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getUserScoreLogDetailList(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        String ogNo = jsonObj.getString("ogNo");
        if (StringUtils.isEmpty(ogNo)) {
            ogNo = null;
        }
        String startTime = jsonObj.getString("startTime");
        if (StringUtils.isEmpty(startTime)) {
            startTime = null;
        }
        String endTime = jsonObj.getString("endTime");
        if (StringUtils.isEmpty(endTime)) {
            endTime = null;
        } else {
            endTime += " 23:59:59";
        }
        Integer pageNo = jsonObj.getInteger("pageNo");
        if (pageNo == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pageNo 不能为空");
        }
        Integer pageSize = jsonObj.getInteger("pageSize");
        if (pageSize == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "pageSize 不能为空");
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("ogNo", ogNo);
        paramMap.put("startTime", startTime);
        paramMap.put("endTime", endTime);
        Integer start = Math.max(pageSize * (pageNo - 1), 0);
        paramMap.put("start", start);
        paramMap.put("pageSize", pageSize);
        //查询详细信息
        List<Map<String, Object>> userScoreList = scoreMainLogDao.getUserScoreLogDetailList(paramMap, memGuid);
        if (userScoreList == null) {
            userScoreList = new ArrayList<>();
        }
        //用户信息 默认为空
        String email = "";
        String phoneNo = "";
        String userName = "";
        //评论信息
        Map<Long, Object> commentDetails = new HashMap<>();
        //如果查到信息
        if (userScoreList.size() > 0) {
            //查询用户信息
            Map<String, Object> memberDetail = searchMemberDao.getMemberInfo(memGuid);
            email = (String) memberDetail.get("email");
            phoneNo = (String) memberDetail.get("phoneNo");
            userName = (String) memberDetail.get("userName");
            //批量查询评论信息
            commentDetails = batchQueryComments(userScoreList);
        }
        for (Map<String, Object> map : userScoreList) {
            Integer channel = (Integer) map.get("channel");
            map.put("goodsName", "");
            map.put("sourcleUrl", "");
            map.put("smSeq", "");
            map.put("email", email);
            map.put("phoneNo", phoneNo);
            map.put("userName", userName);
            map.put("description", Constant.DESCRIPTION[channel]);
            //评论类型的。
            if (isCommentChannle(channel)) {
                Long commentSeq = (Long) map.get("commentSeq");
                map.put("type", "评论");

                @SuppressWarnings("unchecked")
                Map<Object, Object> commentDetail = (Map<Object, Object>) commentDetails.get(commentSeq);
                if (commentDetail != null) {
                    map.put("goodsName", commentDetail.get("goodsName"));
                    map.put("sourcleUrl", commentDetail.get("sourceUrl"));
                    map.put("smSeq", commentDetail.get("smSeq"));
                }
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_BIND_PHONE)) {
                //绑定手机
                map.put("type", "绑定手机");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_BIND_EMAIL)) {
                //绑定邮箱
                map.put("type", "绑定邮箱");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_APPROVAL)) {
                map.put("type", "客服赠送");
                map.put("description", map.get("remark"));
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_PHONE_SIGN)) {
                map.put("type", "签到");
                map.put("description", "签到赠点");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_RAFFLE_GIVE)) {
                map.put("type", "积分抽奖");
                map.put("description", "抽奖获得积分");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_RAFFLE_COST)) {
                map.put("type", "积分抽奖");
                map.put("description", "抽奖使用积分");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_EXCHANGE_CARD_COST)) {
                map.put("type", "购物");
                map.put("description", "积分换券");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_EXCHANGE_GOODS_COST)) {
                map.put("type", "积分兑换");
                map.put("description", "兑换单号：" + map.get("remark"));
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_EXCHANGE_VOUCHER_COST)) {
                map.put("type", "积分兑换");
                map.put("description", "积分兑换优惠券");
            } else if (Objects.equals(channel, Constant.SCORE_CHANNEL_FILL_IN_INTEREST)) {
                map.put("type", "其他");
                map.put("description", "完善兴趣爱好");
            } else {
                map.put("type", "购物");
            }
            Date limitTime = (Date) map.get("limitTime");

            if (ScoreStatus.getStatus(channel, limitTime) != 0) {
                map.put("scoreEffect", map.get("scoreNumber"));
                map.put("scoreNotEffect", 0);
            } else {
                map.put("scoreNotEffect", map.get("scoreNumber"));
                map.put("scoreEffect", 0);
            }
            map.remove("scoreNumber");
            map.remove("remark");

        }

        //总数
        Integer totalNum = scoreMainLogDao.getUserScoreLogDetailListCount(paramMap, memGuid);

        if (totalNum == null) {
            totalNum = 0;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("userScoreList", userScoreList);
        result.put("totalNum", totalNum);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, result, "success");
    }

    /**
     * 评论或订单的详细信息
     */
    @Override
    public Result getDetail(String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        Long commentSeq = jsonObj.getLong("commentSeq");
        if (commentSeq == null || commentSeq == 0) {
            String memGuid = jsonObj.getString("memGuid");
            if (StringUtils.isEmpty(memGuid)) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "commentSeq为空，则memGuid 不能为空");
            }
            String ogSeq = jsonObj.getString("ogSeq");
            if (StringUtils.isEmpty(ogSeq)) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "commentSeq为空，则ogSeq 不能为空");
            }
            String cacheKey = memGuid + "_" + ogSeq + ConstantCache.SCORE_DETIAL_OGSEQ;
            String orderDetailStr = cacheUtils.getCacheData(cacheKey);
            if (StringUtils.isNotBlank(orderDetailStr)) {
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, JSONArray.parseArray(orderDetailStr), "success");
            } else {
                List<Map<String, Object>> orderDetail = scoreGetOrderDetailDao.getOrderDetailByOgSeq(memGuid, ogSeq);
                cacheUtils.putCache(cacheKey, CacheUtils.TEN_DAY, JSONArray.toJSONString(orderDetail));
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, orderDetail, "success");
            }
        } else {
            String cacheKey = commentSeq + ConstantCache.SCORE_DETIAL_COMMENTSEQ;
            String commentDetailStr = cacheUtils.getCacheData(cacheKey);
            if (StringUtils.isNotBlank(commentDetailStr)) {
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, JSONObject.parseObject(commentDetailStr), "success");
            } else {
                Map<String, Object> commentDetail = scoreGetCommmentDetailDao.getCommentDetail(commentSeq);
                cacheUtils.putCache(cacheKey, CacheUtils.TEN_DAY, JSONObject.toJSONString(commentDetail));
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, commentDetail, "success");
            }
        }
    }

    /**
     * 批量查询评论信息
     */
    private Map<Long, Object> batchQueryComments(List<Map<String, Object>> userScoreList) {
        Map<Long, Object> commentDetails = new HashMap<>();
        Set<Long> comments = new HashSet<>();
        for (Map<String, Object> map : userScoreList) {
            Integer channel = (Integer) map.get("channel");
            //评论类型的。
            if (isCommentChannle(channel)) {
                //取得评论id
                Long commentSeq = (Long) map.get("commentSeq");
                comments.add(commentSeq);
            }
        }
        if (comments.size() > 0) {
            StringBuilder sbComment = new StringBuilder();
            for (Long commentId : comments) {
                sbComment.append(commentId);
                sbComment.append(",");
            }
            sbComment.deleteCharAt(sbComment.length() - 1);
            commentDetails = scoreGetCommmentDetailDao.getCommentDetails(sbComment.toString());
        }
        return commentDetails;
    }

    /**
     * 判断是否是评论类型
     */
    private boolean isCommentChannle(Integer channel) {
        return Objects.equals(channel, Constant.SCORE_CHANNEL_COMMENT_PRODUCT)
                || Objects.equals(channel, Constant.SCORE_CHANNEL__COMMENT_SET_ESSENCE)
                || Objects.equals(channel, Constant.SCORE_CHANNEL__COMMENT_SET_TOP)
                || Objects.equals(channel, Constant.SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_COMMENT)
                || Objects.equals(channel, Constant.SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_ESSENCE)
                || Objects.equals(channel, Constant.SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_TOP);
    }


    /**
     * 查询用户是否已经绑定邮箱或手机送积分了
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result haveBindPhoneAndEmail(String memGuid) {
        Map<String, Object> map = new HashMap<>();
        String emailKey = memGuid + "_bind_email";
        String bindEmail = cacheUtils.getCacheData(emailKey);
        if (StringUtils.isBlank(bindEmail)) {
            Integer channel = Constant.SCORE_CHANNEL_BIND_EMAIL;
            Integer count = scoreMainLogDao.getScoreMainLogCountByChannel(memGuid, channel);
            if (count == null || count == 0) {
                map.put("bindEmail", 0);
            } else {
                map.put("bindEmail", 1);
                cacheUtils.putCache(emailKey, CacheUtils.TEN_DAY, 1);
            }

        } else {
            map.put("bindEmail", 1);
        }
        String phoneKey = memGuid + "_bind_phone";
        String bindPhone = cacheUtils.getCacheData(phoneKey);
        if (StringUtils.isBlank(bindPhone)) {
            Integer channel = Constant.SCORE_CHANNEL_BIND_PHONE;
            Integer count = scoreMainLogDao.getScoreMainLogCountByChannel(memGuid, channel);
            if (count == null || count == 0) {
                map.put("bindPhone", 0);
            } else {
                map.put("bindPhone", 1);
                cacheUtils.putCache(phoneKey, CacheUtils.TEN_DAY, 1);
            }
        } else {
            map.put("bindPhone", 1);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, map, "success");
    }

    /**
     * 依据olSeq查询消费积分，获得积分的信息
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result getScoreDetailByOlSeqs(String memGuid, String data) {

        JSONObject jsonObj = JSONObject.parseObject(data);
        JSONArray olSeqs = jsonObj.getJSONArray("olSeqs");

        String cacheKey = ConstantCache.GET_SCORE_DETAIL_OLSEQS + "_" + MD5Util.getMD5Code(olSeqs.toJSONString()) + "_" + memGuid;
        String resultStr = cacheUtils.getCacheData(cacheKey);
        if (StringUtils.isNotBlank(resultStr)) {
            Map result = JSONObject.parseObject(resultStr, Map.class);
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, result, "success");
        } else {
            List<ScoreOrderDetail> lists = scoreOrderDetailDao.getScoreDetailByOlSeqs(memGuid, olSeqs);
            //统计积分信息
            Map<String, ScoreOrderDetail> resultMap = new HashMap<>();
            for (ScoreOrderDetail scoreOrderDetail : lists) {
                Integer type = scoreOrderDetail.getType();
                String olSeq = scoreOrderDetail.getOlSeq();
                ScoreOrderDetail sod = resultMap.get(olSeq);
                if (sod == null) {
                    resultMap.put(olSeq, scoreOrderDetail);
                    continue;
                }
                if (Objects.equals(type, Constant.SCORE_ORDER_DETAIL_TYPE_BUY)) {
                    sod.setScoreGet(scoreOrderDetail.getScoreGet());
                } else if (Objects.equals(type, Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CONSUME)) {
                    sod.setScoreConsume(scoreOrderDetail.getScoreConsume());
                }
                resultMap.put(olSeq, sod);
            }
            Map<String, Map<String, Object>> result = new HashMap<>();
            for (String key : resultMap.keySet()) {
                Map<String, Object> map = new HashMap<>();
                ScoreOrderDetail scoreOrderDetail = resultMap.get(key);
                map.put("scoreConsume", scoreOrderDetail.getScoreConsume());
                map.put("scoreGet", scoreOrderDetail.getScoreGet());
                map.put("money", scoreOrderDetail.getScoreConsume() * 0.01);
                result.put(key, map);
            }
            if (!result.isEmpty()) {
                cacheUtils.putCache(cacheKey, CacheUtils.ONE_DAY, JSONObject.toJSONString(result));
            }
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, result, "success");
        }
    }

    /**
     * 取消商城（应退积分）
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result mallScoreForCancel(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        String ogsSeq = jsonObj.getString("ogsSeq");
        List<ScoreOrderDetail> listConsume = scoreOrderDetailDao.getScoreOrderDetailBuyListByOgsSeq(memGuid, ogsSeq, Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CONSUME);
        JSONArray olsList = jsonObj.getJSONArray("olsList");
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < olsList.size(); i++) {
            JSONObject returnInfo = olsList.getJSONObject(i);
            String olsSeq = returnInfo.getString("olsSeq");
            Integer qty = returnInfo.getInteger("qty");
            JSONArray returnQty = returnInfo.getJSONArray("returnQty");
            ScoreOrderDetail consumeDetail = getMallConsumeScoreOrderDetail(listConsume, olsSeq);
            if (consumeDetail == null) {
                throw new ScoreException("未找到消费积分信息");
            }
            int scoreGet;
            if (returnQty == null) {
                //不是退最后一个商品
                scoreGet = qty * consumeDetail.getScoreConsume() / consumeDetail.getQuantity();
            } else {
                //退最后一个商品
                //历史应退积分
                int shouldScoreGet = 0;
                for (int returnQtyIndex = 0; returnQtyIndex < returnQty.size(); returnQtyIndex++) {
                    int intValue = returnQty.getIntValue(returnQtyIndex);
                    shouldScoreGet += intValue * consumeDetail.getScoreConsume() / consumeDetail.getQuantity();

                }
                scoreGet = consumeDetail.getScoreConsume() - shouldScoreGet;

            }

            Map<String, Object> map = new HashMap<>();
            map.put("ogsSeq", ogsSeq);
            map.put("olsSeq", olsSeq);
            map.put("score", scoreGet);
            dataList.add(map);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, dataList, "success");
    }

    private ScoreOrderDetail getMallConsumeScoreOrderDetail(List<ScoreOrderDetail> listConsume, String olsSeq) {
        ScoreOrderDetail consumeDetail = null;
        for (ScoreOrderDetail scoreOrderDetail : listConsume) {
            if (StringUtils.equals(scoreOrderDetail.getOlSeq(), olsSeq)) {
                if (consumeDetail == null) {
                    consumeDetail = scoreOrderDetail;
                } else {
                    Integer scoreConsume = consumeDetail.getScoreConsume();
                    consumeDetail.setScoreConsume(scoreConsume + scoreOrderDetail.getScoreConsume());
                }
            }
        }
        return consumeDetail;
    }


    /**
     * 退货确认（实退积分）
     */
    @Override
    public Result mallScoreForRefund(String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        String ogsSeq = jsonObj.getString("ogsSeq");
        String olsSeq = jsonObj.getString("olsSeq");
        String rssSeq = jsonObj.getString("rssSeq");
        BigDecimal realPrice = jsonObj.getBigDecimal("realPrice");
        BigDecimal refundablePrice = jsonObj.getBigDecimal("refundablePrice");
        //应退
        int scoreGet = jsonObj.getIntValue("refundableScore");
        //折扣计算
        if (BigDecimal.ZERO.compareTo(refundablePrice) < 0) {
            scoreGet = new BigDecimal(scoreGet).multiply(realPrice).divide(refundablePrice, BigDecimal.ROUND_HALF_UP).intValue();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("ogsSeq", ogsSeq);
        result.put("rssSeq", rssSeq);
        result.put("olsSeq", olsSeq);
        result.put("score", scoreGet);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, result, "success");
    }


    /**
     * 抽奖使用、抽奖获得
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result changeScoreImmediatelyWithChannel(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer scoreValue = jsonObj.getInteger("scoreValue");
        String type = jsonObj.getString("type");
        String channel = jsonObj.getString("channel");
        String exCardNo = jsonObj.getString("exCardNo");
        Integer smlSeq = jsonObj.getInteger("smlSeq");
        if (scoreValue == null && !StringUtils.equals(type, Constant.TYPE_OF_SCORE_ROOLBACK)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "scoreValue不能为空");
        }
        if (type == null || (!StringUtils.equals(type, Constant.TYPE_OF_SCORE_GIVE) && !StringUtils.equals(type, Constant.TYPE_OF_SCORE_COST) && !StringUtils.equals(type, Constant.TYPE_OF_SCORE_ROOLBACK))) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "type参数错误");
        }
        if (channel == null || (!StringUtils.equals(channel, Constant.CHANNEL_OF_RAFFLE) && !StringUtils.equals(channel, Constant.CHANNEL_OF_EXCHANGE_VOUCHER))) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "channel参数错误");
        }
        if (StringUtils.equals(channel, Constant.CHANNEL_OF_RAFFLE)) {
            if (StringUtils.equals(type, Constant.TYPE_OF_SCORE_GIVE)) {
                Integer returnSmlSeq = addScoreEffectiveImmediately(memGuid, "抽奖获得积分", Constant.SCORE_CHANNEL_RAFFLE_GIVE, scoreValue, "", "");
                JSONObject returnData = new JSONObject();
                returnData.put("smlSeq", returnSmlSeq);
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "抽奖获得积分成功");
            }
            if (StringUtils.equals(type, Constant.TYPE_OF_SCORE_COST)) {
                Integer returnSmlSeq = scoreCommonDao.deductScoreImmediately(memGuid, "抽奖使用积分", Constant.SCORE_CHANNEL_RAFFLE_COST, scoreValue, "", "", "");
                JSONObject returnData = new JSONObject();
                returnData.put("smlSeq", returnSmlSeq);
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "抽奖使用积分成功");
            }
            if (StringUtils.equals(type, Constant.TYPE_OF_SCORE_ROOLBACK)) {
                if (smlSeq == null) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "smlSeq不能为空");
                }
                Integer roolbackScore = scoreCommonDao.rollbackExChangeVoucherScore(memGuid, smlSeq);
                JSONObject returnData = new JSONObject();
                if (roolbackScore != null) {
                    returnData.put("roolBackScore", Math.abs(roolbackScore));
                }
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "回滚抽奖积分成功");
            }
        }
        if (StringUtils.equals(channel, Constant.CHANNEL_OF_EXCHANGE_VOUCHER)) {
            if (StringUtils.equals(type, Constant.TYPE_OF_SCORE_GIVE)) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "type参数错误");
            }
            if (StringUtils.equals(type, Constant.TYPE_OF_SCORE_COST)) {
                if (exCardNo == null) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "exCardNo不能为空");
                }
                Integer returnSmlSeq = scoreCommonDao.deductScoreImmediately(memGuid, exCardNo, Constant.SCORE_CHANNEL_EXCHANGE_VOUCHER_COST, scoreValue, "", "", "");
                JSONObject returnData = new JSONObject();
                returnData.put("smlSeq", returnSmlSeq);
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "兑换抵用券使用积分成功");
            }
            if (StringUtils.equals(type, Constant.TYPE_OF_SCORE_ROOLBACK)) {
                if (smlSeq == null) {
                    throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "smlSeq不能为空");
                }
                Integer roolbackScore = scoreCommonDao.rollbackExChangeVoucherScore(memGuid, smlSeq);
                JSONObject returnData = new JSONObject();
                if (roolbackScore != null) {
                    returnData.put("roolBackScore", Math.abs(roolbackScore));
                }
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "回滚兑换抵用券积分成功");
            }
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, null, "success");
    }

    /**
     * 兑换券
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result changeScoreImmediatelyByExchangeCard(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer consumeScore = jsonObj.getInteger("consumeScore");   //负数
        if (consumeScore == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "consumeScore不能为空");
        }
        String exCardNo = jsonObj.getString("exCardNo");
        if (StringUtils.isBlank(exCardNo)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "exCardNo不能为空");
        }

        scoreCommonDao.deductScoreImmediately(memGuid, exCardNo, Constant.SCORE_CHANNEL_EXCHANGE_CARD_COST, consumeScore, "", "", "");

        return new Result(ResultCode.RESULT_STATUS_SUCCESS, null, "success");
    }

    /**
     * 按卖场号查询卖场所有商品可获得的总积分
     */
    @Override
    public Result getScoreListBySmSeqList(String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        String skuSeqsStr = jsonObj.getString("skuSeqs");
        String areaCodeStr = jsonObj.getString("areaCode");
        Integer isFast = jsonObj.getInteger("isFast");
        if (isFast == null) {
            isFast = 0;
        }
        String whSeq = jsonObj.getString("whSeq");
        String[] skuSeqs = skuSeqsStr.split(",");
        Set<String> skuSeqSet = new HashSet<>();
        Collections.addAll(skuSeqSet, skuSeqs);
        //商详页调用时，只有组合卖场会传多个入参，且参数不多。大于15的可以认为是购物车调用，不读写缓存
        if (skuSeqSet.size() < 16) {
            String returnStr = cacheUtils.getCacheData(data + ConstantCache.SMSEQ_SCORE_KEY);
            if (StringUtils.isNotBlank(returnStr)) {
                return new Result(ResultCode.RESULT_STATUS_SUCCESS, JSONObject.parseObject(returnStr), "success");
            }
        }
        JSONObject returnData = scoreGetTotalScoreBySmSeqDao.getScoreListBySmSeqList(skuSeqSet, areaCodeStr, isFast, whSeq);
        if (returnData != null && !returnData.isEmpty() && returnData.getBoolean("allSuccessFlag") && skuSeqSet.size() < 16) {
            cacheUtils.putCache(data + ConstantCache.SMSEQ_SCORE_KEY, 300, returnData.toJSONString());
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, returnData, "success");
    }

    @Override
    @ScoreSlaveDataSource
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getScoreGrantDetail(String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "data 不能为空");
        }
        JSONObject jsonObj = JSONObject.parseObject(data);
        Date startTime = jsonObj.getDate("startTime");
        if (startTime == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "startTime 不能为空");
        }
        Date endTime = jsonObj.getDate("endTime");
        if (endTime == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "endTime 不能为空");
        }
        //只支持一天的查询和按月查询
        checkQueryParam(startTime, endTime);

        Integer pageSize = jsonObj.getInteger("RowCount");
        if (pageSize == null) {
            pageSize = 1000;
        }

        Integer pageNo = jsonObj.getInteger("PageIndex");
        if (pageNo == null) {
            pageNo = 1;
        }

        Map<String, Object> paramMap = new HashMap<>();
        Integer start = Math.max(pageSize * (pageNo - 1), 0);
        paramMap.put("start", start);
        paramMap.put("size", pageSize);
        paramMap.put("edate", buildEdate(startTime, endTime));
        paramMap.put("table", buildTable(startTime, endTime));
        List<ScoreGrant> list = scoreDefalutTableDao.getScoreGrantDetail(paramMap);
        Integer count = scoreDefalutTableDao.getScoreGrantDetailCount(paramMap);
        if (list.size() > 0) {
            Set<String> sellerNos = new HashSet<>();
            Set<String> mallNos = new HashSet<>();
            for (ScoreGrant scoreGrant : list) {
                //门店的
                if (StringUtils.isNotEmpty(scoreGrant.getSellerNo())
                        && StringUtils.isNotEmpty(scoreGrant.getStoreNo())) {
                    sellerNos.add(scoreGrant.getSellerNo());
                }
                //商城的
                if (StringUtils.isNotEmpty(scoreGrant.getSellerNo())
                        && StringUtils.isEmpty(scoreGrant.getStoreNo())) {
                    //错误数据处理
                    if (isMall(scoreGrant.getSellerNo())) {
                        mallNos.add(scoreGrant.getSellerNo());
                    } else {
                        scoreGrant.setType("2");
                        sellerNos.add(scoreGrant.getSellerNo());
                    }
                }
            }
            Map<String, String> storeNames = scoreGetStoreInfoDao.getStoreNameBySellerNos(sellerNos);
            Map<String, String> mallNames = scoreGetStoreInfoDao.getMallNameBySellerNos(mallNos);
            for (ScoreGrant scoreGrant : list) {
                if (StringUtils.isNotEmpty(scoreGrant.getSellerNo())
                        && StringUtils.isNotEmpty(scoreGrant.getStoreNo())) {
                    String sellerName = storeNames.get(scoreGrant.getSellerNo());
                    if (sellerName != null) {
                        scoreGrant.setSellerName(sellerName);
                    }
                }
                if (StringUtils.isNotEmpty(scoreGrant.getSellerNo())
                        && StringUtils.isEmpty(scoreGrant.getStoreNo())) {
                    if (isMall(scoreGrant.getSellerNo())) {
                        String mallName = mallNames.get(scoreGrant.getSellerNo());
                        if (mallName != null) {
                            scoreGrant.setSellerName(mallName);
                        }
                    } else {
                        //错误数据兼容
                        String sellerName = storeNames.get(scoreGrant.getSellerNo());
                        if (sellerName != null) {
                            scoreGrant.setSellerName(sellerName);
                        }
                    }
                }

                if (StringUtils.isEmpty(scoreGrant.getSellerNo())
                        && StringUtils.isEmpty(scoreGrant.getStoreNo())) {
                    scoreGrant.setSellerName("飞牛网");
                }
            }
        }
        Map<String, Object> info = new HashMap<>();
        info.put("TotalItems", NumberUtils.getIntValue(count, 0));
        info.put("list", list);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, info, "success");
    }

    private boolean isMall(String sellerNo) {
        boolean isMall = true;
        for (char ch : sellerNo.toCharArray()) {
            if (!Character.isDigit(ch)) {
                isMall = false;
            }
        }
        return isMall;
    }

    @Override
    @ScoreSlaveDataSource
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getScoreUseDetail(String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "data 不能为空");
        }
        JSONObject jsonObj = JSONObject.parseObject(data);
        Date startTime = jsonObj.getDate("startTime");
        if (startTime == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "startTime 不能为空");
        }
        Date endTime = jsonObj.getDate("endTime");
        if (endTime == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "endTime 不能为空");
        }
        //只支持一天的查询和按月查询
        checkQueryParam(startTime, endTime);

        Integer pageSize = jsonObj.getInteger("RowCount");
        if (pageSize == null) {
            pageSize = 1000;
        }

        Integer pageNo = jsonObj.getInteger("PageIndex");
        if (pageNo == null) {
            pageNo = 1;
        }

        Map<String, Object> paramMap = new HashMap<>();
        Integer start = Math.max(pageSize * (pageNo - 1), 0);
        paramMap.put("start", start);
        paramMap.put("size", pageSize);
        paramMap.put("edate", buildEdate(startTime, endTime));
        paramMap.put("table", buildTable(startTime, endTime));
        List<ScoreUse> list = scoreDefalutTableDao.getScoreUseDetail(paramMap);
        Integer count = scoreDefalutTableDao.getScoreUseDetailCount(paramMap);
        if (list.size() > 0) {
            Set<String> sellerNos = new HashSet<>();
            Set<String> mallNos = new HashSet<>();
            for (ScoreUse scoreUse : list) {
                //门店的
                if (StringUtils.isNotEmpty(scoreUse.getSellerNo())
                        && StringUtils.isNotEmpty(scoreUse.getStoreNo())) {
                    sellerNos.add(scoreUse.getSellerNo());
                }
                //商城的
                if (StringUtils.isNotEmpty(scoreUse.getSellerNo())
                        && StringUtils.isEmpty(scoreUse.getStoreNo())) {
                    //错误数据处理
                    if (isMall(scoreUse.getSellerNo())) {
                        mallNos.add(scoreUse.getSellerNo());
                    } else {
                        scoreUse.setType("2");
                        sellerNos.add(scoreUse.getSellerNo());
                    }
                }
            }
            Map<String, String> storeNames = scoreGetStoreInfoDao.getStoreNameBySellerNos(sellerNos);
            Map<String, String> mallNames = scoreGetStoreInfoDao.getMallNameBySellerNos(mallNos);
            for (ScoreUse scoreUse : list) {
                if (StringUtils.isNotEmpty(scoreUse.getSellerNo())
                        && StringUtils.isNotEmpty(scoreUse.getStoreNo())) {
                    String sellerName = storeNames.get(scoreUse.getSellerNo());
                    scoreUse.setSellerName(sellerName);
                }
                if (StringUtils.isNotEmpty(scoreUse.getSellerNo())
                        && StringUtils.isEmpty(scoreUse.getStoreNo())) {
                    if (isMall(scoreUse.getSellerNo())) {
                        String mallName = mallNames.get(scoreUse.getSellerNo());
                        if (mallName != null) {
                            scoreUse.setSellerName(mallName);
                        }
                    } else {
                        String sellerName = storeNames.get(scoreUse.getSellerNo());
                        scoreUse.setSellerName(sellerName);
                    }
                }
                if (StringUtils.isEmpty(scoreUse.getSellerNo())
                        && StringUtils.isEmpty(scoreUse.getStoreNo())) {
                    scoreUse.setSellerName("飞牛网");
                }
            }
        }
        Map<String, Object> info = new HashMap<>();
        info.put("TotalItems", NumberUtils.getIntValue(count, 0));
        info.put("list", list);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, info, "success");
    }

    private void checkQueryParam(Date startTime, Date endTime) {
        if (startTime.compareTo(endTime) != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int nowDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (lastDay != nowDay) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "入参只能为某一天或一个月的区间");
            }
            int endMonth = calendar.get(Calendar.MONTH);

            calendar.setTime(startTime);
            nowDay = calendar.get(Calendar.DAY_OF_MONTH);
            int startMonth = calendar.get(Calendar.MONTH);
            if (1 != nowDay || endMonth != startMonth) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "入参只能为某一天或一个月的区间");
            }
        }
    }

    private String buildEdate(Date startTime, Date endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (startTime.compareTo(endTime) == 0) {
            return dateFormat.format(startTime);
        } else {
            return dateFormat.format(endTime);
        }
    }


    private int buildTable(Date startTime, Date endTime) {
        if (startTime.compareTo(endTime) == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * 完善兴趣爱好
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result saveScoreByFillInInterest(String memGuid, String data) {
        Integer channel = Constant.SCORE_CHANNEL_FILL_IN_INTEREST;
        Integer count = scoreMainLogDao.getScoreMainLogCountByChannel(memGuid, channel);
        if (count != null && count > 0) {
            throw new ScoreException(ResultCode.RESULT_REPEAT_SUBMIT, "已经填写兴趣爱好获得积分，不能重复提交。");
        }
        Integer scoreGet = Constant.INTEREST_SCORE;
        addScoreEffectiveImmediately(memGuid, "完善兴趣爱好", channel, scoreGet, "", "");

        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    @Override
    public Result getScoreByOgsSeq(String data) {
        JSONArray dataArray = JSONObject.parseArray(data);
        List<Map<String, Object>> list = new ArrayList<>(dataArray.size());
        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            String memGuid = dataObj.getString("memGuid");
            if (StringUtils.isEmpty(memGuid)) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空。");
            }
            String ogsSeq = dataObj.getString("ogsSeq");
            if (StringUtils.isEmpty(ogsSeq)) {
                throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogsSeq 不能为空。");
            }
            String key = memGuid + ":" + ogsSeq;
            String cacheData = cacheUtils.getCacheData(key);
            if (StringUtils.isEmpty(cacheData)) {
                Map<String, Object> scoreByOgsSeq = scoreOrderDetailDao.getScoreByOgsSeq(memGuid, ogsSeq);
                if (scoreByOgsSeq != null) {
                    list.add(scoreByOgsSeq);
                    cacheUtils.putCache(key, CacheUtils.ONE_DAY, JSONObject.toJSONString(scoreByOgsSeq));
                }
            } else {
                list.add(JSONObject.parseObject(cacheData));
            }
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, list, "success");
    }


    //查询最后一次连续签到（包括签一次）的信息
    //查询时包括今天
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getLastCurSignInfo(String memGuid, String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        JSONObject jsonObj = JSONObject.parseObject(data);
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        String LastCurSignInfoStr = cacheUtils.getCacheData(memGuid + ConstantCache.LAST_CUR_SIGN_INFO);
        if (StringUtils.isNotBlank(LastCurSignInfoStr)) {
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, JSONObject.parseObject(LastCurSignInfoStr), "success");
        } else {
            Integer channel = Constant.SCORE_CHANNEL_PHONE_SIGN;

            //不传参数默认查询今天及前七天的签到信息
            Integer durDaysForSel = 7;
            if (jsonObj.getInteger("durDaysForSel") != null) {
                durDaysForSel = jsonObj.getInteger("durDaysForSel");
            }
            Map<String, Object> resultMap = this.getLastCurSignInfo(memGuid, channel, durDaysForSel);
            cacheUtils.putCache(memGuid + ConstantCache.LAST_CUR_SIGN_INFO, DateUtil.getSecondsUntilTomorrowZero().intValue(), JSONObject.toJSONString(resultMap));
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, resultMap, "success");
        }
    }

    private Map<String, Object> getLastCurSignInfo(String memGuid, Integer channel, Integer durDaysForSel) {
        Map<String, Object> paramMap = new HashMap<>();
        //查到当天及之前（durDaysForSel）天的连续签到记录
        paramMap.put("channel", channel);
        SignInfo LastSignInfo = scoreMainLogDao.getLastSignInfo(memGuid, paramMap);

        Date yestoday = DateUtil.getTimeOfZeroDiffToday(-1);
        Map<String, Object> resultMap = new HashMap<>();
        //最近一次签到的日期早于昨天，则连续签到次数为0
        if (LastSignInfo == null || LastSignInfo.getDurEndDate() == null || (LastSignInfo.getDurEndDate() != null && LastSignInfo.getDurEndDate().before(yestoday))) {
            resultMap.put("enduranceDays", 0);
            resultMap.put("durDaysAfterClean", 0);
            resultMap.put("durBeginDate", null);
            resultMap.put("durEndDate", null);
        } else if (LastSignInfo.getEnduranceDays() >= durDaysForSel) {
            resultMap.put("enduranceDays", LastSignInfo.getEnduranceDays() % durDaysForSel);
            if (LastSignInfo.getEnduranceDays() % durDaysForSel == 0) {
                //昨天签到后刚好满（durDaysForSel）日整数倍的，今天连续签到日清为0
                if (LastSignInfo.getDurEndDate().before(DateUtil.getTimeOfZeroDiffToday(0))) {
                    resultMap.put("enduranceDays", LastSignInfo.getEnduranceDays());
                    resultMap.put("durDaysAfterClean", LastSignInfo.getEnduranceDays() % durDaysForSel);
                    resultMap.put("durBeginDate", null);
                    resultMap.put("durEndDate", null);
                } else {
                    //今天签到后刚好满（durDaysForSel）日整数倍的，今天连续签到为durDaysForSel天
                    resultMap.put("enduranceDays", LastSignInfo.getEnduranceDays());
                    resultMap.put("durDaysAfterClean", durDaysForSel);
                    resultMap.put("durBeginDate", DateUtil.getFormatDate(LastSignInfo.getDurBeginDate(), "yyyy-MM-dd"));
                    resultMap.put("durEndDate", DateUtil.getFormatDate(LastSignInfo.getDurEndDate(), "yyyy-MM-dd"));
                }
            } else {
                int moveDays = LastSignInfo.getEnduranceDays() - (LastSignInfo.getEnduranceDays() % durDaysForSel);
                resultMap.put("enduranceDays", LastSignInfo.getEnduranceDays());
                resultMap.put("durDaysAfterClean", LastSignInfo.getEnduranceDays() % durDaysForSel);
                resultMap.put("durBeginDate", DateUtil.getFormatDate(DateUtil.getTimeOfZeroDiffDate(LastSignInfo.getDurBeginDate(), moveDays), "yyyy-MM-dd"));
                resultMap.put("durEndDate", DateUtil.getFormatDate(LastSignInfo.getDurEndDate(), "yyyy-MM-dd"));
            }
        } else {
            resultMap.put("enduranceDays", LastSignInfo.getEnduranceDays());
            resultMap.put("durDaysAfterClean", LastSignInfo.getEnduranceDays());
            resultMap.put("durBeginDate", DateUtil.getFormatDate(LastSignInfo.getDurBeginDate(), "yyyy-MM-dd"));
            resultMap.put("durEndDate", DateUtil.getFormatDate(LastSignInfo.getDurEndDate(), "yyyy-MM-dd"));
        }
        return resultMap;
    }

    /*
    查询今天是否有签到获得积分记录并返回获得的积分
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result haveSignReturnScore(String memGuid) {
        String scoretodayStr = cacheUtils.getCacheData(memGuid + ConstantCache.SCORE_SIGN_TODAY);
        if (StringUtils.isNotBlank(scoretodayStr)) {
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, JSONObject.parseObject(scoretodayStr), "success");
        } else {
            Integer channel = Constant.SCORE_CHANNEL_PHONE_SIGN;
            //查询是否有今天签到获得积分记录并返回
            Integer scoreNumber = scoreMainLogDao.getTodayScoreBySign(memGuid, channel);

            Map<String, Object> resultMap = new HashMap<>();
            if (scoreNumber == null) {
                resultMap.put("flag", false);
                resultMap.put("getScore", null);
            } else {
                resultMap.put("flag", true);
                resultMap.put("getScore", scoreNumber);
            }
            cacheUtils.putCache(memGuid + ConstantCache.SCORE_SIGN_TODAY, DateUtil.getSecondsUntilTomorrowZero().intValue(), JSONObject.toJSONString(resultMap));
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, resultMap, "success");
        }
    }

    /*
    获得用户当月签到的所有日期（只返回日期，不返回年月）
     */
    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result getSignDateThisMonth(String memGuid) {
        String scoreTodayStr = cacheUtils.getCacheData(memGuid + ConstantCache.SIGN_DATE_THIS_MONTH);
        if (StringUtils.isNotBlank(scoreTodayStr)) {
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, JSONObject.parseObject(scoreTodayStr), "success");
        } else {
            Integer channel = Constant.SCORE_CHANNEL_PHONE_SIGN;

            List<String> signDatesThisMonth = scoreMainLogDao.getSignDateThisMonth(memGuid, channel);

            Map<String, Object> resultMap = new HashMap<>();
            if (signDatesThisMonth == null || signDatesThisMonth.size() == 0) {
                resultMap.put("signDates", null);
            } else {
                resultMap.put("signDates", signDatesThisMonth);
            }
            cacheUtils.putCache(memGuid + ConstantCache.SIGN_DATE_THIS_MONTH, DateUtil.getSecondsUntilTomorrowZero().intValue(), JSONObject.toJSONString(resultMap));
            return new Result(ResultCode.RESULT_STATUS_SUCCESS, resultMap, "success");
        }
    }

    @Override
    public boolean dbHealthCheck() {
        try {
            scoreDefalutTableDao.getNow();
            return true;
        } catch (Exception e) {
            log.error("数据库连接错误", "dbHealthCheck", e);
            return false;
        }
    }

    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void signGetScoreForQueue(String memGuid, String from, int scoreGet) {
        try {
            addScoreEffectiveImmediately(memGuid, "签到:" + from, Constant.SCORE_CHANNEL_PHONE_SIGN, scoreGet, "", "");
        } catch (DuplicateKeyException e) {
            log.error("requestNo" + HttpRequestUtils.getRequestNo() + ",重复插入，memGuid=" + memGuid + ",from=" + from + ",scoreGet" + scoreGet, "signGetScoreForQueue");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("flag", true);
        resultMap.put("getScore", scoreGet);
        cacheUtils.putCache(memGuid + ConstantCache.SCORE_SIGN_TODAY, DateUtil.getSecondsUntilTomorrowZero().intValue(), JSONObject.toJSONString(resultMap));
        cacheUtils.removeCacheData(memGuid + ConstantCache.LAST_CUR_SIGN_INFO);
        cacheUtils.removeCacheData(memGuid + ConstantCache.SIGN_DATE_THIS_MONTH);
    }


    @Override
    public Result kafkaMessage(String dataJson) {
        JSONObject jsonObject = JSONObject.parseObject(dataJson);
        //订单流水号
        String ogSeq = jsonObject.getString("ogSeq");
        if (StringUtils.isEmpty(ogSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "ogSeq 不能为空");
        }
        String memGuid = jsonObject.getString("memGuid");
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        Integer consumeScore = jsonObject.getInteger("consumeScore");
        if (consumeScore == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "consumeScore 不能为空");
        }

        //消息补偿
        //发送消息进行订单详细信息入库
        Map<String, Object> data = new HashMap<>();
        data.put("memGuid", memGuid);
        data.put("ogSeq", ogSeq);
        data.put("consumeScore", consumeScore);
        Map<String, Object> info = new HashMap<>();
        info.put("type", Constant.DIRECT_TYPE_SUBMIT_ORDER);
        info.put("data", data);
        String message = JSONObject.toJSONString(info);
        producerClient.sendMessage(scoreTopic, System.currentTimeMillis() + "", message);
        log.info("补偿提交订单message:" + message, "kafkaMessage");
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }


    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void registerGiveZeroScore(String memGuid, String phone) {
        //记录ScoreMainLog日志
        ScoreMainLog scoreMainLog = new ScoreMainLog();
        scoreMainLog.setChannel(Constant.SCORE_CHANNEL_BIND_PHONE);
        scoreMainLog.setRemark(phone + "注册送0积分");
        scoreMainLog.setMemGuid(memGuid);
        scoreMainLog.setRgSeq("");
        scoreMainLog.setOgNo("");
        scoreMainLog.setOgSeq("");
        Calendar calendar = Calendar.getInstance();
        Date nowDate = DateUtil.getNowDate();
        scoreMainLog.setLimitTime(null);
        scoreMainLog.setActualTime(nowDate);
        calendar.add(Calendar.YEAR, 1);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, 31);
        //明年12-31失效
        scoreMainLog.setEndTime(calendar.getTime());
        scoreMainLog.setScoreNumber(0);
        scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLog.setCommentSeq(0);
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
    }

    @Override
    public Result scoreExchangeVoucher(String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        JSONObject dataJson = JSONObject.parseObject(data);
        String memGuid = dataJson.getString("memGuid");
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        String name = dataJson.getString("name");
        if (StringUtils.isEmpty(name)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "name 不能为空");
        }
        Integer score = dataJson.getInteger("score");
        if (score == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "score 不能为空");
        }
        String uniqueKey = dataJson.getString("uniqueKey");
        if (StringUtils.isEmpty(uniqueKey)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "uniqueKey 不能为空");
        }
        scoreCommonDao.deductScoreForVoucher(memGuid, name, score, uniqueKey);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    @Override
    public Result getStoreScoreReportInfo(String data) {
        JSONObject dataJson = JSONObject.parseObject(data);
        String edate = dataJson.getString("edate");
        if (StringUtils.isEmpty(edate)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "edate 不能为空");
        }
        List<StoreReportInfoVo> list = scoreDefalutTableDao.getStoreScoreReportInfo(edate);
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, list, "success");
    }

    @Override
    public Result clearData() {
        String[] tables = {"score_financial_report_use_month", "score_financial_report_use", "score_financial_report_grant", "score_financial_report_grant_month"};
        for (String table : tables) {
            List<String> storeNos = scoreDefalutTableDao.getStoreNo(table);
            if (storeNos.size() > 0) {
                List<String> listFilter = new ArrayList<>();
                for (String storeNo : storeNos) {
                    try {
                        Integer.parseInt(storeNo);
                    } catch (Exception e) {
                        listFilter.add(storeNo);
                    }
                    if (listFilter.size() == 10) {
                        clear(table, listFilter);
                        listFilter.clear();
                    }
                }
                if (listFilter.size() > 0) {
                    clear(table, listFilter);
                    listFilter.clear();
                }
            }
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, "success");
    }

    private void clear(String table, List<String> listFilter) {
        Map<String, Boolean> store = scoreGetStoreInfoDao.isStore(listFilter);
        for (Map.Entry<String, Boolean> entry : store.entrySet()) {
            Boolean value = entry.getValue();
            if (!value) {
                String key = entry.getKey();
                scoreDefalutTableDao.delNoStore(table, key);
            }
        }
    }

    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Result getRecoveryScore(String memGuid, String data) {
        JSONObject json = JSONObject.parseObject(data);
        String rgSeq = json.getString("rgSeq");
        if (StringUtils.isEmpty(rgSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "rgSeq不能为空");
        }
        String skuSeq = json.getString("skuSeq");
        if (StringUtils.isEmpty(skuSeq)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "skuSeq不能为空");
        }
        String key = "grs:" + rgSeq + ":" + skuSeq;
        String cacheData = cacheUtils.getCacheData(key);
        Integer score;
        if (StringUtils.isEmpty(cacheData)) {
            score = scoreOrderDetailDao.getRecoveryScore(memGuid, rgSeq, skuSeq);
            if (score != null) {
                cacheUtils.putCache(key, CacheUtils.ONE_DAY, score);
            }
        } else {
            score = Integer.valueOf(cacheData);
        }
        return new Result(ResultCode.RESULT_STATUS_SUCCESS, score, "success");
    }
}
