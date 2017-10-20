package com.feiniu.dao.score;

import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.feiniu.dao.TestBasicDao;


public class ScoreGetCommmentDetailDaoImplTest extends TestBasicDao {

	@Override
	@Before
	public void doTest() {
	}
	
	@Test
	public void testGetCommentDetail(){
		Map<String, Object> commentDetail = scoreGetCommmentDetailDao.getCommentDetail(319L);
		assertEquals(commentDetail.size(), 4);
	}
	
	
}
