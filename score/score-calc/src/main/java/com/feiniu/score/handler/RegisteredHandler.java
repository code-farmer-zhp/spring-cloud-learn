package com.feiniu.score.handler;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.kafka.client.KafkaStreamHandler;
import com.feiniu.kafka.client.exception.KafkaClientException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.service.PhoneDataPlanService;
import com.feiniu.score.service.ScoreService;
import com.feiniu.score.service.UnionistService;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class RegisteredHandler implements KafkaStreamHandler {

    private static final CustomLog LOGGER = CustomLog.getLogger(RegisteredHandler.class);

    private static final String TYPE_TWO = "2";

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private UnionistService unionistService;

    @Autowired
    private PhoneDataPlanService phoneDataPlanService;

    @Value("${memCannel}")
    private String cannel;


    @Override
    public void handle(KafkaStream<byte[], byte[]> kafkaStream) throws KafkaClientException {
        try {
            ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
            while (it.hasNext()) {
                MessageAndMetadata<byte[], byte[]> msg = it.next();
                String message = new String(msg.message());
                LOGGER.info("message info_RegisteredHandler: partition=" + msg.partition() + ",offset=" + msg.offset() + ", message=" + message);
                try {
                    JSONObject jsonObj = JSONObject.parseObject(message);
                    String type = jsonObj.getString("type");
                    if (StringUtils.equals(type, TYPE_TWO)) {
                        JSONObject info = jsonObj.getJSONObject("info");
                        String memGuid = info.getString("memGuid");
                        String memCellPhone = info.getString("memCellPhone");
                        if (StringUtils.isNotEmpty(memGuid) && StringUtils.isNotEmpty(memCellPhone)) {
                            scoreService.registerGiveZeroScore(memGuid, memCellPhone);
                        } else {
                            LOGGER.error("memGuid为空或memCellPhone为空。");
                        }
                        unionistService.unionRegSendBonus(message);
                    }
                } catch (Exception e) {
                    LOGGER.error("手机注册送积分失败。", e);
                }
                try {
                    JSONObject jsonObj = JSONObject.parseObject(message);
                    String type = jsonObj.getString("type");
                    if (StringUtils.equals(type, TYPE_TWO)) {
                        JSONObject info = jsonObj.getJSONObject("info");
                        String memCellPhone = info.getString("memCellPhone");
                        String memGuid = info.getString("memGuid");
                        String memChannel = info.getString("memChannel");
                        if (!"".equals(memCellPhone) && memCellPhone != null && memChannel.equals(cannel) && StringUtils.isNotEmpty(memGuid)) {
                            // TODO: 2016/11/25  送流量
                            String deviceId = info.getString("cookieGuid");
                            phoneDataPlanService.orderPackage(memGuid, memCellPhone, deviceId);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("送电信流量失败。", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("kafka异常", e);
        }

    }
}
