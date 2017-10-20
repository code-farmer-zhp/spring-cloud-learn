package com.feiniu.score.vo;

import java.math.BigDecimal;
import java.util.Date;

public class GrowthOrderDetail {
	private Long gdSeq;

	private String memGuid;

	private Long orderInfoId;

	private String operate;

	private Integer growthValue;

	private Integer growthChannel;

	private Integer loginFrom;

	private Integer dataFlag;

	private String detailRemark;

	private Date detailInsDate;

	private Long goiSeq;

	private String ogNo;

	private String ogSeq;

	private String ogsSeq;
	
	private String adrId;

	private String olSeq;

	private String itNo;
	
	private String smSeq;

	private String rgSeq;

	private String rlSeq;

	private String packageNo;

	private int ogQty;

	private int returnQty;

	private BigDecimal realPay;

	private String commentSeq;

	private Integer payStatus;

	private Date payDate;
	
	private Date detailUpdDate;

	public Long getGdSeq() {
		return gdSeq;
	}

	public void setGdSeq(Long gdSeq) {
		this.gdSeq = gdSeq;
	}

	public String getMemGuid() {
		return memGuid;
	}

	public void setMemGuid(String memGuid) {
		this.memGuid = memGuid == null ? null : memGuid.trim();
	}

	public Long getOrderInfoId() {
		return orderInfoId;
	}

	public void setOrderInfoId(Long orderInfoId) {
		this.orderInfoId = orderInfoId;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate == null ? null : operate.trim();
	}

	public Integer getGrowthValue() {
		return growthValue;
	}

	public void setGrowthValue(Integer growthValue) {
		this.growthValue = growthValue;
	}

	public Integer getGrowthChannel() {
		return growthChannel;
	}

	public void setGrowthChannel(Integer growthChannel) {
		this.growthChannel = growthChannel;
	}

	public Integer getLoginFrom() {
		return loginFrom;
	}

	public void setLoginFrom(Integer loginFrom) {
		this.loginFrom = loginFrom;
	}

	public Integer getDataFlag() {
		return dataFlag;
	}

	public void setDataFlag(Integer dataFlag) {
		this.dataFlag = dataFlag;
	}

	public Long getGoiSeq() {
		return goiSeq;
	}

	public void setGoiSeq(Long goiSeq) {
		this.goiSeq = goiSeq;
	}

	public String getOgNo() {
		return ogNo;
	}

	public void setOgNo(String ogNo) {
		this.ogNo = ogNo;
	}

	public String getOgSeq() {
		return ogSeq;
	}

	public void setOgSeq(String ogSeq) {
		this.ogSeq = ogSeq;
	}

	public String getOgsSeq() {
		return ogsSeq;
	}

	public void setOgsSeq(String ogsSeq) {
		this.ogsSeq = ogsSeq;
	}

	public String getAdrId() {
		return adrId;
	}

	public void setAdrId(String adrId) {
		this.adrId = adrId;
	}

	public String getOlSeq() {
		return olSeq;
	}

	public void setOlSeq(String olSeq) {
		this.olSeq = olSeq;
	}

	public String getItNo() {
		return itNo;
	}

	public void setItNo(String itNo) {
		this.itNo = itNo;
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

	public String getPackageNo() {
		return packageNo;
	}

	public void setPackageNo(String packageNo) {
		this.packageNo = packageNo;
	}

	public int getOgQty() {
		return ogQty;
	}

	public void setOgQty(int ogQty) {
		this.ogQty = ogQty;
	}

	public int getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(int returnQty) {
		this.returnQty = returnQty;
	}

	public BigDecimal getRealPay() {
		return realPay;
	}

	public void setRealPay(BigDecimal realPay) {
		this.realPay = realPay;
	}

	public String getCommentSeq() {
		return commentSeq;
	}

	public void setCommentSeq(String commentSeq) {
		this.commentSeq = commentSeq;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getDetailRemark() {
		return detailRemark;
	}

	public void setDetailRemark(String detailRemark) {
		this.detailRemark = detailRemark;
	}

	public Date getDetailInsDate() {
		return detailInsDate;
	}

	public void setDetailInsDate(Date detailInsDate) {
		this.detailInsDate = detailInsDate;
	}

	public Date getDetailUpdDate() {
		return detailUpdDate;
	}

	public void setDetailUpdDate(Date detailUpdDate) {
		this.detailUpdDate = detailUpdDate;
	}
}