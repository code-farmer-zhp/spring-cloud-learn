package com.feiniu.score.service;

import java.util.Date;

/**
 * 积分和成长值通用服务
 */
public interface ScoreAndGrowthService {
    void processingScoreMessage(String message,Integer dbType);

    void processingGrowthMessage(String message);

    void processingUnSucessGrowthMessage(String message, Integer code, Date insTime);
}
