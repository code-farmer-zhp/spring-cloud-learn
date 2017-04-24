package com.zhp.mapper.score;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ScoreMapper {

    List<Map<Object,Object>> query();

    int saveScoreYear(@Param("memGuid") String memGuid, @Param("score") int score);
}
