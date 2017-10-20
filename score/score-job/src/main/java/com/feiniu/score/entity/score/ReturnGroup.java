package com.feiniu.score.entity.score;

public class ReturnGroup {
	// 退貨主檔流水號	(分公司別+單據別S+yyyymmdd+6碼)
	private String rpSeq;
	
	// 退貨單編號	國別1碼+R+YYYYMMDD+流水號5碼
	private String rgNo;

	public String getRpSeq() {
		return rpSeq;
	}

	public void setRpSeq(String rpSeq) {
		this.rpSeq = rpSeq;
	}

	public String getRgNo() {
		return rgNo;
	}

	public void setRgNo(String rgNo) {
		this.rgNo = rgNo;
	}
}
