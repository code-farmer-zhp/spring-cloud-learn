package com.feiniu.score.dao.score;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.score.ReturnGroup;
import com.feiniu.score.job.service.onetimes.ScoreDataMigrationJobServiceImpl;
import com.feiniu.score.mapper.score.ReturnGroupMapper;

@Repository
public class ReturnGroupDaoImpl implements ReturnGroupDao {

	@Autowired
	private ReturnGroupMapper returnGroupMapper;
	
	@Value("${oracle.mode.name}")
	private String schema;
	
	/**
	 * 
	 * @param mapParam
	 * @return
	 */
	@Override
	public ReturnGroup getReturnGroupByRgSeq(String rgSeq) {
		// 设置连接的数据库(oracle)
		DataSourceUtils.setCurrentKey(ScoreDataMigrationJobServiceImpl.DATA_SOURCE_CORD);
		return returnGroupMapper.getReturnGroupByRgSeq(schema, rgSeq);
	}

}
