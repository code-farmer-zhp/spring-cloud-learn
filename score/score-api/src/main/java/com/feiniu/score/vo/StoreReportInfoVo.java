package com.feiniu.score.vo;


public class StoreReportInfoVo {

    private int type;

    private Integer effectScore;

    private Integer invalidScore;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getEffectScore() {
        return effectScore;
    }

    public void setEffectScore(Integer effectScore) {
        this.effectScore = effectScore;
    }

    public Integer getInvalidScore() {
        return invalidScore;
    }

    public void setInvalidScore(Integer invalidScore) {
        this.invalidScore = invalidScore;
    }

    @Override
    public String toString() {
        return "StoreReportInfoVo{" +
                "type=" + type +
                ", effectScore=" + effectScore +
                ", invalidScore=" + invalidScore +
                '}';
    }
}
