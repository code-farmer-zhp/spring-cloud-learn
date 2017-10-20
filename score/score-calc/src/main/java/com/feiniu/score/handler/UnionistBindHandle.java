package com.feiniu.score.handler;

import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.UnionistService;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.springframework.beans.factory.annotation.Autowired;


public class UnionistBindHandle implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(RegisteredHandler.class);

    @Autowired
    private UnionistService unionistService;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) throws KafkaClientException {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = new String(msg.message());
                LOGGER.info("message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                try {
                    unionistService.unionBindSendBonus(message);
                } catch (Exception e) {
                    LOGGER.error("手机注册送积分失败。", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }
    }
}
