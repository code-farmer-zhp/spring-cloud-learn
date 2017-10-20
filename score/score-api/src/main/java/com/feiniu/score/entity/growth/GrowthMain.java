package com.feiniu.score.entity.growth;

import java.util.Date;

public class GrowthMain {
    private Long gmSeq;

    private String memGuid;

    private Integer changedGrowthValue=null;

    private Integer growthValue;

    private Date effectiveDate;

    private Date expiryDate;

    private String memLevel;

    private Date insDate;

    private String insMan;

    private Date updDate;

    private String updMan;

    private Date levelChangeDate;

    public Long getGmSeq() {
        return gmSeq;
    }

    public void setGmSeq(Long gmSeq) {
        this.gmSeq = gmSeq;
    }

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid == null ? null : memGuid.trim();
    }

    public Integer getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Integer growthValue) {
        if(growthValue>=0) {
            this.growthValue = growthValue;
        }else{
            this.growthValue =0;
        }
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMemLevel() {
        return memLevel;
    }

    public void setMemLevel(String memLevel) {
        this.memLevel = memLevel == null ? null : memLevel.trim();
    }

    public Date getInsDate() {
        return insDate;
    }

    public void setInsDate(Date insDate) {
        this.insDate = insDate;
    }

    public String getInsMan() {
        return insMan;
    }

    public void setInsMan(String insMan) {
        this.insMan = insMan == null ? null : insMan.trim();
    }

    public Date getUpdDate() {
        return updDate;
    }

    public void setUpdDate(Date updDate) {
        this.updDate = updDate;
    }

    public String getUpdMan() {
        return updMan;
    }

    public void setUpdMan(String updMan) {
        this.updMan = updMan == null ? null : updMan.trim();
    }
    
	public Date getLevelChangeDate() {
		return levelChangeDate;
	}

	public void setLevelChangeDate(Date levelChangeDate) {
		this.levelChangeDate = levelChangeDate;
	}

	@Override
	public String toString() {
		return "GrowthMain {" + "memGuid=" + memGuid + ", growthValue="
				+ growthValue + ", effectiveDate='" + effectiveDate
				+ ", expiryDate=" + expiryDate + ", memLevel='" + memLevel
				+ ", insDate=" + insDate + ", insMan='" + insMan + ", updDate="
				+ updDate + ", updMan='" + updMan +", levelChangeDate='" + levelChangeDate
                +"}";
	}

    /**
     * 只在更新growthMain的growthValue时使用
     */
    public void setChangedGrowthValue(Integer changedGrowthValue) {
        this.changedGrowthValue = changedGrowthValue;
    }


    public Integer getChangedGrowthValue() {
        return changedGrowthValue;
    }
}