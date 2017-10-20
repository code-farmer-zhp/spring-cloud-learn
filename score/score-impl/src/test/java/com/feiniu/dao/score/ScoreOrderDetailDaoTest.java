package com.feiniu.dao.score;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.DatabaseUnitException;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

import com.feiniu.dao.TestBasicDao;
import com.feiniu.score.common.Constant;
import com.feiniu.score.entity.score.ScoreOrderDetail;
public class ScoreOrderDetailDaoTest extends TestBasicDao{

	private String ogSeq = "201403CO11001050";
	
	@Override
	@Before
	public void doTest() {
		try {
			bakcupOneTable("score_order_detail");
			ds = createDateSet("score_order_detail");
		} catch (Exception e) {
			e.printStackTrace();
		}
		getTableAndDBBaseInfo(memGuid);
	}
	@Test
	public  void testSaveScoreOrderDetail() throws DatabaseUnitException, SQLException {
		DatabaseOperation.CLEAN_INSERT.execute(dbunitCon, ds);
		List<ScoreOrderDetail> scoreOrderDetailList = new ArrayList<ScoreOrderDetail>();
		ScoreOrderDetail scoreOrderDetail = new ScoreOrderDetail();
		scoreOrderDetail.setBill(BigDecimal.ONE);
		scoreOrderDetail.setItNo("201501CG200000122");
		scoreOrderDetail.setMemGuid(memGuid);
		scoreOrderDetail.setOgSeq(ogSeq);
		scoreOrderDetail.setOlSeq("");
		scoreOrderDetail.setRgSeq("");
		scoreOrderDetail.setRlSeq("");
		scoreOrderDetail.setRpSeq("");
		scoreOrderDetail.setScoreConsume(10);
		scoreOrderDetail.setScoreGet(0);
		scoreOrderDetail.setType(Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CONSUME);
		scoreOrderDetail.setSmlSeq(0);
		scoreOrderDetail.setScySeq(0);
		scoreOrderDetailList.add(scoreOrderDetail);
		int count = scoreOrderDetailDao.saveScoreOrderDetail(memGuid, scoreOrderDetailList);
		assertEquals(count, 1);
	}

	@Test
	public  void testGetScoreOrderDetailList() throws DatabaseUnitException, SQLException {
		testSaveScoreOrderDetail();
		List<ScoreOrderDetail> scoreOrderDetailList = scoreOrderDetailDao.getScoreOrderDetailList(memGuid, ogSeq,Constant.SCORE_ORDER_DETAIL_TYPE_ORDER_CONSUME);
		assertEquals(scoreOrderDetailList.size(), 1);
	}
	
	@Test
	public void testGetScoreOrderDetail(){
		 Map<String,Object> paramMap = new HashMap<String,Object>();
	    paramMap.put("ogSeq", ogSeq);
	    paramMap.put("rpSeq", "");
	    paramMap.put("olSeq", "");
	    paramMap.put("itNo", "");
	    //paramMap.put("type", Constant.SCORE_ORDER_DETAIL_TYPE_COMMENT_PRODUCT);
	//	ScoreOrderDetail scoreOrderDetail = scoreOrderDetailDao.getScoreOrderDetail(memGuid, paramMap);
		//assertNull(scoreOrderDetail);
	}

	

}
