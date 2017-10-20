package com.feiniu.score.handler;


import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.score.consumer.service.ScoreCalcService;
import com.feiniu.score.log.CustomLog;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class OrderScoreHandler implements KafkaStreamHandler {
    private static final CustomLog LOGGER = CustomLog.getLogger(OrderScoreHandler.class);

    @Autowired
    private ScoreCalcService scoreCalcService;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = new String(msg.message());
                LOGGER.info("message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                try {
                    scoreCalcService.calcScore(message);
                } catch (Exception e) {
                    LOGGER.error("kafka消息处理异常。message=" + message, e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }
    }

}