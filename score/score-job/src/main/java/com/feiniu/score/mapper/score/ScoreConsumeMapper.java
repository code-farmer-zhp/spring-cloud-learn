package com.feiniu.score.mapper.score;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ScoreConsume;

public interface ScoreConsumeMapper {

	/**
	 * 获得积分流水(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	List<ScoreConsume> getScoreConsumeList(@Param("mapParam") Map<String,Object> mapParam);
}
