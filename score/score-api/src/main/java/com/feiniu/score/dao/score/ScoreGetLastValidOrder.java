package com.feiniu.score.dao.score;

import java.util.Date;

/**
 * Created by yue.teng on 2016/5/31.
 */
public interface ScoreGetLastValidOrder {
    Date getLastValidOrder(String memGuid);
}
