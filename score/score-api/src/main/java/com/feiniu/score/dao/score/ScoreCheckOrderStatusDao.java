package com.feiniu.score.dao.score;

/**
 * 检查订单的状态
 */
public interface ScoreCheckOrderStatusDao {
    boolean getOrderSubmitErrorStatus(String ogNo);
}
