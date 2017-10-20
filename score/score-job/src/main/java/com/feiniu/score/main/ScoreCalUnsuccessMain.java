package com.feiniu.score.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.job.service.ScoreCalUnsuccessJobServiceImpl;

/**
 * 已经放入score-task 启动
 */
@Deprecated
public class ScoreCalUnsuccessMain {
	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

	private ApplicationContext applicationContext;
	
	public static ScoreCalUnsuccessJobServiceImpl scoreCalUnsuccessJobService;
	
	public ScoreCalUnsuccessMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		scoreCalUnsuccessJobService = applicationContext.getBean(ScoreCalUnsuccessJobServiceImpl.class);
		// 处理score-cal没有成功的记录
		scoreCalUnsuccessJobService.processScoreCalUnsuccessScheduler();
	}

	public static void main(String[] args) {
		ScoreCalUnsuccessMain main = new ScoreCalUnsuccessMain();
		main.start();
	}
}
