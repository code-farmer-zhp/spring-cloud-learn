package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreCommentDetail {
	private Integer scdSeq;
	
	private Integer smlSeq;
	
	private String memGuid;
	
	private Integer commentSeq;
	
	private String rpSeq;
	
	private String olSeq;
	
	private String ogSeq;
	
	private String ogNo;
	
	private String smSeq;
	
	private String rgSeq;
	
	private String rlSeq;
	
	private String itNo;
	
	private Integer scoreGet;
	
	private Integer scoreConsume;
	
	private Integer type;
	
	private Date insTime;

	private String rgNo;

	private String sellerNo;

	private String storeNo;

	private Integer commentWithPic;
	
	public Integer getScdSeq() {
		return scdSeq;
	}

	public void setScdSeq(Integer scdSeq) {
		this.scdSeq = scdSeq;
	}

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

	
	public Integer getCommentSeq() {
		return commentSeq;
	}

	public void setCommentSeq(Integer commentSeq) {
		this.commentSeq = commentSeq;
	}

	public String getRpSeq() {
		return rpSeq;
	}

	public void setRpSeq(String rpSeq) {
		this.rpSeq = rpSeq;
	}

	public String getOlSeq() {
		return olSeq;
	}

	public void setOlSeq(String olSeq) {
		this.olSeq = olSeq;
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

	public String getSmSeq() {
		return smSeq;
	}

	public void setSmSeq(String smSeq) {
		this.smSeq = smSeq;
	}

	public String getRgSeq() {
		return rgSeq;
	}

	public void setRgSeq(String rgSeq) {
		this.rgSeq = rgSeq;
	}

	public String getRlSeq() {
		return rlSeq;
	}

	public void setRlSeq(String rlSeq) {
		this.rlSeq = rlSeq;
	}

	public String getItNo() {
		return itNo;
	}

	public void setItNo(String itNo) {
		this.itNo = itNo;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getInsTime() {
		return insTime;
	}

	public void setInsTime(Date insTime) {
		this.insTime = insTime;
	}

	public String getRgNo() {
		return rgNo;
	}

	public void setRgNo(String rgNo) {
		this.rgNo = rgNo;
	}

	public String getSellerNo() {
		return sellerNo;
	}

	public void setSellerNo(String sellerNo) {
		this.sellerNo = sellerNo;
	}

	public String getStoreNo() {
		return storeNo;
	}

	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}

	
	public Integer getCommentWithPic() {
		return commentWithPic;
	}

	public void setCommentWithPic(Integer commentWithPic) {
		this.commentWithPic = commentWithPic;
	}

	@Override
	public String toString() {
		return "ScoreCommentDetail{" +
				"scdSeq=" + scdSeq +
				", smlSeq=" + smlSeq +
				", memGuid='" + memGuid + '\'' +
				", commentSeq=" + commentSeq +
				", rpSeq='" + rpSeq + '\'' +
				", olSeq='" + olSeq + '\'' +
				", ogSeq='" + ogSeq + '\'' +
				", ogNo='" + ogNo + '\'' +
				", smSeq='" + smSeq + '\'' +
				", rgSeq='" + rgSeq + '\'' +
				", rlSeq='" + rlSeq + '\'' +
				", itNo='" + itNo + '\'' +
				", scoreGet=" + scoreGet +
				", scoreConsume=" + scoreConsume +
				", type=" + type +
				", insTime=" + insTime +
				", rgNo='" + rgNo + '\'' +
				", sellerNo='" + sellerNo + '\'' +
				", storeNo='" + storeNo + '\'' +
				", commentWithPic='" + commentWithPic + '\'' +
				'}';
	}
}
