package com.zhp.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ScoreDataSourceConfig extends CommonDataSourceConfig {

    @Value("${datasource.url_1}")
    private String url;

    @Value("${datasource.username_1}")
    private String username;

    @Value("${datasource.password_1}")
    private String password;


    @Bean
    @Primary//一定要加 否则报错
    public DataSource scoreDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        configuration(dataSource);
        return dataSource;
    }

    @Bean
    @Primary
    public SqlSessionFactoryBean scoreSqlSessionFactoryBean() {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(scoreDataSource());
        sessionFactoryBean.setConfigLocation(new ClassPathResource("com/zhp/mapper/config-mapper.xml"));
        Resource[] resources = new ClassPathResource[1];
        resources[0] = new ClassPathResource("com/zhp/mapper/score/ScoreMapper.xml");
        sessionFactoryBean.setMapperLocations(resources);
        return sessionFactoryBean;
    }

    @Bean
    @Qualifier("score")
    public PlatformTransactionManager scorePlatformTransactionManager(){
        return new DataSourceTransactionManager(scoreDataSource());
    }

}
