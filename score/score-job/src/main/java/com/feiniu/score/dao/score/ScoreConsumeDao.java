package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import com.feiniu.score.entity.score.ScoreConsume;

public interface ScoreConsumeDao {
	
	/**
	 * 获得积分流水(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	List<ScoreConsume> getScoreConsumeList(Map<String,Object> mapParam);
}
