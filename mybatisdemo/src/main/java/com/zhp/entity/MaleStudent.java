package com.zhp.entity;

import org.apache.ibatis.type.Alias;

import java.util.List;

@Alias("maleStudent")
public class MaleStudent extends Student {
    private List<StudentHealthMale> studentHealthMaleList;

    public List<StudentHealthMale> getStudentHealthMaleList() {
        return studentHealthMaleList;
    }

    public void setStudentHealthMaleList(List<StudentHealthMale> studentHealthMaleList) {
        this.studentHealthMaleList = studentHealthMaleList;
    }

    @Override
    public String toString() {
        return "MaleStudent{" +
                "studentHealthMaleList=" + studentHealthMaleList +
                "} " + super.toString();
    }
}
