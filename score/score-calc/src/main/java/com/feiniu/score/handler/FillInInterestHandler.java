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
import java.util.Objects;

@Component
public class FillInInterestHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(FillInInterestHandler.class);

    @Autowired
    private ScoreService scoreService;

    private static final Integer TYPE_OF_FILL_IN_INTEREST = 9;

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
                    Integer type = resJson.getInteger("type");
                    String memGuid = info.getString("MEM_GUID");
                    Map<String, Object> data = new HashMap<>();
                    data.put("memGuid", memGuid);
                    data.put("type", type);
                    if (Objects.equals(type, TYPE_OF_FILL_IN_INTEREST)) {
                        scoreService.saveScoreByFillInInterest(memGuid, JSONObject.toJSONString(data));
                    } else {
                        LOGGER.error("完善兴趣爱好送，入参的type错误。应该为" + TYPE_OF_FILL_IN_INTEREST);
                    }
                } catch (Exception e) {
                    LOGGER.error("完善兴趣爱好送积分失败。", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
