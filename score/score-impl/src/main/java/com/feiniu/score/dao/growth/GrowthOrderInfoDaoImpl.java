package com.feiniu.score.dao.growth;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.feiniu.score.entity.growth.GrowthOrderInfo;
import com.feiniu.score.mapper.growth.GrowthOrderInfoMapper;
import com.feiniu.score.util.ShardUtils;

@Repository
public class GrowthOrderInfoDaoImpl implements GrowthOrderInfoDao {

	@Autowired
	private GrowthOrderInfoMapper growthOrderInfoMapper;
	
	@Override
	public int saveOrderInfo(String memGuid , GrowthOrderInfo oi) {
		int count =  growthOrderInfoMapper.saveOrderInfo(oi, ShardUtils.getTableNo(memGuid));
//		if(count>0 && oi.getGoiSeq()!=null){
//			growthLogDao.saveLog(memGuid, oi, oi.getGoiSeq(), "growth_order_info");
//		}
		return count;
	}

	@Override
	public GrowthOrderInfo getOrderInfoById(String memGuid ,Long goiSeq) {
		
		return growthOrderInfoMapper.getOrderInfoById(goiSeq, ShardUtils.getTableNo(memGuid));
	}
	
//	@Override
//	public GrowthOrderInfo findOrderByOgSeq(String memGuid ,String ogSeq ){
//		
//		return growthOrderInfoMapper.findOrderByOgSeq(ogSeq, ShardUtils.getTableNo(memGuid));
//	}
	
	@Override
	public List<GrowthOrderInfo> findOrderListByMap(String memGuid , Map<String, Object> paramMap){
		
		return growthOrderInfoMapper.findOrderListByMap(paramMap, ShardUtils.getTableNo(memGuid));
	}
	
	@Override
	public List<GrowthOrderInfo> findOrderListByOlSeqList(String memGuid ,String ogSeq, Set<String> olSeqSet){
		
		
		return growthOrderInfoMapper.findOrderListByOlSeqList(ogSeq, olSeqSet, ShardUtils.getTableNo(memGuid));
	}
	
	
	@Override
	public List<GrowthOrderInfo> findOrderListByMap(
			Map<String, Object> paramMap, int tableNo) {
		return growthOrderInfoMapper.findOrderListByMap(paramMap, tableNo);
	}

	@Override
	public int updateGrowthOrderInfo(String memGuid, GrowthOrderInfo oi) {
		
		int count = growthOrderInfoMapper.updateOrderInfo(oi, ShardUtils.getTableNo(memGuid));
//		if(count>0){
//			growthLogDao.updateLog(memGuid, oi, oi.getGoiSeq(), "growth_order_info");
//		}
		return count;
	}

	@Override
	public List<GrowthOrderInfo> getGrowthOrderInfoListBySelective(
			String memGuid, Map<String, Object> paramMap) {
		return growthOrderInfoMapper.getOrderInfoListBySelective(paramMap,  ShardUtils.getTableNo(memGuid));
	}

	@Override
	public List<GrowthOrderInfo> getGrowthOrderInfoListBySelectiveAndTableNo(
			int tableNo, Map<String, Object> paramMap) {
		return growthOrderInfoMapper.getOrderInfoListBySelective(paramMap,  tableNo);
	}
	
	@Override
	public int getGrowthOrderInfoCountBySelective(String memGuid,
			Map<String, Object> paramMap) {
		return growthOrderInfoMapper.getOrderInfoCountBySelective(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public BigDecimal getOrderPaySum(String memGuid,
			Map<String, Object> paramMap) {
		return growthOrderInfoMapper.getOrderPaySum(paramMap, ShardUtils.getTableNo(memGuid));
	}

	@Override
	public Integer getCountOfEffectiveOrder(String memGuid) {
		return growthOrderInfoMapper.getCountOfEffectiveOrder(memGuid,ShardUtils.getTableNo(memGuid));
	}

	@Override
	public List<GrowthOrderInfo> findOrderListByOgsSeq(String memGuid, Map<String, Object> paramMap) {
		return growthOrderInfoMapper.findOrderListByOgsSeq(paramMap, ShardUtils.getTableNo(memGuid));
	}
}
