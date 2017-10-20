package com.feiniu.score.entity.score;

import com.feiniu.score.common.Constant;

public class ScoreUse {

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

    //使用
    private Integer useScore;

    //累计使用
    private Integer totalUseScore;

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

    public Integer getUseScore() {
        return useScore;
    }

    public void setUseScore(Integer useScore) {
        this.useScore = useScore;
    }

    public Integer getTotalUseScore() {
        return totalUseScore;
    }

    public void setTotalUseScore(Integer totalUseScore) {
        this.totalUseScore = totalUseScore;
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

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getScoreTypeName() {
        switch (scoreType) {
            case Constant.REPORT_SHOPPING:
                return "购物";
            case Constant.CHOU_JANG:
                return "积分抽奖";
            case Constant.DUI_HUAN:
                return "积分兑换";
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

        ScoreUse scoreUse = (ScoreUse) o;

        if (type != null ? !type.equals(scoreUse.type) : scoreUse.type != null) return false;
        if (sellerNo != null ? !sellerNo.equals(scoreUse.sellerNo) : scoreUse.sellerNo != null) return false;
        if (storeNo != null ? !storeNo.equals(scoreUse.storeNo) : scoreUse.storeNo != null) return false;
        return !(scoreType != null ? !scoreType.equals(scoreUse.scoreType) : scoreUse.scoreType != null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (sellerNo != null ? sellerNo.hashCode() : 0);
        result = 31 * result + (storeNo != null ? storeNo.hashCode() : 0);
        result = 31 * result + (scoreType != null ? scoreType.hashCode() : 0);
        return result;
    }
}
