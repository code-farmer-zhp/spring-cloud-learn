package com.zhp.service;

import com.zhp.dao.ScoreDao;
import com.zhp.datasource.DynamicSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScoreService {

    @Autowired
    private ScoreDao scoreDao;

    @DynamicSelect(index = 0)
    @Transactional
    public void txTest(String memGuid) {
        scoreDao.saveScoreYear(memGuid, 10);
        scoreDao.saveMember(memGuid, 20);
        throw new RuntimeException("回滚");
    }
}
