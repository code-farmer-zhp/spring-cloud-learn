package com.feiniu.dao.score;

import com.feiniu.dao.TestBasicDao;
import org.junit.Before;

public class ScoreGetBillDaoImplTest extends TestBasicDao {

	@Override
	@Before
	public void doTest() {
		try {
			bakcupOneTable("score_main_log");
			ds = createDateSet("score_main_log");
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
	}

	
}
