package com.zhp.entity;

import org.apache.ibatis.type.Alias;

import java.util.Date;

@Alias("studentSelfcard")
public class StudentSelfcard {

    private Integer id;

    private Integer studentId;

    private String anative;

    private Date issueDate;

    private Date endDate;

    private String note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getAnative() {
        return anative;
    }

    public void setAnative(String anative) {
        this.anative = anative;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "StudentSelfcard{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", anative='" + anative + '\'' +
                ", issueDate=" + issueDate +
                ", endDate=" + endDate +
                ", note='" + note + '\'' +
                '}';
    }
}
