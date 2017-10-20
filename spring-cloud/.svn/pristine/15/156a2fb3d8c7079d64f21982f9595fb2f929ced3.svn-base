package com.feiniu.favorite.service;

import com.feiniu.favorite.dto.Result;
import com.feiniu.favorite.vo.QueryVo;

import java.io.IOException;
import java.util.List;

/**
 * 收藏夹
 */
public interface FavoriteService {

    /**
     * 收藏商品或者店铺
     */
    Result add(String memGuid, String favoriteSkuId, Integer type, Integer seqKind, String areaCode,
               Integer isCrossborder, String price, String channel);

    /**
     * 查询收藏的商品或店铺类别
     */
    Result queryCategory(QueryVo vo);

    /**
     * 查询收藏的商品或店铺详情
     */
    Result queryAsyncLoadService(QueryVo vo);

    /**
     * 根据用户guid查询收藏夹商品或店铺的ID列表
     */
    Result getIdList(QueryVo vo);

    /**
     * 逻辑删除收藏商品或店铺
     */
    Result delete(String memGuid, String favoriteId);

    /**
     * 删除某一用户的收藏夹商品（自营、商城），店铺（商城）（逻辑删除）
     */
    Result deleteAllByMemGuid(String memGuid);

    /**
     * 查询收藏夹中商品或店铺数量
     */
    Result count(QueryVo query);

    /**
     * 判断某用户，是否收藏商品（自营、商城），店铺（商城）
     */
    Result haveCollectGoodsOrShop(QueryVo query);

    /**
     * 批量查询收藏夹中商城商品数量
     */
    Result countBySkuIds(String skuIds);

    void upMerchanId();

    List<String> getGoodIdsByMemGuid(String memGuid, Integer pageSize, Integer pageIndex);

    Result getFavoriteByIdsAndKindsIds(String memGuid, String ids, String kindId, Integer pageSize,
                                       Integer pageIndex, String provinceCode, String activityQd);

    Result getFavoritePriceBySkuIds(String memGuid, String skuIds, String provinceCode, String activityQd);

    void upKindName();

    void upLoad();

    void upLoadType();

    void returnGo();

    void deleteSmSeq();

    Result deleteBySku(String memGuid, String sku);

    Result deleteById(String memGuid, String favoriteId);

    void upLoadError() throws IOException;

    void upLoadAll();

    void upLoadBySku(String skuSeq);

    void upLoadBySmSeq();

}
