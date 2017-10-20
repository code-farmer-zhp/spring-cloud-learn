package com.feiniu.score.dao.score;

import com.feiniu.score.entity.score.ScoreOrderDetail;
import com.feiniu.score.vo.ReturnJsonVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface ScoreOrderDetailDao {

    int saveScoreOrderDetail(String memGuid, List<ScoreOrderDetail> scoreOrderDetailList);

    List<ScoreOrderDetail> getScoreOrderDetailList(String memGuid, String ogSeq, Integer type);

    List<ScoreOrderDetail> getScoreOrderDetailListByParam(String memGuid, Map<String, Object> mapParam);

    List<Map<String, Object>> loadOlSore(String memGuid, List<Map<String, Object>> lists);


    int updateOrderDetialRgNo(String memGuid, Integer smlSeq, String rgSeq, String rgNo);

    Integer getSourceMode(String memGuid, String ogSeq, String olSeq, String itNo);

    Integer getSodAboutMallCancel(String memGuid, String ogSeq, List<ScoreOrderDetail> scoreMallList);

    Integer getItConsumeScore(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq);

    Integer getItHaveReturnCount(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq);

    Integer getItHaveReturnScore(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq);

    Integer getItHaveRecycleScore(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq);

    BigDecimal getItHaveReturnMoney(String memGuid, ReturnJsonVo.ReturnDetail returnDetail, String ogSeq);

    int updateOrderDetailScySeqBySodSeqs(String memGuid, List<Integer> sodSeqs, Integer scySeq);

    Integer getScoreOrderDetailCountByRlSeqs(String memGuid, List<ReturnJsonVo.ReturnDetail> returnList);

    String getSiteMode(String memGuid, String ogSeq, String olSeq, String itNo);

    List<ScoreOrderDetail> getScoreDetailByOlSeqs(String memGuid, List<Object> olSeqs);

    List<ScoreOrderDetail> getScoreOrderDetailBuyListByOgsSeq(String memGuid, String ogsSeq,Integer type);

	Map<String, Object> getNotScoreMode(String memGuid, String ogSeq,
			String olSeq, String itNo);

    Map<String,Object> getScoreByOgsSeq(String memGuid, String ogsSeq);

    List<Map<String, Object>> loadOlScoreByType(String memGuid, Integer key, List<Map<String,Object>> value);

    int deleteBySmlSeq(String memGuid, Integer smlSeq);

    Integer getScoreOrderDetailBySmlSeq(String memGuid, Integer smlSeq, Integer scoreChannelOrderConsume);

    Integer getRecoveryScore(String memGuid, String rgSeq, String skuSeq);
}
