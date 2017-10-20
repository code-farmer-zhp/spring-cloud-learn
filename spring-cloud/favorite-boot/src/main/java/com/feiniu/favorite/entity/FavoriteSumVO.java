package com.feiniu.favorite.entity;

public class FavoriteSumVO {

    /** 商品id */
    private String smSeq;

    /** 被收藏的总次数 */
    private long totalNum;

    /** 当前时间戳 */
    private long time;

    public String getSmSeq() {
        return smSeq;
    }

    public void setSmSeq(String smSeq) {
        this.smSeq = smSeq;
    }

    public long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
