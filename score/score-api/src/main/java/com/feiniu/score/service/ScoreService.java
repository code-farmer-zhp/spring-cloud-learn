package com.feiniu.score.service;


import com.feiniu.score.dto.Result;


public interface ScoreService {


    /**
     * 查询确认订单时总积分,，仅作显示使用
     */
    Result getOrderScore(String memGuid, String productInfoJson);

    /**
     * 提交订单
     *
     * @param orderInfo
     * @return
     */
    Result submitOrderScore(String memGuid, String orderInfo);

    /**
     * 查询单个订单积分信息
     *
     * @param order
     * @return
     */
    Result getOrderScoreInfo(String memGuid, String order);

    /**
     * 订单付款后发放积分
     *
     * @param order
     * @return
     */
    Result addScore(String memGuid, String order);

    /**
     * 显示用户可用积分信息
     *
     * @param memGuid 会员ID
     * @return
     */
    Result getUserAvaliableScore(String memGuid, String cache);

    /**
     * 查询用户积分信息
     *
     * @param memGuid 会员ID
     * @return
     */
    Result getUserScoreInfo(String memGuid);

    /**
     * 查询用户积分详细信息
     *
     * @param data 输入的JSON参数
     * @return
     */
    Result getUserScoreDetailList(String memGuid, String data);

    /**
     * 查询退货单可返还的消费积分
     *
     * @param data
     * @return
     */
    Result getReturnConsumeScore(String memGuid, String data);

    /**
     * 退货退款确认后回收发放的积分和返还消费的积分
     *
     * @param data
     * @return
     */
    Result confirmReturnScore(String memGuid, String data);

    /**
     * 绑定手机获得积分
     *
     * @param memGuid
     * @param data
     * @return
     */
    Result saveScoreByBindPhone(String memGuid, String data);

    /**
     * 绑定email获得积分
     *
     * @param memGuid
     * @param data
     * @return
     */
    Result saveScoreByBindEmail(String memGuid, String data);

    /**
     * 评论商品获得积分
     *
     * @param memGuid
     * @return
     */
    Result saveScoreByCommentProduct(String memGuid);

    /**
     * 评论设定精华或置顶获得额外积分
     */
    Result saveScoreBySetEssenceOrTop(String data);


    /**
     * 查询单个订单消费积分信息
     *
     * @param memGuid
     * @param order
     * @return
     */
    Result getOrderConsumeScoreInfo(String memGuid, String order);


    /**
     * 审核通过送积分
     *
     * @param memGuid
     * @param data
     * @return
     */
    Result saveScoreByCustomerGive(String memGuid, String data);

    /**
     * 按照日期对积分收支进行统计
     *
     * @param data
     * @return
     */
    Result loadScoreSum(String data);

    /**
     * 依据商品出货号获取商品 赚取/消费 积分
     *
     * @param memGuid
     * @param data
     * @return
     */
    Result loadOlScore(String memGuid, String data);

    /**
     * ERP积分流水查询
     *
     * @param memGuid
     * @param data
     * @return
     */
    Result getUserScoreLogDetailList(String memGuid, String data);

    Result getDetail(String data);

    Result cancelMallOrderConsumeScore(String memGuid, String dataJson);

    Result haveSign(String memGuid);

    Result haveBindPhoneAndEmail(String memGuid);

    Result getScoreDetailByOlSeqs(String memGuid, String data);

    Result mallScoreForCancel(String memGuid, String data);

    Result mallScoreForRefund(String data);

    Result changeScoreImmediatelyWithChannel(String memGuid, String data);

    Result changeScoreImmediatelyByExchangeCard(String memGuid, String data);

    Result getScoreListBySmSeqList(String data);

    Result saveScoreByFillInInterest(String memGuid, String data);

    Result getScoreGrantDetail(String data);

    Result getScoreUseDetail(String data);

    Result getScoreByOgsSeq(String data);

    Result getLastCurSignInfo(String memGuid, String data);

    Result haveSignReturnScore(String memGuid);

    Result getSignDateThisMonth(String memGuid);

    boolean dbHealthCheck();

    void signGetScoreForQueue(String memGuid, String from, int scoreGet);

    Result kafkaMessage(String data);

    void registerGiveZeroScore(String memGuid, String phone);

    Result scoreExchangeVoucher(String data);

    Result getStoreScoreReportInfo(String data);

    Result clearData();

    Result getRecoveryScore(String memGuid, String data);
}
