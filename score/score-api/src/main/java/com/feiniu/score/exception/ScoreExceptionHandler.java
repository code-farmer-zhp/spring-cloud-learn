package com.feiniu.score.exception;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dao.score.ScoreDefalutTableDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.ExceptionMsgUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 积分异常处理
 */
@Component
public class ScoreExceptionHandler {
    private static final CustomLog log = CustomLog.getLogger(ScoreExceptionHandler.class);
    @Autowired
    private ScoreDefalutTableDao scoreDefalutTableDao;

    public int handlerScoreException(Exception e, String memGuid, String message) {
        log.error("kafka消息处理异常。message=" + message, "handlerScoreException", e);
        if (e instanceof DuplicateKeyException) {
            //插入重复错误不处理
            return -1;
        }
        String errorMsg = ExceptionMsgUtil.getMsg(e);
        JSONObject info = JSONObject.parseObject(message);
        Integer type = info.getInteger("type");
        //评论送积分处理异常
        if (Objects.equals(type, Constant.DIRECT_TYPE_COMMENT) || Objects.equals(type, Constant.DIRECT_TYPE_SETESSENCEORTOP)) {
            type = Constant.SCORE_UNSUCESS_COMMENT;
        } else if (e instanceof ScoreException) {
            ScoreException scoreException = (ScoreException) e;
            Integer code = scoreException.getCode();

            if (code == ResultCode.RESULT_SCORE_SUBMIT_ORDER_BUT_NO_CONSUME_LOG) {
                //提交订单详细信息入库
                //未找到订单消费信息，延迟处理
                type = Constant.SCORE_UNSUCESS_TYPE_FOURTEEN;
            } else if (code == ResultCode.RESULT_SCORE_RETURN_TWO_PHASE_DATA) {
                //退货确认，已有退货信息,属于二期数据
                type = Constant.SCORE_UNSUCESS_TYPE_TWO;
            } else if (code == ResultCode.RESULT_SCORE_RETURN_BUT_NO_BUY_LOG) {
                //退货确认，未找到订单购买获得积分信息，延迟处理。
                type = Constant.SCORE_UNSUCESS_TYPE_FIFTEEN;
            } else if (code == ResultCode.RESULT_SCORE_RETURN_BUT_NO_BUY_CONFIRM) {
                //退货确认请求大于付款确认，延迟处理
                type = Constant.SCORE_UNSUCESS_TYPE_SIXTEEN;
            } else if (code == ResultCode.RESULT_SCORE_CANCEL_ORDER_BUT_NOT_FIND_DETAIL) {
                //取消订单但未找到详细信息
                type = Constant.SCORE_UNSUCESS_TYPE_SEVENTEEN;
            }
        }
        //保存错误信息
        scoreDefalutTableDao.handleFailMessage(memGuid, message, type, errorMsg);
        return type;
    }


    /**
     * Growth
     *
     * @param e
     * @param memGuid
     * @param message
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED, value = "transactionManagerScore")
    public int handlerBizException(Exception e, String memGuid, String message, int code) {
        log.error("kafka消息处理异常。message=" + message, "handlerBizException", e);
        if (e instanceof DuplicateKeyException) {
            //插入重复错误不处理
            return -1;
        }
        String errorMsg = ExceptionMsgUtil.getMsg(e);

        DataSourceUtils.setCurrentKey("defaultDataSource");
        scoreDefalutTableDao.handleFailMessage(memGuid, message, code, errorMsg);
        return code;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, value = "transactionManagerScore")
    public void handlerGrowthException(Exception e, String memGuid, String message, Integer type) {
        log.error("growth处理异常。message=" + message, "handlerGrowthException", e);
        if (e instanceof DuplicateKeyException) {
            //插入重复错误不处理
            return;
        }
        String errorMsg = ExceptionMsgUtil.getMsg(e);
        //提交订单详细信息入库
        //未找到订单消费信息，延迟处理
        DataSourceUtils.setCurrentKey("defaultDataSource");
        scoreDefalutTableDao.handleFailMessage(memGuid, message, type, errorMsg);
    }
}
