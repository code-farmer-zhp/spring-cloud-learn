package com.feiniu.score.entity.mrst;

import java.util.Date;

public class Pkad {
    private Integer pkadSeq;

    private String pkadId;

    private String membId;

    private String membGradeF;

    private String pkadType;

    private String mrstUi;

    private String mrstUiName;

    private String ddPkad;
    //沟通出现错误，数据库中is_take代表是否已被领取。而kafka中代表是否需要领取，T这是显示的礼包，F为直接充值
    private Integer isTake;
    //isRecharge是否直充。kafka中is_take为T，数据库中isRecharge为0。
    private Integer isRecharge;
    
    private String isOpen;
    
    private String ddTake;
    
    private String ddTakeF;

    private String ddTakeT;

    private Integer pkadCnt;

    private String growthInfo;

    private String pointInfo;

    private String cardInfo;

    private String mrclNo;
    
    private Integer isCancel;
    
    private Date insTime;

    private Date updateTime;

    private String takeCardSeq;
    
    public Integer getPkadSeq() {
        return pkadSeq;
    }

    public void setPkadSeq(Integer pkadSeq) {
        this.pkadSeq = pkadSeq;
    }

    public String getPkadId() {
        return pkadId;
    }

    public void setPkadId(String pkadId) {
        this.pkadId = pkadId.trim();
    }

    public String getMembId() {
        return membId;
    }

    public void setMembId(String membId) {
        this.membId = membId.trim();
    }
    
    public String getMembGradeF() {
		return membGradeF;
	}

	public void setMembGradeF(String membGradeF) {
		this.membGradeF = membGradeF;
	}
    /*
    *1发放 2取消
    */
	public String getPkadType() {
        return pkadType;
    }

    public void setPkadType(String pkadType) {
        this.pkadType = pkadType.trim();
    }

    public String getMrstUi() {
        return mrstUi;
    }

    public void setMrstUi(String mrstUi) {
        this.mrstUi = mrstUi.trim();
    }
    /*
    *是否被领取
    */
    public Integer getIsTake() {
        return isTake;
    }

    public void setIsTake(Integer isTake) {
        this.isTake = isTake;
    }
    /*
    *是否是直充
    */
    public Integer getIsRecharge() {
		return isRecharge;
	}

	public void setIsRecharge(Integer isRecharge) {
		this.isRecharge = isRecharge;
	}
    /*
    *礼包发放日期
    */
	public String getDdPkad() {
		return ddPkad;
	}

	public void setDdPkad(String ddPkad) {
		this.ddPkad = ddPkad;
	}
    /*
    *用户领取时间
    */
	public String getDdTake() {
		return ddTake;
	}

	public void setDdTake(String ddTake) {
		this.ddTake = ddTake;
	}
    /*
    * 生效日期
     */
	public String getDdTakeF() {
		return ddTakeF;
	}

	public void setDdTakeF(String ddTakeF) {
		this.ddTakeF = ddTakeF;
	}
    /*
    * 失效日期
     */
	public String getDdTakeT() {
		return ddTakeT;
	}

	public void setDdTakeT(String ddTakeT) {
		this.ddTakeT = ddTakeT;
	}
    /*
    * 是否被打开，已作废
     */
	public String getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
	}

	public Integer getPkadCnt() {
        return pkadCnt;
    }

    public void setPkadCnt(Integer pkadCnt) {
        this.pkadCnt = pkadCnt;
    }
    
    public String getGrowthInfo() {
        return growthInfo;
    }

    public void setGrowthInfo(String growthInfo) {
        this.growthInfo = growthInfo == null ? null : growthInfo.trim();
    }

    public String getPointInfo() {
        return pointInfo;
    }

    public void setPointInfo(String pointInfo) {
        this.pointInfo = pointInfo == null ? null : pointInfo.trim();
    }

    public String getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(String cardInfo) {
        this.cardInfo = cardInfo == null ? null : cardInfo.trim();
    }

    public String getMrclNo() {
        return mrclNo;
    }

    public void setMrclNo(String mrclNo) {
        this.mrclNo = mrclNo == null ? null : mrclNo.trim();
    }

	public Integer getIsCancel() {
		return isCancel;
	}

	public void setIsCancel(Integer isCancel) {
		this.isCancel = isCancel;
	}
	
	
	public String getTakeCardSeq() {
		return takeCardSeq;
	}

	public void setTakeCardSeq(String takeCardSeq) {
		this.takeCardSeq = takeCardSeq;
	}

	public Date getInsTime() {
		return insTime;
	}

    public void setInsTime(Date insTime) {
        this.insTime = insTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getUpdateTime() {
		return updateTime;
	}

    public String getMrstUiName() {
        return mrstUiName;
    }

    public void setMrstUiName(String mrstUiName) {
        this.mrstUiName = mrstUiName;
    }

	@Override
	public String toString() {
		return "Pkad {" + "pkadSeq=" + pkadSeq + ", pkadId=" + pkadId
				+ ", membId=" + membId + ", pkadType=" + pkadType + ", mrstUi="
				+ mrstUi + ", dPkad=" + ddPkad + ", isTake=" + isTake
				+ ", isOpen=" + isOpen + ", dTakeF=" + ddTakeF + ", dTakeT="
				+ ddTakeT + ", pkadCnt='" + pkadCnt + ", growthInfo="
				+ growthInfo + ", pointInfo='" + pointInfo + ", cardInfo="
				+ cardInfo + ", mrclNo=" + mrclNo + ", isCancel=" + isCancel
				+ ", insTime=" + insTime + ", updateTime=" + updateTime+", takeCardSeq="+takeCardSeq
				+ '}';
	}
		
}