package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.score.ReportPcs;
import com.feiniu.score.job.service.onetimes.ScoreDataMigrationJobServiceImpl;
import com.feiniu.score.mapper.score.ReportPcsMapper;

@Repository
public class ReportPcsDaoImpl implements ReportPcsDao {

	@Autowired
	private ReportPcsMapper reportPcsMapper;
	
	@Value("${oracle.mode.name}")
	private String schema;
	
	/**
	 * 根据条件获得出退貨回報檔(UCORDERP数据库)列表
	 * @param mapParam
	 * @return
	 */
	@Override
	public List<ReportPcs> getReportPcsList(Map<String, Object> mapParam) {
		// 设置连接的数据库(oracle)
		DataSourceUtils.setCurrentKey(ScoreDataMigrationJobServiceImpl.DATA_SOURCE_CORD);
		return reportPcsMapper.getReportPcsList(mapParam, schema);
	}

}
