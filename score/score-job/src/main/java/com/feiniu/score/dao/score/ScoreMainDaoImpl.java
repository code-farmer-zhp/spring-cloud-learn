package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.score.ScoreMain;
import com.feiniu.score.job.common.Constant;
import com.feiniu.score.mapper.score.ScoreMainMapper;

@Repository
public class ScoreMainDaoImpl implements ScoreMainDao {

	@Autowired
	private ScoreMainMapper scoreMainMapper;
	
	/**
	 * 分页获得积分得失概况表(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	@Override
	public List<ScoreMain> getScoreMainList(Map<String, Object> mapParam) {
		// 设置连接的数据库
		DataSourceUtils.setCurrentKey(Constant.DATASOURCE_BASE_NAME_DM);
		return scoreMainMapper.getScoreMainList(mapParam);
	}

	/**
	 * 通过主键获得积分得失概况表(积分旧库表)
	 * @param scmSeq
	 * @return
	 */
	@Override
	public ScoreMain getScoreMainById(Integer scmSeq) {
		// 设置连接的数据库
		DataSourceUtils.setCurrentKey(Constant.DATASOURCE_BASE_NAME_DM);
		return scoreMainMapper.getScoreMainById(scmSeq);
	}

}
