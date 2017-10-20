package com.feiniu.feign;

import com.feiniu.feign.service.api.User;
import org.springframework.stereotype.Component;

@Component
public class TestServiceFallback implements TestService {
    @Override
    public String hello() {
        return "error";
    }

    @Override
    public String hello(String s) {
        return "error";
    }

    @Override
    public String hello(String s, Integer integer) {
        return "未知";
    }

    @Override
    public User hello(User user) {
        return new User("未知", 0);
    }
}
