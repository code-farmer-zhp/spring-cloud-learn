package com.feiniu.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/hello")
    public String hello() {
       /* String sb = testService.hello() + "\n" +
                testService.hello("zhp") + "\n" +
                testService.hello("zhp", 26) + "\n" +
                testService.hello(new User("zhp", 26)) + "\n";*/
        return testService.hello("zhp");
    }
}
