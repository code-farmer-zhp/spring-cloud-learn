package com.feiniu.score.entity.score;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ScoreReportEntity {

    private String edate;
    //类型（自营，门店，商城）
    private String type;
    //厂商编号
    private String sellerNo;
    //门店编号
    private String storeNo;
    //来源类型
    private String scoreType;
    //自营还是商城
    private Integer sourceType;

    //发送
    private Integer sendScore;

    //生效
    private Integer effectScore;

    //使用
    private Integer useScore;

    //退单还点
    private Integer returnScore;

    //失效积分
    private Integer invalidScore;

    //累计发送
    private Integer totalSendScore;

    //累计生效
    private Integer totalEffectScore;

    //累计使用
    private Integer totalUseScore;

    //累计退订还点
    private Integer totalReturnScore;

    //累计失效积分
    private Integer totalInvalidScore;

    //已生效余额=累计生效-累计使用+累计退货还点-累计失效
    private Integer haveEffectRemainder;

    public String getType() {
        if (StringUtils.isEmpty(sellerNo)) {
            return "1";//自营
        }
        if (StringUtils.isEmpty(storeNo)) {
            return "3";//商城
        } else {
            return "2";//门店
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSellerNo() {
        return sellerNo;
    }

    public void setSellerNo(String sellerNo) {
        this.sellerNo = sellerNo;
    }

    public String getStoreNo() {
        return storeNo;
    }

    public void setStoreNo(String storeNo) {
        this.storeNo = storeNo;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSendScore() {
        return sendScore;
    }

    public void setSendScore(Integer sendScore) {
        this.sendScore = sendScore;
    }

    public Integer getEffectScore() {
        return effectScore;
    }

    public void setEffectScore(Integer effectScore) {
        this.effectScore = effectScore;
    }

    public Integer getUseScore() {
        return useScore;
    }

    public void setUseScore(Integer useScore) {
        this.useScore = useScore;
    }

    public Integer getReturnScore() {
        return returnScore;
    }

    public void setReturnScore(Integer returnScore) {
        this.returnScore = returnScore;
    }

    public Integer getInvalidScore() {
        return invalidScore;
    }

    public void setInvalidScore(Integer invalidScore) {
        this.invalidScore = invalidScore;
    }

    public Integer getTotalSendScore() {
        return totalSendScore;
    }

    public void setTotalSendScore(Integer totalSendScore) {
        this.totalSendScore = totalSendScore;
    }

    public Integer getTotalEffectScore() {
        return totalEffectScore;
    }

    public void setTotalEffectScore(Integer totalEffectScore) {
        this.totalEffectScore = totalEffectScore;
    }

    public Integer getTotalUseScore() {
        return totalUseScore;
    }

    public void setTotalUseScore(Integer totalUseScore) {
        this.totalUseScore = totalUseScore;
    }

    public Integer getTotalReturnScore() {
        return totalReturnScore;
    }

    public void setTotalReturnScore(Integer totalReturnScore) {
        this.totalReturnScore = totalReturnScore;
    }

    public Integer getTotalInvalidScore() {
        return totalInvalidScore;
    }

    public void setTotalInvalidScore(Integer totalInvalidScore) {
        this.totalInvalidScore = totalInvalidScore;
    }

    public Integer getHaveEffectRemainder() {
        return haveEffectRemainder;
    }

    public void setHaveEffectRemainder(Integer haveEffectRemainder) {
        this.haveEffectRemainder = haveEffectRemainder;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreReportEntity that = (ScoreReportEntity) o;
        if (StringUtils.isEmpty(sellerNo)) {
            sellerNo = "";
        }
        if (StringUtils.isEmpty(that.sellerNo)) {
            that.sellerNo = "";
        }
        if (!StringUtils.equals(sellerNo, that.sellerNo)) {
            return false;
        }
        if (sourceType == null) {
            sourceType = 0;
        }
        if (that.sourceType == null) {
            that.sourceType = 0;
        }
        if (!Objects.equals(sourceType, that.sourceType)) {
            return false;
        }
        if (storeNo == null) {
            storeNo = "";
        }
        if (that.storeNo == null) {
            that.storeNo = "";
        }
        if (!StringUtils.equals(storeNo, that.storeNo)) {
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        if (StringUtils.isEmpty(sellerNo)) {
            sellerNo = "";
        }
        int result = sellerNo.hashCode();
        if (sourceType == null) {
            sourceType = 0;
        }
        if(storeNo == null){
            storeNo="";
        }
        result = 31 * result + sourceType.hashCode();
        result = 31 * result + storeNo.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "ScoreReportEntity{" +
                "edate='" + edate + '\'' +
                ", type='" + type + '\'' +
                ", sellerNo='" + sellerNo + '\'' +
                ", storeNo='" + storeNo + '\'' +
                ", scoreType='" + scoreType + '\'' +
                ", sourceType=" + sourceType +
                ", sendScore=" + sendScore +
                ", effectScore=" + effectScore +
                ", useScore=" + useScore +
                ", returnScore=" + returnScore +
                ", invalidScore=" + invalidScore +
                ", totalSendScore=" + totalSendScore +
                ", totalEffectScore=" + totalEffectScore +
                ", totalUseScore=" + totalUseScore +
                ", totalReturnScore=" + totalReturnScore +
                ", totalInvalidScore=" + totalInvalidScore +
                ", haveEffectRemainder=" + haveEffectRemainder +
                '}';
    }

    public static void main(String[] args) {
        ScoreReportEntity scoreReportEntity = new ScoreReportEntity();
        scoreReportEntity.setType("类型");
        scoreReportEntity.setSellerNo("厂商编号");
        scoreReportEntity.setStoreNo("门店编号");
        scoreReportEntity.setScoreType("来源类型");
        scoreReportEntity.setSendScore(1);
        scoreReportEntity.setEffectScore(1);
        scoreReportEntity.setUseScore(1);
        scoreReportEntity.setReturnScore(1);
        scoreReportEntity.setInvalidScore(1);
        scoreReportEntity.setTotalSendScore(1);
        scoreReportEntity.setTotalEffectScore(1);
        scoreReportEntity.setTotalUseScore(1);
        scoreReportEntity.setTotalReturnScore(1);
        scoreReportEntity.setTotalInvalidScore(1);
        scoreReportEntity.setHaveEffectRemainder(1);
        System.out.println(JSONObject.toJSONString(scoreReportEntity));
    }
}
