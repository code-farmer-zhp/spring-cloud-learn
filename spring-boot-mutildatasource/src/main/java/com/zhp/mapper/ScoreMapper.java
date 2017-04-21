package com.zhp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ScoreMapper {

    Map<Object, Object> query(@Param("memGuid") String memGuid, @Param("table") int table);

    int saveMember(@Param("memGuid") String memGuid, @Param("score") int score, @Param("table") int tableNo);

    int saveScoreYear(@Param("memGuid") String memGuid, @Param("score") int score, @Param("table") int tableNo);
}
