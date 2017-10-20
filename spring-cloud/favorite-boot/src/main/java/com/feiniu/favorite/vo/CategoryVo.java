package com.feiniu.favorite.vo;

/**
 * 收藏商品，店铺分类
 */
public class CategoryVo {

    /**
     * 分类Id
     */
    private String kindId;

    /**
     * 分类名称
     */
    private String kindName;

    /**
     * 收藏类型，0：商品，1：店铺
     */
    private Integer type;

    /**
     * 分类属性类型，0：属性分类，1：活动分类
     */
    private Integer cateType;

    /**
     * 收藏量
     */
    private Integer favoriteCount;

    public String getKindId() {
        return kindId;
    }

    public void setKindId(String kindId) {
        this.kindId = kindId;
    }

    public String getKindName() {
        return kindName;
    }

    public void setKindName(String kindName) {
        this.kindName = kindName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getCateType() {
        return cateType;
    }

    /**
     * 分类属性类型，0：属性分类，1：活动分类
     * 
     * @param cateType
     */
    public void setCateType(Integer cateType) {
        this.cateType = cateType;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    @Override
    public String toString() {
        return "CategoryVo [kindId=" + kindId + ", kindName=" + kindName + ", type=" + type + ", cateType="
                + cateType + ", favoriteCount=" + favoriteCount + "]";
    }

}
