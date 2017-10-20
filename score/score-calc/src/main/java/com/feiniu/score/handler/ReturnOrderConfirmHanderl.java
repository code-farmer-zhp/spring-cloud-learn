package com.feiniu.score.handler;


import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ScoreGetReturnDetail;
import com.feiniu.score.dao.score.ScoreOrderHandler;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.GrowthOrderService;
import com.feiniu.score.vo.ReturnJsonVo;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 退货确认
 */
@Component
public class ReturnOrderConfirmHanderl implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(ReturnOrderConfirmHanderl.class);

    @Autowired
    private ScoreOrderHandler scoreOrderHandler;

    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;

    @Autowired
    private ScoreGetReturnDetail scoreGetReturnDetail;

    @Autowired
    private GrowthOrderService growthOrderService;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) throws KafkaClientException {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = new String(msg.message());
                LOGGER.info("message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                JSONObject data = null;
                try {
                    data = JSONObject.parseObject(message);
                    scoreOrderHandler.handlerReturnOrder(data);
                } catch (Exception e) {
                    LOGGER.error("kafka消息处理异常。message=" + message, e);
                    try {
                        String memGuid = data.getString("memGuid");
                        String rgSeq = data.getString("rgSeq");
                        String pay = data.getString("pay");
                        String rssSeq = data.getString("rssSeq");

                        Map<String, Object> info = new HashMap<>();
                        Map<String, Object> newData = new HashMap<>();
                        info.put("type", Constant.DIRECT_TYPE_RETURN_PRODUCT);
                        newData.put("rgSeq", rgSeq);
                        newData.put("memGuid", memGuid);
                        newData.put("pay", pay);
                        newData.put("rssSeq", rssSeq);
                        info.put("data", newData);
                        String mewMessage = JSONObject.toJSONString(info);
                        scoreExceptionHandler.handlerScoreException(e, memGuid, mewMessage);
                    } catch (Exception ex) {
                        LOGGER.error("保存错误信息异常。", ex);
                    }
                }

                String memGuid = null;
                try {
                    //退货确认
                    memGuid = data.getString("memGuid");
                    String rgSeq = data.getString("rgSeq");
                    String pay = data.getString("pay");
                    String rssSeq = data.getString("rssSeq");

                    //成长值：退货确认
                    if (StringUtils.equals(pay, Constant.IS_PAY)) {
                        //查询退货信息
                        ReturnJsonVo returnJsonVo = scoreGetReturnDetail.getReturnDetail(memGuid, rgSeq, rssSeq);

                        growthOrderService.orderReturn(memGuid, returnJsonVo, null);

                    }
                } catch (Exception e) {
                    LOGGER.error("成长值：退货确认失败", e);
                    try {
                        scoreExceptionHandler.handlerBizException(e, memGuid, message, Constant.GROWTH_ORDER_RETURN);
                    } catch (Exception ex) {
                        LOGGER.error("保存错误信息异常。", ex);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
