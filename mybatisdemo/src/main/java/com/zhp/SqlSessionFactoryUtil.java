package com.zhp;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class SqlSessionFactoryUtil {

    private static volatile SqlSessionFactory sqlSessionFactory;

    private SqlSessionFactoryUtil() {

    }

    public static SqlSession openSqlSession() {
        if (sqlSessionFactory == null) {
            synchronized (SqlSessionFactoryUtil.class) {
                if (sqlSessionFactory == null) {
                    try {
                        InputStream stream = Resources.getResourceAsStream("mybatis-config.xml");
                        System.out.println("初始化sqlSessionFactory");
                        InputStream resourceAsStream = Resources.getResourceAsStream("jdbc.properties");
                        Properties properties = new Properties();
                        properties.load(resourceAsStream);

                        sqlSessionFactory = new SqlSessionFactoryBuilder().build(stream, properties);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return sqlSessionFactory.openSession();
    }

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(150, 150,
                5, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        for (int i = 0; i < 150; i++) {
            final int j = i;
            executor.execute(new Runnable() {
                public void run() {
                    SqlSession sqlSession = SqlSessionFactoryUtil.openSqlSession();
                    sqlSession.close();
                    if (j == 149) {
                        System.out.println(j);
                    }
                }
            });
        }
        executor.shutdown();
    }

}
