package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ScoreMainLog;

public interface ScoreMainLogUnsuccessDao {
	/**
	 * 记录积分主日志未成功日志
	 * @param scoreLog
	 * @return
	 */
	int saveScoreMainLogUnsuccessed(@Param("sl") ScoreMainLog scoreLog);
	
	/**
	 * 更新ScoreMainLogUnsuccess的job执行状态
	 * @param smlSeq
	 * @param type
	 * @return
	 */
	int updateScoreMainLogUnsuccessedJobStatus(@Param("smlSeq")Integer smlSeq, @Param("type")Integer type, @Param("status")Integer status);
	
	/**
	 * 分页获积分主日志未成功日志列表
	 * 
	 * @param mapParam
	 * @return
	 */
	List<ScoreMainLog> getScoreMainLogUnsuccessedList(@Param("mapParam") Map<String, Object> mapParam);
}
