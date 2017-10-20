package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

public interface ScoreGetBillDao {
	String BILL_KEY="bill";
	String MULTIPLE_KEY="multiple";

	JSONObject getBillsInfo(HashMap<String, String> itNoMapToCpSeq, boolean isFront);
}
