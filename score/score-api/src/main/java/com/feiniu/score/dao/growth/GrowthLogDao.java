package com.feiniu.score.dao.growth;

import java.util.List;
import java.util.Map;

import com.feiniu.score.entity.growth.GrowthLog;




public interface GrowthLogDao {
	
	int deleteGrowthLogById(String memGuid, Long glSeq);

    int saveGrowthLog(String memGuid, GrowthLog gl);

    GrowthLog getGrowthLogById(String memGuid, Long glSeq);

    int updateGrowthLog(String memGuid, GrowthLog gl);
    
    List<GrowthLog> getGrowthLogListBySelective(String memGuid, Map<String, Object> paramMap);
    
    int getGrowthLogCountBySelective(String memGuid, Map<String, Object> paramMap);
    
    
    public void saveLog(String memGuid , Object obj , Long objId , String tableName);
	
	public void updateLog(String memGuid , Object obj, Long objId , String tableName);
	
	
}
