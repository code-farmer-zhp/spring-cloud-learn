package com.zhp.dao;

import com.zhp.entity.Lecture;

public interface LectureDao {
    Lecture findById(Integer lectureId);
}
