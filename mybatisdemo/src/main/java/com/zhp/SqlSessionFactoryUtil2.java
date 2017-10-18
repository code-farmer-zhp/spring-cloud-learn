package com.zhp;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SqlSessionFactoryUtil2 {

    public static SqlSession openSqlSession() {
        return SqlSessionFactoryProvider.sqlSessionFactory.openSession();
    }

    private static class SqlSessionFactoryProvider {
        private static SqlSessionFactory sqlSessionFactory;

        static {
            try {
                InputStream stream = Resources.getResourceAsStream("mybatis-config.xml");
                System.out.println("初始化sqlSessionFactory");
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SqlSession sqlSession = SqlSessionFactoryUtil2.openSqlSession();
        sqlSession.close();
    }
}
