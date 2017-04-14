package com.zhp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SpringCloudAppClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudAppClientApplication.class, args);
	}
}
