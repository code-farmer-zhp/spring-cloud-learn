package com.feiniu.score.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.job.service.ScoreFinancialJobServiceImpl;

public class ScoreFinancialJobMain {
	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

	private ApplicationContext applicationContext;
	
	public static ScoreFinancialJobServiceImpl scoreFinancialJobService;
	
	public ScoreFinancialJobMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		scoreFinancialJobService = applicationContext.getBean(ScoreFinancialJobServiceImpl.class);
		// 统计财务积分报表job
		scoreFinancialJobService.processScoreFinancialScheduler();
	}

	public static void main(String[] args) {
		ScoreFinancialJobMain main = new ScoreFinancialJobMain();
		main.start();
		// 强制退出
		System.exit(-1);
	}
}
