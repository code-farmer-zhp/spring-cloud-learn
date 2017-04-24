package com.zhp.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ConsigneeDataSourceConfig extends CommonDataSourceConfig {

    @Value("${datasource.url_2}")
    private String url;

    @Value("${datasource.username_2}")
    private String username;

    @Value("${datasource.password_2}")
    private String password;


    @Bean
    public DataSource consigneeDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        configuration(dataSource);
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean consigneeSqlSessionFactoryBean() {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(consigneeDataSource());
        sessionFactoryBean.setConfigLocation(new ClassPathResource("com/zhp/mapper/config-mapper.xml"));
        Resource[] resources = new ClassPathResource[1];
        resources[0] = new ClassPathResource("com/zhp/mapper/consignee/ConsigneeMapper.xml");
        sessionFactoryBean.setMapperLocations(resources);
        return sessionFactoryBean;
    }

    @Bean
    @Qualifier("consignee")
    public PlatformTransactionManager consigneePlatformTransactionManager() {
        return new DataSourceTransactionManager(consigneeDataSource());
    }

}
