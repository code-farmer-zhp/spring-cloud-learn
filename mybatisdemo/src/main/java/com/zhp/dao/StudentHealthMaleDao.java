package com.zhp.dao;

import com.zhp.entity.StudentHealthMale;

import java.util.List;

public interface StudentHealthMaleDao {
    List<StudentHealthMale> getByStudentId(Integer studentId);
}
