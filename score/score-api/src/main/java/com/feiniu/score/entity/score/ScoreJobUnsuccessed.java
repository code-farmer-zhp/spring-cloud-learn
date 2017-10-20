package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreJobUnsuccessed {

	private Integer scuSeq;
	private Date insTime;
	private Integer isDeal;
    private Integer type;
    private Integer srcType;
	private String memGuid;
	private String message;
	private Date upTime;
	private String errorMessage;
	public Integer getScuSeq() {
		return scuSeq;
	}
	public void setScuSeq(Integer scuSeq) {
		this.scuSeq = scuSeq;
	}
	public Date getInsTime() {
		return insTime;
	}
	public void setInsTime(Date insTime) {
		this.insTime = insTime;
	}
	public Integer getIsDeal() {
		return isDeal;
	}
	public void setIsDeal(Integer isDeal) {
		this.isDeal = isDeal;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getSrcType() {
		return srcType;
	}
	public void setSrcType(Integer srcType) {
		this.srcType = srcType;
	}
	public String getMemGuid() {
		return memGuid;
	}
	public void setMemGuid(String memGuid) {
		this.memGuid = memGuid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getUpTime() {
		return upTime;
	}
	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "ScoreJobUnsuccessed{" +
				"scuSeq=" + scuSeq +
				", insTime=" + insTime +
				", isDeal=" + isDeal +
				", type=" + type +
				", srcType=" + srcType +
				", memGuid='" + memGuid + '\'' +
				", message='" + message + '\'' +
				", upTime=" + upTime +
				", errorMessage='" + errorMessage + '\'' +
				'}';
	}
}
