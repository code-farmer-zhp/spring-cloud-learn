package com.feiniu.score.mapper.score;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.feiniu.score.entity.score.ReportPcs;

/*
 * 出退貨回報檔
 */
public interface ReportPcsMapper {
	
	/**
	 * 根据条件获得出退貨回報檔(UCORDERP数据库)列表
	 * @param mapParam
	 * @return
	 */
	List<ReportPcs> getReportPcsList(@Param("mapParam") Map<String,Object> mapParam, @Param("schema") String schema);
}
