package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;

import java.util.Set;

public interface ScoreGetTotalScoreBySmSeqDao {

    JSONObject getScoreListBySmSeqList(Set<String> skuSeqSet, String areaCodeStr, Integer isFast, String whSeq);

}
