package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import com.feiniu.score.entity.score.ScoreInfo;

public interface ScoreInfoDao {
	
	/**
	 * 分页获得积分主表(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	List<ScoreInfo> getScoreInfoList(Map<String,Object> mapParam);
}
