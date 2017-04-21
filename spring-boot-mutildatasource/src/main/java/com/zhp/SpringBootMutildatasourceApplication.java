package com.zhp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement//开启事务等同于xml配置方式的 <tx:annotation-driven />
public class SpringBootMutildatasourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMutildatasourceApplication.class, args);
	}
}
