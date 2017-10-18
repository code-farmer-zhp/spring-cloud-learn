package com.zhp.entity;

import org.apache.ibatis.type.Alias;

import java.util.List;

@Alias("femaleStudent")
public class FemaleStudent extends Student{
    private List<StudentHealthFemale> studentHealthFemaleList;

    public List<StudentHealthFemale> getStudentHealthFemaleList() {
        return studentHealthFemaleList;
    }

    public void setStudentHealthFemaleList(List<StudentHealthFemale> studentHealthFemaleList) {
        this.studentHealthFemaleList = studentHealthFemaleList;
    }

    @Override
    public String toString() {
        return "FemaleStudent{" +
                "studentHealthFemaleList=" + studentHealthFemaleList +
                '}';
    }
}
