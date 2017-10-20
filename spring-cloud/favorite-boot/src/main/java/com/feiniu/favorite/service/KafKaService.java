package com.feiniu.favorite.service;

import java.util.List;
import java.util.Map;

/**
 * kafka消息推送接口
 *
 */
public interface KafKaService {

    /**
     * 异步推送当前所有有效商品信息到kafka中
     */
    void pushAllFavoriteData();

    /**
     * 异步推送某个有效商品信息到kafka中
     */
    void pushFavoriteData(Map<String, Object> smseqs);

    /**
     * 根据id查询favorite_seq list
     */
    List<String> getSmseqsByIds(String ids);

    /**
     * 根据memId查询有效商品的smseqs
     */
    List<String> getSmseqsByMemGuid(String memGuid);

}
