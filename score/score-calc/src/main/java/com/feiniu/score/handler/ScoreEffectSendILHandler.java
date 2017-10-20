package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.dao.notice.MailSender;
import com.feiniu.score.log.CustomLog;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("scoreEffectSendILHandler")
public class ScoreEffectSendILHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(ScoreEffectSendILHandler.class);

    @Autowired
    private MailSender mailSender;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) throws KafkaClientException {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = new String(msg.message());
                LOGGER.info("message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                JSONObject jsonObject = JSONObject.parseObject(message);
                String memGuid = jsonObject.getString("memGuid");
                String score = jsonObject.getString("score");
                String availableScore = jsonObject.getString("availableScore");
                String ogNo = jsonObject.getString("ogNo");
                try {
                    mailSender.sendScoreEffectMsgForInsideLetter(memGuid, score, availableScore, ogNo);
                } catch (Exception e) {
                    LOGGER.error("发送消息失败。", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
