package com.zhp.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
public class TestService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${depen.service.name}")
    private String serviceName;


    /**
     * fallbackMethod 降级方法
     * commandProperties 普通配置属性，可以配置HystrixCommand对应属性，例如采用线程池还是信号量隔离、熔断器熔断规则等等
     * ignoreExceptions 忽略的异常，默认HystrixBadRequestException不计入失败
     * groupKey() 组名称，默认使用类名称
     * commandKey 命令名称，默认使用方法名
     */
    @HystrixCommand(fallbackMethod = "fallBack")
    public String getSomething() {
        boolean flag = new Random().nextBoolean();
        if (flag) {
            throw new RuntimeException("随机异常");
        }
        return restTemplate.getForObject("http://" + serviceName + "/service/test", String.class);
    }

    public String fallBack() {
        return "exception occur call fallback method.";
    }
}
