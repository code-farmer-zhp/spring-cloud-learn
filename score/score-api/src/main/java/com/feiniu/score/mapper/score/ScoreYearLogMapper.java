package com.feiniu.score.mapper.score;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ScoreYearLog;

public interface ScoreYearLogMapper {

	int saveScoreYearLog(@Param("syl") ScoreYearLog scoreYearLog, @Param("tableNo") int tableNo);
 

	List<ScoreYearLog> getScoreYearLogByLM(@Param("smlSeq") Integer smlSeq, @Param("memGuid")String memGuid,
			@Param("tableNo") int tableNo);
	
	List<Map<String, Object>> getScoreYearLogBySmlSeqs(@Param("smlSeqs") String smlSeqs, @Param("memGuid")String memGuid,
			@Param("tableNo") int tableNo);
	
	int deleteScoreYearLogBySmlSeq(@Param("memGuid") String memGuid, @Param("smlSeq") Integer smlSeq, @Param("tableNo") int tableNo);

}
