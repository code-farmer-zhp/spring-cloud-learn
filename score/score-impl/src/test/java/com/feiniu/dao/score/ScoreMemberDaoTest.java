package com.feiniu.dao.score;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import com.feiniu.dao.TestBasicDao;
import com.feiniu.score.entity.score.ScoreMember;
public class ScoreMemberDaoTest extends TestBasicDao{

	@Override
	@Before
	public void doTest() {
		try {
			bakcupOneTable("score_member");
			ds = createDateSet("score_member");
		} catch (Exception e) {
			e.printStackTrace();
		}
		getTableAndDBBaseInfo(memGuid);
		
	}
	
	@Test
	public void testSaveLockedScoreMember() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		int count = scoreMemberDao.saveLockedScoreMember(memGuid, 10);
		assertEquals(count, 1);
	}
	
	@Test
	public void testGetScoreMember() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		int count = scoreMemberDao.saveLockedScoreMember(memGuid, 10);
		assertEquals(count, 1);
		ScoreMember scoreMember = scoreMemberDao.getScoreMember(memGuid);
		assertEquals(scoreMember.getLockedScore(), Integer.valueOf(10));
		assertEquals(scoreMember.getTotalScore(), Integer.valueOf(10));
	}

	@Test
	public void testDeductScore() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		int count = scoreMemberDao.saveLockedScoreMember(memGuid, 10);
		assertEquals(count, 1);
		int count1 = scoreMemberDao.deductAvailableScore(memGuid, 0);
		assertEquals(count1, 1);
		 
	}

	@Test
	public void testUpdateLockedScoreMember() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		int count = scoreMemberDao.saveLockedScoreMember(memGuid, 10);
		assertEquals(count, 1);
		int count2 = scoreMemberDao.updateLockedScoreMember(memGuid, 10);
		assertEquals(count2, 1);
	}

	

	@Test
	public void testAddExpiredScore() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		int count = scoreMemberDao.saveLockedScoreMember(memGuid, 10);
		assertEquals(count, 1);
		int count2 = scoreMemberDao.addExpiredScore(memGuid, 0);
		assertEquals(count2, 1);
	}

	@Test
	public void testDeductLockedScore() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		int count = scoreMemberDao.saveLockedScoreMember(memGuid, 10);
		assertEquals(count, 1);
		int count2 = scoreMemberDao.deductLockedScore(memGuid, 10);
		assertEquals(count2, 1);
	}

	@Test
	public void testAddScoreBecauseReturn() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon,ds);
		int count = scoreMemberDao.saveLockedScoreMember(memGuid, 10);
		assertEquals(count, 1);
		int count2 = scoreMemberDao.addScoreBecauseReturn(memGuid, 0);
		assertEquals(count2, 1);
	}

	
	 



}
