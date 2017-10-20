package com.feiniu.score.dto;

import java.util.List;
import java.util.Map;

public class MessageInfo {

    private Header header;
    private Body body;


    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public static class Header {

        private String key;
        private String serialNumber;
        private String sign;
        private String timestamp;
        private String interfaceVersion;


        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getInterfaceVersion() {
            return interfaceVersion;
        }

        public void setInterfaceVersion(String interfaceVersion) {
            this.interfaceVersion = interfaceVersion;
        }

        @Override
        public String toString() {
            return "Header{" +
                    "key='" + key + '\'' +
                    ", serialNumber='" + serialNumber + '\'' +
                    ", sign='" + sign + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", interfaceVersion='" + interfaceVersion + '\'' +
                    '}';
        }
    }

    public static class Body {

        private String phoneNo;

        private String captcha;

        private String taskId;
        private String sendOccasion;
        private String startTime;
        private String endTime;
        private String contentType;
        private String templateNo;
        private String templateTemp;
        private String serviceType;
        private String priority;
        private String subject;
        private List<Map<String, Object>> messageContent;
        private String messageNum;
        private String receiveType;
        private List<String> receiver; // 逗号隔开  	["zhangsan","lisi","wangwu"]
        private String receiveChannel;
        private String receiveChannelType;
        private String mobileVersion;
        private String mobileVersionAbove;
        private String pushAppType;
        private String wxAccount; //【触屏】：“001”		【合伙人】：“002”
        private String wxType;
        private String messageType;
        private String innerMessageType;
        private String rmc;

        private List<Map<String,Object>> attachment;

        public List<Map<String,Object>> getAttachment() {
            return attachment;
        }

        public void setAttachment(List<Map<String,Object>> attachment) {
            this.attachment = attachment;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public String getCaptcha() {
            return captcha;
        }

        public void setCaptcha(String captcha) {
            this.captcha = captcha;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getSendOccasion() {
            return sendOccasion;
        }

        public void setSendOccasion(String sendOccasion) {
            this.sendOccasion = sendOccasion;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getTemplateNo() {
            return templateNo;
        }

        public void setTemplateNo(String templateNo) {
            this.templateNo = templateNo;
        }

        public String getTemplateTemp() {
            return templateTemp;
        }

        public void setTemplateTemp(String templateTemp) {
            this.templateTemp = templateTemp;
        }


        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public List<Map<String, Object>> getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(List<Map<String, Object>> messageContent) {
            this.messageContent = messageContent;
        }

        public String getMessageNum() {
            return messageNum;
        }

        public void setMessageNum(String messageNum) {
            this.messageNum = messageNum;
        }

        public String getReceiveType() {
            return receiveType;
        }

        public void setReceiveType(String receiveType) {
            this.receiveType = receiveType;
        }

        public List<String> getReceiver() {
            return receiver;
        }

        public void setReceiver(List<String> receiver) {
            this.receiver = receiver;
        }

        public String getReceiveChannel() {
            return receiveChannel;
        }

        public void setReceiveChannel(String receiveChannel) {
            this.receiveChannel = receiveChannel;
        }

        public String getReceiveChannelType() {
            return receiveChannelType;
        }

        public void setReceiveChannelType(String receiveChannelType) {
            this.receiveChannelType = receiveChannelType;
        }

        public String getMobileVersion() {
            return mobileVersion;
        }

        public void setMobileVersion(String mobileVersion) {
            this.mobileVersion = mobileVersion;
        }

        public String getMobileVersionAbove() {
            return mobileVersionAbove;
        }

        public void setMobileVersionAbove(String mobileVersionAbove) {
            this.mobileVersionAbove = mobileVersionAbove;
        }

        public String getPushAppType() {
            return pushAppType;
        }

        public void setPushAppType(String pushAppType) {
            this.pushAppType = pushAppType;
        }

        public String getWxAccount() {
            return wxAccount;
        }

        public void setWxAccount(String wxAccount) {
            this.wxAccount = wxAccount;
        }

        public String getWxType() {
            return wxType;
        }

        public void setWxType(String wxType) {
            this.wxType = wxType;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getRmc() {
            return rmc;
        }

        public void setRmc(String rmc) {
            this.rmc = rmc;
        }

        public String getInnerMessageType() {
            return innerMessageType;
        }

        public void setInnerMessageType(String innerMessageType) {
            this.innerMessageType = innerMessageType;
        }

        @Override
        public String toString() {
            return "Body{" +
                    "phoneNo='" + phoneNo + '\'' +
                    ", captcha='" + captcha + '\'' +
                    ", taskId='" + taskId + '\'' +
                    ", sendOccasion='" + sendOccasion + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", templateNo='" + templateNo + '\'' +
                    ", templateTemp='" + templateTemp + '\'' +
                    ", serviceType='" + serviceType + '\'' +
                    ", priority='" + priority + '\'' +
                    ", subject='" + subject + '\'' +
                    ", messageContent=" + messageContent +
                    ", messageNum='" + messageNum + '\'' +
                    ", receiveType='" + receiveType + '\'' +
                    ", receiver=" + receiver +
                    ", receiveChannel='" + receiveChannel + '\'' +
                    ", receiveChannelType='" + receiveChannelType + '\'' +
                    ", mobileVersion='" + mobileVersion + '\'' +
                    ", mobileVersionAbove='" + mobileVersionAbove + '\'' +
                    ", pushAppType='" + pushAppType + '\'' +
                    ", wxAccount='" + wxAccount + '\'' +
                    ", wxType='" + wxType + '\'' +
                    ", messageType='" + messageType + '\'' +
                    ", innerMessageType='" + innerMessageType + '\'' +
                    ", rmc='" + rmc + '\'' +
                    '}';
        }
    }
}
