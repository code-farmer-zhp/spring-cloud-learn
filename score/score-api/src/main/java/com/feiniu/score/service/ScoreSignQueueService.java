package com.feiniu.score.service;

import com.feiniu.score.dto.Result;

/**
 * Created by yue.teng on 2016/8/4.
 */
public interface ScoreSignQueueService {
    Result saveScoreBySign(String memGuid, String data);
}
