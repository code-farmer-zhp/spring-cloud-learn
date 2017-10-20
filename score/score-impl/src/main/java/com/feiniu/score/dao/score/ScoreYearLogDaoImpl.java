package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.feiniu.score.datasource.DynamicDataSource;
import com.feiniu.score.entity.score.ScoreYearLog;
import com.feiniu.score.mapper.score.ScoreYearLogMapper;
import com.feiniu.score.util.ShardUtils;

@Repository
public class ScoreYearLogDaoImpl implements ScoreYearLogDao {

	@Autowired
	ScoreYearLogMapper scoreYearLogMapper;
	@Override
	public int saveScoreYearLog(String memGuid, ScoreYearLog scoreYearLog) {
		return scoreYearLogMapper.saveScoreYearLog(scoreYearLog, ShardUtils.getTableNo(memGuid));		
	}
	
	@Override
	public List<ScoreYearLog> getScoreYearLogByLM(Integer smlSeq, String memGuid) {
		return scoreYearLogMapper.getScoreYearLogByLM(smlSeq,memGuid,ShardUtils.getTableNo(memGuid));
	}

	@Override
	public List<Map<String, Object>> getScoreYearLogBySmlSeqs(String smlSeqs, String memGuid) {
		return scoreYearLogMapper.getScoreYearLogBySmlSeqs(smlSeqs, memGuid, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public int deleteScoreYearLogBySmlSeq(String memGuid, Integer smlSeq) {
		return scoreYearLogMapper.deleteScoreYearLogBySmlSeq(memGuid, smlSeq, ShardUtils.getTableNo(memGuid));
	}
}
