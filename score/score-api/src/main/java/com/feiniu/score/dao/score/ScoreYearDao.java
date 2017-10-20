package com.feiniu.score.dao.score;

import com.feiniu.score.entity.score.ScoreYear;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ScoreYearDao {

    ScoreYear getScoreYear(String memGuid, Date insTime);

    Integer addLockedScoreYear(String memGuid, Integer scySeq, int totalScore);

    Integer updateLockedAvailableScoreYear(String memGuid, Integer scySeq, int totalScore, int lockedScore, int availableScore);

    Integer saveScoreYear(String memGuid, ScoreYear scoreYear);

    ScoreYear getScoreYearByMemGuid(String memGuid, String dueTime);

    int deductAvailableScore(String memGuid, Integer consumeScore, Integer scySeq);


    ScoreYear getScoreYearById(String memGuid, Integer scySeq);

    int addScoreById(String memGuid, int scoreCosume, Integer scySeq);

    int addExpiredScoreById(String memGuid, int scoreCosume, Integer scySeq);

    int deductLockedScore(String memGuid, Integer scySeq, int orderGetScore);

    int addAvailabeScore(String memGuid, Integer scySeq, int scoreGet);

    int deductAvailabeScore(String memGuid, Integer scySeq, int consumeScore);

    int updateAvailableScoreYear(String memGuid, Integer scySeq,
                                 Integer totalScore);

    int updateExpiredScoreById(String memGuid, Integer scoreRestore,
                               Integer scySeq);

    /**
     * 分页获得积分年度详细列表
     */
    List<ScoreYear> getScoreYearList(Map<String, Object> mapParam, String memGuid);

    /**
     * 更新ScoreYear的job执行状态
     */
    int updateScoreYearJobStatus(String memGuid, Integer scySeq, Integer jobStatus);

    /**
     * 自营积分
     */
    List<ScoreYear> getScoreYearSelf(String memGuid);

    /**
     * 商城积分
     */
    List<Map<String, Object>> getScoreYearMall(String memGuid);

    /**
     * 根据商家编号查找积分信息
     */
    List<ScoreYear> getScoreYearBySerrlerNo(String memGuid, String sellerNo);

    /**
     * 商家对应的积分信息
     */
    ScoreYear getScoreYearForMall(String memGuid, Date insTime, String sellerNo, Integer scoreType);


    Integer getExpiringScore(String memGuid, String dueTime);

    Integer getAvaliableScore(String memGuid, String cache);

    Integer getAvaliableScoreNoCache(String memGuid);

    Set<String> getExpireMemGuids(Map<String, Object> mapParam, int tableNo);
}
