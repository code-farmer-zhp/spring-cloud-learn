package com.feiniu.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
*@author: Max
*@mail:1069905071@qq.com
*@time:2017/7/11 12:51
*/
@RestController
@RefreshScope
public class TestRestController {

    @Autowired
    private Environment environment;

    @RequestMapping("/getVersion")
    public Object home() {
        return environment.getProperty("name", "undefined");
    }

}