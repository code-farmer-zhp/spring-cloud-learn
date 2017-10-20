package com.feiniu.member.util;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

/**
 * 扩展配置文件加载
 */
public class PropertyPlaceholderConfigurerEx extends PropertyPlaceholderConfigurer {

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		super.processProperties(beanFactoryToProcess, props);

		// 添加到系统变量中
		SystemEnv.addProperty(props);
		// 运营后台系统变量
		//com.feiniu.common.core.SystemEnv.addProperty(props);
		// 初始化Log4j
		PropertyConfigurator.configure(props);
	}
}
