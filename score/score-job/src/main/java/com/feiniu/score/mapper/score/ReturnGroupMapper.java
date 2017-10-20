package com.feiniu.score.mapper.score;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ReturnGroup;

public interface ReturnGroupMapper {
	
	public ReturnGroup getReturnGroupByRgSeq(@Param("schema") String schema, @Param("rgSeq") String rgSeq);
}
