package com.feiniu.score.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.job.service.ScoreExpiredJobServiceImpl;

/*
 * 积分失效Main
 */
public class ScoreExpiredJobMain {
	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

	private ApplicationContext applicationContext;
	
	public static ScoreExpiredJobServiceImpl scoreJobService;
	
	public ScoreExpiredJobMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		scoreJobService = applicationContext.getBean(ScoreExpiredJobServiceImpl.class);
		// 处理积分失效
		scoreJobService.executeJob();
	}

	public static void main(String[] args) {
		ScoreExpiredJobMain main = new ScoreExpiredJobMain();
		main.start();
		// 强制退出
		System.exit(-1);
	}
}
