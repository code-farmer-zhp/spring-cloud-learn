package com.zhp;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MainCode {
    public static void main(String[] args) {

        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://msc01.dev1.fn:3306/memberclub");
        dataSource.setUsername("pu_pzhou");
        dataSource.setPassword("De5gT6Hq9");

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);

        Configuration configuration = new Configuration();
        configuration.setEnvironment(environment);
        configuration.getTypeAliasRegistry().registerAliases("com.zhp.entity");
        configuration.addMappers("com.zhp.dao");

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {

        } finally {
            sqlSession.close();
        }
    }
}
