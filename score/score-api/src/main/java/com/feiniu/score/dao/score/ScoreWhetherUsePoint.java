package com.feiniu.score.dao.score;

import java.util.Map;

/**
 * 判断是否可以使用积分
 */
public interface ScoreWhetherUsePoint {
    Map<String,Boolean> getWhetherCanUsePoint(String itNos);
}
