package com.feiniu.dao.score;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import com.feiniu.dao.TestBasicDao;
import com.feiniu.score.common.Constant;
import com.feiniu.score.entity.score.ScoreMainLog;
public class ScoreMainLogDaoTest extends TestBasicDao {

 
	//订单号
	private String ogSeq="201403CO11001050";
	 
	@Override
	@Before
	public void doTest() {
		try {
			bakcupOneTable("score_main_log");
			ds = createDateSet("score_main_log");
		} catch (Exception e) {
			e.printStackTrace();
		}
		getTableAndDBBaseInfo(memGuid);
	}
	
	 
	@Test
	public void testSaveScoreMainLog() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreMainLog scoreMainLog = new ScoreMainLog();
		scoreMainLog.setChannel(Constant.SCORE_CHANNEL_ORDER_CONSUME);
		scoreMainLog.setMemGuid(memGuid);
		scoreMainLog.setOgSeq(ogSeq);
		scoreMainLog.setRemark("订单消费");
		scoreMainLog.setRgSeq("");
		scoreMainLog.setScoreNumber(-20);
		scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
		scoreMainLog.setCommentSeq(0);
		//LimitTime 和 EndTime 都为null
		Integer returnValue = scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
		assertEquals(returnValue, Integer.valueOf(1));
		
	
		Calendar instance = Calendar.getInstance();
		scoreMainLog.setLimitTime(instance.getTime());
		instance.add(Calendar.YEAR, 1);
		scoreMainLog.setEndTime(instance.getTime());
		//LimitTime EndTime 都不为null
		returnValue = scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
		assertEquals(returnValue, Integer.valueOf(1));
		
		//LimitTime 不为null  EndTime为null
		scoreMainLog.setEndTime(null);
		returnValue = scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
		assertEquals(returnValue, Integer.valueOf(1));
		
		
		//LimitTime 为null  EndTime不为null
		scoreMainLog.setLimitTime(null);
		scoreMainLog.setEndTime(instance.getTime());
		returnValue = scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
		assertEquals(returnValue, Integer.valueOf(1));
	}

	 
	@Test
	public void testGetScoreMainLog() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreMainLog scoreMainLog = new ScoreMainLog();
		scoreMainLog.setChannel(Constant.SCORE_CHANNEL_ORDER_SUBMIT_CANCEL);
		scoreMainLog.setMemGuid(memGuid);
		scoreMainLog.setOgSeq(ogSeq);
		scoreMainLog.setRemark("订单取消，发放收回。");
		scoreMainLog.setRgSeq("");
		scoreMainLog.setScoreNumber(-20);
		scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
		scoreMainLog.setCommentSeq(0);
		Integer returnValue = scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
		assertEquals(returnValue, Integer.valueOf(1));
		
	 
		ScoreMainLog scoreMainLogNew = scoreMainLogDao.getScoreMainLog(memGuid, ogSeq, Constant.SCORE_CHANNEL_ORDER_SUBMIT_CANCEL);
		//upTime 和insTime自动生成。放弃比较
	    scoreMainLogNew.setInsTime(null);
	    scoreMainLogNew.setUpTime(null);
	    scoreMainLogNew.setOgNo(null);
		assertThat(scoreMainLog, samePropertyValuesAs(scoreMainLogNew));
		
	}

	@Test 
	public void testGetUserScoreDetailList() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreMainLog scoreMainLog = new ScoreMainLog();
		scoreMainLog.setMemGuid(memGuid);
		scoreMainLog.setOgSeq(ogSeq);
		scoreMainLog.setRemark("");
		scoreMainLog.setRgSeq("");
		scoreMainLog.setScoreNumber(0);
		scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
		scoreMainLog.setCommentSeq(0);
		for (int i = 0; i < 8; i++) {
			scoreMainLog.setChannel(i);
			Integer returnValue = scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
			assertEquals(returnValue, Integer.valueOf(1));
		}
		
		Map<String,Object> mapParam = new HashMap<String,Object>();
		mapParam.put("ogSeq", null);
		mapParam.put("memGuid", memGuid);
		mapParam.put("startTime", null);
		mapParam.put("endTime", null);
		mapParam.put("srcType", 0);//积分来源：0:全部;1:购物
		mapParam.put("directType", 0);////积分消费获取类型：0:全部；1，获取；2，消费
		mapParam.put("pageSize", 15);
		mapParam.put("start", 0);
		List<Map<String, Object>> userScoreDetailList = scoreMainLogDao.getUserScoreDetailList(mapParam, memGuid);
		assertEquals(0, userScoreDetailList.size());
		
		
		
	}

	@Test
	public void testGetUserScoreDetailListCount() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreMainLog scoreMainLog = new ScoreMainLog();
		scoreMainLog.setMemGuid(memGuid);
		scoreMainLog.setOgSeq(ogSeq);
		scoreMainLog.setRemark("");
		scoreMainLog.setRgSeq("");
		scoreMainLog.setScoreNumber(0);
		scoreMainLog.setStatus(Constant.SCORE_MAIN_LOG_STATUS_VAILD);
		scoreMainLog.setCommentSeq(0);
		for (int i = 0; i < 8; i++) {
			scoreMainLog.setChannel(i);
			Integer returnValue = scoreMainLogDao.saveScoreMainLog(memGuid, scoreMainLog);
			assertEquals(returnValue, Integer.valueOf(1));
		}
		
		Map<String,Object> mapParam = new HashMap<String,Object>();
		mapParam.put("ogSeq", null);
		mapParam.put("memGuid", memGuid);
		mapParam.put("startTime", null);
		mapParam.put("endTime", null);
		mapParam.put("srcType", 0);//积分来源：0:全部;1:购物
		mapParam.put("directType", 0);////积分消费获取类型：0:全部；1，获取；2，消费
		mapParam.put("pageSize", 15);
		mapParam.put("start", 0);
		int count = scoreMainLogDao.getUserScoreDetailListCount(mapParam, memGuid);
		assertEquals(0, count);
	}
	
	@Test
	public void testUpdateScoreMainLog() throws DataSetException, DatabaseUnitException, SQLException{
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, createDateSet("update_score_main_log"));
		ScoreMainLog scoreMainLogBack = new ScoreMainLog(); 
		scoreMainLogBack.setSmlSeq(1);
		scoreMainLogBack.setStatus(1);
		scoreMainLogBack.setEndTime(new Date());
		scoreMainLogBack.setInsTime(new Date());
		scoreMainLogBack.setLimitTime(new Date());
		scoreMainLogBack.setRemark("");
		scoreMainLogBack.setCommentSeq(0);
		scoreMainLogBack.setScoreNumber(0);
		scoreMainLogBack.setOgSeq("");
		scoreMainLogBack.setChannel(0);
		scoreMainLogBack.setRgSeq("");
		int count = scoreMainLogDao.updateScoreMainLog(memGuid, scoreMainLogBack);
		assertEquals(count,1);
	}

	@Test
	public void testGetScoreMainLogBack() throws DataSetException, DatabaseUnitException, SQLException{
		String regSeq = "1";
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, createDateSet("back_score_main_log"));
		ScoreMainLog scoreMainLogBack = scoreMainLogDao.getScoreMainLogBack(memGuid, regSeq, Constant.SCORE_CHANNEL_ORDER_CANCEL);
		assertNotNull(scoreMainLogBack);
	}
	
 
	
	

}
