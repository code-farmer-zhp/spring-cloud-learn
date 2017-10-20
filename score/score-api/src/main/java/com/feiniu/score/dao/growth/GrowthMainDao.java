package com.feiniu.score.dao.growth;

import com.feiniu.score.entity.growth.GrowthMain;

import java.util.List;
import java.util.Map;


public interface GrowthMainDao {


	int deleteGrowthMainById(String memGuid, Long gmSeq);

    int saveGrowthMain(String memGuid, GrowthMain gm);

    GrowthMain getGrowthMainById(String memGuid, Long gmSeq);

    int updateGrowthMain(String memGuid, GrowthMain gm);
    
    List<GrowthMain> getGrowthMainList(String memGuid,Map<String, Object> paramMap);

    int getGrowthMainListCount(String memGuid);

    int changeGrowthValue(String memGuid,int changedGrowthValue);

    List<GrowthMain> getGrowthMainListForUpdate(Map<String, Object> mapParam, int tableNo);

	int saveGrowthValueWithValueZero(String memGuid);

    GrowthMain getGrowthMainByMemGuid(String memGuid);

    GrowthMain getGrowthMainByGuidForUpdate(String memGuid);
}
