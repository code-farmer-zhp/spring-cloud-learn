package com.feiniu.score.mapper.score;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ScoreMain;

public interface ScoreMainMapper {
	
	/**
	 * 分页获得积分得失概况表(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	List<ScoreMain> getScoreMainList(@Param("mapParam") Map<String,Object> mapParam);
	
	/**
	 * 通过主键获得积分得失概况表(积分旧库表)
	 * @param scmSeq
	 * @return
	 */
	ScoreMain getScoreMainById(@Param("scmSeq") Integer scmSeq);
}
