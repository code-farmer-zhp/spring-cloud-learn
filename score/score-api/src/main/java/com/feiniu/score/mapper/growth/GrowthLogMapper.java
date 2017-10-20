package com.feiniu.score.mapper.growth;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.growth.GrowthLog;

public interface GrowthLogMapper {
	int deleteGrowthLogById(@Param("glSeq") Long glSeq,@Param("tableNo") int tableNo);

    int saveGrowthLog(@Param("gl") GrowthLog gl,@Param("tableNo") int tableNo);

    GrowthLog getGrowthLogById(@Param("glSeq") Long glSeq,@Param("tableNo") int tableNo);

    int updateGrowthLog(@Param("gl") GrowthLog gl,@Param("tableNo") int tableNo);
    
    List<GrowthLog> getGrowthLogListByMemGuid(@Param("memGuid") String memGuid,@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);
    
    int getGrowthLogCountByMemGuid(@Param("memGuid") String memGuid,@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);
    
    List<GrowthLog> getGrowthLogListBySelective(@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);
    
    int getGrowthLogCountBySelective(@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);
}