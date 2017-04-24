package com.zhp.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsigneeMapperConfig {
    /**
     * TODO  注意 如果将此方法放入 ConsigneeDataSourceConfig 中  则报错。
     * 可能是setSqlSessionFactoryBeanName 方法的原因
     */
    @Bean
    public MapperScannerConfigurer consigneeMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("consigneeSqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage("com.zhp.mapper.consignee");
        return mapperScannerConfigurer;

    }
}
