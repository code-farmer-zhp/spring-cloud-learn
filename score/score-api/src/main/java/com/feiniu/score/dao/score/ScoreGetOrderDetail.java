package com.feiniu.score.dao.score;

import com.feiniu.score.vo.OrderJsonVo;

/**
 * 查询订单详情
 */
public interface ScoreGetOrderDetail {
    OrderJsonVo getOrderDetail(String memGuid, String ogSeq);
}
