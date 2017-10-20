package com.feiniu.score.mapper.score;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ScoreFinancial;

public interface ScoreFinancialMapper {
	
	int saveScoreFinancial(@Param("sf") ScoreFinancial scoreFinancial);
	
	ScoreFinancial getPreDayScoreFinancial(@Param("edate") Date date);
	
}
