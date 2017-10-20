package com.feiniu.favorite.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.favorite.asyncload.domain.AsyncLoadService;
import com.feiniu.favorite.dto.Code;
import com.feiniu.favorite.dto.Result;
import com.feiniu.favorite.entity.FavoriteEntity;
import com.feiniu.favorite.mapper.FavoriteMapper;
import com.feiniu.favorite.utils.RequestNoGen;
import com.feiniu.favorite.vo.CategoryVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.*;

/**
 * 查询收藏商品的有货和降价服务
 */
@Service
public class PriceAndArrivalService {

    private static final Log log = LogFactory.getLog(PriceAndArrivalService.class);

    @Autowired
    private AsyncLoadService asyncLoadService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Value("${api.searchproduct.merchantId.url}")
    private String searchproductMerchantIdUrl;


    public Result getPriceAndArrival(String memGuid, String provinceCode) {
        // 查询收藏实体
        List<FavoriteEntity> favoriteList;
        String hget = cacheService.hget(memGuid, "getPriceAndArrival_new");
        if (StringUtils.isNotBlank(hget)) {
            favoriteList = JSONObject.parseArray(hget, FavoriteEntity.class);
        } else {
            favoriteList = favoriteMapper.queryFavoriteByMemGuid(memGuid);
            cacheService.hset(memGuid, "getPriceAndArrival_new", JSONObject.toJSONString(favoriteList));
            cacheService.expire(memGuid, 172800);
        }
        Set<Long> reducePriceIds = new HashSet<>();
        Set<Long> arrivalidIds = new HashSet<>();
        if (favoriteList.size() > 0) {
            Map<String, String> priceBySeqMap = new HashMap<>();
            Map<String, Long> idBySeqMap = new HashMap<>();
            Set<String> skus = new HashSet<>();
            for (FavoriteEntity favoriteEntity : favoriteList) {
                idBySeqMap.put(favoriteEntity.getFavoriteSku(), favoriteEntity.getId());
                priceBySeqMap.put(favoriteEntity.getFavoriteSku(), favoriteEntity.getPrice());
                skus.add(favoriteEntity.getFavoriteSku());
            }
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("resnum", "2000");
            formData.add("restype", "1004");
            formData.add("sm_seq", StringUtils.join(skus, ","));
            formData.add("province", provinceCode);
            reducePriceIds = getReducePriceIds(searchproductMerchantIdUrl, formData, provinceCode, priceBySeqMap, idBySeqMap);
            arrivalidIds = getArrivalIds(searchproductMerchantIdUrl, formData, idBySeqMap);
        }
        List<CategoryVo> arrivalAndPriceCates = new ArrayList<>();
        CategoryVo arrivalVo = new CategoryVo();
        arrivalVo.setKindId(StringUtils.join(arrivalidIds, ","));
        arrivalVo.setKindName("有货");
        arrivalVo.setCateType(1);
        arrivalVo.setFavoriteCount(arrivalidIds.size());
        arrivalVo.setType(0);
        arrivalAndPriceCates.add(arrivalVo);

        CategoryVo reducePriceVo = new CategoryVo();
        reducePriceVo.setKindId(StringUtils.join(reducePriceIds, ","));
        reducePriceVo.setKindName("降价");
        reducePriceVo.setCateType(1);
        reducePriceVo.setFavoriteCount(reducePriceIds.size());
        reducePriceVo.setType(0);
        arrivalAndPriceCates.add(reducePriceVo);

        JSONObject reuslt = new JSONObject();
        reuslt.put("arrivalAndPriceCates", arrivalAndPriceCates);
        reuslt.put("arrivalCount", arrivalidIds.size());
        reuslt.put("priceCount", reducePriceIds.size());
        reuslt.put("count", favoriteList.size());
        return new Result(Code.RESULT_STATUS_SUCCESS, reuslt, "商品或者店铺分类查询成功");
    }

    private Set<Long> getReducePriceIds(String url, MultiValueMap<String, Object> formData, String provinceCode, Map<String, String> priceBySeqMap, Map<String, Long> idBySeqMap) {
        Set<Long> result = new HashSet<>();
        try {
            JSONObject reducePriceJson = asyncLoadService.post(url, formData, RequestNoGen.getNo());
            if (reducePriceJson == null || reducePriceJson.isEmpty()) {
                return result;
            }
            JSONObject grouped = reducePriceJson.getJSONObject("grouped");
            JSONObject spuSeqGroup = grouped.getJSONObject("spu_seq_group");
            JSONArray groups = spuSeqGroup.getJSONArray("groups");
            if (groups == null || groups.size() == 0) {
                return result;
            }
            for (int i = 0; i < groups.size(); i++) {
                JSONObject groupJson = groups.getJSONObject(i);
                JSONObject doclist = groupJson.getJSONObject("doclist");
                JSONArray docs = doclist.getJSONArray("docs");
                for (int j = 0; j < docs.size(); j++) {
                    JSONObject jsonObject = docs.getJSONObject(j);
                    String skuSeq = jsonObject.getString("sku_seq");
                    //实时价格（分）
                    BigDecimal price = jsonObject.getBigDecimal(provinceCode + "_price");
                    //收藏时的价格（元）
                    String favPrice = priceBySeqMap.get(skuSeq);
                    if (StringUtils.isNotBlank(favPrice) && null != price) {
                        BigDecimal favoritePrice = new BigDecimal(favPrice);
                        BigDecimal yuanPrice = price.divide(new BigDecimal(100), BigDecimal.ROUND_CEILING);
                        if (favoritePrice.compareTo(yuanPrice) > 0) {
                            Long id = idBySeqMap.get(skuSeq);
                            if (id != null) {
                                result.add(id);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("收藏夹调用查询降价异常", e);
        }
        return result;
    }

    private Set<Long> getArrivalIds(String url, MultiValueMap<String, Object> formData, Map<String, Long> idBySeqMap) {
        Set<Long> result = new HashSet<>();
        try {
            formData.add("sale_type", "1");
            JSONObject arrivalJson = asyncLoadService.post(url, formData, RequestNoGen.getNo());
            if (arrivalJson == null || arrivalJson.isEmpty()) {
                return result;
            }
            JSONObject grouped = arrivalJson.getJSONObject("grouped");
            JSONObject spuSeqGroup = grouped.getJSONObject("spu_seq_group");
            JSONArray groups = spuSeqGroup.getJSONArray("groups");
            if (groups == null || groups.size() == 0) {
                return result;
            }
            for (int i = 0; i < groups.size(); i++) {
                JSONObject groupJson = groups.getJSONObject(i);
                JSONObject doclist = groupJson.getJSONObject("doclist");
                JSONArray docs = doclist.getJSONArray("docs");
                for (int j = 0; j < docs.size(); j++) {
                    JSONObject jsonObject = docs.getJSONObject(j);
                    String skuSeq = jsonObject.getString("sku_seq");
                    Long id = idBySeqMap.get(skuSeq);
                    if (id != null) {
                        result.add(id);
                    }
                }
            }
        } catch (Exception e) {
            log.error("收藏夹调用有货查询异常。", e);
        }
        return result;
    }
}
