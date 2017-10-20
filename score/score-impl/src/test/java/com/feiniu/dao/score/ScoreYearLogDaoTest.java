package com.feiniu.dao.score;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import com.feiniu.dao.TestBasicDao;
import com.feiniu.score.entity.score.ScoreYearLog;
public class ScoreYearLogDaoTest extends TestBasicDao{

	@Override
	@Before
	public void doTest() {
		try {
			bakcupOneTable("score_year_log");
			ds = createDateSet("score_year_log");
		} catch (Exception e) {
			e.printStackTrace();
		}
		getTableAndDBBaseInfo(memGuid);
	}
	@Test
	public final void testSaveScoreYearLog() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreYearLog scoreYearLog = new ScoreYearLog();
		scoreYearLog.setSmlSeq(0);
		scoreYearLog.setScySeq(0);
		scoreYearLog.setMemGuid(memGuid);
		scoreYearLog.setScoreGet(10);
		scoreYearLog.setScoreConsume(10);
		int count = scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLog);
		assertEquals(count, 1);
	}
	 
	@Test
	public final void testGetScoreYearLogByLM() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreYearLog scoreYearLog = new ScoreYearLog();
		scoreYearLog.setSmlSeq(0);
		scoreYearLog.setScySeq(0);
		scoreYearLog.setMemGuid(memGuid);
		scoreYearLog.setScoreGet(10);
		scoreYearLog.setScoreConsume(10);
		int count = scoreYearLogDao.saveScoreYearLog(memGuid, scoreYearLog);
		assertEquals(count, 1);
		List<ScoreYearLog> scoreYearLogList = scoreYearLogDao.getScoreYearLogByLM(0, memGuid);
		assertThat(scoreYearLogList.size(),greaterThan(0));
	}
	

}
