package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreMainLogUnsuccessed {
	private Integer smlSeq;
	
	private String memGuid;
	
	private Integer scoreNumber;
	
	private Date insTime;
	
	private Date endTime;
	
	private Date limitTime;
	
	private Integer status;
	
	private String ogSeq;
	
	private Integer channel;
	
	private String rgSeq;
	
	private Integer commentSeq;
	
	private String remark;
	
	private Date upTime;


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
}
