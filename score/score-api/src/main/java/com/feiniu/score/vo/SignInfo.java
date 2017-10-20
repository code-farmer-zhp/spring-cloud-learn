package com.feiniu.score.vo;

import java.util.Date;

/**
 * Created by yue.teng on 2016/3/29.
 */
public class SignInfo {
    /*
     连续签到开始日期
     */
    private Date durBeginDate;

    /*
     连续签到结束日期
     */
    private Date durEndDate;
    /*
     连续签到持续日期
     */
    private int enduranceDays;

    public Date getDurBeginDate() {
        return durBeginDate;
    }

    public void setDurBeginDate(Date durBeginDate) {
        this.durBeginDate = durBeginDate;
    }

    public Date getDurEndDate() {
        return durEndDate;
    }

    public void setDurEndDate(Date durEndDate) {
        this.durEndDate = durEndDate;
    }

    public int getEnduranceDays() {
        return enduranceDays;
    }

    public void setEnduranceDays(int enduranceDays) {
        this.enduranceDays = enduranceDays;
    }
}
