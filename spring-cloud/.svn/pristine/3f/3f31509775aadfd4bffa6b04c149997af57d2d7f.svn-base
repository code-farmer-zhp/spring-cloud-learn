package com.feiniu;

/*
*@author: Max
*@mail:1069905071@qq.com
*@time:2017/7/7 11:10
*/

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableConfigServer
public class SpringBootController {
    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(SpringBootController.class);
        springApplication.run(args).getEnvironment();
    }

}
