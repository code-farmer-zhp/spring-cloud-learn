package com.zhp.rest;

import com.zhp.fegin.FeginRemoteService;
import com.zhp.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private FeginRemoteService feginRemoteService;

    @RequestMapping("/client/test")
    public String getServiceTest() {
        return testService.getSomething();
    }

    @RequestMapping("/client/test2")
    public String getServiceTest2() {
        return feginRemoteService.getTest();
    }
}
