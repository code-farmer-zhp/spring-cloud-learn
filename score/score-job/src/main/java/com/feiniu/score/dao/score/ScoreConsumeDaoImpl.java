package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.score.ScoreConsume;
import com.feiniu.score.job.common.Constant;
import com.feiniu.score.mapper.score.ScoreConsumeMapper;

@Repository
public class ScoreConsumeDaoImpl implements ScoreConsumeDao {

	@Autowired
	private  ScoreConsumeMapper  scoreConsumeMapper;
	
	/**
	 * 获得积分流水(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	@Override
	public List<ScoreConsume> getScoreConsumeList(Map<String, Object> mapParam) {
		// 设置连接的数据库
		DataSourceUtils.setCurrentKey(Constant.DATASOURCE_BASE_NAME_DM);
		return scoreConsumeMapper.getScoreConsumeList(mapParam);
	}
}
