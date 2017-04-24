package com.zhp.config;

import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ScoreMapperConfig {

    /**
     * TODO  注意 如果将此方法放入 ScoreDataSourceConfig 中  则报错。
     * 可能是setSqlSessionFactoryBeanName 方法的原因
     */
    @Bean
    @Primary
    public MapperScannerConfigurer scoreMapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("scoreSqlSessionFactoryBean");
        mapperScannerConfigurer.setBasePackage("com.zhp.mapper.score");
        return mapperScannerConfigurer;

    }
}
