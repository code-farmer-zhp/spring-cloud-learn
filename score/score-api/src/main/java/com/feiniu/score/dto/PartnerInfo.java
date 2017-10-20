package com.feiniu.score.dto;

/**
 * Created by yue.teng on 2016/6/30.
 */
public class PartnerInfo {
    private Boolean isPartner;
    private String becomePartnerTime;

    public PartnerInfo() {
    }

    public PartnerInfo(Boolean isPartner, String becomePartnerTime) {
        this.isPartner = isPartner;
        this.becomePartnerTime = becomePartnerTime;
    }

    public Boolean getIsPartner() {
        return isPartner;
    }

    public void setIsPartner(Boolean isPartner) {
        this.isPartner = isPartner;
    }

    public String getBecomePartnerTime() {
        return becomePartnerTime;
    }

    public void setBecomePartnerTime(String becomePartnerTime) {
        this.becomePartnerTime = becomePartnerTime;
    }

    @Override
    public String toString() {
        return "PartnerInfo{" +
                "isPartner=" + isPartner +
                ", becomePartnerTime='" + becomePartnerTime + '\'' +
                '}';
    }
}
