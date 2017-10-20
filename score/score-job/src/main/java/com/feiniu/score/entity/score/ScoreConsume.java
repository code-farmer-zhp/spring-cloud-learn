package com.feiniu.score.entity.score;

import java.math.BigDecimal;
import java.util.Date;

public class ScoreConsume {
	private Integer sccSeq;
	
	private Integer sciSeq;
	
	private String olSeq;
	
	private Integer scoreGet;
	
	private Integer scoreConsume;
	
	private BigDecimal bill;
	
	private Date insTime;
	
	private String createId;
	
	private String updateId;

	public Integer getSccSeq() {
		return sccSeq;
	}

	public void setSccSeq(Integer sccSeq) {
		this.sccSeq = sccSeq;
	}

	public Integer getSciSeq() {
		return sciSeq;
	}

	public void setSciSeq(Integer sciSeq) {
		this.sciSeq = sciSeq;
	}

	public String getOlSeq() {
		return olSeq;
	}

	public void setOlSeq(String olSeq) {
		this.olSeq = olSeq;
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

	public BigDecimal getBill() {
		return bill;
	}

	public void setBill(BigDecimal bill) {
		this.bill = bill;
	}

	public Date getInsTime() {
		return insTime;
	}

	public void setInsTime(Date insTime) {
		this.insTime = insTime;
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
}
