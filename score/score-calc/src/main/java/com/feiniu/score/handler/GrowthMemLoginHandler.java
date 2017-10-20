package com.feiniu.score.handler;

import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.common.ConstantGrowth;
import com.feiniu.score.log.CustomLog;
import kafka.consumer.KafkaStream;

public class GrowthMemLoginHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(GrowthMemLoginHandler.class);

    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) throws KafkaClientException {
       /* ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> msg = it.next();
            String message = new String(msg.message());
            LOGGER.info("kafkaconsumer growthMemLogin send  message info: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
            登录不再送成长值
            try {
                JSONObject jsonObject = JSONObject.parseObject(message);
                JSONObject info = jsonObject.getJSONObject("info");
                Integer type = info.getInteger("LOGIN_SUCCESS_TYPE");
                if(type!=null&&(type==0||type==8)) {
                    final String memGuid = info.getString("MEM_GUID");
                    Integer from = info.getInteger("CHANNEL");
                    final Integer loginFrom=adapter(from);
                    final Date loginTime = info.getDate("LOGIN_TIME");
                    final String messageForThread= message;
                    threadPoolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                growthMemService.sign(memGuid, loginFrom,loginTime);
                            } catch (Exception e) {
                                LOGGER.error("处理登录送成长值失败消息失败。message"+messageForThread,e);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.error("登录送成长值失败", e);
            }
        }*/
    }


    /**
     * 1:pc，2：触屏  3： android 4:ios
     */
    private Integer adapter(Integer from) {
        if (from == null) {
            return null;
        }
        switch (from) {
            case 1:
                return ConstantGrowth.LOGIN_FROM_PC;
            case 2:
                return ConstantGrowth.LOGIN_FROM_TOUCH;
            case 3:
            case 4:
                return ConstantGrowth.LOGIN_FROM_APP;
            default:
                return null;
        }

    }
}
