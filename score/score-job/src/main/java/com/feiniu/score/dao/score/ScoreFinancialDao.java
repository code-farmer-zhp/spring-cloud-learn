package com.feiniu.score.dao.score;

import java.util.Date;

import com.feiniu.score.entity.score.ScoreFinancial;

public interface ScoreFinancialDao {
	
	int saveScoreFinancial(ScoreFinancial scoreFinancial);
	
	/*
	 * 获取前一天的财务报表统计数据
	 */
	ScoreFinancial getPreDayScoreFinancial(Date date);
}
