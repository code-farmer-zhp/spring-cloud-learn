package com.feiniu.score.service;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.dao.growth.GrowthLogDao;
import com.feiniu.score.entity.growth.GrowthLog;

@Service
public class GrowthLogServiceImpl {

	@Autowired
	private GrowthLogDao growthLogDao;
	
	public void saveGrowthLog(String memGuid , Object obj , Long objId , String tableName){
		
		this.saveGrowthLog(memGuid, obj, objId , tableName ,"save");
	}
	
	public void updateGrowthLog(String memGuid , Object obj, Long objId , String tableName){
		
		this.saveGrowthLog(memGuid, obj, objId , tableName ,"update");
	}
	
	private void saveGrowthLog(final String memGuid ,final Object obj ,final Long objId , final String tableName,final String operate){
		
		
		ExecutorService es = Executors.newSingleThreadExecutor();
		
		es.execute(new Runnable() {
			
			@Override
			public void run() {
				
				GrowthLog growthLog = new GrowthLog();
				
				Date now = new Date();
					growthLog.setMemGuid(memGuid);
					growthLog.setOperate(operate);
					growthLog.setRecId(objId);
					growthLog.setTableName(tableName);
					growthLog.setRemark(JSONObject.toJSONString(obj));
					growthLog.setInsDate(now);
					growthLog.setUpdDate(now);
				
				growthLogDao.saveGrowthLog(memGuid, growthLog);
			}
		});
		
		
	}
	
	
	 
}
