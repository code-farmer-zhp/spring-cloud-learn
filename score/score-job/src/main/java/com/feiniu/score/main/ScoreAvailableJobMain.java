package com.feiniu.score.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.job.service.ScoreAvailableJobServiceImpl;

/*
 * 积分生效Main
 * 已经为单独的job
 */
@Deprecated
public class ScoreAvailableJobMain {
	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

	private ApplicationContext applicationContext;
	
	public static ScoreAvailableJobServiceImpl scoreJobService;
	
	public ScoreAvailableJobMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		scoreJobService = applicationContext.getBean(ScoreAvailableJobServiceImpl.class);
		// 处理积分生效
		scoreJobService.executeJob();
	}

	public static void main(String[] args) {
		ScoreAvailableJobMain main = new ScoreAvailableJobMain();
		main.start();
		// 强制退出
		System.exit(-1);
	}
}
