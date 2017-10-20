package com.feiniu.score.mapper.growth;

import com.feiniu.score.entity.growth.GrowthMain;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GrowthMainMapper {
    int deleteGrowthMainById(@Param("gmSeq") Long gmSeq,@Param("tableNo") int tableNo);

    int  saveGrowthMain(@Param("gm") GrowthMain gm,@Param("tableNo") int tableNo);

    GrowthMain getGrowthMainById(@Param("gmSeq") Long gmSeq,@Param("tableNo") int tableNo);

    int updateGrowthMain(@Param("gm") GrowthMain gm,@Param("tableNo") int tableNo);
    
    List<GrowthMain> getGrowthMainListBymemGuid(@Param("memGuid") String memGuid,@Param("paramMap")Map<String, Object> paramMap,
    		@Param("tableNo") int tableNo);

    int getGrowthMainListCountBymemGuid(@Param("memGuid") String memGuid,
    		@Param("tableNo") int tableNo);
    
    int changeGrowthValue(@Param("memGuid") String memGuid,
    		@Param("changedGrowthValue") int changedGrowthValue,@Param("tableNo") int tableNo);
    
    int getGrowthMainCount(@Param("tableNo") int tableNo);
    
    int getCountLessThanMyGrowthValue(@Param("myGrowthValue") int myGrowthValue,@Param("tableNo") int tableNo);
    
    int getValueBymemGuid(@Param("memGuid") String memGuid,@Param("tableNo") int tableNo);

    List<GrowthMain> getGrowthMainListForUpdate(@Param("paramMap")Map<String, Object> mapParam,
                                                @Param("tableNo")int tableNo);

    GrowthMain getGrowthMainByIdForUpdate(@Param("memGuid")String memGuid,
                                          @Param("tableNo")int tableNo);

    GrowthMain getGrowthMainByMemGuid(@Param("memGuid") String memGuid,@Param("tableNo") int tableNo);
}