package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.score.common.Constant;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.GrowthOrderService;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.springframework.beans.factory.annotation.Autowired;

public class GrowthReceiveOrderHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(GrowthReceiveOrderHandler.class);

    @Autowired
    private GrowthOrderService growthOrderService;

    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;

    /**
     * topic：orderPlacing_topic_CR
     */
    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = null;
                String memGuid = null;
                try {
                    message = new String(msg.message());
                    LOGGER.info("kafkaconsumer gradeorder send  message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                    memGuid = getMemGuid(message);
                    growthOrderService.receiveOrder(memGuid, message, null);
                    LOGGER.info("growthorder consumer success");
                } catch (Exception e) {
                    LOGGER.error("成长值：确认收货处理失败 message" + message, e);
                    try {
                        scoreExceptionHandler.handlerBizException(e, memGuid, message, Constant.GROWTH_ORDER_RECEIVE);
                    } catch (Exception e2) {
                        LOGGER.error("错误日志保存异常。message=" + message, e2);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }

    private String getMemGuid(String message) {
        JSONObject info = JSONObject.parseObject(message);
        return info.getString("memGuid");
    }

}
