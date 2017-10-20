package com.feiniu.score.rest;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public interface ScoreRestService {

    String test();

    /**
     * 确认订单时显示积分信息
     *
     * @param productList 商品信息
     * @return 积分
     */
    Response getOrderScore(@FormParam("data") String productList);


    /**
     * 提交订单
     *
     * @param orderInfo 订单信息
     * @return 积分
     */
    Response submitOrderScore(@FormParam("data") String orderInfo);

    /**
     * 查询单个订单积分信息
     */
    Response getOrderScoreInfo(@QueryParam("data") String order);


    /**
     * 订单付款后发放积分
     */
    Response addScore(@FormParam("data") String order);

    /**
     * 获得用户可用积分信息
     *
     * @param memGuid 会员ID
     * @return 用户可用积分
     */
    Response getUserAvaliableScore(@QueryParam("memGuid") String memGuid, @QueryParam("cache") String cache);

    /**
     * 查询用户可用积分，待生效积分，即将过期积分
     */
    Response getUserScoreInfo(@QueryParam("memGuid") String memGuid);

    Response getUserScoreDetailList(@QueryParam("data") String data);


    /**
     * 取消商城订单
     */
    Response cancelMallOrderConsumeScore(@FormParam("data") String data);

    /**
     * 查询退货单可返还的消费积分
     */
    Response getReturnConsumeScore(@QueryParam("data") String data);

    /**
     * 退货退款确认后回收发放的积分和返还消费的积分
     */
    Response confirmReturnScore(@FormParam("data") String data);

    /**
     * 绑定手机获得积分
     */
    Response saveScoreByBindPhone(@FormParam("data") String data);

    /**
     * 绑定email获得积分
     */
    Response saveScoreByBindEmail(@FormParam("data") String data);

    Response saveScoreBySign(@FormParam("data") String data);

    Response haveSign(@QueryParam("memGuid") String memGuid);

    /**
     * 评论获得积分
     */
    Response saveScoreByCommentProduct(@FormParam("data") String data);

    /**
     * 评论设定精华或置顶获得额外积分
     */
    Response saveScoreBySetEssenceOrTop(@FormParam("data") String data);

    /**
     * 查询单个订单消费积分信息
     */
    Response getOrderConsumeScoreInfo(@FormParam("data") String order);

    /**
     * 审核通过送积分
     */
    Response saveScoreByCustomerGive(@FormParam("data") String data);

    /**
     * 按照日期对积分收支进行统计
     */
    Response loadScoreSum(@QueryParam("data") String data);

    /**
     * 依据商品出货号获取商品 赚取/消费 积分
     */
    Response loadOlScore(@FormParam("data") String data);

    /**
     * ERP积分流水查询
     */
    Response getUserScoreLogDetailList(@QueryParam("data") String data);

    Response addScoreBatch(@FormParam("data") String order);

    Response getDetail(@QueryParam("data") String data);

    Response haveSignReturnScore(@QueryParam("memGuid") String memGuid);

    Response haveBindPhoneAndEmail(@FormParam("memGuid") String memGuid);

    Response getScoreDetailByOlSeqs(@FormParam("data") String data);

    Response mallScoreForCancel(@FormParam("data") String data);

    Response mallScoreForRefund(@FormParam("data") String data);

    Response submitSomeCannelReturnOrderScore(@FormParam("data") String data);

    Response submitReturnOrderScore(@FormParam("data") String returnInfo);

    Response changeImmediately(@FormParam("data") String data);

    Response ExchangeCard(@FormParam("data") String data);

    Response getSaleScore(@QueryParam("data") String data);

    Response getScoreGrantDetail(@FormParam("data") String data);

    Response getStoreScoreReportInfo(@FormParam("data") String data);

    Response getScoreUseDetail(@FormParam("data") String data);

    Response getScoreByOgsSeq(@FormParam("data") String data);

    Response getLastCurSignInfo(@FormParam("data") String data);

    Response getSignDateThisMonth(@FormParam("data") String data);

    Response kafkaMessage(@FormParam("data") String data);

    Response scoreExchangeVoucher(@FormParam("data") String data);

    Response clearData();

    Response getRecoveryScore(@FormParam("data") String data);
}
