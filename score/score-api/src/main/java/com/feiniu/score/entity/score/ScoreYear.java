package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreYear {

	private Integer scySeq;
	
	private String memGuid;
	
	private Integer totalScore;
	
	private Integer availableScore;
	
	private Integer usedScore;
	
	private Integer lockedScore;
	
	private Integer expiredScore;
	
	private Date dueTime;
	
	private Date insTime;
	
	private Date upTime;
	
	private int expirJobStatus;

	private String sellerNo;

	private Integer scoreType;

	private Integer sourceType;

	public Integer getScySeq() {
		return scySeq;
	}

	public void setScySeq(Integer scySeq) {
		this.scySeq = scySeq;
	}

	public String getMemGuid() {
		return memGuid;
	}

	public void setMemGuid(String memGuid) {
		this.memGuid = memGuid;
	}

	public Integer getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}	

	public Integer getAvailableScore() {
		return availableScore;
	}

	public void setAvailableScore(Integer availableScore) {
		this.availableScore = availableScore;
	}

	public Integer getExpiredScore() {
		return expiredScore;
	}

	public void setExpiredScore(Integer expiredScore) {
		this.expiredScore = expiredScore;
	}

	public Integer getUsedScore() {
		return usedScore;
	}

	public void setUsedScore(Integer usedScore) {
		this.usedScore = usedScore;
	}

	public Integer getLockedScore() {
		return lockedScore;
	}

	public void setLockedScore(Integer lockedScore) {
		this.lockedScore = lockedScore;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public Date getInsTime() {
		return insTime;
	}

	public void setInsTime(Date insTime) {
		this.insTime = insTime;
	}

	public Date getUpTime() {
		return upTime;
	}

	public void setUpTime(Date upTime) {
		this.upTime = upTime;
	}

	public int getExpirJobStatus() {
		return expirJobStatus;
	}

	public void setExpirJobStatus(int expirJobStatus) {
		this.expirJobStatus = expirJobStatus;
	}

	public String getSellerNo() {
		return sellerNo;
	}

	public void setSellerNo(String sellerNo) {
		this.sellerNo = sellerNo;
	}

	public Integer getScoreType() {
		return scoreType;
	}

	public void setScoreType(Integer scoreType) {
		this.scoreType = scoreType;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public String toString() {
		return "ScoreYear{" +
				"scySeq=" + scySeq +
				", memGuid='" + memGuid + '\'' +
				", totalScore=" + totalScore +
				", availableScore=" + availableScore +
				", usedScore=" + usedScore +
				", lockedScore=" + lockedScore +
				", expiredScore=" + expiredScore +
				", dueTime=" + dueTime +
				", insTime=" + insTime +
				", upTime=" + upTime +
				", expirJobStatus=" + expirJobStatus +
				", sellerNo='" + sellerNo + '\'' +
				", scoreType=" + scoreType +
				", sourceType=" + sourceType +
				'}';
	}
}
