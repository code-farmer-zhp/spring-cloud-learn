package com.feiniu.score.mapper.score;


import com.feiniu.score.entity.score.ScoreOrderDetail;
import com.feiniu.score.vo.ReturnJsonVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface ScoreOrderDetailMapper {

    int saveScoreOrderDetail(@Param("sodList") List<ScoreOrderDetail> scoreOrderDetailList, @Param("tableNo") Integer tableNo);

    List<ScoreOrderDetail> getScoreOrderDetailList(@Param("memGuid") String memGuid,
                                                   @Param("ogSeq") String ogSeq, @Param("type") Integer type, @Param("tableNo") Integer tableNo);

    List<ScoreOrderDetail> getScoreOrderDetailListByParam(
            @Param("mapParam") Map<String, Object> mapParam,
            @Param("tableNo") Integer tableNo);


    List<Map<String, Object>> loadOlSore(@Param("memGuid") String memGuid,
                                         @Param("lists") List<Map<String, Object>> lists, @Param("tableNo") int tableNo);


    Integer getSourceMode(@Param("memGuid") String memGuid, @Param("ogSeq") String ogSeq, @Param("olSeq") String olSeq,
                          @Param("itNo") String itNo, @Param("tableNo") int tableNo);


    int updateOrderDetialRgNo(@Param("memGuid") String memGuid, @Param("smlSeq") Integer smlSeq, @Param("rgSeq") String rgSeq, @Param("rgNo") String rgNo, @Param("tableNo") int tableNo);


    int updateOrderDetailScySeqBySodSeqs(@Param("memGuid") String memGuid, @Param("sodSeqs") List<Integer> sodSeqs, @Param("scySeq") Integer scySeq, @Param("tableNo") int tableNo);

    Integer getSodAboutMallCancel(@Param("memGuid") String memGuid, @Param("ogSeq") String ogSeq, @Param("sodList") List<ScoreOrderDetail> scoreMallList, @Param("tableNo") int tableNo);

    Integer getItConsumeScore(@Param("memGuid") String memGuid, @Param("returnDetail") ReturnJsonVo.ReturnDetail returnDetail, @Param("ogSeq") String ogSeq, @Param("tableNo") int tableNo);

    Integer getItHaveReturnCount(@Param("memGuid") String memGuid, @Param("returnDetail") ReturnJsonVo.ReturnDetail returnDetail, @Param("ogSeq") String ogSeq, @Param("tableNo") int tableNo);

    Integer getItHaveReturnScore(@Param("memGuid") String memGuid, @Param("returnDetail") ReturnJsonVo.ReturnDetail returnDetail, @Param("ogSeq") String ogSeq, @Param("tableNo") int tableNo);

    Integer getItHaveRecycleScore(@Param("memGuid") String memGuid, @Param("returnDetail") ReturnJsonVo.ReturnDetail returnDetail, @Param("ogSeq") String ogSeq, @Param("tableNo") int tableNo);

    BigDecimal getItHaveReturnMoney(@Param("memGuid") String memGuid, @Param("returnDetail") ReturnJsonVo.ReturnDetail returnDetail, @Param("ogSeq") String ogSeq, @Param("tableNo") int tableNo);


    Integer getScoreOrderDetailCountByRlSeqs(@Param("memGuid") String memGuid,
                                             @Param("lists") List<ReturnJsonVo.ReturnDetail> returnList,
                                             @Param("tableNo") int tableNo);

    String getSiteMode(@Param("memGuid") String memGuid, @Param("ogSeq") String ogSeq, @Param("olSeq") String olSeq,
                       @Param("itNo") String itNo, @Param("tableNo") int tableNo);

    Map<String, Object> getNotScoreMode(@Param("memGuid") String memGuid, @Param("ogSeq") String ogSeq, @Param("olSeq") String olSeq,
                                        @Param("itNo") String itNo, @Param("tableNo") int tableNo);

    List<ScoreOrderDetail> getScoreDetailByOlSeqs(@Param("memGuid") String memGuid,
                                                  @Param("olSeqs") List<Object> olSeqs,
                                                  @Param("tableNo") int tableNo);

    List<ScoreOrderDetail> getScoreOrderDetailBuyListByOgsSeq(@Param("memGuid") String memGuid,
                                                              @Param("ogsSeq") String ogsSeq,
                                                              @Param("type") Integer type,
                                                              @Param("tableNo") int tableNo);

    Map<String, Object> getScoreByOgsSeq(@Param("memGuid") String memGuid, @Param("ogsSeq") String ogsSeq, @Param("tableNo") int tableNo);

    List<Map<String, Object>> loadOlScoreByType(@Param("type") Integer type,
                                                @Param("lists") List<Map<String, Object>> lists, @Param("tableNo") int tableNo);

    int deleteBySmlSeq(@Param("memGuid") String memGuid, @Param("smlSeq") Integer smlSeq, @Param("tableNo") int tableNo);

    int getScoreOrderDetailBySmlSeq(@Param("memGuid") String memGuid, @Param("smlSeq") Integer smlSeq, @Param("type") Integer type, @Param("tableNo") int tableNo);

    Integer getRecoveryScore(@Param("memGuid") String memGuid, @Param("rgSeq") String rgSeq, @Param("skuSeq") String skuSeq, @Param("tableNo") int tableNo);
}
