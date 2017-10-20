package com.feiniu.score.main.onetimes;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.job.service.onetimes.ScoreUpdateRgNoJobServiceImpl;

public class ScoreUpdateRgNoJobMain {
	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

	private ApplicationContext applicationContext;
	
	public static ScoreUpdateRgNoJobServiceImpl scoreUpdateRgNoJobService;
	
	public ScoreUpdateRgNoJobMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		scoreUpdateRgNoJobService = applicationContext.getBean(ScoreUpdateRgNoJobServiceImpl.class);
		// 批量更新rgSeq
		scoreUpdateRgNoJobService.processUpdateRgNo();
	}

	public static void main(String[] args) {
		ScoreUpdateRgNoJobMain main = new ScoreUpdateRgNoJobMain();
		main.start();
		// 强制退出
		System.exit(-1);
	}
}
