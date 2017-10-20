package com.feiniu.dao.score;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import com.feiniu.dao.TestBasicDao;
import com.feiniu.score.common.Constant;
import com.feiniu.score.entity.score.ScoreCommentDetail;
import com.feiniu.score.entity.score.ScoreOrderDetail;

public class ScoreCommentDetailDaoTest extends TestBasicDao {

	@Override
	@Before
	public void doTest() {
		try {
			bakcupOneTable("score_comment_detail");
			ds = createDateSet("score_comment_detail");
		} catch (DataSetException | IOException e) {
			e.printStackTrace();
		}
		getTableAndDBBaseInfo(memGuid);
		
	}
	@Test
	public final void testGetScoreCommentDetail() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		 Map<String, Object> paramMap = new HashMap<String,Object>();
		 paramMap.put("commentSeq", 1);
		 paramMap.put("type", 1);
		 ScoreCommentDetail scoreCommentDetail = scoreCommentDetailDao.getScoreCommentDetail(memGuid, paramMap );
		 assertNull(scoreCommentDetail);
	}

	@Test
	public final void testSaveScoreCommentDetail() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreCommentDetail scd = new ScoreCommentDetail();
		scd.setCommentSeq(1);
		scd.setItNo("s");
		scd.setMemGuid("s");
		scd.setOgSeq("");
		scd.setOlSeq("");
		scd.setRgSeq("");
		scd.setRlSeq("");
		scd.setRpSeq("");
		scd.setScoreConsume(1);
		scd.setScoreGet(0);
		scd.setSmlSeq(0);
		scd.setSmSeq("");
		scd.setType(1);
		scd.setOgNo("");
		Integer count = scoreCommentDetailDao.saveScoreCommentDetail(memGuid, scd);
		assertEquals(1, count.intValue());
	}

	 

	@Test
	public final void testGetCommentSeqByProductDetail() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		ScoreOrderDetail sod = new ScoreOrderDetail();
		sod.setOgSeq("");
		sod.setOlSeq("");
		sod.setItNo("");
		ScoreCommentDetail smd = scoreCommentDetailDao.getCommentDetailByProductDetail(memGuid, sod );
		assertNull(smd);
	}

	 

	

}
