package com.zhp.entity;

public class StudentHealthMale extends StudentHealth {
    private String prostate;

    public String getProstate() {
        return prostate;
    }

    public void setProstate(String prostate) {
        this.prostate = prostate;
    }

    @Override
    public String toString() {
        return "StudentHealthMale{" +
                "prostate='" + prostate + '\'' +
                "} " + super.toString();
    }
}
