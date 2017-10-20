package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.score.dao.score.ScoreCommonDao;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


public class VipScoreRollBackHandler implements KafkaStreamHandler {

    private static final Logger LOGGER = Logger.getLogger(VipScoreRollBackHandler.class);

    @Autowired
    private ScoreCommonDao scoreCommonDao;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = null;
                try {
                    message = new String(msg.message());
                    LOGGER.info("kafkaconsumer vip score rollback send message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                    JSONObject json = JSONObject.parseObject(message);
                    String memGuid = json.getString("memGuid");
                    if (StringUtils.isEmpty(memGuid)) {
                        LOGGER.error("memGuid 为空");
                    }
                    String uniqueKey = json.getString("uniqueKey");
                    if (StringUtils.isEmpty(uniqueKey)) {
                        LOGGER.error("uniqueKey 为空");
                    }
                    scoreCommonDao.rollbackScoreByUniqueKey(memGuid, uniqueKey);
                } catch (Exception e) {
                    LOGGER.info("处理消息失败。message" + message, e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
