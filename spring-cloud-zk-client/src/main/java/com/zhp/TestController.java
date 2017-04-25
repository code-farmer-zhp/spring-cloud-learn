package com.zhp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Configuration
@RestController
public class TestController {

    @Autowired
    private RestTemplate template;

    @Value("${zk.service.name}")
    private String service;

    @Autowired
    private FeignClientDemo feignClientDemo;

    @RequestMapping("/test")
    public String test() {
        return template.getForObject("http://" + service + "/test", String.class);
    }

    @RequestMapping("/test2")
    public String test2() {
        return feignClientDemo.test();
    }

    @RequestMapping("/postTest")
    public String postTest(String data) {
        return feignClientDemo.postTest(data);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
