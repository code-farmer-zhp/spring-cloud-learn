package com.feiniu.score.dao.score;

import com.feiniu.score.common.CacheUtils;
import com.feiniu.score.entity.score.ScoreMember;
import com.feiniu.score.mapper.score.ScoreMemberMapper;
import com.feiniu.score.util.ShardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ScoreMemberDaoImpl implements ScoreMemberDao {

    @Autowired
    private ScoreMemberMapper scoreMemberMapper;

    @Autowired
    private CacheUtils cacheUtils;

    @Override
    public ScoreMember getScoreMember(String memGuid) {
        return scoreMemberMapper.getScoreMember(memGuid, ShardUtils.getTableNo(memGuid));
    }

    @Override
    public int deductScore(String memGuid, Integer consumeScore, Integer type) {
        int count = scoreMemberMapper.deductScore(memGuid, consumeScore, type, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeAvaliableScore(memGuid);
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;
    }

    @Override
    public int updateLockedScoreMember(String memGuid, int totalScore) {
        int count = scoreMemberMapper.updateLockedScoreMember(memGuid, totalScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;

    }

    @Override
    public int updateLockedAvailableScoreMember(String memGuid, int totalScore, int lockedScore, int availableScore) {
        int count = scoreMemberMapper.updateLockedAvailableScoreMember(memGuid, totalScore, lockedScore, availableScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeAvaliableScore(memGuid);
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;
    }

    @Override
    public int saveLockedScoreMember(String memGuid, int totalScore) {
        int count = scoreMemberMapper.saveLockedScoreMember(memGuid, totalScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;

    }

    @Override
    public int addExpiredScore(String memGuid, int scoreConsume) {
        int count = scoreMemberMapper.addExpiredScore(memGuid, scoreConsume, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeAvaliableScore(memGuid);
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;
    }

    @Override
    public int deductLockedScore(String memGuid, int orderGetScore) {
        int count = scoreMemberMapper.deductLockedScore(memGuid, orderGetScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;
    }

    @Override
    public int addScoreBecauseReturn(String memGuid, int consumeScore) {
        int count = scoreMemberMapper.addScoreBecauseReturn(memGuid, consumeScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeAvaliableScore(memGuid);
        return count;
    }

    @Override
    public int saveAvailableScoreMember(String memGuid, Integer availableScore) {
        int count = scoreMemberMapper.saveAvailableScoreMember(memGuid, availableScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeAvaliableScore(memGuid);
        return count;
    }

    @Override
    public int saveExpiredScoreMember(String memGuid, Integer score) {
        int count = scoreMemberMapper.saveExpiredScoreMember(memGuid, score, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;
    }

    @Override
    public int updateAvailableScoreMember(String memGuid, Integer availableScore) {
        int count = scoreMemberMapper.updateAvailableScoreMember(memGuid, availableScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeAvaliableScore(memGuid);
        return count;
    }

    @Override
    public int updateExpiredScore(String memGuid, Integer scoreNumber) {
        int count = scoreMemberMapper.updateExpiredScore(memGuid, scoreNumber, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;
    }

    @Override
    public int addAvailableScore(String memGuid, Integer consumeScore) {
        int count = scoreMemberMapper.addAvailableScore(memGuid, consumeScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeAvaliableScore(memGuid);
        cacheUtils.removeLockedAndExpired(memGuid);
        return count;
    }

    @Override
    public int deductAvailableScore(String memGuid, Integer consumeScore) {
        int count = scoreMemberMapper.deductAvailableScore(memGuid, consumeScore, ShardUtils.getTableNo(memGuid));
        cacheUtils.removeAvaliableScore(memGuid);
        return count;
    }

    @Override
    public ScoreMember getLockedAndExpired(String memGuid) {
        ScoreMember scoreMember = cacheUtils.getLockedAndExpired(memGuid);
        if (scoreMember == null) {
            scoreMember = scoreMemberMapper.getLockedAndExpired(memGuid, ShardUtils.getTableNo(memGuid));
            cacheUtils.putLockedAndExpired(memGuid, scoreMember);
            return scoreMember;
        }
        return scoreMember;
    }


}
