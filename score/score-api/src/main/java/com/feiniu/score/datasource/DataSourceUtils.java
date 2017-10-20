package com.feiniu.score.datasource;

import com.feiniu.score.util.ShardUtils;

/**
 * 数据源工具类
 * @author puyue.zhou
 *
 */
public class DataSourceUtils {
	

	private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
	
	public static final String DATASOURCE_BASE_NAME = "dataSourceScore";

	public static final String DATASOURCE_SLAVE_KEY = "Slave";

	public static String getDataSourceKey(Object key) {
		return getDataSourceKey(key,false);
	}
	/**
	 * 获取数据源Key
	 */
	public static String getDataSourceKey(Object key,boolean isReadOnly) {
		
		if(key == null) {
			throw new IllegalArgumentException("参数key不允许为空！");
		}
		
		int index = ShardUtils.getDbNo(key);

		if(isReadOnly) {
			return String.valueOf(DATASOURCE_BASE_NAME + index+DATASOURCE_SLAVE_KEY);
		}else{
			return String.valueOf(DATASOURCE_BASE_NAME + index);
		}
		
	}
	
	/**
	 * 设置当前数据源标识
	 */
	public static void setCurrentKey(String key) {
		threadLocal.set(key);
	}
	
	/**
	 * 获取当前数据源标识
	 */
	public static String getCurrentKey() {
		return threadLocal.get();
	}
	
	/**
	 * 删除当前数据源标识
	 */
	public static void removeCurrentKey() {
		threadLocal.remove();
	}

}
