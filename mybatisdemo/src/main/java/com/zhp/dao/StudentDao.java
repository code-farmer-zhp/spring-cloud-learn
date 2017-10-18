package com.zhp.dao;

import com.zhp.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentDao {
    Student getStudnetById(Integer id);

    List<Student> findAllStudent();

    List<Student> findByName(@Param("name") String name);

    int updateName(@Param("name") String name);
}
