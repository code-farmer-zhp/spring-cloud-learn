package com.zhp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableEurekaClient//使用服务中心
@EnableCircuitBreaker//使用熔断器
@EnableHystrixDashboard//使用监控
public class SpringCloudAppClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudAppClientApplication.class, args);
    }
}
