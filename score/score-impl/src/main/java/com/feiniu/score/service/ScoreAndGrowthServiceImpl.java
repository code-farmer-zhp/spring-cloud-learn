package com.feiniu.score.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ScoreGetOrderDetail;
import com.feiniu.score.dao.score.ScoreGetReturnDetail;
import com.feiniu.score.dao.score.ScoreOrderHandler;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.vo.OrderJsonVo;
import com.feiniu.score.vo.ReturnJsonVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * 积分和成长值通用服务
 */
@Service
public class ScoreAndGrowthServiceImpl implements ScoreAndGrowthService {

    private static CustomLog log = CustomLog.getLogger(ScoreAndGrowthServiceImpl.class);

    @Autowired
    private ScoreGetReturnDetail scoreGetReturnDetail;

    @Autowired
    private ScoreGetOrderDetail scoreGetOrderDetail;

    @Autowired
    private GrowthOrderService growthOrderService;

    @Autowired
    private GrowthMemService growthMemService;

    @Autowired
    private PkadService pkadService;

    @Autowired
    private UnionistService unionistService;

    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;

    @Autowired
    private ScoreOrderHandler scoreOrderHandler;

    @Override
    public void processingScoreMessage(String message, Integer dbType) {
        JSONObject info = JSONObject.parseObject(message);
        Integer type = info.getInteger("type");
        JSONObject data = info.getJSONObject("data");
        if (Objects.equals(type, Constant.DIRECT_TYPE_SUBMIT_ORDER)) {

            scoreOrderHandler.handlerSubmitOrder(data);

        } else if (Objects.equals(type, Constant.DIRECT_TYPE_ORDER_BUY)) {

            scoreOrderHandler.handlerAddScore(data);

        } else if (Objects.equals(type, Constant.DIRECT_TYPE_RETURN_PRODUCT)) {
            //退货确认
            scoreOrderHandler.handlerReturnOrder(data);

        } else if (Objects.equals(type, Constant.DIRECT_TYPE__CANCLE_MALL_ORDER)) {

            scoreOrderHandler.handlerCancelOrder(data);

        } else if (Objects.equals(type, Constant.CRM_ABOUT_SCORE)) {
            //CRM积分权益
            scoreOrderHandler.handlerCRM(data);
        } else if (Objects.equals(type, Constant.DIRECT_TYPE_COMMENT)) {
            //评论送积分
            scoreOrderHandler.handlerComment(data);
        } else if (Objects.equals(type, Constant.DIRECT_TYPE_SETESSENCEORTOP)) {
            scoreOrderHandler.handlerSetEssenceorTop(data);
        }
    }


    @Override
    public void processingGrowthMessage(String message) {
        JSONObject info = JSONObject.parseObject(message);
        Integer type = info.getInteger("type");
        JSONObject data = info.getJSONObject("data");
        String memGuid = data.getString("memGuid");
        if (Objects.equals(type, Constant.DIRECT_TYPE_SUBMIT_ORDER)) {

            // 成长值：计算订单成长值
            try {
                String ogSeq = data.getString("ogSeq");
                OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
                growthOrderService.orderInput(memGuid, orderJsonVo);
            } catch (Exception e) {
                log.error("成长值：计算成长值失败","processingGrowthMessage", e);
                scoreExceptionHandler.handlerBizException(e, memGuid, message, Constant.GROWTH_ORDER_SUBMIT);
            }

        } else if (Objects.equals(type, Constant.DIRECT_TYPE_ORDER_BUY)) {

            //成长值：订单付款
            try {
                String ogSeq = data.getString("ogSeq");
                OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);

                growthOrderService.orderPay(memGuid, orderJsonVo);
            } catch (Exception e) {

                log.error("成长值：订单支付失败", "processingGrowthMessage",e);
                        scoreExceptionHandler.handlerBizException(e, memGuid, message, Constant.GROWTH_ORDER_PAY);
            }

        } else if (Objects.equals(type, Constant.DIRECT_TYPE_RETURN_PRODUCT)) {
            //退货确认
            String rgSeq = data.getString("rgSeq");
            String pay = data.getString("pay");
            String rssSeq = data.getString("rssSeq");
            //成长值：退货确认
            if (StringUtils.equals(pay, Constant.IS_PAY)) {
                //查询退货信息
                ReturnJsonVo returnJsonVo = scoreGetReturnDetail.getReturnDetail(memGuid, rgSeq, rssSeq);
                try {
                    growthOrderService.orderReturn(memGuid, returnJsonVo, null);
                } catch (Exception e) {
                    log.error("成长值：退货确认失败","processingGrowthMessage", e);
                    scoreExceptionHandler.handlerBizException(e, memGuid, message, Constant.GROWTH_ORDER_RETURN);
                }
            }
        } else if (Objects.equals(type, Constant.DIRECT_TYPE_COMMENT)) {
            try {
                growthMemService.saveGrowthByCommentProduct(memGuid, data.toJSONString());
            } catch (Exception e) {
                log.error("评论获得成长值失败。","processingGrowthMessage");
                scoreExceptionHandler.handlerGrowthException(e, memGuid, data.toJSONString(), Constant.GROWTH_COMMENT_UNSUCESS_TYPE_NO_ORDER);
            }
        } else if (Objects.equals(type, Constant.DIRECT_TYPE_SETESSENCEORTOP)) {
            try {
                growthMemService.saveGrowthBySetEssenceOrTop(memGuid, data.toJSONString());
            } catch (Exception e) {
                log.error("评论置顶或设置精华获得成长值失败。","processingGrowthMessage");
                scoreExceptionHandler.handlerGrowthException(e, memGuid, data.toJSONString(), Constant.GROWTH_SET_ESSENCE_OR_TOP_UNSUCESS_TYPE_NO_ORDER);
            }
        }

    }


    @Override
    public void processingUnSucessGrowthMessage(String message, Integer code, Date upTime) {
        JSONObject msgJson = JSONObject.parseObject(message);
        if (Objects.equals(code, Constant.GROWTH_ORDER_PAY)) {
            JSONObject data = msgJson.getJSONObject("data");
            String memGuid = data.getString("memGuid");
            String ogSeq = data.getString("ogSeq");
            OrderJsonVo orderJsonVo = scoreGetOrderDetail.getOrderDetail(memGuid, ogSeq);
            growthOrderService.orderPay(memGuid, orderJsonVo);
        } else if (Objects.equals(code, Constant.GROWTH_ORDER_RECEIVE)) {
            String memGuid = msgJson.getString("memGuid");
            growthOrderService.receiveOrder(memGuid, message, null);
        } else if (Objects.equals(code, Constant.GROWTH_ORDER_RETURN)) {
            String memGuid = msgJson.getString("memGuid");
            //退货确认
            String rgSeq = msgJson.getString("rgSeq");
            String pay = msgJson.getString("pay");
            String rssSeq = msgJson.getString("rssSeq");
            //成长值：退货确认
            if (StringUtils.equals(pay, Constant.IS_PAY)) {
                //查询退货信息
                ReturnJsonVo returnJsonVo = scoreGetReturnDetail.getReturnDetail(memGuid, rgSeq, rssSeq);
                growthOrderService.orderReturn(memGuid, returnJsonVo, null);

            }
        } else if (Objects.equals(code, Constant.GROWTH_COSUMER_GRADE_RECEIVE)) {
            String memGuid = msgJson.getString("memb_id");
            this.growthMemService.getkafkafromCRM(memGuid, message);
        } else if (Objects.equals(code, Constant.GROWTH_COSUMER_GROWTH_RECEIVE)) {
            String memGuid = msgJson.getString("memb_id");
            this.growthMemService.saveGrowthkafkafromCRM(memGuid, message);
        } else if (Objects.equals(code, Constant.GROWTH_COMMENT_UNSUCESS_TYPE_NO_ORDER)) {
            String memGuid = msgJson.getString("memGuid");
            growthMemService.saveGrowthByCommentProduct(memGuid, message);
        } else if (Objects.equals(code, Constant.GROWTH_SET_ESSENCE_OR_TOP_UNSUCESS_TYPE_NO_ORDER)) {
            String memGuid = msgJson.getString("memGuid");
            growthMemService.saveGrowthBySetEssenceOrTop(memGuid, message);
        } else if (Objects.equals(code, Constant.PKAD_TAKEN_UNSUCCESS)) {
            String memGuid = msgJson.getString("memGuid");
            String pkadSeq = msgJson.getString("pkadSeq");
            Date now = new Date();
            //留足够的时间生成卡券，再去查卡券是否已领取。并取卡券号
            Date insTimeAdd30Second = DateUtil.getTimeAddSecond(upTime, 30);
            if (now.after(insTimeAdd30Second)) {
                pkadService.processOutTimePkadLog(memGuid, pkadSeq);
            }
        }else if (Objects.equals(code, Constant.GROWTH_VALUE_NUM_CHANGE)) {
            growthMemService.growthValueNumChange(message);
        }else if (Objects.equals(code, Constant.UNIONIST_REGISTER_SEND_BONUS)) {
            if(!unionistService.unionRegSendBonus(message)){
                throw new ScoreException("注册工会会员赠送抵用券出现错误");
            }
        }else if (Objects.equals(code, Constant.UNIONIST_BIND_SEND_BONUS)) {
            if(unionistService.unionBindSendBonus(message)){
                throw new ScoreException("绑定工会会员赠送抵用券出现错误");
            }
        }else if(Objects.equals(code, Constant.PKAD_CONSUME_UNSUCCESS)){
//            String memGuid = msgJson.getString("memb_id");
//            this.pkadService.getkafkafromCRM(memGuid, message);
        }
    }
}
