package com.feiniu.score.dao.score;

import java.util.List;
import java.util.Map;

public interface ScoreGetOrderDetailDao {

	List<Map<String,Object>> getOrderDetailByOgSeq(String memGuid, String ogSeq);
}
