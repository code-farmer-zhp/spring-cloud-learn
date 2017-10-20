package com.feiniu.score.mapper.score;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ScoreInfo;

public interface ScoreInfoMapper {
	
	/**
	 * 分页获得积分主表(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	List<ScoreInfo> getScoreInfoList(@Param("mapParam") Map<String,Object> mapParam);
}
