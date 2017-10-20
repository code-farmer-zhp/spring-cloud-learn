package com.feiniu.score.entity.growth;

import java.util.Date;

public class GrowthJobUnsuccessed {

	private Integer scuSeq;
	private Date insTime;
	private String memGuid;
	private String message;
	private Date upTime;
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
	@Override
	public String toString() {
		return "GrowthJobUnsuccessed [scuSeq=" + scuSeq + ", insTime=" + insTime
				 + ", memGuid=" + memGuid + ", message=" + message
				+ ", upTime=" + upTime + "]";
	}
    

}
