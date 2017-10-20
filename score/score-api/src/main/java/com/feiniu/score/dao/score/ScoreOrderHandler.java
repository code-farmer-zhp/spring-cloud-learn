package com.feiniu.score.dao.score;


import com.alibaba.fastjson.JSONObject;

public interface ScoreOrderHandler {
    /**
     * 退货确认
     */
    void handlerReturnOrder(JSONObject data);

    /**
     * 取消商城订单
     */
    void handlerCancelOrder(JSONObject data);

    /**
     * 提交订单
     */
    void handlerSubmitOrder(JSONObject data);

    /**
     *订单付款
     */
    void handlerAddScore(JSONObject data);

    /**
     * CRM赠送或回收
     */
    void handlerCRM(JSONObject data);

    /**
     * 评论送积分
     */
    void handlerComment(JSONObject data);

    /**
     *评论置顶或设置精华送积分
     */
    void handlerSetEssenceorTop(JSONObject data);
}
