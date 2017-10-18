package com.zhp.dao;

import com.zhp.entity.StudentHealthFemale;

import java.util.List;

public interface StudentHealthFemaleDao {
    List<StudentHealthFemale> getByStudentId(Integer studentId);
}
