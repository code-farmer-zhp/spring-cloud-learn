package com.feiniu.score.main.onetimes;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.job.service.onetimes.UpdateAvailableServiceImpl;

public class UpdateAvailableMain {

	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

	private ApplicationContext applicationContext;
	
	public static UpdateAvailableServiceImpl updateAvailableService;
	
	public UpdateAvailableMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		updateAvailableService = applicationContext.getBean(UpdateAvailableServiceImpl.class);
		// 数据迁移
		updateAvailableService.processUpdateAvailable();
	}

	public static void main(String[] args) {
		UpdateAvailableMain main = new UpdateAvailableMain();
		main.start();
		// 强制退出
		System.exit(-1);
	}

}
