package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.score.common.Constant;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.GrowthMemService;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.springframework.beans.factory.annotation.Autowired;


public class GrowthValueNumChangeHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(GrowthValueNumChangeHandler.class);

    @Autowired
    private GrowthMemService growthMemService;

    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = null;
                try {
                    message = new String(msg.message());
                    LOGGER.info("kafkaconsumer growth value num send message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                    growthMemService.growthValueNumChange(message);
                    LOGGER.info("growth value num  consumer success");
                } catch (Exception e) {
                    LOGGER.error("处理消息失败。message" + message, e);
                    try {
                        scoreExceptionHandler.handlerBizException(e, getMemGuid(message), message, Constant.GROWTH_VALUE_NUM_CHANGE);
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