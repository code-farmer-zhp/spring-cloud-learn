package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.entity.mrst.Pkad;
import com.feiniu.score.entity.score.ScoreOrderDetail;
import com.feiniu.score.vo.CrmScoreJsonVo;
import com.feiniu.score.vo.OrderJsonVo;
import com.feiniu.score.vo.ReturnJsonVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 积分的返回和回收
 *
 * @author peng.zhou
 */
public interface ScoreCommonDao {

    /**
     * 退货回收积分
     */
    void returnCommentScoreBecauseReturnProduct(String memGuid, Integer commentSeq, ScoreOrderDetail sod);

    void processCrmScore(String memGuid, CrmScoreJsonVo crmScoreJsonVo);

    void processMallOrderCancel(String memGuid, String ogSeq, String packageNo);

    int addSelfLockedScore(String memGuid, Date insTime, Integer getScore, Integer smlSeq);

    int addMallLockedScore(String memGuid, Date insTime, Integer getScore, Integer smlSeq,
                           String sellerNo, Integer scoreType);

    void processOrderScore(String memGuid, String ogSeq);

    void processReturnOrderScore(String memGuid, ReturnJsonVo returnJsonVo, String pay);

    int computeScoreGet(ReturnJsonVo.ReturnDetail returnDetail, ScoreOrderDetail sodBuy,
                        boolean isMall, Integer countHaveReturn);

    int getScoreGet(ScoreOrderDetail consumeDetail, List<ScoreOrderDetail> listReturn, String olsSeq, Integer qty);

    /**
     * 处理提交订单
     */
    void saveSubmitOrderDetail(String memGuid, OrderJsonVo orderJsonVo, Integer consumeScore);

    Map<String, Object> buildDetailAndComputeScore(OrderJsonVo orderJsonVo);

    void deductOrderComsumeScore(String memGuid, Integer consumeScore,
                                 String ogSeq, String ogNo, String provinceId);

    void addSelfAvailableScore(String memGuid, Date insTime, Integer getScore, Integer smlSeq);

    boolean rollbackScore(String memGuid, String ogSeq, String ogNo);

    void processPkadScore(String memGuid, String data, Pkad pkad);

    Integer deductScoreImmediately(String memGuid, String remark, Integer channel,
                                   Integer consumeScore, String ogSeq, String ogNo, String provinceId);

    void rollbackScoreDirect(String memGuid, String ogSeq);

    Integer rollbackExChangeVoucherScore(String memGuid, Integer smlSeq);

    void saveScoreByCommentProduct(String memGuid, String data);

    void saveScoreBySetEssenceOrTop(String memGuid, String data);

    void addScoreBecauseComment(String data, Integer type, Integer channel, String remark);

    int computeScore(OrderJsonVo orderJsonVo);

    void submitOrderKafkaMsgCompensate(String memGuid, OrderJsonVo orderJsonVo);

    JSONObject buildSubmitOrderMsg(String memGuid, OrderJsonVo orderJsonVo);

    void deductScoreForVoucher(String memGuid, String name, Integer score, String uniqueKey);

    void rollbackScoreByUniqueKey(String memGuid, String uniqueKey);
}
