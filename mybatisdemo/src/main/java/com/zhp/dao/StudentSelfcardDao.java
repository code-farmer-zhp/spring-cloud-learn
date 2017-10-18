package com.zhp.dao;

import com.zhp.entity.StudentSelfcard;

public interface StudentSelfcardDao {
    StudentSelfcard findByStudentId(Integer studentId);
}
