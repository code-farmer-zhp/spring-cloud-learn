package com.zhp.entity;

public class StudentHealthFemale extends StudentHealth {

    private String uterus;

    public String getUterus() {
        return uterus;
    }

    public void setUterus(String uterus) {
        this.uterus = uterus;
    }

    @Override
    public String toString() {
        return "StudentHealthFemale{" +
                "uterus='" + uterus + '\'' +
                "} " + super.toString();
    }
}
