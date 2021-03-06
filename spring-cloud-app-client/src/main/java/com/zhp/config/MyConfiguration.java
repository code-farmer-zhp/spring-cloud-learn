package com.zhp.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyConfiguration {

    @LoadBalanced//Ribbon进行负载均衡路由
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}