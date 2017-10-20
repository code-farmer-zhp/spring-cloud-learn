package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import com.feiniu.score.entity.score.ScoreYearLog;

public interface ScoreYearLogDao {

	int saveScoreYearLog(String memGuid, ScoreYearLog scoreYearLog);

	List<ScoreYearLog> getScoreYearLogByLM(Integer smlSeq, String memGuid);
	
	List<Map<String, Object>> getScoreYearLogBySmlSeqs(String smlSeqs, String memGuid);

	int deleteScoreYearLogBySmlSeq(String memGuid, Integer smlSeq);

}
