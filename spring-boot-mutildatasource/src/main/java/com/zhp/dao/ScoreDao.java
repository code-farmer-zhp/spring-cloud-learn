package com.zhp.dao;


import com.zhp.datasource.DynamicSelect;
import com.zhp.mapper.ScoreMapper;
import com.zhp.utils.ShardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class ScoreDao {

    @Autowired
    private ScoreMapper scoreMapper;

    @DynamicSelect(index = 0)
    public Map<Object, Object> query(String memGuid) {
        return scoreMapper.query(memGuid, ShardUtils.getTableNo(memGuid));
    }

    public int saveMember(String memGuid, int score) {
        return scoreMapper.saveMember(memGuid, score, ShardUtils.getTableNo(memGuid));
    }

    public int saveScoreYear(String memGuid, int score) {
        return scoreMapper.saveScoreYear(memGuid, score, ShardUtils.getTableNo(memGuid));
    }
}
