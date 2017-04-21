package com.zhp.bean;

import com.alibaba.druid.pool.DruidDataSource;
import com.zhp.datasource.MutilDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("db.properties")
@ConfigurationProperties(prefix = "datasource")//前缀只能和setter配合使用 不能和@Value配合使用
public class DruidDBConfig {
    private Logger logger = LoggerFactory.getLogger(DruidDBConfig.class);

    //@Value("${url_1}")
    private String url_1;

    //@Value("${username_1}")
    private String username_1;

    //@Value("${password_1}")
    private String password_1;


    //@Value("${url_2}")
    private String url_2;

    //@Value("${username_2}")
    private String username_2;

    //@Value("${password_2}")
    private String password_2;


    //@Value("${initialSize}")
    private int initialSize;

    //@Value("${minIdle}")
    private int minIdle;

    //@Value("${maxActive}")
    private int maxActive;

    //@Value("${maxWait}")
    private int maxWait;

    //@Value("${timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    //@Value("${minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    //@Value("${validationQuery}")
    private String validationQuery;

    //@Value("${testWhileIdle}")
    private boolean testWhileIdle;

    //@Value("${testOnBorrow}")
    private boolean testOnBorrow;

    //@Value("${testOnReturn}")
    private boolean testOnReturn;

    //@Value("${poolPreparedStatements}")
    private boolean poolPreparedStatements;

    //@Value("${maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    //@Value("${filters}")
    private String filters;

    //@Value("{connectionProperties}")
    private String connectionProperties;

    @Bean     //声明其为Bean实例
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
    public DataSource dataSource() {
        DruidDataSource dataSource1 = new DruidDataSource();
        dataSource1.setUrl(url_1);
        dataSource1.setUsername(username_1);
        dataSource1.setPassword(password_1);
        configuration(dataSource1);

        DruidDataSource dataSource2 = new DruidDataSource();
        dataSource2.setUrl(url_2);
        dataSource2.setUsername(username_2);
        dataSource2.setPassword(password_2);
        configuration(dataSource2);


        Map<Object, Object> map = new HashMap<>();
        map.put("dataSource0", dataSource1);
        map.put("dataSource1", dataSource2);

        MutilDataSource mutilDataSource = new MutilDataSource();
        mutilDataSource.setTargetDataSources(map);
        return mutilDataSource;
    }

    private void configuration(DruidDataSource datasource) {
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        try {
            datasource.setFilters(filters);
        } catch (SQLException e) {
            logger.error("druid configuration initialization filter", e);
        }
        datasource.setConnectionProperties(connectionProperties);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getUrl_1() {
        return url_1;
    }

    public void setUrl_1(String url_1) {
        this.url_1 = url_1;
    }

    public String getUsername_1() {
        return username_1;
    }

    public void setUsername_1(String username_1) {
        this.username_1 = username_1;
    }

    public String getPassword_1() {
        return password_1;
    }

    public void setPassword_1(String password_1) {
        this.password_1 = password_1;
    }

    public String getUrl_2() {
        return url_2;
    }

    public void setUrl_2(String url_2) {
        this.url_2 = url_2;
    }

    public String getUsername_2() {
        return username_2;
    }

    public void setUsername_2(String username_2) {
        this.username_2 = username_2;
    }

    public String getPassword_2() {
        return password_2;
    }

    public void setPassword_2(String password_2) {
        this.password_2 = password_2;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        this.poolPreparedStatements = poolPreparedStatements;
    }

    public int getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public String getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
    }
}