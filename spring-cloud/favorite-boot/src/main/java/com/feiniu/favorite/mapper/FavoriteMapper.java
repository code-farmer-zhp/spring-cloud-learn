package com.feiniu.favorite.mapper;

import com.feiniu.favorite.entity.FavoriteEntity;
import com.feiniu.favorite.entity.FavoriteIdList;
import com.feiniu.favorite.entity.FavoriteSumVO;
import com.feiniu.favorite.vo.QueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 收藏夹Mapper
 */
public interface FavoriteMapper {
    /**
     * 多地多仓通过skuId查询
     */
    FavoriteEntity queryByMemGuidAndTypeAndFavoriteSkuId(@Param("memGuid") String memGuid, @Param("favoriteSku") String favoriteSkuId, @Param("type") int type);

    /**
     * 查询店铺或商品收藏数量
     */

    int countByTypeAndMemGuid(@Param("memGuid") String memGuid, @Param("type") int type);

    /**
     * 多地多仓通过skuId存储
     */
    void saveEntityBySkuId(FavoriteEntity entity);

    /**
     * 查询收藏商品或店铺
     */
    List<FavoriteEntity> query(QueryVo query);

    /**
     * 查询商品或店铺ID列表
     */
    List<FavoriteIdList> getIdList(QueryVo query);

    /**
     * 逻辑删除收藏商品或店铺
     */
    int delete(Map<String, Object> map);

    /**
     * 删除某一用户的所有收藏夹数据（逻辑删除）
     */
    void deleteAllByMemGuid(String memGuid);

    /**
     * 查询收藏夹中商品或店铺数量
     */
    long count(QueryVo query);

    /**
     * 判断某用户，是否收藏某商品（自营、商城），店铺（商城）收藏了则返回该记录的流水ID
     */
    Long haveCollectGoodsOrShop(QueryVo query);

    /**
     * 批量查询收藏夹中商城商品数量
     */
    List<FavoriteSumVO> countBySkuIds(List<String> skuIds);


    List<FavoriteEntity> findMerchanId();

    void upMerchanId(@Param("merchantId") int merchantId, @Param("id") Long id);

    List<String> getGoodIdsByMemGuid(@Param("memGuid") String memGuid, @Param("pageSize") Integer pageSize, @Param("pageAll") Integer pageAll);

    List<FavoriteEntity> queryFavoriteByMemGuid(@Param("memGuid") String memGuid);

    List<FavoriteEntity> getFavoriteByIdsAndKindsIds(@Param("memGuid") String memGuid, @Param("kindId") String kindId);

    List<FavoriteEntity> getFavoritePriceBySkuIds(@Param("memGuid") String memGuid, @Param("skuIds") String[] skuIds);

    List<FavoriteEntity> findKindName();

    void upKindName(@Param("validateAppName") String validateAppName, @Param("kindId") String kindId);


    /**
     * 查询当前所有商品的已收藏总数
     */
    List<FavoriteSumVO> getAllFavoriteData();

    /**
     * 查询入参中的商品已收藏总数
     */
    List<FavoriteSumVO> getFavoriteDataBySmseqs(List<String> smseqs);

    /**
     * 根据ids查询smseqs
     */
    List<String> getSmseqsByIds(List<String> smseqs);

    /**
     * 根据memid查询所有有效的商品smseqs
     */
    List<String> getSmseqsByMemGuid(String memGuid);

    List<FavoriteEntity> findUpLoad(@Param("j") int j, @Param("m") int m);

    void upLoad(@Param("skuSeq") String skuSeq, @Param("smSeq") String smSeq, @Param("spuSeq") String spuSeq);

    void upLoadType();

    List<FavoriteEntity> findUpLoadNext();

    void deleteSmSeq();

    int deleteBySku(@Param("memGuid") String memGuid, @Param("sku") String sku);

    int updateError(@Param("oldSku") Object oldSku, @Param("spu") String spu, @Param("newSku") Object newSku);

    List<FavoriteEntity> findAll(@Param("j") int start, @Param("m") int i);

    List<FavoriteEntity> getSmseqsBySkuSeq(@Param("skuSeq") String skuSeq, @Param("start") int start, @Param("pageSize") int pageSize);
}
