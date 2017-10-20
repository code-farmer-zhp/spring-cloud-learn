package com.feiniu.feign.service.impl;

import com.feiniu.feign.service.api.FeignController;
import com.feiniu.feign.service.api.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class FeignControllerImpl implements FeignController {

    @Override
    public String hello() {
        return "hello";
    }

    @Override
    public String hello(String name) {
        try {
            Thread.sleep(new Random().nextInt(200));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("------");

        return "hello1:" + name;
    }

    @Override
    public String hello(@RequestHeader("name") String name, @RequestHeader("age") Integer age) {
        return "hell2:" + name + ":" + age;
    }

    @Override
    public User hello(@RequestBody User user) {
        return new User(user.getName(), user.getAge());
    }

}