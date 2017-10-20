package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.ScoreService;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemberModifyEmailHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(MemberModifyEmailHandler.class);

    @Autowired
    private ScoreService scoreService;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) throws KafkaClientException {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = new String(msg.message());
                LOGGER.info("message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                try {
                    JSONObject resJson = JSONObject.parseObject(message);
                    JSONObject info = resJson.getJSONObject("info");
                    String memGuid = info.getString("MEM_GUID");
                    String email = info.getString("EMAIL");
                    Map<String, Object> data = new HashMap<>();
                    data.put("memGuid", memGuid);
                    data.put("email", email);
                    scoreService.saveScoreByBindEmail(memGuid, JSONObject.toJSONString(data));
                } catch (Exception e) {
                    LOGGER.error("绑定邮箱送积分失败。", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
