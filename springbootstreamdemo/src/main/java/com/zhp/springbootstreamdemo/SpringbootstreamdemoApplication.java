package com.zhp.springbootstreamdemo;

import com.zhp.springbootstreamdemo.server.SendServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringbootstreamdemoApplication {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext run = SpringApplication.run(SpringbootstreamdemoApplication.class, args);
        SendServer bean = run.getBean(SendServer.class);
        bean.send1();
        bean.send2();
    }
}
