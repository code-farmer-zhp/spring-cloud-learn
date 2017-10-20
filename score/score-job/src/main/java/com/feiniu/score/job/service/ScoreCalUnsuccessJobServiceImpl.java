package com.feiniu.score.job.service;

import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ScoreDefalutTableDao;
import com.feiniu.score.entity.score.ScoreJobUnsuccessed;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.ScoreAndGrowthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class ScoreCalUnsuccessJobServiceImpl {

    private static final CustomLog log = CustomLog.getLogger(ScoreCalUnsuccessJobServiceImpl.class);

    private static final String UNDEAL = "0";

    private static final String DEALT = "1";

    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;

    @Value("${unsuccess.sleep}")
    private int sleep;

    /**
     * CRM相关
     */
    private static final List<Integer> CRM_SCORE_TYPE = new ArrayList<>();

    /**
     * 提交订单
     */
    private static final List<Integer> SUBMIT_ORDER = new ArrayList<>();

    /**
     * 订单付款
     */
    private static final List<Integer> ORDER_BUY = new ArrayList<>();

    /**
     * 退货确认
     */
    private static final List<Integer> RETURN_COMFIRM = new ArrayList<>();

    /**
     * 取消商城订单
     */
    private static final List<Integer> CANCEL_MALL_ORDER = new ArrayList<>();

    /**
     * 订单评论
     */
    private static final List<Integer> ORDER_COMMENT = new ArrayList<>();

    /**
     * 积分解冻发送消息
     */
    private static final List<Integer> SCORE_UNLOCK_MSG = new ArrayList<>();

    private static final List<Integer> GROWTH_SUBMIT_ORDER = new ArrayList<>();
    private static final List<Integer> GROWTH_ORDER_PAY = new ArrayList<>();
    private static final List<Integer> GROWTH_ORDER_RECEIVE = new ArrayList<>();
    private static final List<Integer> GROWTH_ORDER_RETURN = new ArrayList<>();
    private static final List<Integer> GROWTH_COMMENT = new ArrayList<>();
    private static final List<Integer> GROWTH_SET_ESSENCE_OR_TOP = new ArrayList<>();

    private static final List<Integer> GROWTH_CRM_GRADE_AND_GROWTH = new ArrayList<>();

    private static final List<Integer> PKAD_TAKEN_UNSUCCESS = new ArrayList<>();

    private static final List<Integer> GROWTH_VALUE_NUM= new ArrayList<>();

    private static final List<Integer> SCORE_SIGN= new ArrayList<>();

    private static final List<Integer> UNIONIST_REGISTER_SEND_BONUS= new ArrayList<>();

    private static final List<Integer> UNIONIST_BIND_SEND_BONUS= new ArrayList<>();

    private static final List<Integer> PKAD_CONSUME_UNSUCCESS= new ArrayList<>();
    static {
        CRM_SCORE_TYPE.add(Constant.CRM_ABOUT_SCORE);

        SUBMIT_ORDER.add(Constant.DIRECT_TYPE_SUBMIT_ORDER);
        SUBMIT_ORDER.add(Constant.SCORE_UNSUCESS_TYPE_FOURTEEN);

        ORDER_BUY.add(Constant.DIRECT_TYPE_ORDER_BUY);

        ORDER_COMMENT.add(Constant.SCORE_UNSUCESS_COMMENT);

        RETURN_COMFIRM.add(Constant.DIRECT_TYPE_RETURN_PRODUCT);
        RETURN_COMFIRM.add(Constant.SCORE_UNSUCESS_TYPE_FIFTEEN);
        RETURN_COMFIRM.add(Constant.SCORE_UNSUCESS_TYPE_SIXTEEN);

        CANCEL_MALL_ORDER.add(Constant.DIRECT_TYPE__CANCLE_MALL_ORDER);
        CANCEL_MALL_ORDER.add(Constant.SCORE_UNSUCESS_TYPE_SEVENTEEN);

        SCORE_UNLOCK_MSG.add(Constant.SCORE_UNSUCESS_UNLOCK_MSG_FOR_WX);

        GROWTH_SUBMIT_ORDER.add(Constant.GROWTH_ORDER_SUBMIT);
        GROWTH_ORDER_PAY.add(Constant.GROWTH_ORDER_PAY);
        GROWTH_ORDER_RECEIVE.add(Constant.GROWTH_ORDER_RECEIVE);
        GROWTH_ORDER_RETURN.add(Constant.GROWTH_ORDER_RETURN);
        GROWTH_COMMENT.add(Constant.GROWTH_COMMENT_UNSUCESS_TYPE_NO_ORDER);
        GROWTH_SET_ESSENCE_OR_TOP.add(Constant.GROWTH_SET_ESSENCE_OR_TOP_UNSUCESS_TYPE_NO_ORDER);

        GROWTH_CRM_GRADE_AND_GROWTH.add(Constant.GROWTH_COSUMER_GROWTH_RECEIVE);
        GROWTH_CRM_GRADE_AND_GROWTH.add(Constant.GROWTH_COSUMER_GRADE_RECEIVE);

        PKAD_TAKEN_UNSUCCESS.add(Constant.PKAD_TAKEN_UNSUCCESS);

        GROWTH_VALUE_NUM.add(Constant.GROWTH_VALUE_NUM_CHANGE);

        SCORE_SIGN.add(Constant.SCORE_UNSUCESS_SIGN);

        UNIONIST_REGISTER_SEND_BONUS.add(Constant.UNIONIST_REGISTER_SEND_BONUS);

        UNIONIST_BIND_SEND_BONUS.add(Constant.UNIONIST_BIND_SEND_BONUS);

        PKAD_CONSUME_UNSUCCESS.add(Constant.PKAD_CONSUME_UNSUCCESS);
    }

    @Autowired
    private ScoreDefalutTableDao scoreDefalutTableDao;

    @Autowired
    private ScoreAndGrowthService scoreAndGrowthService;

    public void processScoreCalUnsuccessScheduler() {
        while (true) {
            log.info("开始score-cal的处理");
            prcocessMessage(CRM_SCORE_TYPE);

            prcocessMessage(SUBMIT_ORDER);

            prcocessMessage(ORDER_BUY);

            prcocessMessage(RETURN_COMFIRM);

            prcocessMessage(CANCEL_MALL_ORDER);

            prcocessMessage(ORDER_COMMENT);

            prcocessMessage(SCORE_UNLOCK_MSG);

            prcocessMessage(SCORE_SIGN);

            // 以下是成长值相关
            prcocessGrowthMessage(GROWTH_SUBMIT_ORDER);

            prcocessGrowthMessage(GROWTH_ORDER_PAY);

            prcocessGrowthMessage(GROWTH_ORDER_RECEIVE);

            prcocessGrowthMessage(GROWTH_ORDER_RETURN);

            prcocessGrowthMessage(GROWTH_CRM_GRADE_AND_GROWTH);

            prcocessGrowthMessage(GROWTH_COMMENT);

            prcocessGrowthMessage(GROWTH_SET_ESSENCE_OR_TOP);

            prcocessGrowthMessage(PKAD_TAKEN_UNSUCCESS);

            prcocessGrowthMessage(GROWTH_VALUE_NUM);

            prcocessGrowthMessage(PKAD_CONSUME_UNSUCCESS);

            prcocessGrowthMessage(UNIONIST_REGISTER_SEND_BONUS);

            prcocessGrowthMessage(UNIONIST_BIND_SEND_BONUS);
            try {
                TimeUnit.SECONDS.sleep(sleep);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            log.info("结束score-cal的处理");
        }

    }

    private void prcocessMessage(List<Integer> types) {
        //第一页
        int page = 1;
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("isDeal", UNDEAL);
        mapParam.put("start", (page - 1) * Constant.DEFAULT_PAGE_SIZE);
        mapParam.put("pageSize", Constant.DEFAULT_PAGE_SIZE);
        mapParam.put("type", types);
        List<ScoreJobUnsuccessed> list = scoreDefalutTableDao.getScoreCalUnsuccessedList(mapParam);
        while (list.size() > 0) {
            for (ScoreJobUnsuccessed scoreJobUnsuccessed : list) {
                Integer scuSeq = scoreJobUnsuccessed.getScuSeq();
                Integer type = scoreJobUnsuccessed.getType();
                // 处理失败记录
                String message = scoreJobUnsuccessed.getMessage();
                String memGuid = scoreJobUnsuccessed.getMemGuid();
                try {
                    scoreAndGrowthService.processingScoreMessage(message, type);
                    // 更新状态
                    scoreDefalutTableDao.updateScoreCalUnsuccessedIsDel(scuSeq + "", DEALT);
                    log.info("处理core-cal的记录成功, type为:" + type + ",scuSeq为" + scuSeq);
                } catch (Exception e) {
                    log.error("处理core-cal的记录失败, type为:" + type + ",scu_seq为" + scuSeq, e);
                    int currType = scoreExceptionHandler.handlerScoreException(e, memGuid, message);
                    if (!Objects.equals(currType, type)) {
                        //type已经变化
                        scoreDefalutTableDao.updateScoreCalUnsuccessedIsDel(scuSeq + "", DEALT);
                    }

                    moreThanSixtyDays(scoreJobUnsuccessed, scuSeq);

                }

            }
            page++;
            mapParam.put("start", (page - 1) * Constant.DEFAULT_PAGE_SIZE);
            list = scoreDefalutTableDao.getScoreCalUnsuccessedList(mapParam);
        }
    }


    private void prcocessGrowthMessage(List<Integer> types) {
        //第一页
        int page = 1;
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("isDeal", UNDEAL);
        mapParam.put("start", (page - 1) * Constant.DEFAULT_PAGE_SIZE);
        mapParam.put("pageSize", Constant.DEFAULT_PAGE_SIZE);
        mapParam.put("type", types);
        List<ScoreJobUnsuccessed> list = scoreDefalutTableDao.getScoreCalUnsuccessedList(mapParam);
        while (list.size() > 0) {
            for (ScoreJobUnsuccessed scoreJobUnsuccessed : list) {
                Integer scuSeq = scoreJobUnsuccessed.getScuSeq();
                Integer type = scoreJobUnsuccessed.getType();
                Date upTime=scoreJobUnsuccessed.getUpTime();
                if(upTime==null){
                    upTime=scoreJobUnsuccessed.getInsTime();
                }
                // 处理失败记录
                String message = scoreJobUnsuccessed.getMessage();
                String memGuid = scoreJobUnsuccessed.getMemGuid();
                try {
                    scoreAndGrowthService.processingUnSucessGrowthMessage(message, type,upTime);
                    // 更新状态
                    scoreDefalutTableDao.updateScoreCalUnsuccessedIsDel(scuSeq + "", DEALT);
                    log.info("处理Growth-cal的记录成功, type为:" + type + ",scuSeq为" + scuSeq);
                } catch (Exception e) {
                    log.error("处理Growth-cal的记录失败, type为:" + type + ",scu_seq为" + scuSeq, e);
                    int newCode = scoreExceptionHandler.handlerBizException(e, memGuid, message, type);
                    if (newCode == -1) {
                        //重复插入的
                        scoreDefalutTableDao.updateScoreCalUnsuccessedIsDel(scuSeq + "", DEALT);
                    }
                    moreThanSixtyDays(scoreJobUnsuccessed, scuSeq);
                }
            }
            page++;
            mapParam.put("start", (page - 1) * Constant.DEFAULT_PAGE_SIZE);
            list = scoreDefalutTableDao.getScoreCalUnsuccessedList(mapParam);
        }
    }


    private void moreThanSixtyDays(ScoreJobUnsuccessed scoreJobUnsuccessed, Integer scuSeq) {
        //如果超过两个月则标记为已处理。
        Date insTime = scoreJobUnsuccessed.getInsTime();
        Calendar instance = Calendar.getInstance();
        instance.setTime(insTime);
        instance.add(Calendar.DATE, 60);
        if (new Date().after(instance.getTime())) {
            scoreDefalutTableDao.updateScoreCalUnsuccessedIsDel(scuSeq + "", DEALT);
        }
    }

}
