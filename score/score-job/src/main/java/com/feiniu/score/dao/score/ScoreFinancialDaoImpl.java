package com.feiniu.score.dao.score;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.feiniu.score.entity.score.ScoreFinancial;
import com.feiniu.score.mapper.score.ScoreFinancialMapper;

@Repository
public class ScoreFinancialDaoImpl implements ScoreFinancialDao {
	
	@Autowired
	private ScoreFinancialMapper scoreFinancialMapper;

	
	@Override
	public int saveScoreFinancial(ScoreFinancial scoreFinancial) {
		return scoreFinancialMapper.saveScoreFinancial(scoreFinancial);
	}

	/*
	 * 获取前一天的财务报表统计数据
	 */
	@Override
	public ScoreFinancial getPreDayScoreFinancial(Date date) {
		return scoreFinancialMapper.getPreDayScoreFinancial(date);
	}

}
