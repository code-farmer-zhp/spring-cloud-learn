package com.feiniu.score.datasource;

import java.util.Map;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 多数据源封装
 * @author puyue.zhou
 *
 */
public class MultipleDataSource extends AbstractRoutingDataSource {
	
	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceUtils.getCurrentKey();
	}

}
