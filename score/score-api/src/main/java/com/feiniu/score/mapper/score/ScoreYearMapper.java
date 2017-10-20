package com.feiniu.score.mapper.score;

import com.feiniu.score.entity.score.ScoreYear;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ScoreYearMapper {

    ScoreYear getScoreYear(@Param("memGuid") String memGuid, @Param("insTime") Date insTime, @Param("tableNo") int tableNo);

    Integer addLockedScoreYear(@Param("scySeq") Integer scySeq, @Param("scoreGet") int totalScore,
                               @Param("tableNo") int tableNo);

    Integer updateLockedAvailableScoreYear(@Param("scySeq") Integer scySeq,
                                           @Param("totalScore") int totalScore,
                                           @Param("lockedScore") int lockedScore,
                                           @Param("availableScore") int availableScore,
                                           @Param("tableNo") int tableNo);


    Integer saveScoreYear(@Param("memGuid") String memGuid, @Param("sy") ScoreYear scoreYear,
                          @Param("tableNo") int tableNo);

    ScoreYear getScoreYearByMemGuid(@Param("memGuid") String memGuid, @Param("dueTime") String dueTime, @Param("tableNo") int tableNo);

    int deductAvailableScore(@Param("consumeScore") Integer consumeScore, @Param("scySeq") Integer scySeq, @Param("tableNo") int tableNo);


    ScoreYear getScoreYearById(@Param("scySeq") Integer scySeq, @Param("tableNo") int tableNo);

    int addScoreById(@Param("consumeScore") int consumeScore, @Param("scySeq") Integer scySeq, @Param("tableNo") int tableNo);

    int addExpiredScoreById(@Param("consumeScore") int scoreCosume, @Param("scySeq") Integer scySeq, @Param("tableNo") int tableNo);

    int deductLockedScore(@Param("scySeq") Integer scySeq, @Param("scoreGet") int orderGetScore, @Param("tableNo") int tableNo);

    int addAvailabeScore(@Param("scySeq") Integer scySeq, @Param("scoreGet") int scoreGet, @Param("tableNo") int tableNo);

    int deductAvailabeScore(@Param("scySeq") Integer scySeq, @Param("consumeScore") int consumeScore, @Param("tableNo") int tableNo);

    int updateAvailableScoreYear(@Param("scySeq") Integer scySeq, @Param("totalScore") Integer totalScore, @Param("tableNo") int tableNo);

    int updateExpiredScoreById(@Param("scoreNumber") Integer scoreNumber, @Param("scySeq") Integer scySeq, @Param("tableNo") int tableNo);

    /**
     * 分页获得积分年度详细列表(积分失效要用)
     */
    List<ScoreYear> getScoreYearList(@Param("mapParam") Map<String, Object> mapParam, @Param("memGuid") String memGuid, @Param("tableNo") Integer tableNo);

    /**
     * 更新ScoreYear的job执行状态
     */
    int updateScoreYearJobStatus(@Param("scySeq") Integer scySeq, @Param("status") Integer status, @Param("tableNo") int tableNo);

    /**
     * 统计积分年度失效积分
     */
    Integer getExpired(@Param("dueTime") String dueTime, @Param("tableNo") int tableNo);


    /**
     * 自营积分
     */
    List<ScoreYear> getScoreYearSelf(@Param("memGuid") String memGuid, @Param("tableNo") int tableNo);

    /**
     * 商城积分
     */
    List<Map<String, Object>> getScoreYearMall(@Param("memGuid") String memGuid, @Param("tableNo") int tableNo);

    /**
     * 根据商家的编号查询
     */
    List<ScoreYear> getScoreYearBySerrlerNo(@Param("memGuid") String memGuid, @Param("sellerNo") String sellerNo, @Param("tableNo") int tableNo);

    ScoreYear getScoreYearForMall(@Param("memGuid") String memGuid, @Param("insTime") Date insTime, @Param("sellerNo") String sellerNo, @Param("scoreType") Integer scoreType, @Param("tableNo") int tableNo);

    Integer getExpiringScore(@Param("memGuid") String memGuid, @Param("dueTime") String dueTime, @Param("tableNo") int tableNo);

    Integer getAvaliableScore(@Param("memGuid") String memGuid, @Param("tableNo") int tableNo);

    Set<String> getExpireMemGuids(@Param("mapParam") Map<String, Object> mapParam, @Param("tableNo") int tableNo);
}
