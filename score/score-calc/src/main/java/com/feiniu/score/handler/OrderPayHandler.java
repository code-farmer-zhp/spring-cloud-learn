package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.common.Constant;
import com.feiniu.score.consumer.service.ScoreCalcService;
import com.feiniu.score.log.CustomLog;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderPayHandler implements KafkaStreamHandler {

    private static CustomLog LOGGER = CustomLog.getLogger(OrderPayHandler.class);

    @Autowired
    private ScoreCalcService scoreCalcService;

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) throws KafkaClientException {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = new String(msg.message());
                LOGGER.info("message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                try {
                    //封装成订单付款消息 {"data":{"memGuid":"D58EA8CF-9924-78C5-E76C-0D9AD2CF176B","ogSeq":"201510CO15100232"},"type":11}
                    JSONObject jsonObj = JSONObject.parseObject(message);
                    String memGuid = jsonObj.getString("MEM_GUID");
                    String ogSeq = jsonObj.getString("OG_SEQ");
                    if (StringUtils.isEmpty(memGuid) || StringUtils.isEmpty(ogSeq)) {
                        LOGGER.error("MEM_GUID 或 OG_SEQ 为空");
                    } else {
                        Map<String, Object> data = new HashMap<>();
                        data.put("memGuid", memGuid);
                        data.put("ogSeq", ogSeq);
                        Map<String, Object> payMsg = new HashMap<>();
                        payMsg.put("data", data);
                        payMsg.put("type", Constant.DIRECT_TYPE_ORDER_BUY);
                        scoreCalcService.calcScore(JSONObject.toJSONString(payMsg));
                    }
                } catch (Exception e) {
                    LOGGER.error("kafka消息处理异常。message=" + message, e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
