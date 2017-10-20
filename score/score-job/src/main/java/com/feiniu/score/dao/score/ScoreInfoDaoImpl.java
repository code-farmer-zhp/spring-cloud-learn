package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.score.ScoreInfo;
import com.feiniu.score.job.common.Constant;
import com.feiniu.score.mapper.score.ScoreInfoMapper;

@Repository
public class ScoreInfoDaoImpl implements ScoreInfoDao {

	@Autowired
	private ScoreInfoMapper scoreInfoMapper;
	
	/**
	 * 分页获得积分主表(积分旧库表)列表
	 * @param mapParam
	 * @return
	 */
	@Override
	//@DynamicDataSource(index = 0)
	public List<ScoreInfo> getScoreInfoList(Map<String, Object> mapParam) {
		// 设置连接的数据库
		DataSourceUtils.setCurrentKey(Constant.DATASOURCE_BASE_NAME_DM);
		return scoreInfoMapper.getScoreInfoList(mapParam);
	}
	
}
