package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import com.feiniu.score.entity.score.ScoreMain;

public interface ScoreMainDao {
	
	/**
	 * 分页获得积分得失概况表(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	List<ScoreMain> getScoreMainList(Map<String,Object> mapParam);
	
	/**
	 * 通过主键获得积分得失概况表(积分旧库表)
	 * @param scmSeq
	 * @return
	 */
	ScoreMain getScoreMainById(Integer scmSeq);
}
