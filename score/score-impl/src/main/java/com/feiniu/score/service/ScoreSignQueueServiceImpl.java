package com.feiniu.score.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.*;
import com.feiniu.score.dao.score.ScoreGetLastValidOrder;
import com.feiniu.score.dao.score.ScoreMainLogDao;
import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.dto.Result;
import com.feiniu.score.entity.growth.GrowthMain;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.HttpRequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yue.teng on 2016/8/4.
 */
@Service
public class ScoreSignQueueServiceImpl implements ScoreSignQueueService {

    public static final CustomLog log = CustomLog.getLogger(ScoreSignQueueServiceImpl.class);

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private GrowthBaseServiceWithCache growthService;

    @Autowired
    private ScoreMainLogDao scoreMainLogDao;
    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private ScoreGetLastValidOrder scoreGetLastValidOrder;

    @Value("${sign.count.limit}")
    private Integer signCountLimitEmp;

    @Value("${recent.days.with.order}")
    private Integer recentDaysWithOrder;

    @Value("${recent.days.with.order.for.T2T3}")
    private Integer recentDaysWithOrderForT2T3;

    //积分签到消息
    @Value("${fn.topic.score.sign}")
    private String scoreSignTopic;

    // 构造一个线程池
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 0L,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000));


    @Override
    @DynamicDataSource(index = 0, isReadSlave = true)
    @Transactional(readOnly = true, value = "transactionManagerScore")
    public Result saveScoreBySign(final String memGuid, String data) {

        JSONObject jsonObject = JSONObject.parseObject(data);
        final String from = jsonObject.getString("from");
        if (StringUtils.isEmpty(from)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "from 不能为空");
        }

        int code = 0;
        String msg = "success";

        String scoreTodayStr = cacheUtils.getCacheData(memGuid + ConstantCache.SCORE_SIGN_TODAY);
        if (StringUtils.isNotBlank(scoreTodayStr)) {
            JSONObject scoreTodayJson = JSONObject.parseObject(scoreTodayStr);
            if (scoreTodayJson.getBoolean("flag")) {
                if (scoreTodayJson.getInteger("getScore") > 0) {
                    msg = "今天已经签过了，已领" + scoreTodayJson.getInteger("getScore") + "积分";
                } else {
                    msg = "今天已经签到过了，请再接再厉";
                }
                return new Result(ResultCode.RESULT_REPEAT_SUBMIT, msg);
            }
        }
        Integer scoreGet = null;

        if (cacheUtils.isEmployee(memGuid)) {
            int signCountForEmp = scoreMainLogDao.getSignCountAfterLastEffectiveOrder(memGuid, null);
            if (signCountForEmp >= signCountLimitEmp) {
                scoreGet = 0;
                code = ResultCode.RESULT_SIGN_TO_MANY_BECAUSE_OF_EMPLPYEE;
                msg = "员工签到超过" + signCountLimitEmp + "次不赠送积分";
            }
        }

        //有员工的先提示员工，没有再提示其他
        if (code == 0) {
            Date LastEffectiveOrderTime = scoreGetLastValidOrder.getLastValidOrder(memGuid);
            if (LastEffectiveOrderTime == null) {
                scoreGet = 0;
                code = ResultCode.RESULT_SIGN_TO_MANY_WITHOUT_ANY_ORDER;
                msg = "完成首单后签到可获得积分哦~";
            } else {
                Date T0T1DaysAfterLastEffectiveOrderDay = DateUtil.getTimeOfZeroDiffDate(LastEffectiveOrderTime, recentDaysWithOrder);
                Date T2T3DaysAfterLastEffectiveOrderDay = DateUtil.getTimeOfZeroDiffDate(LastEffectiveOrderTime, recentDaysWithOrderForT2T3);

                Date today= DateUtil.getTimeOfZeroDiffDate(new Date(),0);
                if (today.after(T0T1DaysAfterLastEffectiveOrderDay)||today.after(T2T3DaysAfterLastEffectiveOrderDay)) {
                    //合伙人、工会 T2T3会员从最后一次下单日期计 180 天内可签到得积分，若180天未购物，在 180 天后最多只能获得 7 次签到积分（历史签到积分不会被扣除）
                    if(cacheUtils.getIsPartnerInfo(memGuid).getIsPartner()){
                        if (today.after(T2T3DaysAfterLastEffectiveOrderDay)) {
                            scoreGet = 0;
                            code = ResultCode.RESULT_SIGN_TO_MANY_WITHOUT_ORDER_IN_RECENT_DAYS;
                            msg =  recentDaysWithOrderForT2T3 +  "天内无有效订单，签到不再赠送积分";
                        }
                    }else{
                        GrowthMain gm=growthService.getGrowthMainByMemGuid(memGuid,true);

                        if(gm!=null&&(ConstantGrowth.LEVEL_OF_2.equals(gm.getMemLevel())||ConstantGrowth.LEVEL_OF_3.equals(gm.getMemLevel()))){
                            if (today.after(T2T3DaysAfterLastEffectiveOrderDay)) {
                                scoreGet = 0;
                                code = ResultCode.RESULT_SIGN_TO_MANY_WITHOUT_ORDER_IN_RECENT_DAYS;
                                msg = "金卡和白金卡会员" + recentDaysWithOrderForT2T3 + "天内无有效订单，签到不再赠送积分(普通和银卡会员为"+recentDaysWithOrder+"天)";
                            }
                        }else{
                            //T1会员按最后一次订单购买日期计30天内可签到得积分
                            if (today.after(T0T1DaysAfterLastEffectiveOrderDay)) {
                                scoreGet = 0;
                                code = ResultCode.RESULT_SIGN_TO_MANY_WITHOUT_ORDER_IN_RECENT_DAYS;
                                if(gm==null||gm.getMemLevel()==null||gm.getMemLevel().equals("null")||gm.getMemLevel().equals(ConstantGrowth.LEVEL_OF_0)){
                                    msg=ConstantGrowth.DESC_OF_LEVEL_0+recentDaysWithOrder+"天内无有效订单，签到不再赠送积分（金卡和白金卡会员为" + recentDaysWithOrderForT2T3 + "天）";
                                }else if(gm.getMemLevel().equals(ConstantGrowth.LEVEL_OF_1)){
                                    msg=ConstantGrowth.DESC_OF_LEVEL_1+recentDaysWithOrder+"天内无有效订单，签到不再赠送积分（金卡和白金卡会员为" + recentDaysWithOrderForT2T3 + "天）";
                                }
                            }
                        }
                    }
                }
            }
        }

        if (scoreGet == null) {
            scoreGet = Constant.PHONE_SIGN_SCORE;
        }
        if (code == 0) {
            code = ResultCode.RESULT_STATUS_SUCCESS;
            msg = "签到成功，已领" + scoreGet + "积分";
        }
        final int scoreGetFin = scoreGet;
        final String requestNo = HttpRequestUtils.getRequestNo();
        try {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpRequestUtils.set(requestNo);
                        scoreService.signGetScoreForQueue(memGuid, from, scoreGetFin);
                    } catch (Exception e) {
                        log.error("签到失败,requestNo" + HttpRequestUtils.getRequestNo(), "saveScoreBySign", e);
                    }
                }
            });
        } catch (Exception e) {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "签到失败", e);
        }

        return new Result(code, scoreGet, msg);
    }

}
