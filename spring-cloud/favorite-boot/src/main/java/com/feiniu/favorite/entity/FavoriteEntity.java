package com.feiniu.favorite.entity;

import java.util.Date;

/**
 * 收藏夹实体类
 *
 */
public class FavoriteEntity {

    /**
     * 流水ID
     */
    private Long id;

    /**
     * 用户GUID
     */
    private String memGuid;

    /**
     * 卖场流水ID或者店铺ID
     */
    private String favoriteSeq;
    /**
     * 商品ID或者店铺ID
     */
    private String favoriteSku;
    /**
     * 规格品ID
     */
    private String favoriteSpu;
    /**
     * 收藏类型，0:商品，1：店铺
     */
    private Integer type;

    /**
     * 收藏时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否有效
     */
    private Boolean active;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商家ID
     */
    private Integer merchantId;

    /**
     * 卖场类型
     */
    private Integer seqKind;

    /**
     * 收藏类目Id
     */
    private String kindId;

    /**
     * 收藏类目名称
     */
    private String kindName;

    /**
     * 是否跨境
     */
    private Integer isCrossborder;

    /**
     * 唯一性
     */
    private String guidSeqActive;

    /**
     * 收藏时的价格
     */
    private String price;

    /**
     * 收藏时的渠道
     */
    private String channel;

    public Integer getIsCrossborder() {
        return isCrossborder;
    }

    public void setIsCrossborder(Integer isCrossborder) {
        this.isCrossborder = isCrossborder;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public Integer getSeqKind() {
        return seqKind;
    }

    public void setSeqKind(Integer seqKind) {
        this.seqKind = seqKind;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemGuid() {
        return memGuid;
    }

    public void setMemGuid(String memGuid) {
        this.memGuid = memGuid;
    }

    public String getFavoriteSeq() {
        return favoriteSeq;
    }

    public void setFavoriteSeq(String favoriteSeq) {
        this.favoriteSeq = favoriteSeq;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getGuidSeqActive() {
        return guidSeqActive;
    }

    public void setGuidSeqActive(String guidSeqActive) {
        this.guidSeqActive = guidSeqActive;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getFavoriteSku() {
        return favoriteSku;
    }

    public void setFavoriteSku(String favoriteSku) {
        this.favoriteSku = favoriteSku;
    }

    public String getFavoriteSpu() {
        return favoriteSpu;
    }

    public void setFavoriteSpu(String favoriteSpu) {
        this.favoriteSpu = favoriteSpu;
    }


    @Override
    public String toString() {
        return "FavoriteEntity{" +
                "id=" + id +
                ", memGuid='" + memGuid + '\'' +
                ", favoriteSeq='" + favoriteSeq + '\'' +
                ", favoriteSku='" + favoriteSku + '\'' +
                ", favoriteSpu='" + favoriteSpu + '\'' +
                ", type=" + type +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", active=" + active +
                ", remark='" + remark + '\'' +
                ", merchantId=" + merchantId +
                ", seqKind=" + seqKind +
                ", kindId='" + kindId + '\'' +
                ", kindName='" + kindName + '\'' +
                ", isCrossborder=" + isCrossborder +
                ", guidSeqActive='" + guidSeqActive + '\'' +
                ", price='" + price + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}
