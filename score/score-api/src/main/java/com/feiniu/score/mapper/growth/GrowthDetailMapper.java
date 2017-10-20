package com.feiniu.score.mapper.growth;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.growth.GrowthDetail;
import com.feiniu.score.vo.GrowthOrderDetail;
import com.feiniu.score.vo.GrowthOrderDetailByOg;

public interface GrowthDetailMapper {
    int deleteGrowthDetailById(@Param("gdSeq") Long gdSeq,@Param("tableNo") int tableNo);

    int saveGrowthDetail(@Param("gd") GrowthDetail gd,@Param("tableNo") int tableNo);

    GrowthDetail getGrowthDetailById(@Param("gdSeq") Long gdSeq,@Param("tableNo") int tableNo);
    
    
    List<GrowthDetail> findDetailByOrder(@Param("orderInfoId") Long orderInfoId,@Param("growthChannels")List<Integer>growthChannels ,@Param("tableNo") int tableNo);

    int updateGrowthDetail(@Param("gd") GrowthDetail gd,@Param("tableNo") int tableNo);
    
    List<GrowthDetail> getGrowthDetailListByMemGuid(@Param("memGuid") String memGuid,@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);

    int getGrowthDetailCountByMemGuid(@Param("memGuid") String memGuid,
    		@Param("tableNo") int tableNo);
    
    List<GrowthDetail> getGrowthDetailListBySelective(@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);
    
    int getGrowthDetailCountBySelective(@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);
    
    List<GrowthOrderDetail> getGrowthOrderDetailBySelective(@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);
    
    int getGrowthOrderDetailCountBySelective(@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);

    Integer getSumValueByMemGuid(@Param("memGuid")String memGuid, @Param("tableNo")int tableNo);
    
    List<GrowthOrderDetailByOg> getGrowthDetailGroupByOg(@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);
    
    int getGrowthDetailCountGroupByOg(@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);

    List<GrowthOrderDetailByOg> getGrowthDetailGroupByOgWithKey(@Param("paramMap")Map<String, Object> paramMap,
                                                         @Param("tableNo") int tableNo);

    int getGrowthDetailCountGroupByOgWithKey(@Param("paramMap")Map<String, Object> paramMap,
                                                         @Param("tableNo") int tableNo);
}