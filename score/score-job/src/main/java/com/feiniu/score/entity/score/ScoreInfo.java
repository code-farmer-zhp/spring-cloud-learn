package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreInfo {
	private Integer sciSeq;
	
	private Integer scmSeq;
	
	private String ogSeq;
	
	private Integer channel;
	
	private Date insTime;
	
	private String sciSign;
	
	private Integer scoreNumber;
	
	private String memo;
	
	private String rgSeq;
	
	private String createId;
	
	private String updateId;

	public Integer getSciSeq() {
		return sciSeq;
	}

	public void setSciSeq(Integer sciSeq) {
		this.sciSeq = sciSeq;
	}

	public Integer getScmSeq() {
		return scmSeq;
	}

	public void setScmSeq(Integer scmSeq) {
		this.scmSeq = scmSeq;
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

	public Date getInsTime() {
		return insTime;
	}

	public void setInsTime(Date insTime) {
		this.insTime = insTime;
	}

	public String getSciSign() {
		return sciSign;
	}

	public void setSciSign(String sciSign) {
		this.sciSign = sciSign;
	}

	public Integer getScoreNumber() {
		return scoreNumber;
	}

	public void setScoreNumber(Integer scoreNumber) {
		this.scoreNumber = scoreNumber;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getRgSeq() {
		return rgSeq;
	}

	public void setRgSeq(String rgSeq) {
		this.rgSeq = rgSeq;
	}

	public String getCreateId() {
		return createId;
	}

	public void setCreateId(String createId) {
		this.createId = createId;
	}

	public String getUpdateId() {
		return updateId;
	}

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	@Override
	public String toString() {
		return this.sciSeq + ", " + this.scmSeq + ", " 
				+ this.ogSeq + ", " + this.channel + ", "
				+ this.insTime + ", " + this.sciSign + ", "
				+ this.scoreNumber + ", " + this.memo + ", "
				+ this.rgSeq + "," + this.createId + ", "
				+ this.updateId;
	}
}
