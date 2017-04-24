package com.zhp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SpringBootTwodatasourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootTwodatasourceApplication.class, args);
	}
}
