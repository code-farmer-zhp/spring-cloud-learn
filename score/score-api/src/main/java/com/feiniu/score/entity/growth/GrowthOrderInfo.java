package com.feiniu.score.entity.growth;

import java.math.BigDecimal;
import java.util.Date;

public class GrowthOrderInfo implements Comparable<GrowthOrderInfo>{

	
	 	private Long goiSeq;

	    private String memGuid;

	    private String ogNo;

	    private String ogSeq;

	    private String adrId;
	    
	    private String packageNo;

		private String olSeq;

	    private String itNo;
	    private String smSeq;
//	    private String rpSeq;
//	    private BigDecimal rpPoint;
	    
	    private int ogQty;
	    private int returnQty;
	    private BigDecimal returnPay;
	    private BigDecimal realPay;
		private BigDecimal price;
	    private String rgSeq;

	    private String rlSeq;

	    private String commentSeq;

	    private Integer commentWithPic;
	    
	    private String remark;
//	    private Integer returnFlag;
		private Integer payStatus;
	    private Date payDate;

	    private String insMan;

	    private Date insDate;

	    private String updMan;

	    private Date updDate;
	    
	    private String kind;
	    private String ogsSeq;
	    private String fdlSeq;
	    
	    

		public Long getGoiSeq() {
			return goiSeq;
		}

		public void setGoiSeq(Long goiSeq) {
			this.goiSeq = goiSeq;
		}

		public String getMemGuid() {
			return memGuid;
		}

		public void setMemGuid(String memGuid) {
			this.memGuid = memGuid;
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

		public String getCommentSeq() {
			return commentSeq;
		}

		public void setCommentSeq(String commentSeq) {
			this.commentSeq = commentSeq;
		}

		public Integer getCommentWithPic() {
			return commentWithPic;
		}

		public void setCommentWithPic(Integer commentWithPic) {
			this.commentWithPic = commentWithPic;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
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

		public String getInsMan() {
			return insMan;
		}

		public void setInsMan(String insMan) {
			this.insMan = insMan;
		}

		public Date getInsDate() {
			return insDate;
		}

		public void setInsDate(Date insDate) {
			this.insDate = insDate;
		}

		public String getUpdMan() {
			return updMan;
		}

		public void setUpdMan(String updMan) {
			this.updMan = updMan;
		}

		public Date getUpdDate() {
			return updDate;
		}

		public void setUpdDate(Date updDate) {
			this.updDate = updDate;
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

		public BigDecimal getReturnPay() {
			return returnPay;
		}

		public void setReturnPay(BigDecimal returnPay) {
			this.returnPay = returnPay;
		}

		public String getKind() {
			return kind;
		}

		public void setKind(String kind) {
			this.kind = kind;
		}

		public String getOgsSeq() {
			return ogsSeq;
		}

		public void setOgsSeq(String ogsSeq) {
			this.ogsSeq = ogsSeq;
		}

		public String getFdlSeq() {
			return fdlSeq;
		}

		public void setFdlSeq(String fdlSeq) {
			this.fdlSeq = fdlSeq;
		}
		
	@Override
	public String toString() {
		return "GrowthOrderInfo {" + "goiSeq=" + goiSeq + ", memGuid=" + memGuid
				+ ", ogNo='" + ogNo + ", ogSeq=" + ogSeq + ", adrId='" + adrId
				+ ", packageNo=" + packageNo + ", olSeq=" + olSeq + ", ogNo='"
				+ ogNo + ", itNo=" + itNo + ", smSeq='" + smSeq
				+ ", packageNo=" + packageNo + ", ogQty=" + ogQty
				+ ", returnQty='" + returnQty + ", returnPay=" + returnPay
				+ ", realPay='" + realPay + ", rgSeq=" + rgSeq + ", rlSeq="
				+ rlSeq + ", commentSeq='" + commentSeq + ", remark=" + remark
				+ ", payStatus='" + payStatus + ", payDate=" + payDate
				+ ", insMan=" + insMan + ", insDate='" + insDate + ", updMan="
				+ updMan + ", updDate='" + updDate + ", kind=" + kind
				+ ", ogsSeq=" + ogsSeq + ", fdlSeq='" + fdlSeq + '}';
	}
	
	@Override
    public int compareTo(GrowthOrderInfo goi) {  //大于0排在前面
        // 按实付金额升序排序，分摊成长值时将金额最大的做结算。如25.5 10.5 9.5 74.5，分摊为25 10 9 76
        if (this.getRealPay().compareTo(goi.getRealPay())<0) {
            return -1;
        }
        if (this.getRealPay().compareTo(goi.getRealPay())>0) {
            return 1;
        }
        return 0;
    }

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
