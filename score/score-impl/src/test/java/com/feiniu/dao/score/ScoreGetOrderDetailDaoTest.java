package com.feiniu.dao.score;

import org.junit.Test;

import com.feiniu.dao.TestBasicDao;

public class ScoreGetOrderDetailDaoTest extends TestBasicDao{

	@Override
	public void doTest() {
		
	}
	
	@Test
	public void testGetOrderDetailByOgSeq(){
		scoreGetOrderDetailDao.getOrderDetailByOgSeq("B03D348D-6F7A-3405-80EF-C74271279EA0", "201503CO14000070");
	}

}
