package com.zhp;

import com.zhp.mapper.consignee.ConsigneeMapper;
import com.zhp.mapper.score.ScoreMapper;
import com.zhp.service.ConsigneeService;
import com.zhp.service.ScoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class SpringBootTwodatasourceApplicationTests {

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private ConsigneeMapper consigneeMapper;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ConsigneeService consigneeService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testDb() {
        List<Map<Object, Object>> query = scoreMapper.query();
        System.out.println(query);

        query = consigneeMapper.query();
        System.out.println(query);

        scoreService.test();

        consigneeService.test();
    }

}
