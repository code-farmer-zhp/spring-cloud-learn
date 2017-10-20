package com.feiniu.dao.score;

import com.feiniu.dao.TestBasicDao;
import com.feiniu.score.entity.score.ScoreYear;
import org.apache.commons.lang3.time.FastDateFormat;
import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

public class ScoreYearDaoTest extends TestBasicDao{


	@Override
	@Before
	public void doTest() {
		try {
			bakcupOneTable("score_year");
			ds = createDateSet("score_year");
		} catch (Exception e) {
			e.printStackTrace();
		}
		getTableAndDBBaseInfo(memGuid);
	}
	@Test
	public  void testGetScoreYear() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
		Calendar calendar = Calendar.getInstance();
		ScoreYear scoreYear = scoreYearDao.getScoreYear(memGuid, calendar.getTime());
		calendar.add(Calendar.YEAR, 1);
		FastDateFormat sd = FastDateFormat.getInstance("yyyy");
		String dueTimeYear = sd.format(scoreYear.getDueTime());
		assertEquals(Integer.parseInt(dueTimeYear), calendar.get(Calendar.YEAR));

	}

	@Test
	public  void testAddLockedScoreYear() throws DatabaseUnitException, SQLException {

		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
		int count2 = scoreYearDao.addLockedScoreYear(memGuid, scoreYearNew.getScySeq(), 10);
		assertEquals(count2, 1);
	}

	@Test
	public  void testSaveScoreYear() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);

		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
	    assertThat(scoreYearNew.getScySeq(),greaterThan(0));
	}

	@Test
	public  void testGetScoreYearByMemGuid() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);

		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		FastDateFormat sd = FastDateFormat.getInstance("yyyy-12-31");
		ScoreYear scoreYear = scoreYearDao.getScoreYearByMemGuid(memGuid, sd.format(calendar.getTime()));
		assertNotNull(scoreYear);
	}

	@Test
	public  void testDeductScore() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);

		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		FastDateFormat sd = FastDateFormat.getInstance("yyyy-12-31");
	}

	@Test
	public  void testGetScoreYearById() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
		ScoreYear scoreYear = scoreYearDao.getScoreYearById(memGuid, scoreYearNew.getScySeq());
		assertEquals(scoreYearNew.getScySeq(), scoreYear.getScySeq());
	}

	@Test
	public  void testAddScoreById() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
		int count2 = scoreYearDao.addScoreById(memGuid, 0, scoreYearNew.getScySeq());
		assertEquals(count2, 1);
		
		
	}

	@Test
	public  void testAddExpiredScoreById() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		
		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
		
		int count2 = scoreYearDao.addExpiredScoreById(memGuid, 0, scoreYearNew.getScySeq());
		assertEquals(count2, 1);
	}

	@Test
	public  void testDeductLockedScore() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreYear scoreYearNew = new ScoreYear();
		scoreYearNew.setTotalScore(20);
		scoreYearNew.setLockedScore(20);
		scoreYearNew.setAvailableScore(0);
		scoreYearNew.setDueTime(new Date());
		scoreYearNew.setMemGuid(memGuid);
		int count = scoreYearDao.saveScoreYear(memGuid, scoreYearNew);
		assertEquals(count, 1);
		
		int count2 = scoreYearDao.deductLockedScore(memGuid, scoreYearNew.getScySeq(), 20);
		assertEquals(count2, 1);
	}
 
}
