package com.feiniu.score.job.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.feiniu.score.common.Constant;
import com.feiniu.score.dao.score.ScoreFinancialDao;
import com.feiniu.score.dao.score.ScoreMainLogDao;
import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.score.ScoreFinancial;
import com.feiniu.score.entity.score.ScoreMainLog;
import com.feiniu.score.mapper.score.ScoreMainLogMapper;
import com.feiniu.score.mapper.score.ScoreYearMapper;
import com.feiniu.score.util.ShardUtils;

@Service
public class ScoreFinancialJobServiceImpl extends AbstractScoreJobService {
	@Autowired
	private ScoreFinancialDao scoreFinancialDao;
	
	@Autowired
	private ScoreMainLogMapper scoreMainLogMapper;
	
	@Autowired
	private ScoreYearMapper scoreYearMapper;
	
	@Autowired
	private ScoreMainLogDao scoreMainLogDao;
	
	// 统计时间字符串
	@Value("${score.job.score.financial.date}")
	private String dateStr;
	
	private Date date;
	
	ExecutorService executorService = Executors.newFixedThreadPool(2);//Executors.newCachedThreadPool();
	
	/*
	 * 统计财务积分报表job
	 */
	public void processScoreFinancialScheduler(){
		long totalStart = System.currentTimeMillis();
		try {
			// 统计时间
			date = parseDateTimeStr(dateStr);
			if(date == null){
				date = getYesterdayDate();
			}
			setBusinessDate(date);
			
			log.info("开始统计财务积分报表job, 日期:" + getDateStr(getBusinessDate()));
			// 统计时间
			final Date calTime = date;
						
			List<FutureTask<ScoreFinancial>> taskList = new ArrayList<FutureTask<ScoreFinancial>>();
			
			FutureTask<ScoreFinancial> task = null;
			for(int dbIndex = 0; dbIndex < ShardUtils.getDbCount(); dbIndex++){
				final int index = dbIndex;
				task = new FutureTask<ScoreFinancial>(
						new Callable<ScoreFinancial>() {
							public ScoreFinancial call() {
								// 统计单个数据库财务积分报表
								return processOneDbScoreFinancial(DataSourceUtils.DATASOURCE_BASE_NAME + index, calTime);
							}
						});
				
				taskList.add(task);
				// 提交任务
				executorService.submit(task);
			}
			
			List<ScoreFinancial> sfList = new ArrayList<ScoreFinancial>();
			for(int i = 0; i < taskList.size(); i++){
				ScoreFinancial sf = taskList.get(i).get();
				sfList.add(sf);
				log.info("财务积分报表" + (DataSourceUtils.DATASOURCE_BASE_NAME + i) + "统计完成");
			}
			
			// 统计当天财务积分报表
			calScoreFinancial(sfList, calTime);
			// 关闭线程池
			executorService.shutdown();
			long totalEnd = System.currentTimeMillis();
			log.info("结束统计财务积分报表job," + " 用时" + ((double)(totalEnd - totalStart)/1000) + "秒");
		} catch (Exception e) {
			log.error("统计财务积分报表job失败", e);
		}
	}
	
	
	/*
	 * 统计当天财务积分报表
	 */
	public void calScoreFinancial(List<ScoreFinancial> sfList, Date calTime){
		ScoreFinancial currentSf = new ScoreFinancial();
		// 日期
		currentSf.setEdate(calTime);
		// 设置创建人
		currentSf.setCreateId("web-job");
		// 当日回收  （目前没同统计）
		currentSf.setRecycling(0);
		// 适用网站 1/飞牛网
		currentSf.setSuitWebsite(Constant.LOAD_SCORE_DEFAULT_WEBSIT);
		for(int i = 0; i < sfList.size(); i++){
			ScoreFinancial sf = sfList.get(i);
			// 当日发放
			currentSf.setExtend(getValue(currentSf.getExtend()) + getValue(sf.getExtend()));
			// 当日生效
			currentSf.setEffected(getValue(currentSf.getEffected()) + getValue(sf.getEffected()));
			// 当日失效
			currentSf.setFailure(getValue(currentSf.getFailure()) + getValue(sf.getFailure()));
			// 当日使用
			currentSf.setUsed(getValue(currentSf.getUsed()) + getValue(sf.getUsed()));
			// 退订还点
			currentSf.setReciperare(getValue(currentSf.getReciperare()) + getValue(sf.getReciperare()));
			// 未生效余额
			currentSf.setLeftToBeEffective(getValue(currentSf.getLeftToBeEffective()) + getValue(sf.getLeftToBeEffective()));
		}
		
		// 收支(E = 当日生效 - 当日使用 - 当日失效  + 退订还点)
		currentSf.setRecycling(currentSf.getEffected() - currentSf.getUsed() - currentSf.getFailure() + currentSf.getReciperare());
		
		// 设置连接的数据库
		DataSourceUtils.setCurrentKey(DataSourceUtils.DATASOURCE_BASE_NAME);
		// 前一天的统计记录
		ScoreFinancial preSf = scoreFinancialDao.getPreDayScoreFinancial(calTime);
		if (preSf == null) {
			preSf = new ScoreFinancial();
		}
		
		// 已生效余额 (F = 前日 + 收支)
		currentSf.setLeftEffected(getValue(preSf.getLeftEffected()) + currentSf.getRecycling());
		// 未生效余额 (G = 前日 + 当日发放 - 当日生效)
		//currentSf.setLeftToBeEffective(getValue(preSf.getLeftToBeEffective()) + currentSf.getExtend() - currentSf.getEffected());
		// 已发送未使用总额  (H = 已生效余额  + 未生效余额)
		currentSf.setEbnu(currentSf.getLeftEffected() + currentSf.getLeftToBeEffective());
		
		// 保存财务积分报表记录
		scoreFinancialDao.saveScoreFinancial(currentSf);
	}
	
	/*
	 * 统计单个数据库财务积分报表
	 */
	public ScoreFinancial processOneDbScoreFinancial(String dataSourceName, Date calTime){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(calTime);
		boolean lastDayOfYear = false;
		if(calendar.get(Calendar.MONTH) == 11 && calendar.get(Calendar.DATE) == 31){
			// 判断是否为12月31日
			lastDayOfYear = true;
		}
		
		ScoreFinancial sf = new ScoreFinancial();
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("insTime", calTime);
		// 实际支付日期(字符串类型)
		paramMap.put("actualTime", getDateStr(calTime));
		paramMap.put("limitTime", getLimitTime(calTime));
		
		// 积分年度
		String dueTime = calendar.get(Calendar.YEAR) + "-12-31";
		
		// 循环统计每个表的数据
		for(int tableNo = 0; tableNo < ShardUtils.getTableCount(); tableNo++){	
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			// 当日生效
			Integer immediateEffectedScore = scoreMainLogMapper.getEffectedScore(paramMap, tableNo);
			immediateEffectedScore = getValue(immediateEffectedScore);
			Integer effectedScore = immediateEffectedScore + getJobEffectedScore(dataSourceName, tableNo);
			sf.setEffected(effectedScore + getValue(sf.getEffected()));
			
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			// 当日待生效
			Integer lockedScore = scoreMainLogMapper.getLockedScore(paramMap, tableNo);
			// 当日发放(当日待生效 )
			sf.setExtend(immediateEffectedScore + getValue(lockedScore) + getValue(sf.getExtend()));
			
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			// 当日使用
			Integer usedScore = scoreMainLogMapper.getUsedScore(paramMap, tableNo);
			sf.setUsed(getValue(usedScore) + getValue(sf.getUsed()));
			
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			// 退订还点
			Integer reciperare = scoreMainLogMapper.getReciperareScore(paramMap, tableNo);
			sf.setReciperare(getValue(reciperare) + getValue(sf.getReciperare()));
			
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			// 当日失效
			Integer failureScore = scoreMainLogMapper.getFailureScore(paramMap, tableNo);
			failureScore = getValue(failureScore) + getValue(sf.getFailure());
			// 如果是12月31日, 则要加上年度失效积分
			if(lastDayOfYear){
				failureScore += getValue(scoreYearMapper.getExpired(dueTime, tableNo));
			}
			sf.setFailure(failureScore);
			
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			// 当日未生效余额
			Integer leftToBeEffective = scoreMainLogMapper.getLeftToBeEffective(paramMap, tableNo);
			sf.setLeftToBeEffective(getValue(leftToBeEffective) + getValue(sf.getLeftToBeEffective()));
		}
		
		return sf;
	}
	
	/*
	 * 统计job执行生效积分
	 */
	public Integer getJobEffectedScore(String dataSourceName, int tableNo){
		Map<String,Object> mapParam = new HashMap<String,Object>();
		// 每页显示条数
		Integer pageSize = Constant.DEFAULT_PAGE_SIZE;
		mapParam.put("pageSize", pageSize);
		
		// 生效日
		mapParam.put("scoreLimitTime", getDateStr(date));
		// 有效状态
		mapParam.put("status", "1");
		mapParam.put("channels", Constant.SCORE_CHANNEL_ORDER_BUY + ","
				+ Constant.SCORE_CHANNEL_CRM_GIVE);
		// 第几页
		Integer pageNo = 0;
		//分页起始位置
		int start = Math.max(pageSize * pageNo,0);
		mapParam.put("start", start);
		// 设置连接的数据库
		DataSourceUtils.setCurrentKey(dataSourceName);
		// 查询符合条件的记录
		List<ScoreMainLog> smlList = scoreMainLogDao.getScoreMainLogList(mapParam, tableNo);
		
		Integer totalScore = 0;
		ScoreMainLog scoreMainLog = null;
		String memGuid = null;
		while(smlList.size() != 0){
			for(int i = 0; i < smlList.size(); i++){
				scoreMainLog = smlList.get(i);
				memGuid = scoreMainLog.getMemGuid();
				int channel = scoreMainLog.getChannel();
				int score = scoreMainLog.getScoreNumber();
				if (Constant.SCORE_CHANNEL_ORDER_BUY == channel) {
					score = scoreMainLogDao.getOrderAvailableScore(memGuid, scoreMainLog.getOgSeq());
				}
				totalScore += score;
			}
			start = Math.max(pageSize * (++pageNo),0);
			mapParam.put("start", start);
			// 设置连接的数据库
			DataSourceUtils.setCurrentKey(dataSourceName);
			smlList = scoreMainLogDao.getScoreMainLogList(mapParam, tableNo);
		}
		return totalScore;
	}
	
	public Integer getValue(Integer val){
		return val == null ? 0 : val;
	}
}
