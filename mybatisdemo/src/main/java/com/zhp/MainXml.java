package com.zhp;

import com.zhp.dao.StudentDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;


public class MainXml {
    public static void main(String[] args) throws IOException, InterruptedException {
        InputStream resourceAsStream = Resources.getResourceAsStream("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            StudentDao mapper = sqlSession.getMapper(StudentDao.class);
            mapper.getStudnetById(1);

        } finally {
            sqlSession.close();
        }

    }
}
