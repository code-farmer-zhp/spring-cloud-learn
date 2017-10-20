package com.feiniu.score.dao.growth;

import com.feiniu.score.entity.growth.GrowthLog;
import com.feiniu.score.mapper.growth.GrowthLogMapper;
import com.feiniu.score.util.ShardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class GrowthLogDaoImpl implements GrowthLogDao{
	@Autowired
	private GrowthLogMapper growthLogMapper;
	
	@Override
	public int deleteGrowthLogById(String memGuid, Long glSeq) {
		//return growthLogMapper.deleteGrowthLogById(glSeq,  ShardUtils.getTableNo(memGuid));
		return 1;
	}

	@Override
	public int saveGrowthLog(final String memGuid,final GrowthLog gl) {
		
//		log.info("==================================================memGuid="+memGuid);
//		int count = growthLogMapper.saveGrowthLog(gl, ShardUtils.getTableNo(memGuid));
//		
//		return count;
		return 1;
	}

	@Override
	public GrowthLog getGrowthLogById(String memGuid, Long glSeq) {
		return growthLogMapper.getGrowthLogById(glSeq,  ShardUtils.getTableNo(memGuid));
	}

	@Override
	public int updateGrowthLog(String memGuid, GrowthLog gl) {
		return growthLogMapper.updateGrowthLog(gl,  ShardUtils.getTableNo(memGuid));
	}

	@Override
	public List<GrowthLog> getGrowthLogListBySelective(String memGuid, 
			Map<String, Object> paramMap) {
		return growthLogMapper.getGrowthLogListBySelective(paramMap,  ShardUtils.getTableNo(memGuid));
	}

	@Override
	public int getGrowthLogCountBySelective(String memGuid, Map<String, Object> paramMap) {
		return growthLogMapper.getGrowthLogCountBySelective(paramMap,  ShardUtils.getTableNo(memGuid));
	}
	
	
	public void saveLog(String memGuid , Object obj , Long objId , String tableName){
		
		//this.saveGrowthLog(memGuid, obj, objId , tableName ,"save");
	}
	
	public void updateLog(String memGuid , Object obj, Long objId , String tableName){
		
		//this.saveGrowthLog(memGuid, obj, objId , tableName ,"update");
	}
	
	private void saveGrowthLog(final String memGuid ,final Object obj ,final Long objId , final String tableName,final String operate){
		
//		GrowthLog growthLog = new GrowthLog();
//		Date now = new Date();
//			growthLog.setMemGuid(memGuid);
//			growthLog.setOperate(operate);
//			growthLog.setRecId(objId);
//			growthLog.setTableName(tableName);
//			String remark=JSONObject.toJSONString(obj);
//			if(remark.length()>200){
//				remark=remark.substring(0, 199);
//			}
//			growthLog.setRemark(JSONObject.toJSONString(remark));		
//		saveGrowthLog(memGuid, growthLog);
		
	}

}
