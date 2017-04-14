package com.zhp.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class Application {
    @Value("${name}")
    String name;

    @RequestMapping("/")
    String hello() {
        return "Hello " + name + "!";
    }
}