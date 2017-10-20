package com.feiniu.favorite.vo;

import java.util.List;

/**
 * 店铺Vo
 * 
 */
public class StoreVo {

    /*
     * 购物车主键
     */
    private long id;

    /*
     * 商家id
     */
    private String merchantId;

    /*
     * 店铺名称
     */
    private String storeName;

    /*
     * 店铺评分信息List
     */
    private List<StoreGradeVo> storeGrades;

    /*
     * 商家店铺logo图片
     */
    private String storeLogoUrl;

    /*
     * 店铺链接
     */
    private String url;

    /*
     * 店铺是否受罚
     */
    private String punished;

    /*
     * 店铺包含的新上架子商品
     */
    private List<ProductVo> newProducts;

    /*
     * 店铺新品总数
     */
    private int newProductSize;

    public int getNewProductSize() {
        return newProductSize;
    }

    public void setNewProductSize(int newProductSize) {
        this.newProductSize = newProductSize;
    }

    /*
     * 店铺包含的热销子商品
     */
    private List<ProductVo> hotProducts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public List<StoreGradeVo> getStoreGrades() {
        return storeGrades;
    }

    public void setStoreGrades(List<StoreGradeVo> storeGrades) {
        this.storeGrades = storeGrades;
    }

    public String getStoreLogoUrl() {
        return storeLogoUrl;
    }

    public void setStoreLogoUrl(String storeLogoUrl) {
        this.storeLogoUrl = storeLogoUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ProductVo> getNewProducts() {
        return newProducts;
    }

    public void setNewProducts(List<ProductVo> newProducts) {
        this.newProducts = newProducts;
    }

    public List<ProductVo> getHotProducts() {
        return hotProducts;
    }

    public void setHotProducts(List<ProductVo> hotProducts) {
        this.hotProducts = hotProducts;
    }

    public String getPunished() {
        return punished;
    }

    public void setPunished(String punished) {
        this.punished = punished;
    }

    /*******************************************************************************************************************
     * 设置商品数据的返回值
     ******************************************************************************************************************/
    public static StoreVo setData(long id, String merchantId, String storeName,
                                  List<StoreGradeVo> storeGrades, String storeLogoUrl, String url, String punished,
                                  List<ProductVo> newProducts, int newProductSize, List<ProductVo> hotProducts) {

        StoreVo vo = new StoreVo();

        // 购物车主键
        vo.setId(id);
        // 商家id
        vo.setMerchantId(merchantId);
        // 店铺名称
        vo.setStoreName(storeName);
        // 店铺评分信息List
        vo.setStoreGrades(storeGrades);
        // 商家店铺logo图片
        vo.setStoreLogoUrl(storeLogoUrl);
        // 店铺链接
        vo.setUrl(url);
        // 店铺是否受罚
        vo.setPunished(punished);
        // 店铺包含的新上架子商品
        vo.setNewProducts(newProducts);
        // 新品总数
        vo.setNewProductSize(newProductSize);
        // 店铺包含的热销子商品
        vo.setHotProducts(hotProducts);

        return vo;

    }
}
