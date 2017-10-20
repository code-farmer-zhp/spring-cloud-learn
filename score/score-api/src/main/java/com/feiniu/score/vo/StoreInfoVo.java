package com.feiniu.score.vo;

public class StoreInfoVo {
    private String supId;
    private String storeNo;

    public StoreInfoVo(String supId, String storeNo) {
        this.supId = supId;
        this.storeNo = storeNo;
    }

    public String getSupId() {
        return supId;
    }

    public void setSupId(String supId) {
        this.supId = supId;
    }

    public String getStoreNo() {
        return storeNo;
    }

    public void setStoreNo(String storeNo) {
        this.storeNo = storeNo;
    }

    @Override
    public String toString() {
        return "StoreInfoVo{" +
                "supId='" + supId + '\'' +
                ", storeNo='" + storeNo + '\'' +
                '}';
    }
}
