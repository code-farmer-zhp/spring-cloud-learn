package com.feiniu.score.mapper.growth;


import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.growth.GrowthValueNum;

public interface GrowthValueNumMapper {

    int saveGrowthValueNum(@Param("gvn") GrowthValueNum gvn);

    GrowthValueNum selectGrowthValueNumById(@Param("gvnSeq") Long gvnSeq);
    
    GrowthValueNum selectGrowthValueNumByValue(@Param("value") int value);

    int updateGrowthValueNum(@Param("gvn") GrowthValueNum gvn);
    
    Integer getGrowthValueNumSumLessThanValue(@Param("value") int value);
    
    Integer getGrowthValueNumSum();

	Integer changeGrowthValueNum(@Param("gvnSeq")Long gvnSeq, @Param("changeNum") int changeNum);
}