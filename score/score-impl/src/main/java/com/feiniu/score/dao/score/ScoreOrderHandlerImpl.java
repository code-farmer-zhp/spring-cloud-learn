package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.vo.CrmScoreJsonVo;
import com.feiniu.score.vo.OrderJsonVo;
import com.feiniu.score.vo.ReturnJsonVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import scala.actors.threadpool.TimeUnit;

import java.util.Objects;

/**
 * 订单，退货处理
 */
@Component
public class ScoreOrderHandlerImpl implements ScoreOrderHandler {

    private static final CustomLog log = CustomLog.getLogger(ScoreOrderHandlerImpl.class);

    @Autowired
    private ScoreCommonDao scoreCommonDao;

    @Autowired
    private ScoreGetReturnDetail scoreGetReturnDetail;

    @Autowired
    private ScoreGetOrderDetail scoreGetOrderDetail;

    @Override
    public void handlerReturnOrder(JSONObject data) {
        //退货确认
        String memGuid = data.getString("memGuid");
        String rgSeq = data.getString("rgSeq");
        String pay = data.getString("pay");
        String rssSeq = data.getString("rssSeq");

        //查询退货信息
        ReturnJsonVo returnJsonVo = scoreGetReturnDetail.getReturnDetail(memGuid, rgSeq, rssSeq);
        String ogSeq = returnJsonVo.getOgSeq();
        OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
        //分销平台
        if (Objects.equals(orderJsonVo.getSourceMode(), Constant.DISTRIBUTION_PLATFORM)) {
            return;
        }
        //电子屏
        if (StringUtils.equals(orderJsonVo.getSiteMode(), Constant.ELECTRONIC_SCREEN)) {
            return;
        }
        //组团订单
        if (StringUtils.isNotEmpty(orderJsonVo.getGroupId())) {
            return;
        }
        //虚拟订单
        Integer virtual = orderJsonVo.getVirtual();
        if (virtual != null && virtual == 1) {
            return;
        }
        JSONObject dataSubmit = scoreCommonDao.buildSubmitOrderMsg(memGuid, orderJsonVo);
        if (dataSubmit != null) {
            handlerSubmitOrder(dataSubmit);
        }
        scoreCommonDao.processReturnOrderScore(memGuid, returnJsonVo, pay);
    }

    @Override
    public void handlerCancelOrder(JSONObject data) {
        //取消商城订单
        String memGuid = data.getString("memGuid");
        String ogSeq = data.getString("ogSeq");
        String packageNo = data.getString("packageNo");
        OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
        JSONObject dataSubmit = scoreCommonDao.buildSubmitOrderMsg(memGuid, orderJsonVo);
        if (dataSubmit != null) {
            handlerSubmitOrder(dataSubmit);
        }
        scoreCommonDao.processMallOrderCancel(memGuid, ogSeq, packageNo);
    }

    @Override
    public void handlerSubmitOrder(JSONObject data) {
        //提交订单
        String memGuid = data.getString("memGuid");
        String ogSeq = data.getString("ogSeq");
        String ogNo = data.getString("ogNo");
        Integer consumeScore = data.getInteger("consumeScore");
        String provinceId = data.getString("provinceId");
        //查看订单的状态。如果是失败的订单则回滚积分
        boolean backStatus = scoreCommonDao.rollbackScore(memGuid, ogSeq, ogNo);
        if (backStatus) {
            //回滚了积分
            return;
        }
        //请求订单的详细信息对象。
        OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
        orderJsonVo.setProvinceId(provinceId == null ? "" : provinceId);
        //重试三次
        for (int i = 0; i < 3; i++) {
            try {
                scoreCommonDao.saveSubmitOrderDetail(memGuid, orderJsonVo, consumeScore);
                break;
            } catch (DeadlockLoserDataAccessException e) {
                log.error("并发发生插入死锁进行重试", "handlerSubmitOrder", e);
                if (i == 2) {
                    throw e;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e1) {
                    //ignore
                }
            }
        }

    }

    @Override
    public void handlerAddScore(JSONObject data) {
        //订单付款
        String memGuid = data.getString("memGuid");
        String ogSeq = data.getString("ogSeq");
        //请求订单的详细信息对象。
        OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
        //分销平台
        if (Objects.equals(orderJsonVo.getSourceMode(), Constant.DISTRIBUTION_PLATFORM)) {
            return;
        }
        //电子屏
        if (StringUtils.equals(orderJsonVo.getSiteMode(), Constant.ELECTRONIC_SCREEN)) {
            return;
        }
        //组团订单
        if (StringUtils.isNotEmpty(orderJsonVo.getGroupId())) {
            return;
        }
        //虚拟订单
        Integer virtual = orderJsonVo.getVirtual();
        if (virtual != null && virtual == 1) {
            return;
        }
        //重试三次
        for (int i = 0; i < 3; i++) {
            try {
                scoreCommonDao.processOrderScore(memGuid, ogSeq);
                break;
            } catch (DuplicateKeyException | DeadlockLoserDataAccessException e) {
                log.error("插入订单支付获得积分时遇到重复记录或插入死锁重新处理。", "handlerAddScore", e);
                if (i == 2) {
                    throw e;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e1) {
                    //ignore
                }
            }
        }
    }

    @Override
    public void handlerCRM(JSONObject data) {
        //CRM积分权益
        CrmScoreJsonVo crmScoreJsonVo = CrmScoreJsonVo.convertJson(data);
        scoreCommonDao.processCrmScore(crmScoreJsonVo.getMemGuid(), crmScoreJsonVo);
    }

    @Override
    public void handlerComment(JSONObject data) {
        //评论送积分
        String memGuid = data.getString("memGuid");
        scoreCommonDao.saveScoreByCommentProduct(memGuid, data.toJSONString());
    }

    @Override
    public void handlerSetEssenceorTop(JSONObject data) {
        String memGuid = data.getString("memGuid");
        scoreCommonDao.saveScoreBySetEssenceOrTop(memGuid, data.toJSONString());
    }
}
