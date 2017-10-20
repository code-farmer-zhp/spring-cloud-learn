package com.feiniu.score.dao.notice;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.NoticeConstant;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dto.MessageInfo;
import com.feiniu.score.dto.MessageInfo.Body;
import com.feiniu.score.dto.MessageInfo.Header;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.HttpRequestUtil;
import com.feiniu.score.util.HttpRequestUtils;
import com.feiniu.score.util.SignUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MailSender {

    private static final CustomLog logger = CustomLog.getLogger(MailSender.class);

    private static long count = 0;

    @Value("${service.SendFewMessage.url}")
    private String sendApi;

    @Value("${service.SendFewMessage.key}")
    private String sendKey;

    @Value("${service.SendFewMessage.token}")
    private String sendToken;

    @Autowired
    private HttpRequestUtil httpRequestUtil;

    @Value("${lockScoreMsg.TemplateNo}")
    private String lockScoreMsgTemplateNo;

    @Value("${scoreEffectMsg.TemplateNo}")
    private String scoreEffectMsgTemplateNo;


    /**
     * 积分获取提醒
     */
    public void sendGetLockScoreMsgForInsideLetter(String memGuid, String lockScore) {
        MessageInfo messageInfo = new MessageInfo();
        buildHeader(messageInfo, sendKey, sendToken);
        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("score", lockScore);
        param.put("url_type", 1);
        content.add(param);
        buildBodyForInsideLetter(messageInfo, memGuid, content, lockScoreMsgTemplateNo, NoticeConstant.GET_LOCK_SCORE);
        sendMessage(messageInfo);
    }

    /**
     * 积分生效提醒
     */
    public void sendScoreEffectMsgForInsideLetter(String memGuid, String effectScore, String availableScore, String ogNo) {
        MessageInfo messageInfo = new MessageInfo();
        buildHeader(messageInfo, sendKey, sendToken);
        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("score", effectScore);
        param.put("availableScore", availableScore);
        param.put("url_type", 3);
        param.put("ogNo", ogNo);
        content.add(param);
        buildBodyForInsideLetter(messageInfo, memGuid, content, scoreEffectMsgTemplateNo, NoticeConstant.SCORE_EFFECT);
        sendMessage(messageInfo);
    }


    private void sendMessage(MessageInfo messageInfo) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("<").append(HttpRequestUtils.getRequestNo()).append(">");
        logStr.append("发送信息params:[ header:").append(messageInfo.getHeader()).append(" body: ").append(messageInfo.getBody().toString()).append("]");

        try {
            String resultStr = httpRequestUtil.sendPostJson(sendApi, JSONObject.toJSONString(messageInfo));
            logStr.append("return:[").append(resultStr).append("]");
            logger.info(logStr.toString(), "sendMessage");
            JSONObject json = JSONObject.parseObject(resultStr);
            int statusCode = json.getIntValue("statusCode");
            if (statusCode != 100) {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "发送信息失败。");
            }
        } catch (Exception e) {
            logger.error(logStr.toString(), "sendMessage", e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "调用发送信息API失败", e);
        }
    }

    private void buildHeader(MessageInfo messageInfo, String key, String token) {
        Header header = new Header();
        header.setKey(key);
        Date now = new Date();
        String timestamp = DateUtil.getFormatDate(now,"yyyy-MM-dd HH:mm:ss");
        header.setTimestamp(timestamp);
        TreeMap<String, String> params = new TreeMap<>();
        params.put("key", header.getKey());
        params.put("timestamp", timestamp);

        String sign = SignUtil.generate(params, token);
        header.setSign(sign);
        header.setSerialNumber(String.valueOf(getSerialNumber()));
        header.setInterfaceVersion("1.0");
        messageInfo.setHeader(header);
    }


    private void buildBodyForInsideLetter(MessageInfo messageInfo, String receiver, List<Map<String, Object>> content,
                                          String templateNo, String subject) {
        Body body = new Body();
        body.setTaskId("pointUnlockIL" + System.currentTimeMillis());
        body.setSendOccasion("1");//即时
        body.setContentType("1"); // 0：已组装；1：未组装-已存模板；2:未组装-临时模板;
        body.setTemplateNo(templateNo);
        body.setPriority("5");//优先级
        body.setSubject(subject);
        body.setServiceType("9"); //1.注册找回密码2.下单3.支付4.退货5.配送6.商户7.优惠券8.市场营销9监控
        body.setMessageContent(content);
        body.setMessageNum("1");
        body.setReceiveType("0");    //0:买家账号；1:卖家账号；2:订单号；3:买家手机号等
        body.setReceiver(Arrays.asList(receiver));
        body.setReceiveChannel("3"); // 消息接收渠道 3：站内信
        body.setReceiveChannelType("1");//0：营销;1：非营销。
        body.setInnerMessageType("1-1");
        body.setRmc("1");
        messageInfo.setBody(body);

    }

    public  String sendMess(String subject ,String mailTo , String content,String receiveChannel,String channelType,String priority,String attachment){
        MessageInfo messageInfo = new MessageInfo();

        buildHeader(messageInfo,sendKey,sendToken);
        if(StringUtils.isBlank(priority)){
            priority="9";
        }
        buildSMSBodyForPm(messageInfo, subject, mailTo, content,receiveChannel,channelType,priority, attachment);
        String resultStr="";
        try {
            resultStr = httpRequestUtil.sendPostJson(sendApi, JSONObject.toJSONString(messageInfo));
            return "发送成功:"+ "params-sendMess:"+JSONObject.toJSONString(messageInfo)+"rtn-sendMess:"+resultStr;

        } catch (Exception e) {
            logger.error("发送信息失败", e);
            return "发送失败:"+ e.toString();

        }
    }

    private synchronized long getSerialNumber() {
        return count++;
    }

    private  void buildSMSBodyForPm(MessageInfo messageInfo ,String subject ,String mailTo , String content,String receiveChannel,String channelType,String priority,String attachment){
//        MessageInfo.Body body = new MessageInfo().new Body();
        Body body = new Body();
        body.setTaskId(mailTo + "_" + System.currentTimeMillis()); //
        body.setSendOccasion("1");
        body.setContentType("0"); // 0：已组装；1：未组装-已存模板；2:未组装-临时模板;
//		body.setTemplateNo(templateNo);
        body.setPriority(priority);
        if(!"".equals(attachment)){
            List<Map<String,Object>> listattanch = new ArrayList<Map<String,Object>>();
            Map<String,Object> mapAttachment = new HashMap<String,Object>();
            mapAttachment.put("filename","FN020/"+attachment);
            String fileName = attachment;
            if(attachment.indexOf("/")>=0){
                String[] split = attachment.split("/");
                fileName = split[split.length-1];

            }
            mapAttachment.put("showFileName",fileName);
            listattanch.add(mapAttachment);
            body.setAttachment(listattanch);
        }

        body.setSubject(subject);
        if(channelType.equals("0")){
            body.setServiceType("8");
        }
        else{
            body.setServiceType("1");//业务类型 1.注册找回密码2.下单3.支付4.退货5.配送6.商户7.优惠券8.市场营销9监控
        }
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msg", content);
        list.add(map);
        body.setMessageContent(list);

        body.setMessageNum("1");
        body.setReceiveType("0");

        List<String> mailToList = new ArrayList<String>();
        mailToList.add(mailTo);
        body.setReceiver(mailToList);

        body.setReceiveChannel(receiveChannel); // 消息接收渠道	1：短信;2：邮件;3：站内信;4：哞哞;5：Android;6：IOS; （暂不支持7：微博;8：微信;9：QQ）
        body.setReceiveChannelType(channelType);
        messageInfo.setBody(body);

    }


}
