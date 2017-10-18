package com.zhp.dao;

import com.zhp.entity.StudentLecture;

import java.util.List;

public interface StudentLectureDao {
    List<StudentLecture> getByStudentId(Integer studentId);
}
