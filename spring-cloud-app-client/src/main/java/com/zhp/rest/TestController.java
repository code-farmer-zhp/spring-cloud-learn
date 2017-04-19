package com.zhp.rest;

import com.zhp.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/client/test")
    public String getServiceTest() {
        return testService.getSomething();
    }
}
