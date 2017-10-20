package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.ProducerClient;
import com.feiniu.score.common.*;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.entity.score.*;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.HttpRequestUtils;
import com.feiniu.score.util.ScoreAverageAlgorithm;
import com.feiniu.score.vo.CrmScoreJsonVo;
import com.feiniu.score.vo.OrderJsonVo;
import com.feiniu.score.vo.OrderJsonVo.OrderDetail;
import com.feiniu.score.vo.ReturnJsonVo;
import com.feiniu.score.vo.ReturnJsonVo.ReturnDetail;
import com.feiniu.score.vo.StoreInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;


@Repository
public class ScoreCommonDaoImpl implements ScoreCommonDao {

    public static final CustomLog log = CustomLog.getLogger(ScoreCommonDaoImpl.class);
    @Autowired
    private ScoreMemberDao scoreMemberDao;

    @Autowired
    private ScoreMainLogDao scoreMainLogDao;

    @Autowired
    private ScoreYearDao scoreYearDao;

    @Autowired
    private ScoreYearLogDao scoreYearLogDao;

    @Autowired
    private ScoreOrderDetailDao scoreOrderDetailDao;

    @Autowired
    private ScoreCommentDetailDao scoreCommentDetailDao;

    @Autowired
    private ScoreGetBillDao scoreGetBillDao;
    @Autowired
    private ScoreGetOrderDetail scoreGetOrderDetail;
    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private ScoreCheckOrderStatusDao scoreCheckOrderStatusDao;

    @Autowired
    private ScoreGetStoreInfoDao scoreGetStoreInfoDao;

    @Autowired
    private ProducerClient<Object, String> producerClient;

    @Value("${fn.topic.order.score}")
    private String scoreTopic;

    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void processCrmScore(String memGuid, CrmScoreJsonVo crmScoreJsonVo) {
        FastDateFormat sdf = FastDateFormat.getInstance("yyyyMMdd");
        //生效日期dEfff
        String effTimeStr = crmScoreJsonVo.getdEfff();
        int score = crmScoreJsonVo.getMrdfPoint();
        Integer type = crmScoreJsonVo.getPtadType();
        String ptadId = crmScoreJsonVo.getPtadId();
        String membGradef = crmScoreJsonVo.getMembGradef();
        Date limitTime;
        try {
            limitTime = sdf.parse(effTimeStr);
        } catch (ParseException e) {
            throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "时间转换异常。", e);
        }
        //计算失效日
        Calendar instance = Calendar.getInstance();
        instance.setTime(limitTime);
        instance.add(Calendar.YEAR, 1);
        instance.set(Calendar.MONTH, Calendar.DECEMBER);
        instance.set(Calendar.DATE, 31);
        Date dueTime = instance.getTime();
        ScoreMainLog scoreMainLog = new ScoreMainLog();
        scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLog.setOgSeq("");
        scoreMainLog.setOgNo("");
        scoreMainLog.setRgSeq("");
        scoreMainLog.setRgNo("");
        scoreMainLog.setLimitTime(limitTime);
        scoreMainLog.setEndTime(dueTime);
        scoreMainLog.setCommentSeq(0);
        scoreMainLog.setMemGuid(memGuid);
        scoreMainLog.setGeneralId(ptadId + "_" + membGradef);
        if (Objects.equals(type, Constant.CRM_SCORE_GIVE)) {
            scoreMainLog.setChannel(Constant.SCORE_CHANNEL_CRM_GIVE);
            scoreMainLog.setScoreNumber(score);
            //赠送积分
            if (new Date().after(limitTime)) {
                //生效积分
                scoreMainLog.setRemark("CRM赠送可用积分");
                scoreMainLog.setActualTime(new Date());
                scoreMainLog.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);
                scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
                addSelfAvailableScore(memGuid, limitTime, score, scoreMainLog.getSmlSeq());
            } else {
                //冻结积分
                scoreMainLog.setRemark("CRM赠送冻结积分");
                scoreMainLog.setActualTime(new Date());
                scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
                addSelfLockedScore(memGuid, limitTime, score, scoreMainLog.getSmlSeq());
            }
        } else if (Objects.equals(type, Constant.CRM_SCORE_RECOVER)) {
            //加锁 为了保持加锁的顺序
            scoreMemberDao.getScoreMember(memGuid);
            //回收积分
            List<ScoreYear> scoreYearSelf = scoreYearDao.getScoreYearSelf(memGuid);
            int availableScore = 0;
            for (ScoreYear scoreYear : scoreYearSelf) {
                availableScore += scoreYear.getAvailableScore();
            }
            if (score > availableScore) {
                score = availableScore;
            }
            scoreMainLog.setRemark("CRM回收积分" + score);
            scoreMainLog.setChannel(Constant.SCORE_CHANNEL_CRM_RECOVER);
            scoreMainLog.setScoreNumber(-score);
            scoreMainLog.setActualTime(new Date());
            // 统计用
            scoreMainLog.setLimitTime(null);
            scoreMainLog.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);
            scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
            if (score > 0) {
                scoreMemberDao.deductAvailableScore(memGuid, score);
                deductScoreAlgorithm(memGuid, scoreMainLog.getSmlSeq(), scoreYearSelf, score);
            }

        }


    }

    /**
     * 取消商城订单
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void processMallOrderCancel(String memGuid, String ogSeq, String packageNo) {

        //从scoreMainLog中取得消费的积分
        ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_CONSUME);
        if (scoreMainLog == null) {
            //虚拟订单没有调用submitOrderScore,产生了很多该类信息。判断虚拟订单时直接return
            OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
            if (orderJsonVo.getVirtual() == 1) {
                return;
            }
            //重新计算
            throw new ScoreException(ResultCode.RESULT_SCORE_CANCEL_ORDER_BUT_NOT_FIND_DETAIL, "取消商城订单，未找到消费积分记录");
        }
        //订单消费详细信息
        List<ScoreOrderDetail> scoreODList = scoreOrderDetailDao.getScoreOrderDetailList(memGuid, ogSeq, Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CONSUME);
        if (scoreODList == null || scoreODList.size() == 0) {
            OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
            if (orderJsonVo.getVirtual() == 1) {
                return;
            }
            throw new ScoreException(ResultCode.RESULT_SCORE_CANCEL_ORDER_BUT_NOT_FIND_DETAIL, "取消商城订单，未找到消费积分详细记录。");
        }
        //商城商品
        List<ScoreOrderDetail> scoreMallList = new ArrayList<>();
        //返回消费的积分
        int consumeScoreBack = 0;
        for (ScoreOrderDetail sod : scoreODList) {
            if (Objects.equals(sod.getSourceType(), Constant.MALLTYPE)) {
                String sodPackageNo = sod.getPackageNo();
                //当包裹号为null或者指定包裹号时
                if (packageNo == null || (StringUtils.equals(packageNo, sodPackageNo))) {
                    Integer scoreGet = sod.getScoreGet();
                    Integer scoreConsume = sod.getScoreConsume();
                    sod.setScoreConsume(scoreGet);
                    sod.setScoreGet(scoreConsume);
                    sod.setType(Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CANCEL_CONSUME_RETURN);
                    scoreMallList.add(sod);
                    consumeScoreBack += scoreConsume;
                }
            }
        }
        if (scoreMallList.size() == 0) {
            throw new ScoreException(ResultCode.RESULT_SCORE_CANCEL_ORDER_BUT_NOT_FIND_DETAIL, "取消商城订单，未找到订单商品详细信息。");
        }
        //检测要取消的商品是否已经取消
        Integer count = scoreOrderDetailDao.getSodAboutMallCancel(memGuid, ogSeq, scoreMallList);
        if (count != null && count > 0) {
            log.error("不能重复取消订单。memGuid=" + memGuid + ",ogSeq=" + ogSeq, "processMallOrderCancel");
            return;
        }
        String remark = "取消商城订单";
        String ogNo = scoreMainLog.getOgNo();
        ScoreMainLog scoreMainLogGet = new ScoreMainLog();
        scoreMainLogGet.setChannel(Constant.SCORE_CHANNEL_ORDER_CANCEL);
        scoreMainLogGet.setMemGuid(memGuid);
        scoreMainLogGet.setOgSeq(ogSeq);
        scoreMainLogGet.setOgNo(ogNo);
        scoreMainLogGet.setRgSeq("");
        scoreMainLogGet.setRgNo("");
        scoreMainLogGet.setGeneralId(packageNo);
        scoreMainLogGet.setRemark(remark + ",消费退回.");
        scoreMainLogGet.setCommentSeq(0);
        scoreMainLogGet.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLogGet.setScoreNumber(consumeScoreBack);
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogGet);
        log.info("用户：" + memGuid + ",未付款取消商城订单ogSeq:" + ogSeq + "，返还用户消费积分：" + consumeScoreBack, "processMallOrderCancel");
        for (ScoreOrderDetail sod : scoreMallList) {
            sod.setSmlSeq(scoreMainLogGet.getSmlSeq());
        }
        //记录详细信息
        scoreOrderDetailDao.saveScoreOrderDetail(memGuid, scoreMallList);

        if (consumeScoreBack > 0) {
            //进行加锁
            scoreMemberDao.getScoreMember(memGuid);
            // 订单取消。消费积分退回。
            scoreMemberDao.addScoreBecauseReturn(memGuid, consumeScoreBack);

            Map<Integer, Integer> map = new HashMap<>();
            // 计算退还中，多少积分是退还到 thisYear，多少积分退还到nextYear
            for (ScoreOrderDetail sod : scoreMallList) {
                Integer scySeq = sod.getScySeq();
                if (scySeq == 0) {
                    continue;
                }
                Integer scoreGet = sod.getScoreGet();
                if (map.containsKey(scySeq)) {
                    map.put(scySeq, map.get(scySeq) + scoreGet);
                } else {
                    map.put(scySeq, scoreGet);
                }
            }
            // 创建scoreYearLog日志对象
            ScoreYearLog scoreYearLogNew = new ScoreYearLog();
            // score_main_log表主键
            scoreYearLogNew.setSmlSeq(scoreMainLogGet.getSmlSeq());
            // 用户ID
            scoreYearLogNew.setMemGuid(memGuid);
            // 消费积分
            scoreYearLogNew.setScoreConsume(0);

            Calendar calendar = Calendar.getInstance();
            //今年是哪一年
            int thisYear = calendar.get(Calendar.YEAR);
            FastDateFormat sdfY = FastDateFormat.getInstance("yyyy");
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                //scoreYear ID
                Integer scySeq = entry.getKey();
                // 得到对应的ScoreYear对象
                ScoreYear scoreYear = scoreYearDao.getScoreYearById(memGuid, scySeq);
                // 实际返回的积分
                int realBack = entry.getValue();
                // score_year表主键
                scoreYearLogNew.setScySeq(scySeq);
                scoreYearLogNew.setScoreGet(realBack);
                // 保存scoreYearLog日志
                scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLogNew);
                // 过期时间
                Date dueTime = scoreYear.getDueTime();
                // 过期年份 如2014
                int dueTimeYear = Integer.parseInt(sdfY.format(dueTime));
                // 如果过期年份小于当前年份
                if (dueTimeYear < thisYear) {
                    // 积分过期 返回积分直接失效
                    returnScoreButExpired(memGuid, ogSeq, ogNo, "", remark, scySeq, realBack, dueTimeYear);
                    log.info("返还用户：" + memGuid + "积分" + realBack + ",超过积分过期时间，积分过期。", "processMallOrderCancel");
                } else {
                    // 返回消费的积分
                    scoreYearDao.addScoreById(memGuid, realBack, scySeq);
                }

            }
        }
    }


    /**
     * 订单购买增加冻结积分
     */
    private void addLockedScoreBecauseOrderBuy(String memGuid, Date insTime, String ogSeq, Integer getScore, Integer smlSeq) {
        if (getScore > 0) {

            //查找scoreMainLog对应的scoreOrderDetail信息列表
            List<ScoreOrderDetail> orderDetailList = scoreOrderDetailDao.getScoreOrderDetailList(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_BUY);

            //自营积分信息
            int selfLockedScore = 0;
            List<Integer> selfSodSeqList = new ArrayList<>();

            //商城积分信息
            Map<String, List<Integer>> mallSodSeqMap = new HashMap<>();
            Map<String, Integer> mallLockedScoreMap = new HashMap<>();

            for (ScoreOrderDetail sod : orderDetailList) {
                Integer sourceType = sod.getSourceType();
                Integer scoreGet = sod.getScoreGet();
                if (scoreGet == 0) {
                    continue;
                }
                Integer sodSeq = sod.getSodSeq();
                if (Objects.equals(sourceType, Constant.SELFTYPE)) {
                    //自营商品
                    selfLockedScore += scoreGet;
                    selfSodSeqList.add(sodSeq);
                } else if (Objects.equals(sourceType, Constant.MALLTYPE)) {
                    //商城商品
                    String sellerNo = sod.getSellerNo();
                    Integer sellerScore = mallLockedScoreMap.get(sellerNo);
                    if (sellerScore == null) {
                        mallLockedScoreMap.put(sellerNo, scoreGet);
                        List<Integer> sodSeqList = new ArrayList<>();
                        sodSeqList.add(sodSeq);
                        mallSodSeqMap.put(sellerNo, sodSeqList);
                    } else {
                        mallLockedScoreMap.put(sellerNo, scoreGet + sellerScore);
                        mallSodSeqMap.get(sellerNo).add(sodSeq);
                    }

                }
            }

            //自营
            if (selfLockedScore > 0) {
                //添加冻结积分
                Integer scySeq = addSelfLockedScore(memGuid, insTime, selfLockedScore, smlSeq);
                //更新orderDetail scySeq
                scoreOrderDetailDao.updateOrderDetailScySeqBySodSeqs(memGuid, selfSodSeqList, scySeq);
            }
            //总的商城积分
            int mallLockedScore = 0;
            //商城
            for (Map.Entry<String, Integer> entry : mallLockedScoreMap.entrySet()) {
                String sellerNo = entry.getKey();
                Integer lockedScore = entry.getValue();
                mallLockedScore += lockedScore;
                //添加对应的商家的冻结积分
                if (lockedScore > 0) {
                    Integer scySeq = addMallLockedScore(memGuid, insTime, lockedScore, smlSeq, sellerNo, Constant.MALL_SCORE_TYPE_ORDER_BUY);
                    //更新orderDetail scySeq
                    List<Integer> mallSodSeqList = mallSodSeqMap.get(sellerNo);
                    scoreOrderDetailDao.updateOrderDetailScySeqBySodSeqs(memGuid, mallSodSeqList, scySeq);
                }
            }
            if (getScore != (selfLockedScore + mallLockedScore)) {
                throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "计算积分异常。");
            }

        }


    }

    /**
     * 添加自营冻结积分
     */
    @Override
    public int addSelfLockedScore(String memGuid, Date insTime, Integer getScore, Integer smlSeq) {
        //保存或更新score_member
        addMemberLockedScore(memGuid, getScore);
        //保存或更新score_year
        ScoreYear scoreYear = scoreYearDao.getScoreYear(memGuid, insTime);
        //返回scoreYear 的ID
        return addLockedScore(memGuid, insTime, getScore, smlSeq, "", Constant.SELF_SCORE_TYPE, Constant.SELFTYPE, scoreYear);
    }

    /**
     * 添加商城冻结积分
     */
    @Override
    public int addMallLockedScore(String memGuid, Date insTime, Integer getScore, Integer smlSeq,
                                  String sellerNo, Integer scoreType) {
        addMemberLockedScore(memGuid, getScore);
        ScoreYear scoreYear = scoreYearDao.getScoreYearForMall(memGuid, insTime, sellerNo, scoreType);
        //返回scoreYear 的ID
        return addLockedScore(memGuid, insTime, getScore, smlSeq, sellerNo, scoreType, Constant.MALLTYPE, scoreYear);
    }

    private void addMemberLockedScore(String memGuid, Integer getScore) {
        //保存或更新score_member
        ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
        if (scoreMember != null) {
            //更新
            scoreMemberDao.updateLockedScoreMember(memGuid, getScore);
        } else {
            scoreMemberDao.saveLockedScoreMember(memGuid, getScore);
        }
    }

    private int addLockedScore(String memGuid, Date insTime, Integer getScore, Integer smlSeq,
                               String sellerNo, Integer scoreType, Integer sourceType, ScoreYear scoreYear) {

        Integer scySeq;
        if (scoreYear != null) {
            scySeq = scoreYear.getScySeq();
            scoreYearDao.addLockedScoreYear(memGuid, scySeq, getScore);
        } else {
            scoreYear = new ScoreYear();
            scoreYear.setTotalScore(getScore);
            scoreYear.setLockedScore(getScore);
            scoreYear.setAvailableScore(0);
            scoreYear.setExpiredScore(0);
            scoreYear.setDueTime(insTime);
            scoreYear.setMemGuid(memGuid);
            scoreYear.setSellerNo(sellerNo);
            scoreYear.setSourceType(sourceType);
            scoreYear.setScoreType(scoreType);
            scoreYearDao.saveScoreYear(memGuid, scoreYear);
            scySeq = scoreYear.getScySeq();
        }
        //保存score_year_log
        ScoreYearLog scoreYearLog = new ScoreYearLog();
        scoreYearLog.setMemGuid(memGuid);
        scoreYearLog.setScoreConsume(0);
        scoreYearLog.setScoreGet(getScore);
        scoreYearLog.setSmlSeq(smlSeq);
        scoreYearLog.setScySeq(scySeq);
        scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLog);
        return scySeq;
    }


    /**
     * 退货回收评论有关积分
     */
    @Override
    public void returnCommentScoreBecauseReturnProduct(String memGuid, Integer commentSeq, ScoreOrderDetail sod) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("commentSeq", commentSeq);
        //评论获取积分记录
        paramMap.put("type", Constant.SCORE_COMMENT_DETAIL_TYPE_COMMENT_PRODUCT);
        Integer channel = Constant.SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_COMMENT;
        String remark = "客人退货回收评论积分";
        Integer type = Constant.SCORE_COMMENT_DETAIL_TYPE_RETURN_PRODUCT_RECOVER_COMMENT;
        returnCommentScoreDetail(memGuid, paramMap, channel, remark, type, sod);
        //评论设置为精华获得积分的记录
        paramMap.put("type", Constant.SCORE_COMMENT_DETAIL_TYPE_COMMENT_SET_ESSENCE);
        channel = Constant.SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_ESSENCE;
        remark = "客人退货回收评论设置精华积分";
        type = Constant.SCORE_COMMENT_DETAIL_TYPE_RETURN_PRODUCT_RECOVER_ESSENCE;
        returnCommentScoreDetail(memGuid, paramMap, channel, remark, type, sod);
        //评论置顶获得积分记录
        paramMap.put("type", Constant.SCORE_COMMENT_DETAIL_TYPE_COMMENT_SET_TOP);
        channel = Constant.SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_TOP;
        remark = "客人退货回收评论置顶积分";
        type = Constant.SCORE_COMMENT_DETAIL_TYPE_RETURN_PRODUCT__RECOVER_TOP;
        returnCommentScoreDetail(memGuid, paramMap, channel, remark, type, sod);
    }


    private void returnCommentScoreDetail(String memGuid, Map<String, Object> paramMap,
                                          Integer channel, String remark, Integer type, ScoreOrderDetail sod) {
        ScoreCommentDetail scoreCommentDetail = scoreCommentDetailDao.getScoreCommentDetail(memGuid, paramMap);
        if (scoreCommentDetail != null) {
            paramMap.put("type", type);
            ScoreCommentDetail scoreCommentDetailRecover = scoreCommentDetailDao.getScoreCommentDetail(memGuid, paramMap);
            if (scoreCommentDetailRecover != null) {
                log.error("已经回收评论相关积分，不能重复提交。", "returnCommentScoreDetail");
                return;
            }

            ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLogById(memGuid, scoreCommentDetail.getSmlSeq());
            Integer smlSeq = scoreMainLog.getSmlSeq();
            Integer scoreBack = scoreMainLog.getScoreNumber();

            ScoreMainLog scoreMainLogBack = new ScoreMainLog();
            scoreMainLogBack.setChannel(channel);
            scoreMainLogBack.setMemGuid(memGuid);
            scoreMainLogBack.setOgSeq(scoreMainLog.getOgSeq());
            scoreMainLogBack.setRgSeq(sod.getRgSeq());
            scoreMainLogBack.setCommentSeq(scoreMainLog.getCommentSeq());
            scoreMainLogBack.setScoreNumber(-scoreBack);
            scoreMainLogBack.setRemark(remark);
            scoreMainLogBack.setOgNo(sod.getOgNo());
            scoreMainLogBack.setRgNo(sod.getRgNo());
            //设置为有效，不进行扫描
            scoreMainLogBack.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);
            scoreMainLogBack.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
            scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogBack);

            List<ScoreYearLog> scoreYearLogList = scoreYearLogDao.getScoreYearLogByLM(smlSeq, memGuid);
            ScoreYearLog scoreYearLog = scoreYearLogList.get(0);
            Integer scySeq = scoreYearLog.getScySeq();
            Integer smlSeqBack = scoreMainLogBack.getSmlSeq();
            Integer realDeductAvailableScore = 0;

            //积分已生效
            Map<String, Integer> realDeductScoreMap = handleEffectScore(memGuid, scySeq, scoreBack, smlSeqBack);
            Integer availableScore = realDeductScoreMap.get("availableScore");
            if (availableScore != null) {
                realDeductAvailableScore += availableScore;
            }
            scoreMainLogBack.setActualTime(new Date());
            scoreMainLogBack.setScoreNumber(-realDeductAvailableScore);
            scoreMainLogBack.setRemark(remark + "，扣除可用积分" + realDeductAvailableScore);
            scoreMainLogDao.updateScoreMainLog(memGuid, scoreMainLogBack);

            Integer scoreConsume = scoreCommentDetail.getScoreGet();
            scoreCommentDetail.setScoreConsume(scoreConsume);
            scoreCommentDetail.setScoreGet(0);
            scoreCommentDetail.setType(type);
            scoreCommentDetail.setSmlSeq(smlSeqBack);
            scoreCommentDetail.setRgSeq(sod.getRgSeq());
            scoreCommentDetail.setRpSeq(sod.getRpSeq());
            scoreCommentDetail.setRlSeq(sod.getRlSeq());
            scoreCommentDetail.setOgNo(sod.getOgNo());
            scoreCommentDetail.setRgNo(sod.getRgNo());
            scoreCommentDetailDao.saveScoreCommentDetail(memGuid, scoreCommentDetail);
        }
    }


    /**
     * 计算订单获取的积分
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void processOrderScore(String memGuid, String ogSeq) {

        String uniqueId = ogSeq + "_" + Constant.SCORE_CHANNEL_ORDER_BUY;
        ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLogByUniqueIdForUpdate(memGuid, uniqueId);
        // 查询订单是否获得了积分
        if (scoreMainLog == null) {
            //没有记录 进行插入操作
            //所有订单信息列表
            OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);

            //处理订单详细和可获得的积分
            Map<String, Object> detailAndScore = buildDetailAndComputeScore(orderJsonVo);

            @SuppressWarnings("unchecked")
            List<ScoreOrderDetail> allList = (List<ScoreOrderDetail>) detailAndScore.get("detailList");
            //总积分
            Integer totalScore = (Integer) detailAndScore.get("totalScore");

            //订单购买的详细记录
            orderBuyScoreDetailWhenPay(allList, orderJsonVo, totalScore);
            //消息补偿
            submitOrderKafkaMsgCompensate(memGuid, orderJsonVo);
        } else {
            //已经有记录了 进行更新操作
            if (Objects.equals(scoreMainLog.getStatus(), Constant.SCORE_MAIN_LOG_STATUS_VAILD)) {
                log.error("订单购买获得积分ScoreMainLog的status已为有效状态，不能重复提交。" + " memGuid:" + memGuid + ",ogSeq:" + ogSeq, "processOrderScore");
                return;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DateUtil.getNowDate());
            calendar.add(Calendar.DATE, Constant.SCORE_EFFECT_DAY);
            Date limitTime = calendar.getTime();
            //生效日期 = 订单付款日期+ 10天
            scoreMainLog.setLimitTime(limitTime);
            //失效日期 = 下一年的12月31日
            calendar.add(Calendar.YEAR, 1);
            calendar.set(Calendar.MONTH, Calendar.DECEMBER);
            calendar.set(Calendar.DATE, 31);
            scoreMainLog.setEndTime(calendar.getTime());
            scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
            scoreMainLog.setActualTime(new Date());
            //更新score_main_log
            scoreMainLogDao.updateScoreMainLog(memGuid, scoreMainLog);

            int totalScore = scoreMainLog.getScoreNumber();
            Integer smlSeq = scoreMainLog.getSmlSeq();
            log.info("用户:" + memGuid + "付款,订单ogSeq:" + ogSeq + "获得冻结积分：" + totalScore, "processOrderScore");
            //增加冻结积分
            addLockedScoreBecauseOrderBuy(memGuid, limitTime, ogSeq, totalScore, smlSeq);
        }
    }

    private void orderBuyScoreDetailWhenPay(List<ScoreOrderDetail> allList, OrderJsonVo orderJsonVo, int totalScore) {
        String memGuid = orderJsonVo.getMemGuid();
        String ogSeq = orderJsonVo.getOgSeq();
        String ogNo = orderJsonVo.getOgNo();

        ScoreMainLog scoreMainLogGet = new ScoreMainLog();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.getNowDate());
        calendar.add(Calendar.DATE, Constant.SCORE_EFFECT_DAY);
        Date limitTime = calendar.getTime();
        //生效日期 = 订单付款日期+ 10天
        scoreMainLogGet.setLimitTime(limitTime);
        //失效日期 = 下一年的12月31日
        calendar.add(Calendar.YEAR, 1);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DATE, 31);
        scoreMainLogGet.setEndTime(calendar.getTime());
        scoreMainLogGet.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLogGet.setActualTime(new Date());
        scoreMainLogGet.setChannel(Constant.SCORE_CHANNEL_ORDER_BUY);
        scoreMainLogGet.setMemGuid(memGuid);
        scoreMainLogGet.setOgSeq(ogSeq);
        scoreMainLogGet.setOgNo(ogNo);
        scoreMainLogGet.setRemark("订单购买");
        scoreMainLogGet.setRgSeq("");
        scoreMainLogGet.setRgNo("");
        scoreMainLogGet.setScoreNumber(totalScore);
        scoreMainLogGet.setCommentSeq(0);
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogGet);
        log.info("用户:" + memGuid + "订单付款ogSeq:" + ogSeq + "，获得积分:" + totalScore + ",状态status：1", "orderBuyScoreDetailWhenPay");

        Integer smlSeq = scoreMainLogGet.getSmlSeq();
        for (ScoreOrderDetail sod : allList) {
            sod.setSmlSeq(smlSeq);
            //当付款之后更新为对应的ScoreYear ID
            sod.setScySeq(0);
            //积分获得的类型。积分兑换 会员专享不获得积分，但也要设置一条类型为购买获得的记录。否则退货校验时会有问题。
            sod.setType(Constant.SCORE_ORDER_DETAIL_TYPE_BUY);

        }
        scoreOrderDetailDao.saveScoreOrderDetail(memGuid, allList);
        //增加冻结积分
        addLockedScoreBecauseOrderBuy(memGuid, limitTime, ogSeq, totalScore, smlSeq);

    }

    /**
     * 退货确认
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void processReturnOrderScore(String memGuid, ReturnJsonVo returnJsonVo, String pay) {
        //scoreMainLog channel 标识
        Integer channelBack = Constant.SCORE_CHANNEL_RETURN_PRODUCT_REVOKE;
        Integer channelGet = Constant.SCORE_CHANNEL_RETURN_PRODUCT_ROCKBACK;
        //scoreOrderDetial type 标识
        Integer typeBack = Constant.SCORE_ORDER_DETAIL_TYPE_RETURN_PRODUCT;
        Integer typeGet = Constant.SCORE_ORDER_DETAIL_TYPE_RETURN_PRODUCT_CONSUME_RETURN;

        //详细信息入库
        saveOrderReturnDetail(returnJsonVo, channelBack, typeBack, channelGet, typeGet, pay);
        //退货回收评论送的积分
        String ogSeq = returnJsonVo.getOgSeq();
        //1.查找退货单详细信息
        List<ScoreOrderDetail> scoreOrderDetailList = scoreOrderDetailDao.getScoreOrderDetailList(memGuid, ogSeq, typeBack);
        Set<Integer> commentSeqs = new HashSet<>();
        for (ScoreOrderDetail sod : scoreOrderDetailList) {
            //2.找到退货对应的评论ID
            ScoreCommentDetail scoreCommentDetail = scoreCommentDetailDao.getCommentDetailByProductDetail(memGuid, sod);
            if (scoreCommentDetail != null) {
                Integer commentSeq = scoreCommentDetail.getCommentSeq();
                //如果对应的评论积分还没有回收
                if (!commentSeqs.contains(commentSeq)) {
                    returnCommentScoreBecauseReturnProduct(memGuid, scoreCommentDetail.getCommentSeq(), sod);
                    commentSeqs.add(commentSeq);
                }
            }
        }
    }

    /**
     * 退货详细信息
     */
    private void saveOrderReturnDetail(ReturnJsonVo returnJsonVo, Integer chanelBack,
                                       Integer typeBack, Integer channelGet, Integer typeGet, String pay) {
        String memGuid = returnJsonVo.getMemGuid();
        String rgSeq = returnJsonVo.getRgSeq();
        String ogSeq = returnJsonVo.getOgSeq();
        String ogNo = returnJsonVo.getOgNo();
        String rgNo = returnJsonVo.getRgNo();
        String errorSuffix = "memGuid:" + memGuid + ",rgSeq:" + rgSeq;

        //商城退单列表
        List<ReturnDetail> mallList = returnJsonVo.getMallList();

        //自营退单列表
        List<ReturnDetail> selfList = returnJsonVo.getSelfList();
        //退货商品信息总列表
        List<ReturnDetail> returnList = new ArrayList<>();
        //是否是商城退货
        boolean isMall = false;
        if (mallList != null && mallList.size() > 0) {
            returnList.addAll(mallList);
            isMall = true;
        }
        //多地多仓上线之前商城是通过包裹取消订单的，现在是走退货流程，可能会造成退两次积分。现进行拦截判断
        if (isMall && (!"1".equals(pay))) {
            ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_CANCEL);
            if (scoreMainLog != null) {
                log.error("商城订单重复退积分：" + scoreMainLog);
                return;
            }
        }
        if (selfList != null && selfList.size() > 0) {
            returnList.addAll(selfList);
        }
        Integer count = scoreOrderDetailDao.getScoreOrderDetailCountByRlSeqs(memGuid, returnList);
        if (count != null && count > 0) {
            log.error("已经有退货记录，不能重复提交。" + errorSuffix, "saveOrderReturnDetail");
            return;
        }
        //积分兑换和会员专享在main_log表中channel也为消费
        ScoreMainLog scoreMainLogBuy = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_BUY);

        if (scoreMainLogBuy == null) {
            throw new ScoreException(ResultCode.RESULT_SCORE_RETURN_BUT_NO_BUY_LOG, "未找到订单购买获得积分信息，延迟处理。");
        }

        if (StringUtils.equals(pay, Constant.IS_PAY)) {
            //如果是支付状态。
            //查看是否已经是付款加积分了。
            if (Objects.equals(scoreMainLogBuy.getStatus(), Constant.SCORE_MAIN_LOG_STATUS_INVAILD)) {
                throw new ScoreException(ResultCode.RESULT_SCORE_RETURN_BUT_NO_BUY_CONFIRM, "退货先于订单购买加积分，加入失败日志，延迟处理。");
            }
        }

        //用户订单购买获得积分的详细信息
        List<ScoreOrderDetail> orderBuyGetScoreDetailList = scoreOrderDetailDao.getScoreOrderDetailList(memGuid, ogSeq, Constant.SCORE_ORDER_DETAIL_TYPE_BUY);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("memGuid", memGuid);
        paramMap.put("ogSeq", ogSeq);
        paramMap.put("typesOfConsumeAndExchange", Constant.TYPES_OF_CONSUME_AND_EXCHANGE);
        //订单消费积分详细信息,包括订单消费 积分兑换和会员专享
        List<ScoreOrderDetail> orderBuyScoreConsumeDetailList = scoreOrderDetailDao.getScoreOrderDetailListByParam(memGuid, paramMap);
        //要返回的已获得积分
        int scoreConsumeTotal = 0;
        //退还给用户的消费积分
        int scoreGetTotal = 0;
        //退货的ScoreOrderDetail
        List<ScoreOrderDetail> scoreOrderDetailList = new ArrayList<>();

        for (ReturnDetail returnDetail : returnList) {
            //订单明细
            String olSeq = returnDetail.getOlSeq();
            //查找订单购买时对应的商品
            ScoreOrderDetail scoreOrderDetailBuy = null;
            ScoreOrderDetail scoreOrderDetailConsume = null;
            for (ScoreOrderDetail sodBuy : orderBuyGetScoreDetailList) {
                //找到对应的商品
                if (StringUtils.equals(olSeq, sodBuy.getOlSeq())) {
                    scoreOrderDetailBuy = sodBuy;
                    break;
                }
            }
            for (ScoreOrderDetail sodConsume : orderBuyScoreConsumeDetailList) {
                //找到对应的商品
                if (StringUtils.equals(olSeq, sodConsume.getOlSeq())) {
                    if (scoreOrderDetailConsume == null) {
                        scoreOrderDetailConsume = sodConsume;
                    } else {
                        Integer scoreConsume = scoreOrderDetailConsume.getScoreConsume();
                        scoreOrderDetailConsume.setScoreConsume(scoreConsume + sodConsume.getScoreConsume());
                    }
                }
            }
            if (scoreOrderDetailBuy == null) {
                throw new ScoreException(ResultCode.RESULT_SCORE_RETURN_BUT_NO_BUY_LOG, "未找到购买详细信息，延迟处理。");
            }
            if (scoreOrderDetailConsume == null) {
                throw new ScoreException(ResultCode.RESULT_SCORE_RETURN_BUT_NO_BUY_LOG, "未找到消费详细信息，延迟处理。");
            }
            ScoreOrderDetail orderDetail = bulidScoreDetail(scoreOrderDetailBuy, scoreOrderDetailConsume, returnDetail, isMall, pay, rgSeq, rgNo);
            scoreOrderDetailList.add(orderDetail);
            scoreConsumeTotal += orderDetail.getScoreConsume();
            scoreGetTotal += orderDetail.getScoreGet();
        }

        ScoreMainLog scoreMainLogNew = new ScoreMainLog();
        scoreMainLogNew.setMemGuid(memGuid);
        scoreMainLogNew.setOgSeq(ogSeq);
        scoreMainLogNew.setOgNo(ogNo);
        scoreMainLogNew.setRgSeq(rgSeq);
        scoreMainLogNew.setRgNo(rgNo);
        scoreMainLogNew.setCommentSeq(0);
        scoreMainLogNew.setLimitTime(scoreMainLogBuy.getLimitTime());
        scoreMainLogNew.setEndTime(scoreMainLogBuy.getEndTime());

        //放入缓存
        cacheUtils.putGetScore(rgSeq, scoreGetTotal);
        cacheUtils.putConsumeScore(rgSeq, scoreConsumeTotal);

        //得到一份copy对象
        String arrayJson = JSONObject.toJSONString(scoreOrderDetailList);
        List<ScoreOrderDetail> sodListCopy = JSONObject.parseArray(arrayJson, ScoreOrderDetail.class);
        //返回用户消费的积分
        returnConsumeScoreDetail(channelGet, typeGet, memGuid, ogSeq, scoreGetTotal, scoreOrderDetailList, scoreMainLogNew);
        if (StringUtils.equals(pay, Constant.IS_PAY)
                || Objects.equals(scoreMainLogBuy.getStatus(), Constant.SCORE_MAIN_LOG_STATUS_VAILD)) {
            //回收用户获得的积分
            recoverGetScore(chanelBack, typeBack, memGuid, scoreConsumeTotal, sodListCopy, scoreMainLogNew);

        }


    }


    private ScoreOrderDetail bulidScoreDetail(ScoreOrderDetail sodBuy, ScoreOrderDetail sodConsume, ReturnDetail returnDetail,
                                              boolean isMall, String pay, String rgSeq, String rgNo) {
        //查询此商品已经退货了多少个。
        Integer countHaveReturn = scoreOrderDetailDao.getItHaveReturnCount(sodBuy.getMemGuid(), returnDetail, sodBuy.getOgSeq());
        countHaveReturn = NumberUtils.getIntValue(countHaveReturn, 0);
        //退货商品可以获得返还的消费积分
        int scoreGet = computeScoreGet(returnDetail, sodConsume, isMall, countHaveReturn);
        //退货商品需要退还的订单购买赠送积分
        int scoreConsume = 0;
        if (StringUtils.equals(pay, Constant.IS_PAY)) {
            scoreConsume = computeScoreConsume(returnDetail, sodBuy, countHaveReturn);
        }
        ScoreOrderDetail scoreOrderDetail = new ScoreOrderDetail();
        scoreOrderDetail.setBill(sodBuy.getBill());
        scoreOrderDetail.setItNo(sodBuy.getItNo());
        scoreOrderDetail.setMemGuid(sodBuy.getMemGuid());
        scoreOrderDetail.setOgSeq(sodBuy.getOgSeq());
        scoreOrderDetail.setOgsSeq(sodBuy.getOgsSeq());
        scoreOrderDetail.setOgNo(sodBuy.getOgNo());
        scoreOrderDetail.setOlSeq(sodBuy.getOlSeq());
        scoreOrderDetail.setRgSeq(rgSeq);
        scoreOrderDetail.setRgNo(rgNo);
        scoreOrderDetail.setRlSeq(returnDetail.getRlSeq());
        scoreOrderDetail.setRpSeq("");
        scoreOrderDetail.setSmlSeq(0);
        scoreOrderDetail.setScoreConsume(scoreConsume);
        scoreOrderDetail.setScoreGet(scoreGet);
        scoreOrderDetail.setScySeq(sodBuy.getScySeq());
        scoreOrderDetail.setSourceMode(sodBuy.getSourceMode());
        scoreOrderDetail.setSourceType(sodBuy.getSourceType());
        scoreOrderDetail.setSiteMode(sodBuy.getSiteMode());
        scoreOrderDetail.setBuyMode(sodBuy.getBuyMode());
        scoreOrderDetail.setPromotionGrade(sodBuy.getPromotionGrade());
        scoreOrderDetail.setSellerNo(sodBuy.getSellerNo());
        scoreOrderDetail.setQuantity(returnDetail.getQuantity());
        scoreOrderDetail.setKind(sodBuy.getKind());
        scoreOrderDetail.setPackageNo(sodBuy.getPackageNo());
        scoreOrderDetail.setMoney(returnDetail.getRealReturn());
        scoreOrderDetail.setStoreNo(sodBuy.getStoreNo());
        return scoreOrderDetail;
    }

    @Override
    public int computeScoreGet(ReturnDetail returnDetail, ScoreOrderDetail sodConsume,
                               boolean isMall, Integer countHaveReturn) {
        int scoreGet;
        //查询此商品消费了多少积分
        //Integer scoreConsumeBuy = scoreOrderDetailDao.getItConsumeScore(sodConsume.getMemGuid(), returnDetail, sodConsume.getOgSeq());
        // scoreConsumeBuy = NumberUtils.getIntValue(scoreConsumeBuy, 0);

        //如果此次退货个数和未退还的一样
        if (returnDetail.getQuantity() == (sodConsume.getQuantity() - countHaveReturn)) {
            //查询此商品已经退还了多少消费积分
            //Integer scoreHaveReturn = scoreOrderDetailDao.getItHaveReturnScore(sodBuy.getMemGuid(), returnDetail, sodBuy.getOgSeq());
            // scoreHaveReturn = NumberUtils.getIntValue(scoreHaveReturn, 0);
            //使用减法处理
            if (isMall) {
                //商城的自己计算
                //商城的是先计算实际应该退还的积分 然后进行折扣计算
                List<ScoreOrderDetail> listReturn = scoreOrderDetailDao.getScoreOrderDetailBuyListByOgsSeq(sodConsume.getMemGuid(), sodConsume.getOgsSeq(), Constant.SCORE_ORDER_DETAIL_TYPE_RETURN_PRODUCT);
                scoreGet = getScoreGet(sodConsume, listReturn, sodConsume.getOlSeq(), returnDetail.getQuantity());

                if (BigDecimal.ZERO.compareTo(returnDetail.getRefundablePrice()) < 0) {
                    scoreGet = new BigDecimal(scoreGet).multiply(returnDetail.getPrice()).divide(returnDetail.getRefundablePrice(), BigDecimal.ROUND_HALF_UP).intValue();
                }
            } else {
                //自营的已经有结果
                //scoreGet = scoreConsumeBuy - scoreHaveReturn;
                scoreGet = returnDetail.getReturnScore();
            }
        } else {
            //按个数比例计算
            if (isMall) {
                //商城的自己计算
                scoreGet = (sodConsume.getScoreConsume() * returnDetail.getQuantity()) / sodConsume.getQuantity();
                if (BigDecimal.ZERO.compareTo(returnDetail.getRefundablePrice()) < 0) {
                    scoreGet = new BigDecimal(scoreGet).multiply(returnDetail.getPrice()).divide(returnDetail.getRefundablePrice(), BigDecimal.ROUND_HALF_UP).intValue();
                }
            } else {
                //自营的已经有结果
                scoreGet = returnDetail.getReturnScore();
            }
        }
        return scoreGet;
    }

    @Override
    public int getScoreGet(ScoreOrderDetail sodDetail, List<ScoreOrderDetail> listReturn, String olsSeq, Integer qty) {
        ScoreOrderDetail sodReturn = findReturnSod(listReturn, olsSeq, sodDetail);
        int scoreGet;
        if (sumValue(sodReturn.getQuantity(), qty) == sodDetail.getQuantity()) {
            scoreGet = subtractValue(sodDetail.getScoreConsume(), sodReturn.getScoreGet());
        } else {
            scoreGet = qty * sodDetail.getScoreConsume() / sodDetail.getQuantity();
        }
        return scoreGet;
    }

    /**
     * 应该已经退了多少积分
     */
    private ScoreOrderDetail findReturnSod(List<ScoreOrderDetail> list, String olSeq, ScoreOrderDetail sodDetail) {
        ScoreOrderDetail scoreOrderDetail = new ScoreOrderDetail();

        for (ScoreOrderDetail sod : list) {
            if (StringUtils.equals(sod.getOlSeq(), olSeq)) {
                //应退。不是实退
                int scoreGet = NumberUtils.getIntValue(sod.getQuantity(), 0) * sodDetail.getScoreConsume() / sodDetail.getQuantity();
                scoreOrderDetail.setScoreGet(sumValue(scoreOrderDetail.getScoreGet(), scoreGet));
                scoreOrderDetail.setQuantity(sumValue(scoreOrderDetail.getQuantity(), sod.getQuantity()));
            }
        }
        return scoreOrderDetail;
    }

    private int sumValue(Integer one, Integer two) {
        return NumberUtils.getIntValue(one, 0) + NumberUtils.getIntValue(two, 0);
    }

    private int subtractValue(Integer one, Integer two) {
        return NumberUtils.getIntValue(one, 0) - NumberUtils.getIntValue(two, 0);
    }

    private int computeScoreConsume(ReturnDetail returnDetail, ScoreOrderDetail sodBuy, Integer countHaveReturn) {
        int scoreConsume;
        //计算要退还的积分
        //如果此次退货个数和未退还的一样
        if (returnDetail.getQuantity() == (sodBuy.getQuantity() - countHaveReturn)) {
            scoreConsume = computeScoreBackForAllReturn(returnDetail, sodBuy);
        } else {
            scoreConsume = (int) Math.floor(returnDetail.getRealReturn().multiply(sodBuy.getBill()).doubleValue());
        }
        return scoreConsume;
    }

    private int computeScoreBackForAllReturn(ReturnDetail returnDetail, ScoreOrderDetail sodBuy) {
        int scoreConsume;
        if (StringUtils.isEmpty(returnDetail.getSellerNo())) {
            //自营商品 ，用减法回收积分
            //查询此自营商品已经回收了多少积分
            Integer scoreHaveRecycle = scoreOrderDetailDao.getItHaveRecycleScore(sodBuy.getMemGuid(), returnDetail, sodBuy.getOgSeq());
            scoreHaveRecycle = NumberUtils.getIntValue(scoreHaveRecycle, 0);
            //要返还的积分
            scoreConsume = sodBuy.getScoreGet() - scoreHaveRecycle;
        } else {
            //查询此商城商品已经退还了多少钱
            BigDecimal moneyHaveReturn = scoreOrderDetailDao.getItHaveReturnMoney(sodBuy.getMemGuid(), returnDetail, sodBuy.getOgSeq());
            if (moneyHaveReturn == null) {
                moneyHaveReturn = BigDecimal.ZERO;
            }
            //剩下没退的钱
            BigDecimal restMoney = sodBuy.getMoney().subtract(moneyHaveReturn);
            //如果钱是全退的
            if (restMoney.compareTo(returnDetail.getRealReturn()) <= 0) {
                //用减法回收剩下的积分
                Integer scoreHaveRecycle = scoreOrderDetailDao.getItHaveRecycleScore(sodBuy.getMemGuid(), returnDetail, sodBuy.getOgSeq());
                scoreHaveRecycle = NumberUtils.getIntValue(scoreHaveRecycle, 0);
                //用减法计算要返还的积分
                scoreConsume = sodBuy.getScoreGet() - scoreHaveRecycle;
            } else {
                //用乘法计算要返还的积分
                scoreConsume = (int) Math.floor(returnDetail.getRealReturn().multiply(sodBuy.getBill()).doubleValue());
            }
        }
        return scoreConsume;
    }


    private void recoverGetScore(Integer chanelBack,
                                 Integer typeBack, String memGuid, int backScoreTotal,
                                 List<ScoreOrderDetail> scoreOrderDetailList, ScoreMainLog scoreMainLogNew) {


        scoreMainLogNew.setChannel(chanelBack);
        scoreMainLogNew.setScoreNumber(-backScoreTotal);
        scoreMainLogNew.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLogNew.setActualTime(new Date());
        scoreMainLogNew.setRemark("退货");
        scoreMainLogNew.setLockJobStatus(Constant.JOB_STATUS_DEFAULT);
        //记录scoreMainLog日志
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogNew);

        Integer smlSeq = scoreMainLogNew.getSmlSeq();
        Map<Integer, Integer> scySeqScoreMap = new HashMap<>();
        for (ScoreOrderDetail scoreOrderDetail : scoreOrderDetailList) {
            scoreOrderDetail.setSmlSeq(smlSeq);
            scoreOrderDetail.setType(typeBack);
            Integer scySeq = scoreOrderDetail.getScySeq();
            int scoreBack = scoreOrderDetail.getScoreConsume();
            if (scySeqScoreMap.containsKey(scySeq)) {
                Integer scySeqScore = scySeqScoreMap.get(scySeq);
                scySeqScoreMap.put(scySeq, scoreBack + scySeqScore);
            } else {
                scySeqScoreMap.put(scySeq, scoreBack);
            }

        }
        String ogSeq = scoreMainLogNew.getOgSeq();
        ScoreMainLog scoreMainLogAvailable = scoreMainLogDao.getAvailbaleScoreMainLogAboutOrder(memGuid, ogSeq, Constant.SCORE_CHANNEL_SCORE_ADD_AVAILABLE);
        Integer realDeductLockedScore = 0;
        Integer realDeductAvailableScore = 0;
        for (Map.Entry<Integer, Integer> entry : scySeqScoreMap.entrySet()) {
            Integer scySeq = entry.getKey();
            Integer score = entry.getValue();
            if (scoreMainLogAvailable == null) {
                //积分未生效处理
                Map<String, Integer> realDeductScoreMap = handleNotEffectScore(memGuid, scySeq, score, smlSeq);
                Integer lockedScore = realDeductScoreMap.get("lockedScore");
                Integer availableScore = realDeductScoreMap.get("availableScore");
                if (lockedScore != null) {
                    realDeductLockedScore += lockedScore;
                }
                if (availableScore != null) {
                    realDeductAvailableScore += availableScore;
                }

            } else {
                //积分已生效处理
                Map<String, Integer> realDeductScoreMap = handleEffectScore(memGuid, scySeq, score, smlSeq);
                Integer availableScore = realDeductScoreMap.get("availableScore");
                if (availableScore != null) {
                    realDeductAvailableScore += availableScore;
                }
                // 统计用
                scoreMainLogNew.setLimitTime(null);
                scoreMainLogNew.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);
            }
        }
        scoreMainLogNew.setScoreNumber(-(realDeductAvailableScore + realDeductLockedScore));
        scoreMainLogNew.setRemark("退货，扣除可用积分" + realDeductAvailableScore + "，扣除冻结积分" + realDeductLockedScore);
        scoreMainLogDao.updateScoreMainLog(memGuid, scoreMainLogNew);
        //用户要返回积分详细信息
        scoreOrderDetailDao.saveScoreOrderDetail(memGuid, scoreOrderDetailList);
    }

    /**
     * 处理生效积分
     */
    private Map<String, Integer> handleEffectScore(String memGuid, Integer scySeq, int scoreBack, Integer smlSeq) {
        Map<String, Integer> info = new HashMap<>();
        if (scoreBack > 0) {
            //scoreMember 加锁
            ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
            //scoreYear 加锁
            ScoreYear scoreYear = scoreYearDao.getScoreYearById(memGuid, scySeq);
            Integer availableScore = scoreYear.getAvailableScore();
            int realScore;
            if (availableScore >= scoreBack) {
                realScore = scoreBack;
            } else {
                realScore = availableScore;
            }

            if (realScore > 0) {
                scoreMemberDao.deductAvailableScore(memGuid, realScore);
                scoreYearDao.deductAvailableScore(memGuid, realScore, scySeq);
                ScoreYearLog scoreYearLogNew = new ScoreYearLog();
                scoreYearLogNew.setMemGuid(memGuid);
                scoreYearLogNew.setScoreConsume(realScore);
                scoreYearLogNew.setScoreGet(0);
                scoreYearLogNew.setScySeq(scySeq);
                scoreYearLogNew.setSmlSeq(smlSeq);
                //记录scoreYearLog日志
                scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLogNew);
            }
            info.put("availableScore", realScore);
            int restScore = scoreBack - realScore;
            if (restScore > 0) {

                int userAvailableScore = scoreMember.getAvailableScore() - realScore;
                if (restScore > userAvailableScore) {
                    restScore = userAvailableScore;
                }
                if (restScore > 0 && userAvailableScore > 0) {
                    deductAvailableScoreAlgorithm(memGuid, restScore, userAvailableScore, smlSeq);
                    info.put("availableScore", (realScore + restScore));
                }
            }
        }
        return info;
    }

    private Map<String, Integer> handleNotEffectScore(String memGuid, Integer scySeq, int scoreBack, Integer smlSeq) {

        Map<String, Integer> info = new HashMap<>();
        if (scoreBack > 0) {
            //scoreMember 加锁
            ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
            //scoreYear 加锁
            ScoreYear scoreYear = scoreYearDao.getScoreYearById(memGuid, scySeq);
            Integer lockedScore = scoreYear.getLockedScore();
            Integer realDeductLockedScore;
            if (lockedScore >= scoreBack) {
                realDeductLockedScore = scoreBack;
            } else {
                realDeductLockedScore = lockedScore;

            }
            //score_membe冻结的分数去掉。
            scoreMemberDao.deductLockedScore(memGuid, realDeductLockedScore);
            //score_year冻结的分数去掉。
            scoreYearDao.deductLockedScore(memGuid, scySeq, realDeductLockedScore);
            ScoreYearLog scoreYearLogNew = new ScoreYearLog();
            scoreYearLogNew.setMemGuid(memGuid);
            scoreYearLogNew.setScoreConsume(realDeductLockedScore);
            scoreYearLogNew.setScoreGet(0);
            scoreYearLogNew.setScySeq(scySeq);
            scoreYearLogNew.setSmlSeq(smlSeq);
            //记录scoreYearLog日志
            scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLogNew);

            info.put("lockedScore", realDeductLockedScore);
            Integer restScore = scoreBack - realDeductLockedScore;
            if (restScore > 0) {
                //扣除有效积分。
                int userAvailableScore = scoreMember.getAvailableScore();
                if (restScore > userAvailableScore) {
                    restScore = userAvailableScore;
                }
                if (restScore > 0) {
                    deductAvailableScoreAlgorithm(memGuid, restScore, userAvailableScore, smlSeq);
                    info.put("availableScore", restScore);
                }
            }
        }
        return info;
    }


    /**
     * 返还消费的积分
     */
    private void returnConsumeScoreDetail(Integer channelGet,
                                          Integer typeGet, String memGuid, String ogSeq, int getScoreTotal,
                                          List<ScoreOrderDetail> scoreOrderDetailList, ScoreMainLog scoreMainLogNew) {

        String remark = "退货";
        ScoreMainLog scoreMainLogConsume = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_CONSUME);
        if (scoreMainLogConsume == null) {
            log.error("返回消费积分时，未找到消费积分的记录。memGuid:" + memGuid + " ogSeq:" + ogSeq, "returnConsumeScoreDetail");
            return;
        }
        String rgSeq = scoreMainLogNew.getRgSeq();
        String ogNo = scoreMainLogNew.getOgNo();

        if (getScoreTotal > 0) {
            //进行加锁
            scoreMemberDao.getScoreMember(memGuid);
            //消费积分退回。
            scoreMemberDao.addScoreBecauseReturn(memGuid, getScoreTotal);
        }
        scoreMainLogNew.setChannel(channelGet);
        scoreMainLogNew.setScoreNumber(getScoreTotal);
        scoreMainLogNew.setRemark(remark + "，消费积分退回。");
        scoreMainLogNew.setActualTime(new Date());
        scoreMainLogNew.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        //记录scoreMainLog日志
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogNew);

        log.info("用户：" + memGuid + ",提交退货单rgSeq：" + rgSeq + ",返回消费的积分：" + getScoreTotal, "returnConsumeScoreDetail");


        //订单消费的详细列表
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("memGuid", memGuid);
        paramMap.put("ogSeq", ogSeq);
        paramMap.put("typesOfConsumeAndExchange", Constant.TYPES_OF_CONSUME_AND_EXCHANGE);
        //订单消费积分详细信息,包括订单消费 积分兑换和会员专享
        List<ScoreOrderDetail> orderConsumeList = scoreOrderDetailDao.getScoreOrderDetailListByParam(memGuid, paramMap);

        //消费退回的详细列表
        List<ScoreOrderDetail> orderGetList = scoreOrderDetailDao.getScoreOrderDetailList(memGuid, ogSeq, typeGet);

        List<ScoreOrderDetail> realList = new ArrayList<>();

        Map<Integer, Integer> returnScySeqScoreMap = new HashMap<>();
        Integer smlSeq = scoreMainLogNew.getSmlSeq();
        //在订单消费详细列表中查找与退货单相关的商品信息。
        for (ScoreOrderDetail returnOrderDetail : scoreOrderDetailList) {
            //返还多少积分
            Integer scoreGetReturn = returnOrderDetail.getScoreGet();
            //设置对应是scoreMainLog主键
            returnOrderDetail.setSmlSeq(smlSeq);
            //设置type类型
            returnOrderDetail.setType(typeGet);
            //返还0的不进行处理
            if (scoreGetReturn == 0) {
                returnOrderDetail.setScySeq(0);
                realList.add(returnOrderDetail);
                continue;
            }
            String olSeq = returnOrderDetail.getOlSeq();
            Map<Integer, Integer> scyToScore = new HashMap<>();
            //这个商品消费了scoreYear的多少积分
            for (ScoreOrderDetail sodConsume : orderConsumeList) {
                //找到对应的商品
                String olSeqConsume = sodConsume.getOlSeq();
                if (StringUtils.equals(olSeq, olSeqConsume)) {
                    //消费积分
                    Integer scoreConsume = sodConsume.getScoreConsume();
                    //scoreYear ID
                    Integer scySeq = sodConsume.getScySeq();
                    //商品消费对应scoreYear的积分
                    if (scyToScore.containsKey(scySeq)) {
                        scyToScore.put(scySeq, scyToScore.get(scySeq) + scoreConsume);
                    } else {
                        scyToScore.put(scySeq, scoreConsume);
                    }
                }

            }
            //减去已经退还的积分
            for (ScoreOrderDetail sodGet : orderGetList) {
                String olSeqGet = sodGet.getOlSeq();
                //找到对应的商品
                if (StringUtils.equals(olSeq, olSeqGet)) {
                    //已退还的积分
                    Integer scoreGet = sodGet.getScoreGet();
                    //对应的scoreYear ID
                    Integer scySeq = sodGet.getScySeq();
                    //计算对应的scoreYear的积分 还有多少没有退还\
                    Integer haveScore = scyToScore.get(scySeq);
                    if (haveScore == null) {
                        continue;
                    }
                    scyToScore.put(scySeq, haveScore - scoreGet);
                }

            }
            Set<Map.Entry<Integer, Integer>> entries = scyToScore.entrySet();
            //转换为List
            List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(entries);
            //进行从大到小的排序
            Collections.sort(entryList, new Comparator<Map.Entry<Integer, Integer>>() {
                @Override
                public int compare(Map.Entry<Integer, Integer> entryFirst, Map.Entry<Integer, Integer> entrySecond) {
                    return entrySecond.getValue() - entryFirst.getValue();
                }
            });
            //计算要返还的积分
            for (Map.Entry<Integer, Integer> entry : entryList) {
                //没退还的积分
                Integer score = entry.getValue();
                if (score == 0) {
                    continue;
                }
                //scoreYear ID
                Integer scySeq = entry.getKey();
                //如果退还的积分 <= score
                if (scoreGetReturn <= score) {
                    //复制对象
                    String toJSONString = JSONObject.toJSONString(returnOrderDetail);
                    ScoreOrderDetail sodCopy = JSONObject.parseObject(toJSONString, ScoreOrderDetail.class);
                    //赋值对应的scoreYear ID
                    sodCopy.setScySeq(scySeq);
                    //赋值退还的积分
                    sodCopy.setScoreGet(scoreGetReturn);
                    realList.add(sodCopy);
                    //对应的scoreYear 减掉 退还的积分
                    entry.setValue(score - scoreGetReturn);
                    //缓存到map对象中
                    Integer scySeqScore = returnScySeqScoreMap.get(scySeq);
                    if (scySeqScore == null) {
                        returnScySeqScoreMap.put(scySeq, scoreGetReturn);
                    } else {
                        returnScySeqScoreMap.put(scySeq, scySeqScore + scoreGetReturn);
                    }
                    break;
                } else {
                    //复制对象
                    String toJSONString = JSONObject.toJSONString(returnOrderDetail);
                    ScoreOrderDetail sodCopy = JSONObject.parseObject(toJSONString, ScoreOrderDetail.class);
                    //剩下多少积分没有退还
                    scoreGetReturn -= score;
                    //scoreYear ID
                    sodCopy.setScySeq(scySeq);
                    //退还的积分
                    sodCopy.setScoreGet(score);
                    realList.add(sodCopy);
                    entry.setValue(0);
                    //缓存到map中
                    Integer scySeqScore = returnScySeqScoreMap.get(scySeq);
                    if (scySeqScore == null) {
                        returnScySeqScoreMap.put(scySeq, score);
                    } else {
                        returnScySeqScoreMap.put(scySeq, scySeqScore + score);
                    }
                    if (scoreGetReturn == 0) {
                        break;
                    }
                }
            }
        }
        //返还积分
        int scoreYearBackTotalScore = 0;
        for (Map.Entry<Integer, Integer> entry : returnScySeqScoreMap.entrySet()) {
            Integer scySeq = entry.getKey();
            Integer score = entry.getValue();
            addScoreYear(remark, memGuid, ogSeq, rgSeq, ogNo, smlSeq, score, scySeq);
            scoreYearBackTotalScore += score;
        }

        if (getScoreTotal != scoreYearBackTotalScore) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "返还消费积分不一致。");
        }

        if (realList.size() > 0) {
            //保存返回消费积分详细信息
            scoreOrderDetailDao.saveScoreOrderDetail(memGuid, realList);
        } else {
            throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "生成返还用户消费积分详细信息发生错误，list的size为0。");
        }

    }

    private void addScoreYear(String remark, String memGuid, String ogSeq, String rgSeq, String ogNo, Integer smlSeq, Integer scoreGetReturn, Integer scySeq) {
        // 创建scoreYearLog日志对象
        ScoreYearLog scoreYearLogNew = new ScoreYearLog();
        // score_main_log表主键
        scoreYearLogNew.setSmlSeq(smlSeq);
        // 用户ID
        scoreYearLogNew.setMemGuid(memGuid);
        // 消费积分
        scoreYearLogNew.setScoreConsume(0);
        scoreYearLogNew.setScySeq(scySeq);
        scoreYearLogNew.setScoreGet(scoreGetReturn);
        scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLogNew);
        if (scoreGetReturn > 0) {
            // 得到对应的ScoreYear对象
            ScoreYear scoreYear = scoreYearDao.getScoreYearById(memGuid, scySeq);
            // 保存scoreYearLog日志
            Date dueTime = scoreYear.getDueTime();

            Calendar calendar = Calendar.getInstance();
            // 今年年份 如2015
            int thisYear = calendar.get(Calendar.YEAR);
            FastDateFormat sdfY = FastDateFormat.getInstance("yyyy");
            // 过期年份 如2014
            int dueTimeYear = Integer.parseInt(sdfY.format(dueTime));
            // 如果过期年份小于当前年份
            if (dueTimeYear < thisYear) {
                // 积分过期 返回积分直接失效
                returnScoreButExpired(memGuid, ogSeq, ogNo, rgSeq, remark, scySeq, scoreGetReturn, dueTimeYear);
                log.info("返还用户：" + memGuid + "积分" + scoreGetReturn + ",超过积分过期时间，积分过期。", "addScoreYear");
            } else {
                // 返回消费的积分
                scoreYearDao.addScoreById(memGuid, scoreGetReturn, scySeq);
            }
        }
    }

    private void returnScoreButExpired(String memGuid, String ogSeq, String ogNo,
                                       String rgSeq, String remark, Integer scySeq, int realBack, Integer dueTime) {
        //积分过期
        scoreMemberDao.addExpiredScore(memGuid, realBack);
        ScoreMainLog scoreMemberLogExpired = new ScoreMainLog();
        scoreMemberLogExpired.setChannel(Constant.SCORE_CHANNEL_SCORE_EXPIRED);
        scoreMemberLogExpired.setMemGuid(memGuid);
        scoreMemberLogExpired.setOgSeq(ogSeq);
        scoreMemberLogExpired.setRgSeq(rgSeq);
        scoreMemberLogExpired.setOgNo(ogNo);
        scoreMemberLogExpired.setRemark(remark + ",退还积分过期回收。");
        StringBuilder sb = new StringBuilder();
        sb.append(ogSeq).append("_");
        if (StringUtils.isNotEmpty(rgSeq)) {
            sb.append(rgSeq).append("_");
        }
        sb.append(scySeq).append("_").append(System.currentTimeMillis()).append("_").append(dueTime).append("1231").append("_").append(Constant.SCORE_CHANNEL_SCORE_EXPIRED);
        scoreMemberLogExpired.setUniqueId(sb.toString());
        scoreMemberLogExpired.setScoreNumber(-realBack);
        scoreMemberLogExpired.setCommentSeq(0);
        scoreMemberLogExpired.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        //记录日志
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMemberLogExpired);

        //会员年表 同步更新
        scoreYearDao.addExpiredScoreById(memGuid, realBack, scySeq);

        ScoreYearLog scorYearLogExpired = new ScoreYearLog();
        scorYearLogExpired.setMemGuid(memGuid);
        scorYearLogExpired.setScoreConsume(realBack);
        scorYearLogExpired.setScoreGet(0);
        scorYearLogExpired.setScySeq(scySeq);
        scorYearLogExpired.setSmlSeq(scoreMemberLogExpired.getSmlSeq());
        //记录日志
        scoreYearLogDao.saveScoreYearLog(memGuid, scorYearLogExpired);
    }


    /**
     * 订单详细信息入库
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void saveSubmitOrderDetail(String memGuid, OrderJsonVo orderJsonVo, Integer consumeScore) {
        //订单号
        String ogSeq = orderJsonVo.getOgSeq();
        //查询此订单消费了多少积分
        ScoreMainLog scoreMainLog;
        if (consumeScore != null && consumeScore == 0) {
            //入库scoreMainLog
            //记录消费积分日志 scoreMainLog
            scoreMainLog = new ScoreMainLog();
            scoreMainLog.setChannel(Constant.SCORE_CHANNEL_ORDER_CONSUME);
            scoreMainLog.setMemGuid(memGuid);
            scoreMainLog.setOgSeq(ogSeq);
            scoreMainLog.setOgNo(orderJsonVo.getOgNo());
            scoreMainLog.setRemark("订单消费");
            scoreMainLog.setRgSeq("");
            scoreMainLog.setRgNo("");
            scoreMainLog.setScoreNumber(0);
            scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
            scoreMainLog.setCommentSeq(0);
            scoreMainLog.setProvinceId(orderJsonVo.getProvinceId());
            scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
        } else {
            //查询此订单消费积分记录
            scoreMainLog = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_CONSUME);
            if (scoreMainLog == null) {
                throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "未找到订单消费信息，延迟处理。");
            }
        }
        //所有订单信息列表
        ArrayList<OrderDetail> allOrderList = new ArrayList<>();
        allOrderList.addAll(orderJsonVo.getSelfList());
        allOrderList.addAll(orderJsonVo.getMallList());
        //获得可以使用积分的商品
        //List<OrderDetail> filterOrderList = scoreFilter.getCanUseScoreDetail(allOrderList,orderJsonVo.getProvinceId());
        //平摊积分
        ScoreAverageAlgorithm.amorizate(allOrderList, Math.abs(scoreMainLog.getScoreNumber()));

        //处理订单详细和可获得的积分
        Map<String, Object> detailAndScore = buildDetailAndComputeScore(orderJsonVo);
        //总积分
        Integer totalScore = (Integer) detailAndScore.get("totalScore");
        //缓存订单获得积分记录
        cacheUtils.putGetScore(ogSeq, totalScore);

        @SuppressWarnings("unchecked")
        List<ScoreOrderDetail> allList = (List<ScoreOrderDetail>) detailAndScore.get("detailList");

        //订单购买的详细记录
        try {
            orderBuyScoreDetail(allList, orderJsonVo, totalScore);
        } catch (DuplicateKeyException e) {
            log.error("提交订单插入订单获得积分信息出现重复。忽略错误", "saveSubmitOrderDetail");
        }
        //订单消费信息详细记录
        scoreSplitAlgorithm(allList, orderJsonVo);

    }

    private void orderBuyScoreDetail(List<ScoreOrderDetail> allList, OrderJsonVo orderJsonVo, int totalScore) {
        String memGuid = orderJsonVo.getMemGuid();
        String ogSeq = orderJsonVo.getOgSeq();
        String ogNo = orderJsonVo.getOgNo();

        String uniqueId = ogSeq + "_" + Constant.SCORE_CHANNEL_ORDER_BUY;
        ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLogByUniqueId(memGuid, uniqueId);
        if (scoreMainLog != null) {
            return;
        }
        ScoreMainLog scoreMainLogGet = new ScoreMainLog();
        scoreMainLogGet.setChannel(Constant.SCORE_CHANNEL_ORDER_BUY);
        scoreMainLogGet.setMemGuid(memGuid);
        scoreMainLogGet.setOgSeq(ogSeq);
        scoreMainLogGet.setOgNo(ogNo);
        scoreMainLogGet.setRemark("订单购买");
        scoreMainLogGet.setRgSeq("");
        scoreMainLogGet.setRgNo("");
        scoreMainLogGet.setScoreNumber(totalScore);
        scoreMainLogGet.setCommentSeq(0);
        //无效状态，当付款之后设置有效状态
        scoreMainLogGet.setStatus(Constant.SCORE_MAIN_LOG_STATUS_INVAILD);
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogGet);
        log.info("用户:" + memGuid + "提交订单ogSeq:" + ogSeq + "，获得积分:" + totalScore + ",状态status：0", "orderBuyScoreDetail");

        Integer smlSeq = scoreMainLogGet.getSmlSeq();
        for (ScoreOrderDetail sod : allList) {
            sod.setSmlSeq(smlSeq);
            //当付款之后更新为对应的ScoreYear ID
            sod.setScySeq(0);
            //积分获得的类型。积分兑换 会员专享不获得积分，但也要设置一条类型为购买获得的记录。否则退货校验时会有问题。
            sod.setType(Constant.SCORE_ORDER_DETAIL_TYPE_BUY);

        }
        scoreOrderDetailDao.saveScoreOrderDetail(memGuid, allList);

    }


    /**
     * 处理订单详细信息
     */
    @Override
    public Map<String, Object> buildDetailAndComputeScore(OrderJsonVo orderJsonVo) {

        //所有订单信息
        List<OrderJsonVo.OrderDetail> orderDetailList = new ArrayList<>();

        List<OrderDetail> mallList = orderJsonVo.getMallList();
        if (mallList != null && mallList.size() > 0) {
            orderDetailList.addAll(mallList);
        }
        List<OrderDetail> selfList = orderJsonVo.getSelfList();
        if (selfList != null && selfList.size() > 0) {
            orderDetailList.addAll(selfList);
        }

        //商品比例信息
        JSONObject billsByItNos = new JSONObject();
        //如果是分销平台就是5
        Integer sourceMode = orderJsonVo.getSourceMode();
        //如果是电子屏订单 就是17
        String siteMode = orderJsonVo.getSiteMode();
        String memGuid = orderJsonVo.getMemGuid();
        //组团ID
        String groupId = orderJsonVo.getGroupId();
        //订单流水号
        String ogSeq = orderJsonVo.getOgSeq();
        if (Objects.equals(sourceMode, Constant.DISTRIBUTION_PLATFORM)
                || StringUtils.equals(siteMode, Constant.ELECTRONIC_SCREEN)
                || cacheUtils.isCompanyUser(memGuid)
                || StringUtils.isNotEmpty(groupId)
                || Integer.valueOf(1).equals(orderJsonVo.getVirtual())) {
            log.info("用户是企业用户或订单是分销平台或电子屏平台或组团订单或虚拟订单，不给积分。sourceMode="
                    + sourceMode + "siteMode=" + siteMode +
                    " memGuid=" + memGuid + " ogSeq=" + ogSeq + " groupId=" + groupId + ",virtual=" + orderJsonVo.getVirtual(), "buildDetailAndComputeScore");
        } else {
            //传入itNo和cpSeq查询比例
            HashMap<String, String> itNoMapToCpSeq = new HashMap<>();
            for (OrderJsonVo.OrderDetail order : orderDetailList) {
                itNoMapToCpSeq.put(order.getItNo(), order.getCpSeq());
            }
            if (itNoMapToCpSeq.size() > 0) {
                //取比例
                billsByItNos = scoreGetBillDao.getBillsInfo(itNoMapToCpSeq, false);
            }
        }

        int totalScore = 0;
        //用户订单积分详细信息
        List<ScoreOrderDetail> scoreOrderDetailList = new ArrayList<>();
        String ogNo = orderJsonVo.getOgNo();
        //门店信息
        Map<String, StoreInfoVo> storeInfoVoMap = scoreGetStoreInfoDao.getStoreNoByOgSeq(ogSeq, memGuid);
        for (OrderJsonVo.OrderDetail order : orderDetailList) {
            ScoreOrderDetail scoreOrderDetail = new ScoreOrderDetail();
            //会员ID
            scoreOrderDetail.setMemGuid(memGuid);
            //订单流水号
            scoreOrderDetail.setOgSeq(ogSeq);
            //子订单号
            scoreOrderDetail.setOgsSeq(order.getOgsSeq());
            //订单号
            scoreOrderDetail.setOgNo(ogNo);
            //平台来源
            scoreOrderDetail.setSourceMode(sourceMode);
            scoreOrderDetail.setSiteMode(siteMode);
            scoreOrderDetail.setRpSeq("");
            scoreOrderDetail.setOlSeq(order.getOlSeq());
            scoreOrderDetail.setRgSeq("");
            scoreOrderDetail.setRgNo("");
            scoreOrderDetail.setRlSeq("");
            scoreOrderDetail.setOgsSeq(order.getOgsSeq());
            String sellerNo = order.getSellerNo();
            scoreOrderDetail.setSellerNo(sellerNo);
            scoreOrderDetail.setOlSeq(order.getOlSeq());
            scoreOrderDetail.setQuantity(order.getQuantity());
            scoreOrderDetail.setPackageNo(order.getPackageNo());
            scoreOrderDetail.setKind(order.getKind());
            scoreOrderDetail.setBuyMode(order.getBuyMode());
            scoreOrderDetail.setPromotionGrade(order.getSsmGrade());

            //商品ID
            String itNo = order.getItNo();
            scoreOrderDetail.setItNo(itNo);

            //门店信息
            StoreInfoVo storeInfoVo = storeInfoVoMap.get(itNo);
            if (storeInfoVo != null) {
                scoreOrderDetail.setSellerNo(storeInfoVo.getSupId());
                scoreOrderDetail.setStoreNo(storeInfoVo.getStoreNo());
            }

            //商品支付金额(只包括算积分的金额)
            BigDecimal realPay = order.getRealPay();
            scoreOrderDetail.setMoney(realPay);

            BigDecimal rate;
            if (billsByItNos.get(itNo) != null) {
                rate = billsByItNos.getJSONObject(itNo).getBigDecimal(ScoreGetBillDao.BILL_KEY);
            } else {
                rate = new BigDecimal(0);
            }
            String buyMode = order.getBuyMode();//积分兑换商品
            int oversea = order.getOversea();//1是跨境订单
            String promotionGrade = order.getSsmGrade();//促销等级
            String groupBatchNo = order.getGroupBatchNo();//团购批次号
            boolean canGetScore = rate != null
                    && oversea != Constant.IS_OVERSEA
                    && !StringUtils.equals(buyMode, Constant.BUY_MODE_OF_SCORE_EXCHANGE)
                    && !StringUtils.equals(buyMode, Constant.BUY_MODE_OF_EXCLUSIVE)
                    && !StringUtils.equals(promotionGrade, Constant.PROMOTION_GRADE_OF_1)
                    && !StringUtils.equals(promotionGrade, Constant.PROMOTION_GRADE_OF_3)
                    && !StringUtils.equals(promotionGrade, Constant.PROMOTION_GRADE_OF_4)
                    && StringUtils.isEmpty(groupBatchNo);
            if (canGetScore) {
                //计算可获得的积分
                int scoreGet = (int) Math.floor(realPay.multiply(rate).doubleValue());
                totalScore += scoreGet;
                scoreOrderDetail.setBill(rate);
                scoreOrderDetail.setScoreGet(scoreGet);
            } else {
                scoreOrderDetail.setBill(BigDecimal.valueOf(0.0000));
                scoreOrderDetail.setScoreGet(0);
            }
            if (StringUtils.isEmpty(sellerNo)) {
                scoreOrderDetail.setSourceType(Constant.SELFTYPE);
            } else {
                scoreOrderDetail.setSourceType(Constant.MALLTYPE);
            }
            scoreOrderDetail.setScoreConsume(order.getConsumeScore());
            scoreOrderDetailList.add(scoreOrderDetail);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("totalScore", totalScore);
        map.put("detailList", scoreOrderDetailList);
        return map;
    }

    @Override
    public int computeScore(OrderJsonVo orderJsonVo) {
        //商品比例信息
        JSONObject billsByItNos = new JSONObject();
        //如果是分销平台就是5
        Integer sourceMode = orderJsonVo.getSourceMode();
        //如果是电子屏订单 就是17
        String siteMode = orderJsonVo.getSiteMode();
        String memGuid = orderJsonVo.getMemGuid();
        //组团ID
        String groupId = orderJsonVo.getGroupId();
        //订单流水号
        String ogSeq = orderJsonVo.getOgSeq();
        if (Objects.equals(sourceMode, Constant.DISTRIBUTION_PLATFORM)
                || StringUtils.equals(siteMode, Constant.ELECTRONIC_SCREEN)
                || cacheUtils.isCompanyUser(memGuid)
                || StringUtils.isNotEmpty(groupId)
                || Integer.valueOf(1).equals(orderJsonVo.getVirtual())) {
            log.info("用户是企业用户或订单是分销平台或电子屏平台或组团订单或虚拟订单，不给积分。sourceMode="
                    + sourceMode + "siteMode=" + siteMode +
                    " memGuid=" + memGuid + " ogSeq=" + ogSeq + " groupId=" + groupId + ",virtual=" + orderJsonVo.getVirtual(), "computeScore");
            return 0;
        } else {
            //所有订单信息
            List<OrderJsonVo.OrderDetail> orderDetailList = new ArrayList<>();

            List<OrderDetail> mallList = orderJsonVo.getMallList();
            if (mallList != null && mallList.size() > 0) {
                orderDetailList.addAll(mallList);
            }
            List<OrderDetail> selfList = orderJsonVo.getSelfList();
            if (selfList != null && selfList.size() > 0) {
                orderDetailList.addAll(selfList);
            }
            //传入itNo和cpSeq查询比例
            HashMap<String, String> itNoMapToCpSeq = new HashMap<>();
            for (OrderJsonVo.OrderDetail order : orderDetailList) {
                itNoMapToCpSeq.put(order.getItNo(), order.getCpSeq());
            }
            if (itNoMapToCpSeq.size() > 0) {
                //取比例
                billsByItNos = scoreGetBillDao.getBillsInfo(itNoMapToCpSeq, true);
            }

            int totalScore = 0;

            for (OrderJsonVo.OrderDetail order : orderDetailList) {
                String itNo = order.getItNo();
                BigDecimal realPay = order.getRealPay();
                BigDecimal rate;
                if (billsByItNos.get(itNo) != null) {
                    rate = billsByItNos.getJSONObject(itNo).getBigDecimal(ScoreGetBillDao.BILL_KEY);
                } else {
                    rate = new BigDecimal(0);
                }
                String buyMode = order.getBuyMode();//积分兑换商品
                int oversea = order.getOversea();//1是跨境订单
                String promotionGrade = order.getSsmGrade();//促销等级
                String groupBatchNo = order.getGroupBatchNo();//团购批次号
                boolean canGetScore = rate != null
                        && oversea != Constant.IS_OVERSEA
                        && !StringUtils.equals(buyMode, Constant.BUY_MODE_OF_SCORE_EXCHANGE)
                        && !StringUtils.equals(buyMode, Constant.BUY_MODE_OF_EXCLUSIVE)
                        && !StringUtils.equals(promotionGrade, Constant.PROMOTION_GRADE_OF_1)
                        && !StringUtils.equals(promotionGrade, Constant.PROMOTION_GRADE_OF_3)
                        && !StringUtils.equals(promotionGrade, Constant.PROMOTION_GRADE_OF_4)
                        && StringUtils.isEmpty(groupBatchNo);
                if (canGetScore) {
                    //计算可获得的积分
                    int scoreGet = (int) Math.floor(realPay.multiply(rate).doubleValue());
                    totalScore += scoreGet;
                }
            }
            return totalScore;
        }
    }

    /**
     * 积分平摊算法
     */
    private void scoreSplitAlgorithm(List<ScoreOrderDetail> scoreOrderDetailList, OrderJsonVo orderJsonVo) {

        /*
         * 按消费积分从大到小排序
         */
        Collections.sort(scoreOrderDetailList, new Comparator<ScoreOrderDetail>() {
            @Override
            public int compare(ScoreOrderDetail first, ScoreOrderDetail second) {
                return second.getScoreConsume() - first.getScoreConsume();
            }
        });
        String memGuid = orderJsonVo.getMemGuid();
        String ogSeq = orderJsonVo.getOgSeq();
        //订单消费记录
        ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_CONSUME);

        //查看退货详细
        Integer countDetail = scoreOrderDetailDao.getScoreOrderDetailBySmlSeq(memGuid, scoreMainLog.getSmlSeq(), Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CONSUME);
        if (countDetail != null && countDetail > 0) {
            log.error("重复数据。忽略");
            return;
        }
        //对应的消费日志
        Integer smlSeq = scoreMainLog.getSmlSeq();
        List<ScoreYearLog> scoreYearLogList = scoreYearLogDao.getScoreYearLogByLM(smlSeq, memGuid);

        //消费的积分从大到小排序
        Comparator<ScoreYearLog> yearLogComparator = new Comparator<ScoreYearLog>() {
            @Override
            public int compare(ScoreYearLog first, ScoreYearLog second) {
                return second.getScoreConsume() - first.getScoreConsume();
            }
        };

        List<ScoreOrderDetail> realList = new ArrayList<>();
        //统计对应的socre Year 分摊的积分
        int yearScoreConsumeTotal = 0;
        for (ScoreOrderDetail sod : scoreOrderDetailList) {
            sod.setSmlSeq(smlSeq);
            if (StringUtils.equals(sod.getBuyMode(), Constant.BUY_MODE_OF_SCORE_EXCHANGE)) {
                sod.setType(Constant.SCORE_CHANNEL_EXCHANGE_GOODS_COST);
            } else if (StringUtils.equals(sod.getBuyMode(), Constant.BUY_MODE_OF_EXCLUSIVE)) {
                sod.setType(Constant.SCORE_CHANNEL_EXCLUSIVE_COST);
            } else {
                sod.setType(Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CONSUME);
            }
            int scoreConsume = sod.getScoreConsume();
            //消费是0的情况
            if (scoreConsume == 0) {
                sod.setScySeq(0);
                realList.add(sod);
                continue;
            }
            //排序
            Collections.sort(scoreYearLogList, yearLogComparator);

            for (ScoreYearLog scoreYearLog : scoreYearLogList) {
                //scoreYear 消费的积分
                Integer scoreConsumeYear = scoreYearLog.getScoreConsume();
                //对应的scoreYear ID
                Integer scySeq = scoreYearLog.getScySeq();
                //如果足够扣除
                if (scoreConsume <= scoreConsumeYear) {
                    sod.setScySeq(scySeq);
                    sod.setScoreConsume(scoreConsume);
                    yearScoreConsumeTotal += scoreConsume;
                    realList.add(sod);
                    scoreConsumeYear -= scoreConsume;
                    scoreYearLog.setScoreConsume(scoreConsumeYear);
                    break;
                } else {
                    //复制一份对象
                    String sodJson = JSONObject.toJSONString(sod);
                    ScoreOrderDetail sodCopy = JSONObject.parseObject(sodJson, ScoreOrderDetail.class);
                    sodCopy.setScySeq(scySeq);
                    sodCopy.setScoreConsume(scoreConsumeYear);
                    yearScoreConsumeTotal += scoreConsumeYear;
                    realList.add(sodCopy);
                    scoreConsume -= scoreConsumeYear;
                    scoreYearLog.setScoreConsume(0);
                    if (scoreConsume == 0) {
                        break;
                    }
                }
            }

        }
        int scoreNumber = Math.abs(scoreMainLog.getScoreNumber());
        if (scoreNumber != yearScoreConsumeTotal) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "积分不一致，消费积分分摊失败。");
        }
        if (realList.size() == 0) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "realList.size() == 0,消费积分分摊失败。");
        }
        scoreOrderDetailDao.saveScoreOrderDetail(memGuid, realList);
    }

    /**
     * 提交订单扣积分
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void deductOrderComsumeScore(String memGuid, Integer consumeScore,
                                        String ogSeq, String ogNo, String provinceId) {

        if (consumeScore > 0) {
            //查询用户积分信息 scoreMember for update 加锁。
            //ScoreMember中的可用积分 在1月1日查询时可能不准确。因为当天会有积分过期job在跑。
            ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);

            //用户的总可用积分
            Integer userAvailableScore = scoreYearDao.getAvaliableScoreNoCache(memGuid);
            if (userAvailableScore == null) {
                throw new ScoreException(ResultCode.RESULT_AVAILABLE_SCORE_NOT_ENOUGH, "可用积分不足");
            }

            if (userAvailableScore < consumeScore) {
                cacheUtils.removeAvaliableScore(memGuid);
                throw new ScoreException(ResultCode.RESULT_AVAILABLE_SCORE_NOT_ENOUGH, "可用积分不足");
            }

            //记录消费积分日志 scoreMainLog
            ScoreMainLog scoreMainLog = new ScoreMainLog();
            scoreMainLog.setChannel(Constant.SCORE_CHANNEL_ORDER_CONSUME);
            scoreMainLog.setMemGuid(memGuid);
            scoreMainLog.setOgSeq(ogSeq);
            scoreMainLog.setOgNo(ogNo);
            scoreMainLog.setRemark("订单消费");
            scoreMainLog.setRgSeq("");
            scoreMainLog.setRgNo("");
            scoreMainLog.setScoreNumber(-consumeScore);
            scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
            scoreMainLog.setCommentSeq(0);
            scoreMainLog.setProvinceId(provinceId);
            scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);

            Integer smlSeq = scoreMainLog.getSmlSeq();
            deductAvailableScoreAlgorithm(memGuid, consumeScore, userAvailableScore, smlSeq);
        }
    }

    /**
     * 扣除可用积分算法
     */
    private void deductAvailableScoreAlgorithm(String memGuid, Integer consumeScore, int userAvailableScore, Integer smlSeq) {
        if (consumeScore == 0 || userAvailableScore == 0) {
            return;
        }
        //减少可用积分
        scoreMemberDao.deductAvailableScore(memGuid, consumeScore);
        //计算扣除自营多少积分和扣除商城多少积分
        //取自营的积分
        //自营总积分
        int selfTotalScore = 0;
        List<ScoreYear> scoreYearSelfList = scoreYearDao.getScoreYearSelf(memGuid);
        for (ScoreYear scoreYear : scoreYearSelfList) {
            selfTotalScore += scoreYear.getAvailableScore();
        }
        //计算消费自营多少积分。
        BigInteger multiply = new BigInteger(consumeScore + "").multiply(new BigInteger(selfTotalScore + ""));
        int consumeSelfScore = multiply.divide(new BigInteger(userAvailableScore + "")).intValue();
        //有余数则+1
        if (multiply.mod(new BigInteger(userAvailableScore + "")).compareTo(BigInteger.ZERO) > 0) {
            consumeSelfScore++;
        }
        int consumeTotalScore = 0;
        //扣除自营的积分
        if (consumeSelfScore > 0) {
            consumeTotalScore += consumeSelfScore;
            deductScoreAlgorithm(memGuid, smlSeq, scoreYearSelfList, consumeSelfScore);
        }
        //计算消费商城多少积分。
        int consumeMallScore = consumeScore - consumeSelfScore;
        if (consumeMallScore > 0) {
            //取出各个商家的积分信息 从大到小排序
            List<Map<String, Object>> scoreYearMallList = scoreYearDao.getScoreYearMall(memGuid);
            for (Map<String, Object> map : scoreYearMallList) {
                //对应商家编号和积分
                Integer scoreTotal = ((BigDecimal) map.get("scoreTotal")).intValue();
                String sellerNo = (String) map.get("sellerNo");
                //查询用户对应商家的积分信息 先按照积分类型排序（先扣除评论的，再扣除订单购买的。）再按过期时间排序
                List<ScoreYear> scoreYearList = scoreYearDao.getScoreYearBySerrlerNo(memGuid, sellerNo);
                if (scoreTotal >= consumeMallScore) {
                    //已经扣除所有的积分了。退出循环。
                    consumeTotalScore += consumeMallScore;
                    deductScoreAlgorithm(memGuid, smlSeq, scoreYearList, consumeMallScore);
                    break;
                } else {
                    consumeTotalScore += scoreTotal;
                    deductScoreAlgorithm(memGuid, smlSeq, scoreYearList, scoreTotal);
                    consumeMallScore -= scoreTotal;
                }
            }
        }
        if (consumeScore != consumeTotalScore) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "扣除积分异常。");
        }

    }


    /**
     * 扣除scoreYear积分的算法
     */
    private void deductScoreAlgorithm(String memGuid, Integer smlSeq, List<ScoreYear> scoreYearList, int consumeScore) {
        if (consumeScore == 0) {
            return;
        }
        for (ScoreYear scoreYear : scoreYearList) {

            if (consumeScore == 0) {
                break;
            }
            int availableScore = scoreYear.getAvailableScore();
            int realComsumeScore;
            if (availableScore >= consumeScore) {
                scoreYearDao.deductAvailableScore(memGuid, consumeScore, scoreYear.getScySeq());
                realComsumeScore = consumeScore;
                consumeScore = 0;
            } else {
                scoreYearDao.deductAvailableScore(memGuid, availableScore, scoreYear.getScySeq());
                realComsumeScore = availableScore;
                consumeScore -= availableScore;
            }
            //记录日志
            ScoreYearLog scoreYearLog = new ScoreYearLog();
            //设置score_main_log表主键
            scoreYearLog.setSmlSeq(smlSeq);
            //设置用户ID
            scoreYearLog.setMemGuid(memGuid);
            //设置获得积分
            scoreYearLog.setScoreGet(0);
            //score_year表主键
            scoreYearLog.setScySeq(scoreYear.getScySeq());
            //消费、失去积分
            scoreYearLog.setScoreConsume(realComsumeScore);
            //记录日志
            scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLog);

        }
    }

    /**
     * 添加自营立即生效积分
     */
    @Override
    public void addSelfAvailableScore(String memGuid, Date insTime, Integer getScore, Integer smlSeq) {
        //保存或更新score_member
        //加锁
        ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
        if (scoreMember != null) {
            //更新
            scoreMemberDao.updateAvailableScoreMember(memGuid, getScore);
        } else {
            scoreMemberDao.saveAvailableScoreMember(memGuid, getScore);
        }
        //保存或更新score_year
        ScoreYear scoreYear = scoreYearDao.getScoreYear(memGuid, insTime);
        Integer scySeq;
        if (scoreYear != null) {
            scySeq = scoreYear.getScySeq();
            scoreYearDao.updateAvailableScoreYear(memGuid, scySeq, getScore);
        } else {
            scoreYear = new ScoreYear();
            scoreYear.setTotalScore(getScore);
            scoreYear.setAvailableScore(getScore);
            scoreYear.setLockedScore(0);
            scoreYear.setDueTime(insTime);
            scoreYear.setMemGuid(memGuid);
            scoreYear.setScoreType(Constant.SELF_SCORE_TYPE);
            scoreYear.setSourceType(Constant.SELFTYPE);
            scoreYear.setSellerNo("");
            scoreYearDao.saveScoreYear(memGuid, scoreYear);
            scySeq = scoreYear.getScySeq();
        }
        //保存score_year_log
        ScoreYearLog scoreYearLog = new ScoreYearLog();
        scoreYearLog.setMemGuid(memGuid);
        scoreYearLog.setScoreConsume(0);
        scoreYearLog.setScoreGet(getScore);
        scoreYearLog.setSmlSeq(smlSeq);
        scoreYearLog.setScySeq(scySeq);
        scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLog);
    }


    /**
     * 回滚积分
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public boolean rollbackScore(String memGuid, String ogSeq, String ogNo) {
        //消费积分的一定有scoreMainLog
        ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_CONSUME);
        if (StringUtils.isEmpty(ogNo) && scoreMainLog != null) {
            ogNo = scoreMainLog.getOgNo();
        }
        if (StringUtils.isNotEmpty(ogNo)) {
            //查询缓存
            String cacheData = cacheUtils.getCacheData(ogNo + "_error");
            Boolean errorStatus;
            if (StringUtils.isBlank(cacheData)) {
                //由于时间差问题根据接口查询的订单状态不准确 订单的回滚依赖于订单组发送的订单状态kafka消息
                //errorStatus = scoreCheckOrderStatusDao.getOrderSubmitErrorStatus(ogNo);
                //cacheUtils.putCache(ogNo + "_error", 60 * 10, errorStatus);
                return false;
            } else {
                errorStatus = Boolean.parseBoolean(cacheData);
            }
            if (errorStatus) {
                //回滚积分
                if (scoreMainLog != null) {
                    rollbackScoreByScoreMainLog(memGuid, scoreMainLog);
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void rollbackScoreDirect(String memGuid, String ogSeq) {
        //消费积分的一定有scoreMainLog
        ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_CONSUME);
        //回滚积分
        if (scoreMainLog != null) {
            rollbackScoreByScoreMainLog(memGuid, scoreMainLog);
        }
    }

    /**
     * 回滚积分,用于兑换券和积分抽奖得商品失败时
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Integer rollbackExChangeVoucherScore(String memGuid, Integer smlSeq) {
        ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLogById(memGuid, smlSeq);
        if (scoreMainLog == null || (!Objects.equals(scoreMainLog.getChannel(), Constant.SCORE_CHANNEL_EXCHANGE_VOUCHER_COST)
                && !Objects.equals(scoreMainLog.getChannel(), Constant.SCORE_CHANNEL_RAFFLE_COST)
                && !Objects.equals(scoreMainLog.getChannel(), Constant.SCORE_CHANNEL_RAFFLE_GIVE))) {
            throw new ScoreException(ResultCode.RESULT_ROLL_BACK_BUT_NO_EXCHANGE_CONSUME_LOG, "按smlSeq找不到积分的记录");
        }
        //回滚积分
        rollbackScoreByScoreMainLog(memGuid, scoreMainLog);
        return scoreMainLog.getScoreNumber();
    }

    private void rollbackScoreByScoreMainLog(String memGuid, ScoreMainLog scoreMainLog) {
        int smlSeq = scoreMainLog.getSmlSeq();
        int score = Math.abs(scoreMainLog.getScoreNumber());
        if (score > 0) {
            //得到scoreYearLog
            List<ScoreYearLog> scoreYearLogs = scoreYearLogDao.getScoreYearLogByLM(smlSeq, memGuid);
            Map<Integer, Integer> backScoreMap = new HashMap<>();
            int totalBackScore = 0;
            for (ScoreYearLog scoreYearLog : scoreYearLogs) {
                Integer scySeq = scoreYearLog.getScySeq();
                Integer scoreConsume = scoreYearLog.getScoreConsume();
                Integer tempScore = backScoreMap.get(scySeq);
                if (tempScore == null) {
                    backScoreMap.put(scySeq, scoreConsume);
                } else {
                    backScoreMap.put(scySeq, tempScore + scoreConsume);
                }
                totalBackScore += scoreConsume;
            }
            if (totalBackScore != score) {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "回滚积分SocreYear和ScoreMember不相等。");
            }
            //删除scoreMainLog
            scoreMainLogDao.deleteScoreMainLogById(memGuid, smlSeq);
            //删除scoreYearLog
            scoreYearLogDao.deleteScoreYearLogBySmlSeq(memGuid, smlSeq);
            //加锁
            scoreMemberDao.getScoreMember(memGuid);
            //加scoreMember积分
            scoreMemberDao.addScoreBecauseReturn(memGuid, score);
            //加scoreYear积分
            for (Integer scySeq : backScoreMap.keySet()) {
                Integer backScore = backScoreMap.get(scySeq);
                scoreYearDao.addScoreById(memGuid, backScore, scySeq);
            }
        }
    }


    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void processPkadScore(String memGuid, String data, Pkad pkad) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        String mrstId = jsonObj.getString("mrst_id");//主键编号(去重)
        String membId = jsonObj.getString("memb_id");//会员id（去重）
        String membGradef = jsonObj.getString("memb_grade_f");//权益生效时间（去重）
        Integer mrdfPoint = jsonObj.getInteger("mrdf_point");//积分
        if (memGuid == null || !membId.equals(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "memb_id 参数错误");
        }
        if (mrstId == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "mrst_id 不能为 null");
        }
        if (membGradef == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memb_grade_f 不能为 null");
        }
        String pkadType = pkad.getPkadType();
        if (!"1".equals(pkadType) && !"2".equals(pkadType) && !"3".equals(pkadType) && !"4".equals(pkadType)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_ILLEGAL_EXCEPTION, "pkad_type 参数 错误");
        }
        if (mrdfPoint == null) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "mrdf_point 不能为 null");
        }

        FastDateFormat sdf = FastDateFormat.getInstance("yyyyMMdd");
        //生效日期dEfff
        String FTimeStr = jsonObj.getString("d_eff_f");
        Date limitTime;
        Date dueTime;
        if (StringUtils.isBlank(FTimeStr)) {
            FTimeStr = sdf.format(new Date());
        }
        try {
            limitTime = sdf.parse(FTimeStr);
        } catch (ParseException e) {
            throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "时间转换异常。", e);
        }

        //计算失效日
        Calendar instance = Calendar.getInstance();
        instance.setTime(limitTime);
        instance.add(Calendar.YEAR, 1);
        instance.set(Calendar.MONTH, Calendar.DECEMBER);
        instance.set(Calendar.DATE, 31);
        dueTime = instance.getTime();
        ScoreMainLog scoreMainLog = new ScoreMainLog();
        scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLog.setOgSeq("");
        scoreMainLog.setOgNo("");
        scoreMainLog.setRgSeq("");
        scoreMainLog.setRgNo("");
        scoreMainLog.setLimitTime(limitTime);
        scoreMainLog.setEndTime(dueTime);
        scoreMainLog.setCommentSeq(0);
        scoreMainLog.setMemGuid(memGuid);
        scoreMainLog.setGeneralId(mrstId + "_" + membGradef);
        if (Objects.equals(pkadType, String.valueOf(Constant.CRM_SCORE_GIVE))) {
            StringBuilder uniIdSB = new StringBuilder(membId).append("_").append(mrstId).append("_").append(membGradef).append("_").append(Constant.SCORE_CHANNEL_PKAD_GIVE);
            String uniId = uniIdSB.toString();
            ScoreMainLog checkedScoreMainLog = scoreMainLogDao.getScoreMainLogByUniqueId(membId, uniId);
            if (checkedScoreMainLog != null) {
                log.info("RequestNo:" + HttpRequestUtils.getRequestNo() + " , 领取礼包时重复领取积分， uniId : " + uniId);
                return;
            }
            scoreMainLog.setChannel(Constant.SCORE_CHANNEL_PKAD_GIVE);
            scoreMainLog.setScoreNumber(mrdfPoint);
            //赠送积分
            if (new Date().after(limitTime)) {
                //生效积分
                if (!StringUtils.isBlank(pkad.getMrstUi()) && ConstantMrst.MRSTUIList.contains(pkad.getMrstUi())) {
                    scoreMainLog.setRemark(ConstantMrst.GET_MRSTUI_DESC.get(pkad.getMrstUi()));
                }
                // scoreMainLog.setRemark("礼包赠送可用积分");
                scoreMainLog.setActualTime(new Date());
                scoreMainLog.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);
                scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
                addSelfAvailableScore(memGuid, limitTime, mrdfPoint, scoreMainLog.getSmlSeq());
            } else {
                //冻结积分
                scoreMainLog.setRemark("礼包赠送冻结积分");
                scoreMainLog.setActualTime(new Date());
                scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
                addSelfLockedScore(memGuid, limitTime, mrdfPoint, scoreMainLog.getSmlSeq());
            }
        } else if (Objects.equals(pkadType, String.valueOf(Constant.CRM_SCORE_RECOVER))) {
            //加锁 为了保持加锁的顺序
            scoreMemberDao.getScoreMember(memGuid);
            //回收积分
            List<ScoreYear> scoreYearSelf = scoreYearDao.getScoreYearSelf(memGuid);
            int availableScore = 0;
            for (ScoreYear scoreYear : scoreYearSelf) {
                availableScore += scoreYear.getAvailableScore();
            }
            if (mrdfPoint > availableScore) {
                mrdfPoint = availableScore;
            }
            scoreMainLog.setRemark("礼包回收积分" + mrdfPoint);
            scoreMainLog.setChannel(Constant.SCORE_CHANNEL_PKAD_RECOVER);
            scoreMainLog.setScoreNumber(-mrdfPoint);
            scoreMainLog.setActualTime(new Date());
            // 统计用
            scoreMainLog.setLimitTime(null);
            scoreMainLog.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);
            scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
            if (mrdfPoint > 0) {
                scoreMemberDao.deductAvailableScore(memGuid, mrdfPoint);
                deductScoreAlgorithm(memGuid, scoreMainLog.getSmlSeq(), scoreYearSelf, mrdfPoint);
            }
        }
    }


    /**
     * 即时扣积分
     *
     * @return saveMailLog记录ID
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public Integer deductScoreImmediately(String memGuid, String remark,
                                          Integer channel, Integer consumeScore, String ogSeq, String ogNo, String provinceId) {
        //查询用户积分信息 scoreMember for update 加锁
        ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
        //检测用户是否有足够的积分可用
        if (scoreMember == null && consumeScore > 0) {
            cacheUtils.removeAvaliableScore(memGuid);
            throw new ScoreException(ResultCode.RESULT_AVAILABLE_SCORE_NOT_ENOUGH, "可用积分不足,consumeScore=" + consumeScore + ",availablescore=" + 0);
        }
        if (scoreMember != null && scoreMember.getAvailableScore() < consumeScore) {
            cacheUtils.removeAvaliableScore(memGuid);
            throw new ScoreException(ResultCode.RESULT_AVAILABLE_SCORE_NOT_ENOUGH, "可用积分不足,consumeScore=" + consumeScore + ",availablescore=" + scoreMember.getAvailableScore());
        }
        log.info("用户:" + memGuid + "，扣除可用积分:" + consumeScore + ",channel=" + channel + "remark" + remark, "deductScoreImmediately");
        //记录消费积分日志 scoreMainLog
        ScoreMainLog scoreMainLog = new ScoreMainLog();
        scoreMainLog.setChannel(channel);
        scoreMainLog.setMemGuid(memGuid);
        scoreMainLog.setOgSeq(ogSeq);
        scoreMainLog.setOgNo(ogNo);
        scoreMainLog.setRemark(remark);
        scoreMainLog.setRgSeq("");
        scoreMainLog.setRgNo("");
        scoreMainLog.setScoreNumber(-consumeScore);
        scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLog.setCommentSeq(0);
        scoreMainLog.setProvinceId(provinceId);
        try {
            scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
        } catch (DuplicateKeyException e) {
            log.error("重复提交数据,scoreMainLog=" + scoreMainLog, "deductScoreImmediately");
            throw new ScoreException(ResultCode.RESULT_REPEAT_SUBMIT, "重复提交数据");
        }
        Integer smlSeq = scoreMainLog.getSmlSeq();
        if (consumeScore > 0) {
            //用户的总可用积分
            int userAvailableScore = scoreMember.getAvailableScore();
            deductAvailableScoreAlgorithm(memGuid, consumeScore, userAvailableScore, smlSeq);
        }
        return smlSeq;
    }

    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void saveScoreByCommentProduct(String memGuid, String data) {
        Integer type = Constant.SCORE_COMMENT_DETAIL_TYPE_COMMENT_PRODUCT;
        Integer channel = Constant.SCORE_CHANNEL_COMMENT_PRODUCT;
        String remark = "评论商品";
        addScoreBecauseComment(data, type, channel, remark);
    }

    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void saveScoreBySetEssenceOrTop(String memGuid, String data) {
        JSONObject jsonObj = JSONObject.parseObject(data);
        Integer dirType = jsonObj.getInteger("dirType");
        if (Objects.equals(dirType, Constant.COMMENT_SET_ESSENCE)) {
            Integer type = Constant.SCORE_COMMENT_DETAIL_TYPE_COMMENT_SET_ESSENCE;
            Integer channel = Constant.SCORE_CHANNEL__COMMENT_SET_ESSENCE;
            String remark = "评论设定精华";
            addScoreBecauseComment(data, type, channel, remark);
        } else if (Objects.equals(dirType, Constant.COMMENT_SET_TOP)) {
            Integer type = Constant.SCORE_COMMENT_DETAIL_TYPE_COMMENT_SET_TOP;
            Integer channel = Constant.SCORE_CHANNEL__COMMENT_SET_TOP;
            String remark = "评论置顶";
            addScoreBecauseComment(data, type, channel, remark);
        }
    }

    public void addScoreBecauseComment(String data, Integer type, Integer channel, String remark) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        String isMall = jsonObject.getString("isMall");
        //如果是商城的评论
        if (StringUtils.equals(Constant.IS_MALL, isMall)) {
            log.error("data:" + data + "商城评论。", "addScoreBecauseComment");
            return;
        }
        //会员ID
        String memGuid = jsonObject.getString("memGuid");
        //订单流水号
        String ogSeq = jsonObject.getString("ogSeq");
        //订单号
        String ogNo = jsonObject.getString("ogNo");
        //订单明细流水号
        String olSeq = jsonObject.getString("olSeq");
        //商品ID
        String itNo = jsonObject.getString("itNo");
        //卖场编号
        String smSeq = jsonObject.getString("smSeq");
        //评论商品ID
        Integer commentSeq = jsonObject.getInteger("commentSeq");
        //评论获得的积分
        Integer getScore = jsonObject.getInteger("getScore");
        Integer isWithPic;
        Integer picCount = jsonObject.getInteger("picCount");
        if (picCount == null || picCount < Constant.PIC_COUNT) {
            isWithPic = 0;
        } else {
            isWithPic = 1;
        }

        Map<String, Object> notScoreModeMap = scoreOrderDetailDao.getNotScoreMode(memGuid, ogSeq, olSeq, itNo);

        Integer sourceMode = 0;
        //订单详细没有入库
        if (notScoreModeMap == null) {
            OrderJsonVo orderDetailVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
            notScoreModeMap = new HashMap<>();
            notScoreModeMap.put("source_mode", orderDetailVo.getSourceMode());
            notScoreModeMap.put("site_mode", orderDetailVo.getSiteMode());
        }
        //评论只看价格
        if (notScoreModeMap.get("source_mode") != null) {
            sourceMode = (Integer) notScoreModeMap.get("source_mode");
        }
        String siteMode = "0";
        if (notScoreModeMap.get("site_mode") != null) {
            siteMode = (String) notScoreModeMap.get("site_mode");
        }
        //如果是企业用户
        if (cacheUtils.isCompanyUser(memGuid)
                || StringUtils.equals(siteMode, Constant.ELECTRONIC_SCREEN)
                || Objects.equals(sourceMode, Constant.DISTRIBUTION_PLATFORM)) {
            log.error("增加评论积分：因为用户是企业用户或商品来自分销平台或电子屏，获得积分为0。"
                    + "memGuid=" + memGuid + " siteMode=" + siteMode + " sourceMode=" + sourceMode, "addScoreBecauseComment");
            getScore = 0;
        }

        //退货情况
        List<ScoreOrderDetail> scoreOrderDetailListOld = scoreOrderDetailDao.getScoreOrderDetailList(memGuid, ogSeq, Constant.SCORE_ORDER_DETAIL_TYPE_RETURN_PRODUCT);

        for (ScoreOrderDetail sod : scoreOrderDetailListOld) {
            if (sod.getOgSeq().equals(ogSeq) && sod.getItNo().equals(itNo)) {
                log.error("data:" + data + "此商品已退货，不能获得评论相关积分。", "addScoreBecauseComment");
                return;
            }
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("commentSeq", commentSeq);
        paramMap.put("type", type);
        paramMap.put("memGuid", memGuid);
        ScoreCommentDetail scoreCommentDetail = scoreCommentDetailDao.getScoreCommentDetail(memGuid, paramMap);
        if (scoreCommentDetail != null) {
            log.error("data:" + data + "该商品已经获得积分，不能重复提交。", "addScoreBecauseComment");
            return;
        }
        ScoreMainLog scoreMainLogNew = new ScoreMainLog();
        scoreMainLogNew.setChannel(channel);
        scoreMainLogNew.setMemGuid(memGuid);
        scoreMainLogNew.setOgSeq(ogSeq);
        scoreMainLogNew.setOgNo(ogNo);
        scoreMainLogNew.setRemark(remark + "获得积分(立即生效)");
        scoreMainLogNew.setRgSeq("");
        scoreMainLogNew.setCommentSeq(commentSeq);
        scoreMainLogNew.setScoreNumber(getScore);
        scoreMainLogNew.setActualTime(new Date());
        scoreMainLogNew.setLockJobStatus(Constant.JOB_STATUS_SUCCESSED);

        Calendar calendarLimitTime = Calendar.getInstance();
        //立即生效
        Date nowDate = DateUtil.getNowDate();
        scoreMainLogNew.setLimitTime(nowDate);
        //失效日期 = 下一年的12月31日
        calendarLimitTime.add(Calendar.YEAR, 1);
        calendarLimitTime.set(Calendar.MONTH, Calendar.DECEMBER);
        calendarLimitTime.set(Calendar.DATE, 31);
        scoreMainLogNew.setEndTime(calendarLimitTime.getTime());
        scoreMainLogNew.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLogNew);

        ScoreCommentDetail scd = new ScoreCommentDetail();
        scd.setSmlSeq(scoreMainLogNew.getSmlSeq());
        scd.setMemGuid(memGuid);
        scd.setCommentSeq(commentSeq);
        scd.setRpSeq("");
        scd.setOlSeq(olSeq);
        scd.setOgSeq(ogSeq);
        scd.setOgNo(ogNo);
        scd.setSmSeq(smSeq);
        scd.setRgSeq("");
        scd.setRlSeq("");
        scd.setItNo(itNo);
        scd.setScoreGet(getScore);
        scd.setScoreConsume(0);
        scd.setType(type);
        scd.setCommentWithPic(isWithPic);

        Map<String, StoreInfoVo> storeInfoVoMap = scoreGetStoreInfoDao.getStoreNoByOgSeq(ogSeq, memGuid);
        StoreInfoVo storeInfoVo = storeInfoVoMap.get(itNo);
        if (storeInfoVo != null) {
            scd.setSellerNo(storeInfoVo.getSupId());
            scd.setStoreNo(storeInfoVo.getStoreNo());
        }
        scoreCommentDetailDao.saveScoreCommentDetail(memGuid, scd);
        addSelfAvailableScore(memGuid, nowDate, getScore, scoreMainLogNew.getSmlSeq());
    }


    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public void submitOrderKafkaMsgCompensate(String memGuid, OrderJsonVo orderJsonVo) {
        //查询是否有消费积分记录,如果没有消费积分记录。且查询 显示消费积分为0 则补偿消息
        String uniqueConsumeId = orderJsonVo.getOgSeq() + "_" + Constant.SCORE_CHANNEL_ORDER_CONSUME;
        ScoreMainLog scoreMainLogConsume = scoreMainLogDao.getScoreMainLogByUniqueId(orderJsonVo.getMemGuid(), uniqueConsumeId);
        if (scoreMainLogConsume == null && orderJsonVo.isNotConsumeScore()) {
            //发送消息进行订单详细信息入库
            Map<String, Object> data = new HashMap<>();
            data.put("memGuid", orderJsonVo.getMemGuid());
            data.put("ogSeq", orderJsonVo.getOgSeq());
            data.put("consumeScore", 0);
            data.put("ogNo", orderJsonVo.getOgNo());
            data.put("provinceId", "");
            Map<String, Object> info = new HashMap<>();
            info.put("type", Constant.DIRECT_TYPE_SUBMIT_ORDER);
            info.put("data", data);
            String message = JSONObject.toJSONString(info);
            try {
                producerClient.sendMessage(scoreTopic, System.currentTimeMillis() + "", message);
            } catch (Exception e) {
                log.error("补偿提交订单kafka消息失败。", "submitOrderScore", e);
            }
        }
    }

    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public JSONObject buildSubmitOrderMsg(String memGuid, OrderJsonVo orderJsonVo) {
        //查询是否有消费积分记录,如果没有消费积分记录。且查询 显示消费积分为0 则补偿消息
        String uniqueConsumeId = orderJsonVo.getOgSeq() + "_" + Constant.SCORE_CHANNEL_ORDER_CONSUME;
        ScoreMainLog scoreMainLogConsume = scoreMainLogDao.getScoreMainLogByUniqueId(orderJsonVo.getMemGuid(), uniqueConsumeId);
        if (scoreMainLogConsume == null && orderJsonVo.isNotConsumeScore()) {
            //发送消息进行订单详细信息入库
            JSONObject data = new JSONObject();
            data.put("memGuid", orderJsonVo.getMemGuid());
            data.put("ogSeq", orderJsonVo.getOgSeq());
            data.put("consumeScore", 0);
            data.put("ogNo", orderJsonVo.getOgNo());
            data.put("provinceId", "");
            return data;
        }
        return null;
    }

    /**
     * 兑换站内券
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void deductScoreForVoucher(String memGuid, String name, Integer consumeScore, String uniqueKey) {

        //查询用户积分信息 scoreMember for update 加锁
        ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
        //检测用户是否有足够的积分可用
        if ((scoreMember == null && consumeScore > 0) || (scoreMember != null && scoreMember.getAvailableScore() < consumeScore)) {
            cacheUtils.removeAvaliableScore(memGuid);
            throw new ScoreException(ResultCode.RESULT_AVAILABLE_SCORE_NOT_ENOUGH, "可用积分不足");

        }
        //记录消费积分日志 scoreMainLog
        ScoreMainLog scoreMainLog = new ScoreMainLog();
        scoreMainLog.setChannel(Constant.SCORE_CHANNEL_EXCHANGE_VOUCHER_COST);
        scoreMainLog.setMemGuid(memGuid);
        scoreMainLog.setOgSeq("");
        scoreMainLog.setOgNo("");
        String remark = Constant.VOUCHER_EXCHANGE_REMARK_PREFIX + name;
        //数据库字段长度为100
        if (remark.length() > 100) {
            remark = remark.substring(0, 100);
        }
        scoreMainLog.setRemark(remark);
        scoreMainLog.setRgSeq("");
        scoreMainLog.setRgNo("");
        scoreMainLog.setScoreNumber(-consumeScore);
        scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
        scoreMainLog.setCommentSeq(0);
        scoreMainLog.setProvinceId("");
        scoreMainLog.setUniqueId(uniqueKey);
        scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);

        Integer smlSeq = scoreMainLog.getSmlSeq();
        //用户的总可用积分
        int userAvailableScore = scoreMember.getAvailableScore();
        deductAvailableScoreAlgorithm(memGuid, consumeScore, userAvailableScore, smlSeq);
    }


    /**
     * 根据唯一键回滚积分,用于兑换券失败时
     */
    @Override
    @DynamicDataSource(index = 0)
    @Transactional(propagation = Propagation.REQUIRED, value = "transactionManagerScore")
    public void rollbackScoreByUniqueKey(String memGuid, String uniqueKey) {
        ScoreMainLog scoreMainLog = scoreMainLogDao.getScoreMainLogByUniqueId(memGuid, uniqueKey);
        if (scoreMainLog == null || !Objects.equals(scoreMainLog.getChannel(), Constant.SCORE_CHANNEL_EXCHANGE_VOUCHER_COST)) {
            throw new ScoreException(ResultCode.RESULT_ROLL_BACK_BUT_NO_EXCHANGE_CONSUME_LOG, "根据uniqueKey找不到积分的记录");
        }
        //回滚积分
        rollbackScoreByScoreMainLog(memGuid, scoreMainLog);
    }
}
