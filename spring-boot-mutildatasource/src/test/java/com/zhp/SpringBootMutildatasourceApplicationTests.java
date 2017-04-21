package com.zhp;

import com.zhp.dao.ScoreDao;
import com.zhp.service.ScoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootMutildatasourceApplicationTests {

    @Autowired
    private ScoreDao scoreDao;

    @Autowired
    private ScoreService scoreService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void dataSourceTest() {
        Map<Object, Object> query = scoreDao.query("9D50423D-F5A8-C58D-A087-366075BB30BF");
        System.out.println(query);

        query = scoreDao.query("611CB7BE-D484-B04D-B540-908ADFFB7DFB");
        System.out.println(query);
    }

    @Test
    public void txTest(){
        scoreService.txTest("9BEA4868-E60B-7860-EF9D-7537BA238AEC");
    }

}
