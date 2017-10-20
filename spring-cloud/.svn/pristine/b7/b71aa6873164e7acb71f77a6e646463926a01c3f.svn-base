package com.feiniu.member.dto;

public class TouchResultDto {
    private Long elapsedTime;
    private int errorCode;
    private String errorDesc;
    private Object body;

    public TouchResultDto(){
    }

    public TouchResultDto(Long startTime, Long endTime, int errorCode, String errorDesc, Object body){
        this.elapsedTime=(endTime-startTime);
        this.errorCode=errorCode;
        this.errorDesc=errorDesc;
        this.body=body;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
