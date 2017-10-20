package com.feiniu.dao.score;

import com.feiniu.score.service.ScoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-score-impl.xml")
public class ScoreServiceImplTest {

    @Autowired
    private ScoreService scoreService;

    @Test
    public void submitOrderTest(){
        scoreService.submitOrderScore("515E5A94-C859-4C19-3D76-B4F2D9F4D738","{\"consumeScore\":50000,\"memGuid\":\"515E5A94-C859-4C19-3D76-B4F2D9F4D738\",\"memType\":0,\"ogNo\":\"201510CP22014173\",\"ogSeq\":\"201510CO22014173\",\"provinceId\":\"CS000016\",\"sourceMode\":\"1\"}");
    }

}
