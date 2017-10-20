package com.feiniu.score.service;

import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.util.ShardUtils;
import com.feiniu.score.vo.JobResultVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 积分定时任务服务类
 * @author zhifang.chen
 *
 */
public abstract class AbstractScoreJobService {
	public Log log = LogFactory.getLog(this.getClass());
	


	/*
	 * 处理job
	 */
	public void processJob(final JobResultVo resultVo,final boolean isSlave) throws InterruptedException{
		ExecutorService executorService = Executors.newFixedThreadPool(2);//Executors.newCachedThreadPool();
		// 计数器类
		final CountDownLatch latch = new CountDownLatch(ShardUtils.getDbCount());
		for(int dbIndex = 0; dbIndex < ShardUtils.getDbCount(); dbIndex++){
			final int index = dbIndex;
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					long timeStart = System.currentTimeMillis();
					String dataSourceName=isSlave? DataSourceUtils.DATASOURCE_BASE_NAME + index+ DataSourceUtils.DATASOURCE_SLAVE_KEY: DataSourceUtils.DATASOURCE_BASE_NAME + index;
					try {
						// 处理单个数据库
						JobResultVo dbResultVo = processOneDb(dataSourceName, resultVo.getClass().newInstance());
						//
						resultVo.addProcessResultVo(dbResultVo);
						log.info("数据源:" + (dataSourceName) + "处理完成" + getTimeAndResultStr(timeStart, dbResultVo));
					} catch(Exception e){
						log.error("数据源:" + (dataSourceName) + "处理出错", e);
					} finally {
						// 计数器减一
						latch.countDown();
					}
				}
			});
		}
		// 等待所有的线程都处理完
		latch.await();
		// 关闭线程池
		executorService.shutdown();
	}
	
	/*
	 * 处理单个数据库
	 */
	public JobResultVo processOneDb(String dataSourceName, JobResultVo dbResultVo){
		for(int tableNo = 0; tableNo < ShardUtils.getTableCount(); tableNo++){
			long timeStart = System.currentTimeMillis();
			// 处理单个表
			JobResultVo tableResultVo = processOneTable(dataSourceName, tableNo);
			log.info("数据源:" + dataSourceName + ", 数据表" + tableNo + "处理完成" + getTimeAndResultStr(timeStart, dbResultVo));
			// 
			dbResultVo.addProcessResultVo(tableResultVo);
		}
		return dbResultVo;
	}
	
	/*
	 * 处理单个表(不强制子类实现)
	 */
	public abstract JobResultVo processOneTable(String dataSourceName, int tableNo);
	/*
	 * 获取用时和执行结构
	 */
	public String getTimeAndResultStr(long totalStart, JobResultVo resultVo){
		String msg = " 用时" + ((double)(System.currentTimeMillis() - totalStart)/1000) + "秒," + resultVo.getPrintString();
		return msg;
	}
	
}
