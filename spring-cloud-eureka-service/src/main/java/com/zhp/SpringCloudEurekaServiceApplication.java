package com.zhp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer//服务中心
public class SpringCloudEurekaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudEurekaServiceApplication.class, args);
	}
}
