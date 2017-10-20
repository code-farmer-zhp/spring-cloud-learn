package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreMember {
	private Integer smSeq;
	
	private String memGuid;
	
	private Integer totalScore;
	
	private Integer availableScore;
	
	private Integer usedScore;
	
	private Integer expiredScore;
	
	private Integer lockedScore;
	
	private Date insTime;
	
	private Date upTime;

	public Integer getSmSeq() {
		return smSeq;
	}

	public void setSmSeq(Integer smSeq) {
		this.smSeq = smSeq;
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

	public Integer getUsedScore() {
		return usedScore;
	}

	public void setUsedScore(Integer usedScore) {
		this.usedScore = usedScore;
	}

	public Integer getExpiredScore() {
		return expiredScore;
	}

	public void setExpiredScore(Integer expiredScore) {
		this.expiredScore = expiredScore;
	}

	public Integer getLockedScore() {
		return lockedScore;
	}

	public void setLockedScore(Integer lockedScore) {
		this.lockedScore = lockedScore;
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

	@Override
	public String toString() {
		return "ScoreMember [smSeq=" + smSeq + ", memGuid=" + memGuid
				+ ", totalScore=" + totalScore + ", availableScore="
				+ availableScore + ", usedScore=" + usedScore
				+ ", expiredScore=" + expiredScore + ", lockedScore="
				+ lockedScore + ", insTime=" + insTime + ", upTime=" + upTime
				+ "]";
	}
	
	
}
