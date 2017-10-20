package com.feiniu.score.entity.growth;

import java.math.BigDecimal;
import java.util.Date;

public class GrowthDetail {
    private Long gdSeq;

    private String memGuid;

    private Long orderInfoId;

    private String operate;

    private Integer growthValue;

    private Integer growthChannel;

    private Integer loginFrom;

    private Integer dataFlag;

    private String remark;

    private String insMan;

    private Date insDate;

    private Date loginDateTmp;

    private String updMan;

    private Date updDate;
    
    private String uniqueKey;
    private String groupKey;
    private String rgSeq;
    private String rlSeq;
    private int returnQty;
    private BigDecimal returnPay;
    

    public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getInsMan() {
        return insMan;
    }

    public void setInsMan(String insMan) {
        this.insMan = insMan == null ? null : insMan.trim();
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
        this.updMan = updMan == null ? null : updMan.trim();
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
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

	public int getReturnQty() {
		return returnQty;
	}

	public void setReturnQty(int returnQty) {
		this.returnQty = returnQty;
	}

	public BigDecimal getReturnPay() {
		return returnPay;
	}

	public void setReturnPay(BigDecimal returnPay) {
		this.returnPay = returnPay;
	}
	
	@Override
	public String toString() {
		return "GrowthDetail {" + "gdSeq=" + gdSeq + ", memGuid=" + memGuid
				+ ", orderInfoId='" + orderInfoId + ", operate=" + operate + ", growthValue='" + growthValue
				+ ", growthChannel=" + growthChannel + ", loginFrom=" + loginFrom  + ", loginDateTmp=" + loginDateTmp+ ", dataFlag='"
				+ dataFlag + ", remark=" + remark + ", insMan='" + insMan
				+ ", insDate=" + insDate + ", updMan=" + updMan
				+ ", updDate='" + updDate + ", returnPay=" + returnPay
				+ ", uniqueKey='" + uniqueKey + ", groupKey='" + groupKey + ", rgSeq=" + rgSeq + ", rlSeq="
				+ rlSeq + ", returnQty='" + returnQty + ", remark=" + remark
				+ ", returnPay='" + returnPay + '}';
	}

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public Date getLoginDateTmp() {
        return loginDateTmp;
    }

    public void setLoginDateTmp(Date loginDateTmp) {
        this.loginDateTmp = loginDateTmp;
    }
}