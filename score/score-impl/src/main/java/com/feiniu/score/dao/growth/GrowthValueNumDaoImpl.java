package com.feiniu.score.dao.growth;

import com.feiniu.score.datasource.DataSourceUtils;
import com.feiniu.score.entity.growth.GrowthValueNum;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.mapper.growth.GrowthValueNumMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
@Repository
public class GrowthValueNumDaoImpl implements GrowthValueNumDao{
	public static final CustomLog log = CustomLog.getLogger(GrowthValueNumDaoImpl.class);
	@Autowired
	private GrowthValueNumMapper growthValueNumMapper;

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED,value = "transactionManagerScore")
	public double getPercentLessThanMyGrowthValue(int growthvalue) {
		/*
		 * 只适用于单表，作废 int
		 * memCount=growthMainMapper.getGrowthMainCount(ShardUtils.
		 * getTableNo(memGuid)); Map<String,Object> paramMap = new
		 * HashMap<String,Object>(); paramMap.put("start", 0);
		 * paramMap.put("pageSize", 50); paramMap.put("memGuid", memGuid);
		 * List<GrowthMain>
		 * GrowthMains=growthMainMapper.getGrowthMainListBymemGuid(memGuid,
		 * paramMap, ShardUtils.getTableNo(memGuid)); int countLess=0;
		 * if(GrowthMains!=null){ int
		 * myGrowthValue=GrowthMains.get(0).getGrowthValue();
		 * countLess=growthMainMapper
		 * .getCountLessThanMyGrowthValue(myGrowthValue
		 * ,ShardUtils.getTableNo(memGuid)); } double
		 * lessPercent=(double)countLess/(double)memCount; BigDecimal bg = new
		 * BigDecimal(lessPercent); lessPercent = bg.setScale(2,
		 * BigDecimal.ROUND_HALF_UP).doubleValue();
		 * 
		 * return lessPercent;
		 */
		DataSourceUtils.setCurrentKey("defaultDataSourceSlave");
		Integer memCount = growthValueNumMapper.getGrowthValueNumSum();
		Integer lessCount = growthValueNumMapper
				.getGrowthValueNumSumLessThanValue(growthvalue);

		if (memCount == null) {
			memCount = 0;
		}
		if (lessCount == null) {
			lessCount = 0;
		}
		double lessPercent;
		if (memCount == 0) {
			lessPercent = 1;
		} else {
			lessPercent = (double) lessCount / (double) memCount;
		}
		BigDecimal bg = new BigDecimal(lessPercent);
		lessPercent = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		DataSourceUtils.removeCurrentKey();
		return lessPercent;
	}


	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED,value = "transactionManagerScore")
	public void changeTableGrowthValueNum(Integer myGrowthValueOld,
										  Integer myGrowthValueNew) {
		try {
			if(myGrowthValueOld!=null) {
				GrowthValueNum gvnOld = growthValueNumMapper
						.selectGrowthValueNumByValue(myGrowthValueOld);
				if (gvnOld == null || gvnOld.getNum() < 1) {
					log.error("changeTableGrowthValueNum: growthValueNum表中没有该条成长值的记录: myGrowthValueOld"
							+ myGrowthValueOld
							+ " myGrowthValueNew "
							+ myGrowthValueNew,"changeTableGrowthValueNum");
				} else {
					growthValueNumMapper.changeGrowthValueNum(gvnOld.getGvnSeq(),
							-1);
				}
			}
			if(myGrowthValueNew!=null) {
				GrowthValueNum gvnNew = growthValueNumMapper
						.selectGrowthValueNumByValue(myGrowthValueNew);
				if (myGrowthValueNew >= 0) {
					if (gvnNew == null) {
						GrowthValueNum gvnSave = new GrowthValueNum();
						gvnSave.setValue(myGrowthValueNew);
						gvnSave.setNum(1);
						growthValueNumMapper
								.saveGrowthValueNum(gvnSave);
					} else {
						growthValueNumMapper.changeGrowthValueNum(
								gvnNew.getGvnSeq(), 1);
					}
				}
			}
		} catch (Exception e) {
			log.error("更新统计表记录出错","changeTableGrowthValueNum",e);
		}
	}

}
