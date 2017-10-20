package com.feiniu.score.entity.growth;

import java.math.BigDecimal;
import java.util.Date;

public class OrderInfo {
    private Long oiSeq;

    private String memGuid;

    private String ogNo;

    private String ogSeq;

    private String adrId;

    private String olSeq;

    private String itNo;

    private String rpSeq;

    private BigDecimal rpPoint;

    private String rgSeq;

    private String rlSeq;

    private String commentSeq;

    private String remark;

    private String insMan;

    private Date insDate;

    private String updMan;

    private Date updDate;

    public Long getOiSeq() {
        return oiSeq;
    }

    public void setOiSeq(Long oiSeq) {
        this.oiSeq = oiSeq;
    }

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid == null ? null : memGuid.trim();
    }

    public String getOgNo() {
        return ogNo;
    }

    public void setOgNo(String ogNo) {
        this.ogNo = ogNo == null ? null : ogNo.trim();
    }

    public String getOgSeq() {
        return ogSeq;
    }

    public void setOgSeq(String ogSeq) {
        this.ogSeq = ogSeq == null ? null : ogSeq.trim();
    }

    public String getAdrId() {
        return adrId;
    }

    public void setAdrId(String adrId) {
        this.adrId = adrId == null ? null : adrId.trim();
    }

    public String getOlSeq() {
        return olSeq;
    }

    public void setOlSeq(String olSeq) {
        this.olSeq = olSeq == null ? null : olSeq.trim();
    }

    public String getItNo() {
        return itNo;
    }

    public void setItNo(String itNo) {
        this.itNo = itNo == null ? null : itNo.trim();
    }

    public String getRpSeq() {
        return rpSeq;
    }

    public void setRpSeq(String rpSeq) {
        this.rpSeq = rpSeq == null ? null : rpSeq.trim();
    }

    public BigDecimal getRpPoint() {
        return rpPoint;
    }

    public void setRpPoint(BigDecimal rpPoint) {
        this.rpPoint = rpPoint;
    }

    public String getRgSeq() {
        return rgSeq;
    }

    public void setRgSeq(String rgSeq) {
        this.rgSeq = rgSeq == null ? null : rgSeq.trim();
    }

    public String getRlSeq() {
        return rlSeq;
    }

    public void setRlSeq(String rlSeq) {
        this.rlSeq = rlSeq == null ? null : rlSeq.trim();
    }

    public String getCommentSeq() {
        return commentSeq;
    }

    public void setCommentSeq(String commentSeq) {
        this.commentSeq = commentSeq == null ? null : commentSeq.trim();
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