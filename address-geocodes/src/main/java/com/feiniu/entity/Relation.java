package com.feiniu.entity;


import java.util.Date;

public class Relation {
    private String memGuid;
    private String warehouseCode;
    private Date insTime;
    private Date upTime;

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Date getInsTime() {
        return insTime;
    }

    public void setInsTime(Date insTime) {
        this.insTime = insTime;
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }

    @Override
    public String toString() {
        return "Relation{" +
                "memGuid='" + memGuid + '\'' +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", insTime=" + insTime +
                ", upTime=" + upTime +
                '}';
    }
}
