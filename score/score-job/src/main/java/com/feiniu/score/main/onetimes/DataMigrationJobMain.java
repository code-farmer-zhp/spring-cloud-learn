package com.feiniu.score.main.onetimes;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.feiniu.score.job.service.onetimes.ScoreDataMigrationJobServiceImpl;


/*
 * 数据迁移Main
 */
public class DataMigrationJobMain {
	private static final String APPLICATION_CONTEXT_CONFIG = "/applicationContext_main.xml";

	private ApplicationContext applicationContext;
	
	public static ScoreDataMigrationJobServiceImpl scoreDataMigrationJobService;
	
	public DataMigrationJobMain() {
		this.applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_CONFIG);
	}

	public void start() {
		scoreDataMigrationJobService = applicationContext.getBean(ScoreDataMigrationJobServiceImpl.class);
		// 数据迁移
		scoreDataMigrationJobService.migrationScoreMainData();
	}

	public static void main(String[] args) {
		DataMigrationJobMain main = new DataMigrationJobMain();
		main.start();
		// 强制退出
		System.exit(-1);
	}
}
