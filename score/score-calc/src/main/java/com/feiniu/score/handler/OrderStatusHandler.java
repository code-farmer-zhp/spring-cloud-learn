package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.common.CacheUtils;
import com.feiniu.score.dao.score.ScoreCommonDao;
import com.feiniu.score.log.CustomLog;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(OrderStatusHandler.class);

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private ScoreCommonDao scoreCommonDao;

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
                    String ogno = resJson.getString("ogno");
                    int succ = resJson.getIntValue("succ");
                    if (succ == 0) {
                        //失败状态
                        cacheUtils.putCache(ogno + "_error", 60 * 10, true);
                        String memguid = resJson.getString("memguid");
                        String ogseq = resJson.getString("ogseq");
                        scoreCommonDao.rollbackScoreDirect(memguid, ogseq);
                    } else {
                        //成功状态
                        cacheUtils.putCache(ogno + "_error", 60 * 10, false);
                    }
                } catch (Exception e) {
                    LOGGER.error("订单状态处理失败", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
