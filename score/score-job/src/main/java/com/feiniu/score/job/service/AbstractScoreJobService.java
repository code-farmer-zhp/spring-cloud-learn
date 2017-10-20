package com.feiniu.score.job.service;

import com.feiniu.score.common.Constant;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.util.DateUtil;
import com.feiniu.score.util.ShardUtils;
import com.feiniu.score.vo.JobResultVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 积分定时任务服务类
 * @author zhifang.chen
 *
 */
public abstract class AbstractScoreJobService {
	public CustomLog log = CustomLog.getLogger(this.getClass());
	
	// job执行业务日期
	private Date businessDate;


	/*
	 * 处理job
	 */
	public void processJob(final JobResultVo resultVo) throws InterruptedException{
		ExecutorService executorService = Executors.newFixedThreadPool(2);//Executors.newCachedThreadPool();
		// 计数器类
		final CountDownLatch latch = new CountDownLatch(ShardUtils.getDbCount());
		for(int dbIndex = 0; dbIndex < ShardUtils.getDbCount(); dbIndex++){
			final int index = dbIndex;
			
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					long timeStart = System.currentTimeMillis();
					try {
						String dataSourceName = DataSourceUtils.DATASOURCE_BASE_NAME + index;
						// 处理单个数据库
						JobResultVo dbResultVo = processOneDb(dataSourceName, resultVo.getClass().newInstance());
						
						// 
						resultVo.addProcessResultVo(dbResultVo);
						log.info("数据源:" + (DataSourceUtils.DATASOURCE_BASE_NAME + index) + "处理完成" + getTimeAndResultStr(timeStart, dbResultVo));
					} catch(Exception e){
						log.error("数据源:" + (DataSourceUtils.DATASOURCE_BASE_NAME + index) + "处理出错", e);
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
			if(!(testConditions(dataSourceName, tableNo))){
				continue;
			}

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
	public JobResultVo processOneTable(String dataSourceName, int tableNo){
		return null;
	}
	
	/*
	 * 获取用时和执行结构
	 */
	public String getTimeAndResultStr(long totalStart, JobResultVo resultVo){
		String msg = " 用时" + ((double)(System.currentTimeMillis() - totalStart)/1000) + "秒," + resultVo.getPrintString();
		if(businessDate != null){
			msg += ", 日期:" + getDateStr(businessDate);
		}
		return msg;
	}
	
	/*
	 * 
	 */
	public int getChannel(int channel){
		if (Constant.SCORE_CHANNEL_ORDER_BUY == channel) {
			return Constant.SCORE_CHANNEL_SCORE_ADD_AVAILABLE;
		} else if (Constant.SCORE_CHANNEL_COMMENT_PRODUCT == channel) {
			return Constant.SCORE_CHANNEL_COMMENT_PRODUCT_AVAILABLE;
		} else if (Constant.SCORE_CHANNEL__COMMENT_SET_ESSENCE == channel) {
			return Constant.SCORE_CHANNEL__COMMENT_SET_ESSENCE_AVAILABLE;
		} else if (Constant.SCORE_CHANNEL__COMMENT_SET_TOP == channel) {
			return Constant.SCORE_CHANNEL__COMMENT_SET_TOP_AVAILABLE;
		}
		return channel;
	}
	
	/*
	 * 
	 */
	public String getRemark(int channel){
		if (Constant.SCORE_CHANNEL_SCORE_ADD_AVAILABLE == channel) {
			return "积分变成可用";
		} else if (Constant.SCORE_CHANNEL_COMMENT_PRODUCT_AVAILABLE == channel) {
			return "评论商品获得积分";
		} else if (Constant.SCORE_CHANNEL__COMMENT_SET_ESSENCE_AVAILABLE == channel) {
			return "评论设定精华";
		} else if (Constant.SCORE_CHANNEL__COMMENT_SET_TOP_AVAILABLE == channel) {
			return "评论置顶";
		}
		return "";
	}
	
	/*
	 * 获取某个时间的生效日(10天)
	 */
	public Date getLimitTime(Date time){
		Calendar calendarLimitTime = Calendar.getInstance();
		calendarLimitTime.setTime(time);
		calendarLimitTime.add(Calendar.DATE, Constant.SCORE_EFFECT_DAY);
		return  calendarLimitTime.getTime();
	}
	
	/*
	 * 获取失效时间(下一年的12月31号)
	 */
	public Date getEndTime(Date time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		calendar.add(Calendar.YEAR, 1);
		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DATE, 31);
		return calendar.getTime();
	}
	
	/*
	 * 获取当前日期的前一天
	 */
	public Date getYesterdayDate(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}
	
	/*
	 * 解析时间字符串为时间
	 */
	public Date parseDateTimeStr(String dateStr){
		Date date = null;
		try {
			if(StringUtils.isNotEmpty(dateStr)){
				dateStr = dateStr.trim();
				date = DateUtils.parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
			}
		} catch (ParseException e) {
			log.error("时间解析异常", e);
		}
		return date;
	}
	
	public Date parseDateStr(String dateStr){
		return parseDateTimeStr(dateStr + " 00:00:00");
	}
	
	/*
	 * 格式话日期
	 */
	public String getDateStr(Date date){
		// 日期格式化
		return DateUtil.getFormatDate(date,"yyyy-MM-dd");
	}
	
	/*
	 * 通过用户guid获取数据库号及表号信息
	 */
	public String getDbTableInfo(String memGuid){
		return "  memGuid=" + memGuid + ", db=" + DataSourceUtils.getDataSourceKey(memGuid) + ",  tableNo=" + ShardUtils.getTableNo(memGuid);
	}
	
	/*
	 * 本地测试用
	 */
	public boolean testConditions(String dataSourceName, Integer tableNo){
		return true;
		//return ("dataSourceScore1".equals(dataSourceName) && tableNo == 122);
		//return ("dataSourceScore1".equals(dataSourceName) && tableNo == 121);
	}

	public Date getBusinessDate() {
		return businessDate;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate;
	}
}
