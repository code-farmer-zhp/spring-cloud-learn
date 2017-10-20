package com.feiniu.score.entity.score;

import java.util.Date;

public class ScoreFinancial {
	
	private Integer id;
	
	// 日期
	private Date edate;
	
	// 退订还点
	private Integer reciperare;
	
	// 待生效积分(无数据库映射)
	private Integer lockedScore;
		
	// 当日发放
	private Integer extend;
	
	// 当日生效
	private Integer effected;
	
	// 当日失效
	private Integer failure;
	
	// 当日使用
	private Integer used;
	
	// 当日回收
	private Integer recycling;
	
	// 收支
	private Integer incomeAndExpenses;
	
	// 已生效余额
	private Integer leftEffected;
	
	// 未生效余额
	private Integer leftToBeEffective;
		
	// 已发送未使用总额
	private Integer ebnu;
	
	// 适用网站 1/飞牛网
	private Integer suitWebsite;
	
	// 发布者
	private String createId;
	
	// 更新者
	private String updateId;
	
	// 发布时间
	private Date createTime;
	
	// 更新时间
	private Date updateTime;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getEdate() {
		return edate;
	}

	public void setEdate(Date edate) {
		this.edate = edate;
	}

	public Integer getExtend() {
		return extend;
	}

	public void setExtend(Integer extend) {
		this.extend = extend;
	}

	public Integer getEffected() {
		return effected;
	}

	public void setEffected(Integer effected) {
		this.effected = effected;
	}

	public Integer getFailure() {
		return failure;
	}

	public void setFailure(Integer failure) {
		this.failure = failure;
	}

	public Integer getUsed() {
		return used;
	}

	public void setUsed(Integer used) {
		this.used = used;
	}

	public Integer getRecycling() {
		return recycling;
	}

	public void setRecycling(Integer recycling) {
		this.recycling = recycling;
	}

	public Integer getLeftEffected() {
		return leftEffected;
	}

	public void setLeftEffected(Integer leftEffected) {
		this.leftEffected = leftEffected;
	}

	public Integer getLeftToBeEffective() {
		return leftToBeEffective;
	}

	public void setLeftToBeEffective(Integer leftToBeEffective) {
		this.leftToBeEffective = leftToBeEffective;
	}

	public Integer getEbnu() {
		return ebnu;
	}

	public void setEbnu(Integer ebnu) {
		this.ebnu = ebnu;
	}

	public Integer getSuitWebsite() {
		return suitWebsite;
	}

	public void setSuitWebsite(Integer suitWebsite) {
		this.suitWebsite = suitWebsite;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getIncomeAndExpenses() {
		return incomeAndExpenses;
	}

	public void setIncomeAndExpenses(Integer incomeAndExpenses) {
		this.incomeAndExpenses = incomeAndExpenses;
	}

	public Integer getLockedScore() {
		return lockedScore;
	}

	public void setLockedScore(Integer lockedScore) {
		this.lockedScore = lockedScore;
	}

	public Integer getReciperare() {
		return reciperare;
	}

	public void setReciperare(Integer reciperare) {
		this.reciperare = reciperare;
	}
}
