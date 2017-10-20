package com.feiniu.score.entity.growth;

import java.util.Date;

public class GrowthLog {
    private Long glSeq;

    private String memGuid;

    private String tableName;

    private Long recId;

    private String operate;

    private String remark;

    private String insMan;

    private Date insDate;

    private String updMan;

    private Date updDate;

    public Long getGlSeq() {
        return glSeq;
    }

    public void setGlSeq(Long glSeq) {
        this.glSeq = glSeq;
    }

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid == null ? null : memGuid.trim();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public Long getRecId() {
        return recId;
    }

    public void setRecId(Long recId) {
        this.recId = recId;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate == null ? null : operate.trim();
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
}