package com.zhp.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping("/service/test")
    public String test() {
        return "app-service-test";
    }
}
