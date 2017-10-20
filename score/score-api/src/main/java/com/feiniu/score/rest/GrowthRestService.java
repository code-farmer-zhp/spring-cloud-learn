package com.feiniu.score.rest;

import javax.ws.rs.core.Response;

public interface GrowthRestService {

    Response getMemLevel(String data);

    Response getGrowthDetail(String data);


    Response getGrowthDetailCount(String data);

    Response getMemOverPercent(String data);

    Response queryMemLevelList();

    Response getMemScoreAndGrowthInfo(String data);

    Response getGrowthDetailWithGroupByKey(String data);

    Response getGrowthDetailCountWithGroupByKey(String data);

    Response clearCacheValue(String key);

    Response putCacheValue(String key, String value);

    Response showCacheValue(String key, String field);
}
