package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreMainLog {
	private Integer smlSeq;
	
	private String memGuid;
	
	private Integer scoreNumber;
	
	private Date insTime;
	
	private Date endTime;
	
	private Date limitTime;
	
	private Integer status;
	
	private String ogSeq;
	
	private String ogNo;
	
	private Integer channel;
	
	private String rgSeq;
	
	private Integer commentSeq;
	
	private String remark;
	
	private Date upTime;
	
	private Integer lockJobStatus;

	private Date actualTime;

	private String rgNo;

	private String uniqueId;

	private String generalId;

	private String provinceId;


	public Integer getSmlSeq() {
		return smlSeq;
	}

	public void setSmlSeq(Integer smlSeq) {
		this.smlSeq = smlSeq;
	}

	public String getMemGuid() {
		return memGuid;
	}

	public void setMemGuid(String memGuid) {
		this.memGuid = memGuid;
	}

	public Integer getScoreNumber() {
		return scoreNumber;
	}

	public void setScoreNumber(Integer scoreNumber) {
		this.scoreNumber = scoreNumber;
	}

	public Date getInsTime() {
		return insTime;
	}

	public void setInsTime(Date insTime) {
		this.insTime = insTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(Date limitTime) {
		this.limitTime = limitTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getOgSeq() {
		return ogSeq;
	}

	public void setOgSeq(String ogSeq) {
		this.ogSeq = ogSeq;
	}
	

	public String getOgNo() {
		return ogNo;
	}

	public void setOgNo(String ogNo) {
		this.ogNo = ogNo;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getRgSeq() {
		return rgSeq;
	}

	public void setRgSeq(String rgSeq) {
		this.rgSeq = rgSeq;
	}

	public Integer getCommentSeq() {
		return commentSeq;
	}

	public void setCommentSeq(Integer commentSeq) {
		this.commentSeq = commentSeq;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getUpTime() {
		return upTime;
	}

	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}

	public Integer getLockJobStatus() {
		return lockJobStatus;
	}

	public void setLockJobStatus(Integer lockJobStatus) {
		this.lockJobStatus = lockJobStatus;
	}

	public Date getActualTime() {
		return actualTime;
	}

	public void setActualTime(Date actualTime) {
		this.actualTime = actualTime;
	}

	public String getRgNo() {
		return rgNo;
	}

	public void setRgNo(String rgNo) {
		this.rgNo = rgNo;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getGeneralId() {
		return generalId;
	}

	public void setGeneralId(String generalId) {
		this.generalId = generalId;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	@Override
	public String toString() {
		return "ScoreMainLog{" +
				"smlSeq=" + smlSeq +
				", memGuid='" + memGuid + '\'' +
				", scoreNumber=" + scoreNumber +
				", insTime=" + insTime +
				", endTime=" + endTime +
				", limitTime=" + limitTime +
				", status=" + status +
				", ogSeq='" + ogSeq + '\'' +
				", ogNo='" + ogNo + '\'' +
				", channel=" + channel +
				", rgSeq='" + rgSeq + '\'' +
				", commentSeq=" + commentSeq +
				", remark='" + remark + '\'' +
				", upTime=" + upTime +
				", lockJobStatus=" + lockJobStatus +
				", actualTime=" + actualTime +
				", rgNo='" + rgNo + '\'' +
				", uniqueId='" + uniqueId + '\'' +
				", generalId='" + generalId + '\'' +
				", provinceId='" + provinceId + '\'' +
				'}';
	}
}
