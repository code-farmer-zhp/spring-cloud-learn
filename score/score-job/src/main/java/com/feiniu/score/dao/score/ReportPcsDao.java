package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

import com.feiniu.score.entity.score.ReportPcs;

public interface ReportPcsDao {

	/**
	 * 根据条件获得出退貨回報檔(UCORDERP数据库)列表
	 * @param mapParam
	 * @return
	 */
	List<ReportPcs> getReportPcsList(Map<String,Object> mapParam);
}
