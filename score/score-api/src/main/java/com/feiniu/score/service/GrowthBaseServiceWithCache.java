package com.feiniu.score.service;

import com.feiniu.score.entity.growth.GrowthMain;
import com.feiniu.score.vo.OrderJsonVo;

/**
 * Created by yue.teng on 2016/7/18.
 */
public interface GrowthBaseServiceWithCache {
    GrowthMain getGrowthMainByMemGuid(String memGuid, boolean withCache);

    int updateGrowthMain(String memGuid, GrowthMain gm, boolean cleanCache);

    int saveGrowthValueWithValueZero(String memGuid, boolean cleanCache);

    int changeGrowthValue(String memGuid, int changedGrowthValue, boolean cleanCache);

    int saveGrowthMain(String memGuid, GrowthMain gm, boolean cleanCache);

    boolean canGetGrowth(OrderJsonVo vo);
}
