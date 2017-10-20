package com.feiniu.score.handler;


import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ScoreOrderHandler;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CancelMallOrderHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(CancelMallOrderHandler.class);

    @Autowired
    private ScoreOrderHandler scoreOrderHandler;

    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;

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
                    scoreOrderHandler.handlerCancelOrder(data);
                } catch (Exception e) {
                    LOGGER.error("kafka消息处理异常。message=" + message, e);
                    try {
                        String memGuid = data.getString("memGuid");
                        String ogSeq = data.getString("ogSeq");
                        String packageNo = data.getString("packageNo");
                        Map<String, Object> info = new HashMap<>();
                        Map<String, Object> newData = new HashMap<>();
                        info.put("type", Constant.DIRECT_TYPE__CANCLE_MALL_ORDER);
                        newData.put("ogSeq", ogSeq);
                        newData.put("memGuid", memGuid);
                        newData.put("packageNo", packageNo);
                        info.put("data", newData);
                        String newMessage = JSONObject.toJSONString(info);
                        scoreExceptionHandler.handlerScoreException(e, memGuid, newMessage);
                    } catch (Exception ex) {
                        LOGGER.error("保存错误信息异常。" + message, ex);
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
