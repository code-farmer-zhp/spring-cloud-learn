package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreYearLog {
	private Integer sylSeq;
	
	private Integer smlSeq;
	
	private Integer scySeq;
	
	private String memGuid;
	
	private Integer scoreGet;
	
	private Integer scoreConsume;
	
	private Date insTime;

	

	public Integer getSylSeq() {
		return sylSeq;
	}

	public void setSylSeq(Integer sylSeq) {
		this.sylSeq = sylSeq;
	}

	public Integer getSmlSeq() {
		return smlSeq;
	}

	public void setSmlSeq(Integer smlSeq) {
		this.smlSeq = smlSeq;
	}

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

	public Integer getScoreGet() {
		return scoreGet;
	}

	public void setScoreGet(Integer scoreGet) {
		this.scoreGet = scoreGet;
	}

	public Integer getScoreConsume() {
		return scoreConsume;
	}

	public void setScoreConsume(Integer scoreConsume) {
		this.scoreConsume = scoreConsume;
	}

	public Date getInsTime() {
		return insTime;
	}

	public void setInsTime(Date insTime) {
		this.insTime = insTime;
	}

	@Override
	public String toString() {
		return "ScoreYearLog [sylSeq=" + sylSeq + ", smlSeq=" + smlSeq
				+ ", scySeq=" + scySeq + ", memGuid=" + memGuid + ", scoreGet="
				+ scoreGet + ", scoreConsume=" + scoreConsume + ", insTime="
				+ insTime + "]";
	}
	
	

}
