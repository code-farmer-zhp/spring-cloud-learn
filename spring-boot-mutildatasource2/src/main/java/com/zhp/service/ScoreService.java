package com.zhp.service;

import com.zhp.mapper.score.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Transactional("score")
    public void test() {
        System.out.println(scoreMapper.query());
        scoreMapper.saveScoreYear("zhouxxx", 10);
        throw new RuntimeException("score 回滚测试");
    }
}
