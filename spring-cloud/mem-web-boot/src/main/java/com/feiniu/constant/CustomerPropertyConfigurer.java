/**
 * Package name:com.feiniu.utils
 * File name:CustomerPropertyConfigurer.java
 * Date:2015年11月19日-下午7:33:11
 * feiniu.com Inc.Copyright (c) 2013-2015 All Rights Reserved.
 *
 */
package com.feiniu.constant;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @ClassName CustomerPropertyConfigurer
 * @Description 配置读取
 * @date 2015年11月19日 下午7:33:11
 * @author jun.wu
 * @version 1.0.0
 *
 */
public class CustomerPropertyConfigurer extends PropertyPlaceholderConfigurer {
	// 存储 properties
	private static Map<String, Object> properties = null;

	/**
	 * 根据key获取properties值
	 * <pre>
	 * 假如没有对应key则返回null
	 * </pre>
	 * @param key
	 * @return
	 */
	public static Object getProperty(String key) {
		return properties.get(key);
	}
	
	/**
	 * 重写 processProperties
	 */
	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {
		
		properties = new HashMap<String, Object>();
		
		for (Entry<Object, Object> entry : props.entrySet()) {
			String key = String.valueOf(entry.getKey());
			Object value = entry.getValue();
			properties.put(key, value);
		}

		super.processProperties(beanFactoryToProcess, props);
	}
}
