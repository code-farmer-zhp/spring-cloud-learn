package com.feiniu.score.entity.score;

import com.feiniu.score.common.Constant;

public class ScoreGrant {


    private String edate;
    //类型（自营，门店，商城）
    private String type;

    private String typeName;
    //厂商编号
    private String sellerNo;

    private String sellerName;

    //门店编号
    private String storeNo;
    //来源类型
    private String scoreType;

    private String scoreTypeName;

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

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public String getType() {
        return type;
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

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getTypeName() {
        switch (type) {
            case "1":
                return "自营";
            case "2":
                return "门店";
            case "3":
                return "商城";
            default:
                return typeName;
        }
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getScoreTypeName() {
        switch (scoreType) {
            case Constant.REPORT_SHOPPING:
                return "购物";
            case Constant.REPORT_BIND_PHONE:
                return "绑定手机";
            case Constant.REPORT_BIND_EMAIL:
                return "绑定邮箱";
            case Constant.REPORT_SIGN:
                return "签到";
            case Constant.COMMENT:
                return "评论";
            case Constant.FEI_NIU:
                return "飞牛赠送";
            default:
                return scoreTypeName;
        }
    }

    public void setScoreTypeName(String scoreTypeName) {
        this.scoreTypeName = scoreTypeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreGrant that = (ScoreGrant) o;

        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (sellerNo != null ? !sellerNo.equals(that.sellerNo) : that.sellerNo != null) return false;
        if (storeNo != null ? !storeNo.equals(that.storeNo) : that.storeNo != null) return false;
        return !(scoreType != null ? !scoreType.equals(that.scoreType) : that.scoreType != null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (sellerNo != null ? sellerNo.hashCode() : 0);
        result = 31 * result + (storeNo != null ? storeNo.hashCode() : 0);
        result = 31 * result + (scoreType != null ? scoreType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ScoreGrant{" +
                "edate='" + edate + '\'' +
                ", type='" + type + '\'' +
                ", typeName='" + typeName + '\'' +
                ", sellerNo='" + sellerNo + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", storeNo='" + storeNo + '\'' +
                ", scoreType='" + scoreType + '\'' +
                ", scoreTypeName='" + scoreTypeName + '\'' +
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
}
