package com.zhp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/test")
    public String getTest() {
        return "spring-zk-service";
    }

    @RequestMapping(value = "/postTest", method = RequestMethod.POST)
    public String postTest(@RequestParam("data") String data) {
        System.out.println(data);
        return data;
    }
}
