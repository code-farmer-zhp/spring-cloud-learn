package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.score.common.Constant;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.exception.ScoreExceptionHandler;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.PkadService;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class PkadHandler implements KafkaStreamHandler {

    private static final CustomLog logger = CustomLog.getLogger(PkadHandler.class);

    @Autowired
    private PkadService pkadService;

    @Autowired
    private ScoreExceptionHandler scoreExceptionHandler;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 6, 1000L,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(600), new ThreadPoolExecutor.CallerRunsPolicy());

    public void handle(KafkaStream<byte[], byte[]> kafkaStream) {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = null;
                String memGuid = null;
                try {
                    message = new String(msg.message());
                    logger.info("kafkaconsumer pkad message info: partition " + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                    memGuid = getMemGuid(message);
                    final String messageForThread = message;
                    final String memGuidForThread = memGuid;
                    threadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String noRollBackFailReason = pkadService.getkafkafromCRM(memGuidForThread, messageForThread);
                                //do record error message with no Exception thrown to roll back
                                if (!StringUtils.isBlank(noRollBackFailReason)) {
                                    ScoreException e = new ScoreException(noRollBackFailReason);
                                    scoreExceptionHandler.handlerBizException(e, memGuidForThread, messageForThread, Constant.PKAD_CONSUME_UNSUCCESS);
                                }
                            } catch (Exception e) {
                                logger.error("处理消息失败。message" + messageForThread, e);
                                scoreExceptionHandler.handlerBizException(e, memGuidForThread, messageForThread, Constant.PKAD_CONSUME_UNSUCCESS);
                            }
                        }
                    });
                } catch (Exception e) {
                    logger.error("处理消息失败。message" + message, e);
                    try {
                        scoreExceptionHandler.handlerBizException(e, memGuid, message, Constant.PKAD_CONSUME_UNSUCCESS);
                    } catch (Exception e2) {
                        logger.error("错误日志保存异常。message=" + message, e2);
                    }

                }
            }
        } catch (Exception e) {
            logger.error("kafka异常", e);
        }

    }

    private String getMemGuid(String message) {
        JSONObject info = JSONObject.parseObject(message);
        return info.getString("memb_id");
    }
}