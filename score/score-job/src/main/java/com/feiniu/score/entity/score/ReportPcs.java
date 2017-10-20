package com.feiniu.score.entity.score;

import java.math.BigDecimal;

public class ReportPcs {
	// 出货退货回档流水号
	private String rpSeq;
	
	// 退货表流水号
	private String rgSeq;
	
	// 退货明细流水号
	private String rlSeq;
	
	// 訂單明細檔流水號
	private String olSeq = "";
	
	// 订单表流水号
	private String ogSeq;
	
	// 商品ID
	private String itNo;
	
	// DISCOUNT_PRICE - USE_FULLDIS_POINTS(折扣後售價(不含抵用券抵扣) - 使用現金券)
	private BigDecimal price;
	
	// 积分
	private Integer score;
	
	// 订单号
	private String ogNo;

	public String getRpSeq() {
		return rpSeq;
	}

	public void setRpSeq(String rpSeq) {
		this.rpSeq = rpSeq;
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

	public String getItNo() {
		return itNo;
	}

	public void setItNo(String itNo) {
		this.itNo = itNo;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getOgNo() {
		return ogNo;
	}

	public void setOgNo(String ogNo) {
		this.ogNo = ogNo;
	}
}
