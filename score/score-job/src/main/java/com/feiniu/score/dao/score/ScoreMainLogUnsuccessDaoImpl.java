package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.mapper.score.ScoreMainLogUnsuccessMapper;

@Repository
public class ScoreMainLogUnsuccessDaoImpl implements ScoreMainLogUnsuccessDao {
	
	@Autowired
	private ScoreMainLogUnsuccessMapper scoreMainLogUnsuccessMapper;
	
	/**
	 * 记录积分主日志未成功日志
	 * @param scoreLog
	 * @return
	 */
	public int saveScoreMainLogUnsuccessed(ScoreMainLog scoreLog){
		return scoreMainLogUnsuccessMapper.saveScoreMainLogUnsuccessed(scoreLog);
	}
	
	/**
	 * 更新ScoreMainLogUnsuccess的job执行状态
	 * @param smlSeq
	 * @param type
	 * @return
	 */
	public int updateScoreMainLogUnsuccessedJobStatus(Integer smlSeq, Integer type, Integer status){
		return scoreMainLogUnsuccessMapper.updateScoreMainLogUnsuccessedJobStatus(smlSeq, type, status);
	}
	
	/**
	 * 分页获积分主日志未成功日志列表
	 * 
	 * @param mapParam
	 * @return
	 */
	public List<ScoreMainLog> getScoreMainLogUnsuccessedList(Map<String, Object> mapParam){
		return scoreMainLogUnsuccessMapper.getScoreMainLogUnsuccessedList(mapParam);
	}
}
