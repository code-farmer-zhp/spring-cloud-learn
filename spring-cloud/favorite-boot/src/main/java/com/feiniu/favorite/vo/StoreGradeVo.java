package com.feiniu.favorite.vo;

import java.math.BigDecimal;

public class StoreGradeVo {

    /**
     * 评分信息项名
     */
    private String label;

    /**
     * 评分信息项分数
     */
    private BigDecimal score;

    /**
     * 与行业相比的增减幅度(增：正数；减：负数)
     */
    private BigDecimal offset;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getOffset() {
        return offset;
    }

    public void setOffset(BigDecimal offset) {
        this.offset = offset;
    }

}
