package com.feiniu.score.entity.score;

import java.math.BigDecimal;
import java.util.Date;

public class ScoreOrderDetail {
	private Integer sodSeq;

	private Integer smlSeq;

	private String memGuid;

	private String rpSeq;

	private String olSeq;

	private String ogSeq;

	private String ogNo;

	private String rgSeq;

	private String rlSeq;

	private String itNo;

	private Integer scoreGet;

	private Integer scoreConsume;

	private BigDecimal bill;

	private Integer type;

	private Date insTime;

	private Integer scySeq;

	private Integer sourceMode;

	private String siteMode;

	private String buyMode;
	
	private String promotionGrade;
	
	private String rgNo;

	//类型：1:自营2：商城
	private Integer sourceType;

	//商家编号
	private String sellerNo;

	//包裹号
	private String packageNo;

	//该商品的数量
	private Integer quantity;

	private String kind;

	private BigDecimal money;

	private String ogsSeq;

	//门店编号
	private String storeNo;

	public Integer getSodSeq() {
		return sodSeq;
	}

	public void setSodSeq(Integer sodSeq) {
		this.sodSeq = sodSeq;
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

	public BigDecimal getBill() {
		return bill;
	}

	public void setBill(BigDecimal bill) {
		this.bill = bill;
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

	public Integer getScySeq() {
		return scySeq;
	}

	public void setScySeq(Integer scySeq) {
		this.scySeq = scySeq;
	}

	public Integer getSourceMode() {
		return sourceMode;
	}

	public void setSourceMode(Integer sourceMode) {
		this.sourceMode = sourceMode;
	}

	public String getBuyMode() {
		return buyMode;
	}

	public void setBuyMode(String buyMode) {
		this.buyMode = buyMode;
	}

	public String getPromotionGrade() {
		return promotionGrade;
	}

	public void setPromotionGrade(String promotionGrade) {
		this.promotionGrade = promotionGrade;
	}

	public String getRgNo() {
		return rgNo;
	}

	public void setRgNo(String rgNo) {
		this.rgNo = rgNo;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	public String getSellerNo() {
		return sellerNo;
	}

	public void setSellerNo(String sellerNo) {
		this.sellerNo = sellerNo;
	}

	public String getPackageNo() {
		return packageNo;
	}

	public void setPackageNo(String packageNo) {
		this.packageNo = packageNo;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getOgsSeq() {
		return ogsSeq;
	}

	public void setOgsSeq(String ogsSeq) {
		this.ogsSeq = ogsSeq;
	}

	public String getSiteMode() {
		return siteMode;
	}

	public void setSiteMode(String siteMode) {
		this.siteMode = siteMode;
	}

	public String getStoreNo() {
		return storeNo;
	}

	public void setStoreNo(String storeNo) {
		this.storeNo = storeNo;
	}

	@Override
	public String toString() {
		return "ScoreOrderDetail{" +
				"sodSeq=" + sodSeq +
				", smlSeq=" + smlSeq +
				", memGuid='" + memGuid + '\'' +
				", rpSeq='" + rpSeq + '\'' +
				", olSeq='" + olSeq + '\'' +
				", ogSeq='" + ogSeq + '\'' +
				", ogNo='" + ogNo + '\'' +
				", rgSeq='" + rgSeq + '\'' +
				", rlSeq='" + rlSeq + '\'' +
				", itNo='" + itNo + '\'' +
				", scoreGet=" + scoreGet +
				", scoreConsume=" + scoreConsume +
				", bill=" + bill +
				", type=" + type +
				", insTime=" + insTime +
				", scySeq=" + scySeq +
				", sourceMode=" + sourceMode +
				", siteMode='" + siteMode + '\'' +
				", rgNo='" + rgNo + '\'' +
				", sourceType=" + sourceType +
				", sellerNo='" + sellerNo + '\'' +
				", packageNo='" + packageNo + '\'' +
				", quantity=" + quantity +
				", kind='" + kind + '\'' +
				", money=" + money +
				", ogsSeq='" + ogsSeq + '\'' +
				", storeNo='" + storeNo + '\'' +
				'}';
	}
}
