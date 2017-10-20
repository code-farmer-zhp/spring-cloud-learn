package com.feiniu.score.dao.score;

import com.feiniu.score.common.NumberUtils;
import com.feiniu.score.entity.score.ScoreReportEntity;
import com.feiniu.score.mapper.score.ScoreFinancialReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ScoreFinancialReportDaoImpl implements ScoreFinancialReportDao {
    @Autowired
    private ScoreFinancialReportMapper scoreFinancialReportMapper;

    @Override
    public List<ScoreReportEntity> shoppingGrantScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.shoppingGrantScore(params);
    }

    @Override
    public List<ScoreReportEntity> bindAndSignGrantScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.bindAndSignGrantScore(params);
    }

    @Override
    public List<ScoreReportEntity> shoppingGrantScoreEffect(Map<String, Object> params) {
        return scoreFinancialReportMapper.shoppingGrantScoreEffect(params);
    }

    @Override
    public List<ScoreReportEntity> selfShoppingUseScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.selfShoppingUseScore(params);
    }

    @Override
    public List<ScoreReportEntity> shoppingReturnConsumeScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.shoppingReturnConsumeScore(params);
    }

    @Override
    public List<ScoreReportEntity> recoveryGrantEffectScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.recoveryGrantEffectScore(params);
    }

    @Override
    public List<ScoreReportEntity> commentGrantScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.commentGrantScore(params);
    }

    @Override
    public List<ScoreReportEntity> recoveryCommentGrantScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.recoveryCommentGrantScore(params);
    }

    @Override
    public int saveReport(List<ScoreReportEntity> entityList) {
        return scoreFinancialReportMapper.saveReport(entityList);
    }

    private int sumValue(Integer one, Integer two) {
        return NumberUtils.getIntValue(one, 0) + NumberUtils.getIntValue(two, 0);
    }

    @Override
    public List<ScoreReportEntity> shoppingToUseScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.shoppingToUseScore(params);
    }

    @Override
    public int saveUseReport(List<ScoreReportEntity> scoreToUseEntities) {
        return scoreFinancialReportMapper.saveUseReport(scoreToUseEntities);
    }

    @Override
    public List<Map<String, Object>> feiniuGrantAndRecoveryScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.feiniuGrantAndRecoveryScore(params);
    }

    @Override
    public List<Map<String, Object>> chouJangUseScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.chouJangUseScore(params);
    }

    @Override
    public List<ScoreReportEntity> otherUseScore(Map<String, Object> params) {
        return scoreFinancialReportMapper.otherUseScore(params);
    }
}
