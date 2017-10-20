package com.feiniu.score.service;

import com.feiniu.score.dto.Result;
import com.feiniu.score.entity.mrst.Pkad;

public interface GrowthMemService {

	Result saveGrowthByCommentProduct(String memGuid, String data);

	Result saveGrowthBySetEssenceOrTop(String memGuid, String data);

	Result getMemLevel(String memGuid);

	Result getGrowthDetail(String memGuid, String data);
    
	void saveGrowthkafkafromCRM(String memGuid, String data);
	
	void getkafkafromCRM(String memGuid, String data);
	
	void saveGrowthChangeTokafkaForCRM(String memGuid);

	Integer getGrowthDetailCount(String memGuid, String data);

	Result getMemOverPercent(String memGuid);

	void saveGrowthfromPkad(String memGuid, String data, Pkad pkad);

	Result queryMemLevelList();

	Result getMemScoreAndGrowthInfo(String memGuid);

	Result getGrowthDetailGroupByOg(String memGuid, String data, Integer isWithGroupKey);

	Result getGrowthDetailCountGroupByOg(String memGuid, String data, Integer isWithGroupKey);

	Result clearCacheValue(String key);

	String showCacheValue(String key, String field);

	void growthValueNumChange(String data);

	Result putCacheValue(String key, String value);
}
