package com.feiniu.favorite.vo;

import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询收藏商品或店铺
 */
public class QueryVo {

    /**
     * 用户GUID
     */
    @QueryParam("mem_guid")
    private String memGuid;

    /**
     * 商品流水号或者店铺流水号
     */
    @QueryParam("favorite_seq")
    private String favoriteSeq;
    /**
     * 商品流水号或者店铺流水号
     */
    @QueryParam("favorite_sku")
    private String favoriteSku;
    /**
     * 商品流水号或者店铺流水号
     */
    @QueryParam("favorite_spu")
    private String favoriteSpu;

    /**
     * 类型，0-自营商品或商城商品，1-店铺
     */
    @QueryParam("type")
    private Integer type;

    /**
     * 区域代码
     */
    @QueryParam("area_code")
    private String areaCode;

    /**
     * 是否有效
     */
    @QueryParam("active")
    private Boolean active;

    /**
     * 偏移量
     */
    @QueryParam("offset")
    private Integer offset;

    /**
     * 返回数据条数
     */
    @QueryParam("limit")
    private Integer limit;

    /**
     * 活动渠道(1:pc端 2:app端3:触屏)
     */
    @QueryParam("activity_qd")
    private String activityQd;

    /**
     * 商城商品情况下：商家ID
     */
    @QueryParam("merchant_id")
    private Integer merchantId;

    /**
     * 收藏分类编号
     */
    @QueryParam("kind_id")
    private String kindId;

    /**
     * 收藏id（批量）
     */
    @QueryParam("ids")
    private String ids;

    /**
     * 是否跨境商品 is_crossborder
     */
    @QueryParam("is_crossborder")
    private Integer isCrossborder;

    /**
     * 收藏时的价格
     */
    @QueryParam("price")
    private String price;

    /**
     * 收藏时的渠道
     */
    @QueryParam("channel")
    private String channel;

    /**
     * 是否店铺新品列表 newListShop
     */
    @QueryParam("newListShop")
    private Integer newListShop;



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

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }


    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getIsCrossborder() {
        return isCrossborder;
    }

    public void setIsCrossborder(Integer isCrossborder) {
        this.isCrossborder = isCrossborder;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }


    public Integer getNewListShop() {
        return newListShop;
    }

    public void setNewListShop(Integer newListShop) {
        this.newListShop = newListShop;
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

    public List<Integer> getIds() {
        if (StringUtils.isEmpty(ids)) {
            return null;
        }
        List<Integer> idlist = new ArrayList<Integer>();
        String[] idres = ids.split(",");
        for (String isstr : idres) {
            if (StringUtils.isNotEmpty(isstr)) {
                idlist.add(Integer.parseInt(isstr));
            }
        }
        return idlist.size() > 0 ? idlist : null;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getActivityQd() {
        return activityQd;
    }

    public void setActivityQd(String activityQd) {
        this.activityQd = activityQd;
    }

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
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
        return "QueryVo{" +
                "memGuid='" + memGuid + '\'' +
                ", favoriteSeq='" + favoriteSeq + '\'' +
                ", favoriteSku='" + favoriteSku + '\'' +
                ", favoriteSpu='" + favoriteSpu + '\'' +
                ", type=" + type +
                ", areaCode='" + areaCode + '\'' +
                ", active=" + active +
                ", offset=" + offset +
                ", limit=" + limit +
                ", activityQd='" + activityQd + '\'' +
                ", merchantId=" + merchantId +
                ", kindId='" + kindId + '\'' +
                ", ids='" + ids + '\'' +
                ", isCrossborder=" + isCrossborder +
                ", price='" + price + '\'' +
                ", channel='" + channel + '\'' +
                ", newListShop=" + newListShop +
                '}';
    }
}
