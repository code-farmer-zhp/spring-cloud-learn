package com.zhp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudAppServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudAppServiceApplication.class, args);
	}
}
