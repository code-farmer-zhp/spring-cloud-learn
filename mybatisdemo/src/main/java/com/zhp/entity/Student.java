package com.zhp.entity;

import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.List;

@Alias("student")
public class Student implements Serializable{

    private Integer id;

    private String cnname;

    private Integer sex;

    private StudentSelfcard studentSelfcard;

    private List<StudentLecture> studentLectureList;

    private String note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCnname() {
        return cnname;
    }

    public void setCnname(String cnname) {
        this.cnname = cnname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public StudentSelfcard getStudentSelfcard() {
        return studentSelfcard;
    }

    public void setStudentSelfcard(StudentSelfcard studentSelfcard) {
        this.studentSelfcard = studentSelfcard;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<StudentLecture> getStudentLectureList() {
        return studentLectureList;
    }

    public void setStudentLectureList(List<StudentLecture> studentLectureList) {
        this.studentLectureList = studentLectureList;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", cnname='" + cnname + '\'' +
                ", sex=" + sex +
                ", studentSelfcard=" + studentSelfcard +
                ", studentLectureList=" + studentLectureList +
                ", note='" + note + '\'' +
                '}';
    }
}
