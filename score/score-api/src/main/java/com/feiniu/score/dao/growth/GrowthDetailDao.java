package com.feiniu.score.dao.growth;

import java.util.List;
import java.util.Map;

import com.feiniu.score.entity.growth.GrowthDetail;
import com.feiniu.score.vo.GrowthOrderDetail;
import com.feiniu.score.vo.GrowthOrderDetailByOg;




public interface GrowthDetailDao {


	int deleteGrowthDetailById(String memGuid, Long gdSeq);

    int saveGrowthDetail(String memGuid, GrowthDetail gd);

    GrowthDetail getGrowthDetailById(String memGuid, Long gdSeq);

    int updateGrowthDetail(String memGuid, GrowthDetail gd);
    
    List<GrowthDetail> getGrowthDetailListByMemGuid(String memGuid,Map<String, Object> paramMap);
    
    List<GrowthDetail> findDetailByOrder(String memGuid,Long orderInfoId,List<Integer>growthChannels);

    int getGrowthDetailListCountByMemGuid(String memGuid);
    
    List<GrowthDetail> getGrowthDetailListBySelective(String memGuid,Map<String, Object> paramMap);
    
    int getGrowthDetailCountBySelective(String memGuid,Map<String, Object> paramMap);

    List<GrowthOrderDetail>  getGrowthOrderDetailBySelective(String memGuid,Map<String, Object> paramMap);
	
    List<GrowthOrderDetail> getGrowthOrderDetailOfChannel(String memGuid,
			Integer Channel);

	int getGrowthOrderDetailCountBySelective(String memGuid,
			Map<String, Object> paramMap);

    Integer getSumValueByMemGuid(String memGuid, int tableNo);

	List<GrowthOrderDetailByOg> getGrowthDetailGroupByOg(String memGuid,
			Map<String, Object> paramMap);

	int getGrowthDetailCountGroupByOg(String memGuid,
			Map<String, Object> paramMap);

    List<GrowthOrderDetailByOg> getGrowthDetailGroupByOgWithKey(String memGuid,
                                                         Map<String, Object> paramMap);

    int getGrowthDetailCountGroupByOgWithKey(String memGuid,
                                      Map<String, Object> paramMap);
}
