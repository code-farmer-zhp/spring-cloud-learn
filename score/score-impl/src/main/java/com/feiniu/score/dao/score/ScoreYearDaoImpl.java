package com.feiniu.score.dao.score;

import com.feiniu.score.common.CacheUtils;
import com.feiniu.score.entity.score.ScoreYear;
import com.feiniu.score.mapper.score.ScoreYearMapper;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.ShardUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class ScoreYearDaoImpl implements ScoreYearDao {

    @Autowired
    ScoreYearMapper scoreYearMapper;

    @Autowired
    private CacheUtils cacheUtils;

    @Override
    public ScoreYear getScoreYear(String memGuid, Date insTime) {
        return scoreYearMapper.getScoreYear(memGuid, insTime, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public Integer saveScoreYear(String memGuid, ScoreYear scoreYear) {
        if (scoreYear.getExpiredScore() == null) {
            scoreYear.setExpiredScore(0);
        }
        return scoreYearMapper.saveScoreYear(memGuid, scoreYear, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public Integer addLockedScoreYear(String memGuid, Integer scySeq,
                                      int totalScore) {
        return scoreYearMapper.addLockedScoreYear(scySeq, totalScore, ShardUtils.getTableNo(memGuid));

    }


    @Override
    public Integer updateLockedAvailableScoreYear(String memGuid,
                                                  Integer scySeq, int totalScore, int lockedScore, int availableScore) {
        return scoreYearMapper.updateLockedAvailableScoreYear(scySeq, totalScore, lockedScore, availableScore, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public ScoreYear getScoreYearByMemGuid(String memGuid, String dueTime) {

        return scoreYearMapper.getScoreYearByMemGuid(memGuid, dueTime, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public int deductAvailableScore(String memGuid, Integer consumeScore, Integer scySeq) {

        return scoreYearMapper.deductAvailableScore(consumeScore, scySeq, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public ScoreYear getScoreYearById(String memGuid, Integer scySeq) {

        return scoreYearMapper.getScoreYearById(scySeq, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public int addScoreById(String memGuid, int scoreCosume, Integer scySeq) {
        return scoreYearMapper.addScoreById(scoreCosume, scySeq, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public int addExpiredScoreById(String memGuid, int scoreCosume, Integer scySeq) {
        return scoreYearMapper.addExpiredScoreById(scoreCosume, scySeq, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public int deductLockedScore(String memGuid, Integer scySeq,
                                 int orderGetScore) {

        return scoreYearMapper.deductLockedScore(scySeq, orderGetScore, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public int addAvailabeScore(String memGuid, Integer scySeq, int scoreGet) {

        return scoreYearMapper.addAvailabeScore(scySeq, scoreGet, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public int deductAvailabeScore(String memGuid, Integer scySeq,
                                   int consumeScore) {

        return scoreYearMapper.deductAvailabeScore(scySeq, consumeScore, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public int updateAvailableScoreYear(String memGuid, Integer scySeq,
                                        Integer totalScore) {
        return scoreYearMapper.updateAvailableScoreYear(scySeq, totalScore, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public int updateExpiredScoreById(String memGuid, Integer scoreRestore,
                                      Integer scySeq) {
        return scoreYearMapper.updateExpiredScoreById(scoreRestore, scySeq, ShardUtils.getTableNo(memGuid));
    }

    /**
     * 分页获得积分年度详细列表
     */
    @Override
    public List<ScoreYear> getScoreYearList(Map<String, Object> mapParam, String memGuid) {
        return scoreYearMapper.getScoreYearList(mapParam, memGuid, ShardUtils.getTableNo(memGuid));
    }

    /**
     * 更新ScoreYear的job执行状态
     */
    @Override
    public int updateScoreYearJobStatus(String memGuid, Integer scySeq, Integer jobStatus) {
        return scoreYearMapper.updateScoreYearJobStatus(scySeq, jobStatus, ShardUtils.getTableNo(memGuid));
    }


    @Override
    public List<ScoreYear> getScoreYearSelf(String memGuid) {
        return scoreYearMapper.getScoreYearSelf(memGuid, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<Map<String, Object>> getScoreYearMall(String memGuid) {
        return scoreYearMapper.getScoreYearMall(memGuid, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public List<ScoreYear> getScoreYearBySerrlerNo(String memGuid, String sellerNo) {
        return scoreYearMapper.getScoreYearBySerrlerNo(memGuid, sellerNo, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public ScoreYear getScoreYearForMall(String memGuid, Date insTime, String sellerNo, Integer scoreType) {
        return scoreYearMapper.getScoreYearForMall(memGuid, insTime, sellerNo, scoreType, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Integer getExpiringScore(String memGuid, String dueTime) {
        String key = "expiringScore:" + memGuid + ":" + dueTime;
        String cacheData = cacheUtils.getCacheData(key);
        if (StringUtils.isEmpty(cacheData)) {
            Integer expiringScore = scoreYearMapper.getExpiringScore(memGuid, dueTime, ShardUtils.getTableNo(memGuid));
            cacheUtils.putCache(key, DateUtil.getSecondsUntilTomorrowZero().intValue(), expiringScore == null ? 0 : expiringScore);
            return expiringScore;
        } else {
            if ("null".equals(cacheData)) {
                return 0;
            }
            return Integer.parseInt(cacheData);
        }
    }

    @Override
    public Integer getAvaliableScore(String memGuid, String cache) {
        Integer avaliableScore;
        if ("true".equals(cache)) {
            avaliableScore = cacheUtils.getAvaliableScore(memGuid);
            if (avaliableScore != null) {
                return avaliableScore;
            }
        }
        avaliableScore = scoreYearMapper.getAvaliableScore(memGuid, ShardUtils.getTableNo(memGuid));
        cacheUtils.putAvaliableScore(memGuid, avaliableScore);
        return avaliableScore;
    }

    @Override
    public Integer getAvaliableScoreNoCache(String memGuid) {
        return scoreYearMapper.getAvaliableScore(memGuid, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public Set<String> getExpireMemGuids(Map<String, Object> mapParam, int tableNo) {
        return scoreYearMapper.getExpireMemGuids(mapParam, tableNo);
    }
}
