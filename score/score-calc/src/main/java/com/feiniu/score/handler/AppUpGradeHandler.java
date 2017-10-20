package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.score.dao.score.ScoreFeiniuGiveDao;
import com.feiniu.score.log.CustomLog;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("appUpGradeHandler")
public class AppUpGradeHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(AppUpGradeHandler.class);

    @Autowired
    private ScoreFeiniuGiveDao scoreFeiniuGiveDao;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = null;
                try {
                    message = new String(msg.message());
                    LOGGER.info("kafkaconsumer pkad message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                    JSONObject json = JSONObject.parseObject(message);
                    String memGuid = json.getString("memGuid");
                    if (StringUtils.isEmpty(memGuid)) {
                        LOGGER.error("memGuid 为空。");
                        continue;
                    }
                    Integer type = json.getInteger("type");
                    if (type == null) {
                        LOGGER.error("type 为空。");
                        continue;
                    }
                    String version = json.getString("version");
                    if (StringUtils.isEmpty(version)) {
                        LOGGER.error("version 为空。");
                        continue;
                    }
                    Integer score = json.getInteger("score");
                    if (score == null) {
                        LOGGER.error("score 为空。");
                        continue;
                    }
                    scoreFeiniuGiveDao.appUpGradeGiveScore(memGuid, type, version, score);
                } catch (Exception e) {
                    LOGGER.error("处理消息失败。message" + message, e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
