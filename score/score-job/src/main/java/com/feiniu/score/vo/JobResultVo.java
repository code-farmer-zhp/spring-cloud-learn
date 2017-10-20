package com.feiniu.score.vo;


public class JobResultVo {
	private int successNum;

	private int failureNum;

	public JobResultVo(){}
	
	public void addSuccessNum() {
		successNum++;
	}

	public void addSuccessNum(int num){
		this.successNum = this.successNum + num;
	}
	public void addFailureNum() {
		failureNum++;
	}

	public void addFailureNum(int num) {
		this.failureNum = this.failureNum + num;
	}
	
	public synchronized void addProcessResultVo(JobResultVo dataSyncVo) {
		this.successNum = this.successNum + dataSyncVo.getSuccessNum();
		this.failureNum = this.failureNum + dataSyncVo.getFailureNum();
	}

	/*
	 * 打印信息
	 */
	public String getPrintString() {
		return "成功" + this.getSuccessNum() + "条, 失败" + this.getFailureNum() + "条.";
	}

	public int getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}

	public int getFailureNum() {
		return failureNum;
	}

	public void setFailureNum(int failureNum) {
		this.failureNum = failureNum;
	}
}
