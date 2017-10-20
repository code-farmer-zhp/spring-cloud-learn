package com.feiniu.score.entity.dataplan;

import java.util.Date;

public class OrderPackageLog {
    private Integer oplSeq;

    private String memGuid;

    private String reqId;

    private String orderPhone;

    private String deviceId;

    private Date orderTime;

    private String goodName;

    private String offerSpecl;

    private String status;

    private String staffValue;

    private Date insTime;

    private Date upTime;

    private String errorMessage;

    private Date regTime;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public Integer getOplSeq() {
        return oplSeq;
    }

    public void setOplSeq(Integer oplSeq) {
        this.oplSeq = oplSeq;
    }

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid == null ? null : memGuid.trim();
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId == null ? null : reqId.trim();
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone == null ? null : orderPhone.trim();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName == null ? null : goodName.trim();
    }

    public String getOfferSpecl() {
        return offerSpecl;
    }

    public void setOfferSpecl(String offerSpecl) {
        this.offerSpecl = offerSpecl == null ? null : offerSpecl.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getStaffValue() {
        return staffValue;
    }

    public void setStaffValue(String staffValue) {
        this.staffValue = staffValue == null ? null : staffValue.trim();
    }

    public Date getInsTime() {
        return insTime;
    }

    public void setInsTime(Date insTime) {
        this.insTime = insTime;
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }


}