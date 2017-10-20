package com.feiniu.favorite.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.favorite.asyncload.domain.AsyncLoadService;
import com.feiniu.favorite.dto.Code;
import com.feiniu.favorite.dto.Result;
import com.feiniu.favorite.entity.FavoriteEntity;
import com.feiniu.favorite.entity.FavoriteIdList;
import com.feiniu.favorite.entity.FavoriteSumVO;
import com.feiniu.favorite.mapper.FavoriteMapper;
import com.feiniu.favorite.service.FavoriteService;
import com.feiniu.favorite.service.KafKaService;
import com.feiniu.favorite.service.SoaProductService;
import com.feiniu.favorite.utils.PicRandomUtil;
import com.feiniu.favorite.utils.PicUtil;
import com.feiniu.favorite.utils.RequestNoGen;
import com.feiniu.favorite.vo.*;
import com.fn.cache.client.RedisCacheClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * 收藏夹Service
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final Log log = LogFactory.getLog(FavoriteServiceImpl.class);

    // 每个用户收藏的商品最大量
    private static final int maxNumGoodsPerUser = 2000;

    // 每个用户收藏的店铺最大量
    private static final int maxNumStoresPerUser = 1000;

    // 缓存时间
    private static int redisTime = 172800;

    // 自营商品类目查询
    @Value("${api.product.category.url}")
    private String productCategoryUrl;

    // 商城商品类目查询
    @Value("${api.mall.category.url}")
    private String malltCategoryUrl;

    // 店铺类目查询
    @Value("${api.store.category.url}")
    private String storeCategoryUrl;

    // 行销service搜索页接口url
    @Value("${api.mall.promotion.search.activity}")
    private String searchPageNormalActivityUrl;

    @Value("${api.GetIntegralActivity}")
    private String apiGetIntegralActivity;

    @Value("${api.mall.product.new}")
    private String mallProductNew;

    @Value("${item.product.domain.new}")
    private String itemProductDomainNew;

    // 商城店铺基本信息
    @Value("${api.mall.store.url}")
    private String mallStoreUrl;

    // 商城店铺评分信息
    @Value("${api.mall.store.grade.info}")
    private String mallStoreGradeUrl;

    // 根据skuid 判断是否存在merchantId
    @Value("${api.mall.product.merchantId}")
    private String mallProMerchantId;

    // 查询店铺有劵
    @Value("${api.couponcs.merchantId.url}")
    private String couponcsMerchantIdUrl;

    // 查询店铺活动
    @Value("${api.promotion.merchantId.url}")
    private String promotionMerchantIdUrl;

    // 查询店铺新品
    @Value("${api.searchproduct.merchantId.url}")
    private String searchproductMerchantIdUrl;

    // 查询商品kindName
    @Value("${api.category.getAppNameByGcSeq}")
    private String apiCategoryGetAppNameByGcSeq;

    @Value("${java.seckill.drp}")
    private String javaSeckillDrp;

    // 秒杀配置的大区
    @Value("${province.pgseq.map}")
    private String provincePgseqMap;

    @Value("${integral.batchCheckSendIntegral.url}")
    private String integralBatchCheckSendIntegralUrl;

    @Value("${ipay.parseItemsUseHB.url}")
    private String ipayParseItemsUseHBUrl;

    @Value("${m.staticDomain.url}")
    private String mStaticUrl;

    @Value("${store.url}")
    private String storeUrl;

    @Value("${imgInside.url}")
    private String imgInsideUrl;

    @Value("${findSkuInfoByItnos.url}")
    private String findSkuInfoByItnosUrl;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisCacheClient redisCacheClient;

    @Autowired
    private AsyncLoadService asyncLoadService;

    @Autowired
    private KafKaService kafKaService;

    @Autowired
    private SoaProductService soaProductService;

    private FastDateFormat myFmt = FastDateFormat.getInstance("yyyy-MM-dd HH:mm");

    /**
     * 添加收藏夹
     */
    @Override
    @HystrixCommand
    public Result add(String memGuid, String favoriteSkuId, Integer type, Integer seqKind, String areaCode,
                      Integer isCrossborder, String price, String channel) {
        int merchantId = -1;// 商家ID默认为-1
        FavoriteEntity queryByMemGuidAndType = favoriteMapper.queryByMemGuidAndTypeAndFavoriteSkuId(memGuid,
                favoriteSkuId, type);

        if (queryByMemGuidAndType != null) {
            return new Result(Code.RESULT_STATUS_DUPLICATE_FAVORITE, "用户的收藏夹中已经存在该"
                    + (type == 2 ? "店铺" : "商品") + "！");
        }
        // 校验用户收藏数量限制：每个用户可收藏自营商品+商城商品2000个，店铺1000个
        if (type == 0 || type == 1) {
            int sumPerUser1 = favoriteMapper.countByTypeAndMemGuid(memGuid, 3);
            if (sumPerUser1 >= maxNumGoodsPerUser) {
                return new Result(Code.RESULT_STATUS_FAVORITE_COUNT_EXCEED, "您的收藏夹已满!");
            }
        } else if (type == 2) {
            int sumPerUser1 = favoriteMapper.countByTypeAndMemGuid(memGuid, 2);
            if (sumPerUser1 >= maxNumStoresPerUser) {
                return new Result(Code.RESULT_STATUS_FAVORITE_COUNT_EXCEED, "您的收藏夹已满!");
            }
        }
        // 组装添加收藏实例
        FavoriteEntity entity = new FavoriteEntity();
        entity.setMemGuid(memGuid);
        entity.setFavoriteSeq(favoriteSkuId);
        entity.setFavoriteSku(favoriteSkuId);
        entity.setType(type);
        // 非自营商品没有 虚拟卖场seq，不处理单品多件问题
        if (type != 0 || seqKind == null) {
            seqKind = 0;
        }
        entity.setSeqKind(seqKind);
        if (isCrossborder == null) {
            isCrossborder = 0;
        }
        entity.setIsCrossborder(isCrossborder); // 新增是否跨境商品
        if (type == 2) {
            // 店铺收藏时价格设为0
            price = "0";
        }
        if (StringUtils.isBlank(price) || "null".equals(price)) {
            price = "0";
        }
        if (StringUtils.isBlank(channel)) {
            channel = "";
        }
        entity.setPrice(price);
        entity.setChannel(channel);
        if (type == 1) {// 商城商品保存商家ID 多地多仓不变
            Result merchantIdBy = getMerchantIdByFavoriteSeq(favoriteSkuId);
            if (merchantIdBy.getCode() != Code.RESULT_STATUS_SUCCESS) {
                return merchantIdBy;
            } else {
                merchantId = (int) merchantIdBy.getData();
            }
        }
        entity.setMerchantId(merchantId);
        String kindId = "";
        String kindName = "";
        String spuId = "";
        // 商品或店铺查询所属类别
        if (type == 0) {// 自营商品类别
            Result kindIdAndKindNameByFavoriteSeq = getKindIdAndKindNameByFavoriteSeqSelfSupport(favoriteSkuId);
            if (kindIdAndKindNameByFavoriteSeq.getCode() != Code.RESULT_STATUS_SUCCESS) {
                return kindIdAndKindNameByFavoriteSeq;
            } else {
                JSONObject kindIdAndKindNameByFavoriteSeqJSONObject = (JSONObject) kindIdAndKindNameByFavoriteSeq
                        .getData();
                kindId = kindIdAndKindNameByFavoriteSeqJSONObject.getString("kindId");
                kindName = kindIdAndKindNameByFavoriteSeqJSONObject.getString("kindName");
                spuId = kindIdAndKindNameByFavoriteSeqJSONObject.getString("spuId");
            }
        } else if (type == 1) {// 商城商品类别
            Result kindIdAndKindNameByFavoriteSeqShoppingMall = getMallKindIdAndName(favoriteSkuId);
            if (kindIdAndKindNameByFavoriteSeqShoppingMall.getCode() != Code.RESULT_STATUS_SUCCESS) {
                return kindIdAndKindNameByFavoriteSeqShoppingMall;
            } else {
                JSONObject kindIdAndKindNameByFavoriteSeqShoppingMallJSONObject = (JSONObject) kindIdAndKindNameByFavoriteSeqShoppingMall
                        .getData();
                kindId = kindIdAndKindNameByFavoriteSeqShoppingMallJSONObject.getString("kindId");
                kindName = kindIdAndKindNameByFavoriteSeqShoppingMallJSONObject.getString("kindName");
            }
        } else if (type == 2) {// 店铺类别
            Result getKindIdAndKindNameByFavoriteSeqShop = getKindIdAndKindNameByFavoriteSeqShop(favoriteSkuId);
            if (getKindIdAndKindNameByFavoriteSeqShop.getCode() != Code.RESULT_STATUS_SUCCESS) {
                return getKindIdAndKindNameByFavoriteSeqShop;
            } else {
                JSONObject getKindIdAndKindNameByFavoriteSeqShopJSONObject = (JSONObject) getKindIdAndKindNameByFavoriteSeqShop
                        .getData();
                kindId = getKindIdAndKindNameByFavoriteSeqShopJSONObject.getString("kindId");
                kindName = getKindIdAndKindNameByFavoriteSeqShopJSONObject.getString("kindName");
            }
        }
        entity.setFavoriteSpu(spuId);
        entity.setKindId(kindId);
        entity.setKindName(kindName);
        // 新增字段 唯一性 0925
        String guidSeqActive = memGuid + "_" + favoriteSkuId + "_1";
        entity.setGuidSeqActive(guidSeqActive);
        favoriteMapper.saveEntityBySkuId(entity);

        return new Result(Code.RESULT_STATUS_SUCCESS, "加入收藏夹成功");
    }

    /**
     * 查询收藏的商品或店铺分类以及促销信息
     */
    @Override
    @HystrixCommand
    public Result queryCategory(QueryVo query) {
        String requestNo = RequestNoGen.getNo();
        // 查询收藏实体
        List<FavoriteEntity> favoriteList = new ArrayList<>();
        String memGuid = query.getMemGuid();

        String hget = redisCacheClient.hget(memGuid, "category_new" + JSONObject.toJSONString(query));
        if (StringUtils.isNotBlank(hget)) {
            favoriteList = JSONObject.parseArray(hget, FavoriteEntity.class);
        } else {
            if (query.getType() == 0) {
                // type=3，自营和商城的商品一起查询
                query.setType(3);
                favoriteList = favoriteMapper.query(query);
                query.setType(0);
            } else if (query.getType() == 1) {
                // type=2是店铺收藏查询
                query.setType(2);
                favoriteList = favoriteMapper.query(query);
                query.setType(1);
            }
            redisCacheClient.hset(memGuid, "category_new" + JSONObject.toJSONString(query),
                    JSONObject.toJSONString(favoriteList));
            redisCacheClient.expire(memGuid, redisTime);
        }
        JSONObject areaCode = JSONObject.parseObject(query.getAreaCode());
        String provinceCode = areaCode.getString("provinceCode"); // 省
        int catetypecount = favoriteList.size();// 总分类总量
        int acttypecount = 0;// 商品促销分类总量
        int havecoupon = 0;// 店铺有劵分类总量
        int havenew = 0; // 店铺上新分类总量
        int haveactivity = 0; // 店铺活动分类总量
        // 实例化分类容器list
        // 基础类别实例
        List<CategoryVo> categorys = new ArrayList<>();
        // 商品促销类别实例
        List<CategoryVo> actCates = new ArrayList<>();
        // 类目ID及类目名
        Map<String, String> cates = new HashMap<>();
        // 类目ID及数量
        Map<String, Integer> shops = new HashMap<>();
        // 商品促销
        StringBuilder pid = new StringBuilder();
        // 店铺有劵
        StringBuilder mid = new StringBuilder();
        // 店铺新品
        StringBuilder xid = new StringBuilder();
        // 店铺活动
        StringBuilder sid = new StringBuilder();
        JSONArray skuList = new JSONArray();
        Map<String, Long> merchantIdKeys = new HashMap<>();
        List<String> merchantIds = new ArrayList<>();
        StringBuilder merchantIdString = new StringBuilder();
        StringBuilder merchantIdShip = new StringBuilder();
        for (FavoriteEntity favoriteEntity : favoriteList) {
            shops.put(
                    favoriteEntity.getKindId(),
                    shops.containsKey(favoriteEntity.getKindId()) ? (shops.get(favoriteEntity.getKindId()) + 1)
                            : 1);
            if (!cates.containsKey(favoriteEntity.getKindId())) {
                cates.put(favoriteEntity.getKindId(), favoriteEntity.getKindName());
            }
            JSONObject skuListobj = new JSONObject();
            skuListobj.put("skuId", favoriteEntity.getFavoriteSku());
            if (favoriteEntity.getType() == 0) {// 自营商品
                skuListobj.put("merchantId", "-1");
            } else if (favoriteEntity.getType() == 1) {// 商城商品
                skuListobj.put("merchantId", favoriteEntity.getMerchantId());
            }
            skuList.add(skuListobj);
            if (favoriteEntity.getType() == 2) {
                merchantIds.add(favoriteEntity.getFavoriteSku());
                merchantIdString.append(favoriteEntity.getFavoriteSku()).append(",");
            }
            if (favoriteEntity.getType() != 2) {
                merchantIdShip.append(favoriteEntity.getFavoriteSku()).append(",");
            }
            merchantIdKeys.put(favoriteEntity.getFavoriteSku(), favoriteEntity.getId());
        }
        for (String kindId : cates.keySet()) {
            String kindName = cates.get(kindId);
            CategoryVo vo = new CategoryVo();
            vo.setKindId(kindId);
            vo.setKindName(kindName);
            vo.setCateType(0);
            vo.setFavoriteCount(shops.get(kindId));
            if (query.getType() == 0) {
                vo.setType(0);
            } else {
                vo.setType(1);

            }
            categorys.add(vo);
        }

        if (query.getType() == 0) {
            if (skuList.size() > 0) {
                if ("2".equals(query.getActivityQd()) || "3".equals(query.getActivityQd())
                        || "1".equals(query.getActivityQd())) {
                    String restype = "1004";
                    if ("3".equals(query.getActivityQd())) {
                        restype = "1005";
                    } else if ("1".equals(query.getActivityQd())) {
                        restype = "1000";
                    }
                    StringBuilder delete = merchantIdShip.delete(merchantIdShip.length() - 1, merchantIdShip.length());
                    MultiValueMap<String, Object> formParam = new LinkedMultiValueMap<>();
                    formParam.add("sm_seq", delete.toString());
                    formParam.add("province", provinceCode);
                    formParam.add("resnum", "2000");
                    formParam.add("onlycamp", "1");
                    formParam.add("restype", restype);
                    JSONObject jsonObjectModel = asyncLoadService.post(searchproductMerchantIdUrl, formParam, requestNo);
                    try {
                        if (!jsonObjectModel.isEmpty()) {
                            Integer ngroups = jsonObjectModel.getJSONObject("grouped")
                                    .getJSONObject("spu_seq_group").getInteger("matches");
                            acttypecount = ngroups;
                            JSONArray groups = jsonObjectModel.getJSONObject("grouped")
                                    .getJSONObject("spu_seq_group").getJSONArray("groups");
                            if (groups != null && groups.size() > 0) {
                                for (int i = 0; i < groups.size(); i++) {
                                    JSONArray docs = groups.getJSONObject(i).getJSONObject("doclist")
                                            .getJSONArray("docs");
                                    for (int j = 0; j < docs.size(); j++) {
                                        JSONObject jsonObject = docs.getJSONObject(j);
                                        pid.append(merchantIdKeys.get(jsonObject.getString("sku_seq")))
                                                .append(",");

                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("调用大数据促销service接口异常,url:" + searchproductMerchantIdUrl, e);
                    }
                }
            }
            CategoryVo vo = new CategoryVo();
            vo.setKindId(pid.toString());
            vo.setKindName("促销");
            vo.setCateType(1);
            vo.setFavoriteCount(acttypecount);
            vo.setType(0);
            actCates.add(vo);
        } else {
            if (merchantIds.size() > 0) {
                StringBuilder delete = merchantIdString.delete(merchantIdString.length() - 1,
                        merchantIdString.length());
                MultiValueMap<String, Object> formData2 = new LinkedMultiValueMap<String, Object>();
                JSONObject dataObject1 = new JSONObject();
                dataObject1.put("merchantIds", merchantIds);
                formData2.add("data", dataObject1.toJSONString());
                JSONObject activityResp = asyncLoadService.post(
                        couponcsMerchantIdUrl, formData2, requestNo);
                MultiValueMap<String, Object> formData3 = new LinkedMultiValueMap<String, Object>();
                JSONObject dataObject3 = new JSONObject();
                dataObject3.put("merchantId", delete);
                formData3.add("param", dataObject3.toJSONString());
                JSONObject activityResPromotion = asyncLoadService.post(
                        promotionMerchantIdUrl, formData3, requestNo);
                Calendar calendar = Calendar.getInstance();// 日历对象
                calendar.setTime(new Date());// 设置当前日期
                calendar.add(Calendar.DAY_OF_WEEK, -15);// 月份减一

                MultiValueMap<String, Object> formParam = new LinkedMultiValueMap<>();
                formParam.add("shopid", delete.toString());
                formParam.add("province", provinceCode);
                formParam.add("facet", "shopid");
                formParam.add("group_field", "store_id");
                formParam.add("resnum", "1000");
                formParam.add("first_on_dt", calendar.getTimeInMillis() / 1000 + "_" + System.currentTimeMillis() / 1000);
                JSONObject jsonObjectModel = asyncLoadService.post(searchproductMerchantIdUrl, formParam, requestNo);
                try {
                    if (!activityResp.isEmpty() && "200".equals(activityResp.getString("code"))) {
                        JSONArray jsonArray = activityResp.getJSONArray("data");
                        if (jsonArray.size() > 0) {
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Integer integer = jsonObject.getInteger("canReceiveMount");
                                if (integer > 0) {
                                    havecoupon = havecoupon + 1;
                                    mid.append(merchantIdKeys.get(jsonObject.getString("merchantId")))
                                            .append(",");
                                }
                            }
                        }
                    } else {
                        log.error("调用店铺有劵service接口错误,url : " + couponcsMerchantIdUrl + ",return text : "
                                + activityResp);
                    }
                } catch (Exception e) {
                    log.error("调用店铺有劵service接口Read timed out,url:" + couponcsMerchantIdUrl, e);
                }
                try {
                    if (!jsonObjectModel.isEmpty()) {
                        JSONArray jsonArray = jsonObjectModel.getJSONObject("facet_counts")
                                .getJSONObject("facet_fields").getJSONArray("store_id");
                        if (jsonArray != null && jsonArray.size() > 0) {
                            havenew = jsonArray.size() / 2;
                            for (int i = 0; i < jsonArray.size(); i++) {
                                if (i % 2 == 0) {
                                    xid.append(merchantIdKeys.get(jsonArray.getString(i))).append(",");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("调用店铺新品service接口Read timed out,url:" + searchproductMerchantIdUrl + "?shopid="
                            + delete, e);
                }
                try {
                    if (!activityResPromotion.isEmpty()
                            && "200".equals(activityResPromotion.getString("code"))) {
                        JSONArray jsonArray = activityResPromotion.getJSONArray("body");
                        if (jsonArray.size() > 0) {
                            haveactivity = jsonArray.size();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String merchantId = jsonObject.getString("merchantId");
                                sid.append(merchantIdKeys.get(merchantId)).append(",");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("调用店铺活动service接口Read timed out,url:" + promotionMerchantIdUrl, e);
                }

            }

            CategoryVo vo2 = new CategoryVo();
            vo2.setKindId(sid.toString());
            vo2.setKindName("促销");
            vo2.setCateType(3);
            vo2.setFavoriteCount(haveactivity);
            vo2.setType(0);
            actCates.add(vo2);

            CategoryVo vo1 = new CategoryVo();
            vo1.setKindId(xid.toString());
            vo1.setKindName("上新");
            vo1.setCateType(2);
            vo1.setFavoriteCount(havenew);
            vo1.setType(0);
            actCates.add(vo1);

            CategoryVo vo = new CategoryVo();
            vo.setKindId(mid.toString());
            vo.setKindName("有券");
            vo.setCateType(1);
            vo.setFavoriteCount(havecoupon);
            vo.setType(0);
            actCates.add(vo);


        }
        JSONObject catejson = new JSONObject();
        if (query.getType() == 1) { // 店铺类型
            catejson.put("cateCounts", catetypecount);
            catejson.put("cates", categorys);
            catejson.put("actcates", actCates);
        } else if (query.getType() == 0) {// 商品类型
            catejson.put("cateCounts", catetypecount);
            catejson.put("actCounts", acttypecount);
            catejson.put("cates", categorys);
            catejson.put("actcates", actCates);
        }
        return new Result(Code.RESULT_STATUS_SUCCESS, catejson, "商品或者店铺分类查询成功");
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "8000")
    })
    public Result queryAsyncLoadService(QueryVo query) {
        String requestNo = RequestNoGen.getNo();
        String memGuid = query.getMemGuid();
        // 没有设置页数默认第一页
        if (query.getOffset() == null || query.getOffset() < 0) {
            query.setOffset(0);
        }
        // 当为商品(自营商品+商城商品)时：设置默认10条,为店铺时：设置默认5条
        if (query.getType() == 1) {
            if (query.getLimit() == null) {
                query.setLimit(5);
            }
        } else {
            if (query.getLimit() == null) {
                query.setLimit(10);
            }
        }
        if (query.getLimit() <= 0 || query.getLimit() > 20) {
            query.setLimit(20);
        }
        // 没有设置有效的默认查询有效商品
        if (query.getActive() == null) {
            query.setActive(true);
        }
        // 新增是否跨境查询
        if (query.getIsCrossborder() == null || query.getIsCrossborder() == 1) {
            query.setIsCrossborder(1);
        } else {
            query.setIsCrossborder(0);
        }
        List<FavoriteEntity> favoriteList = null;
        String hget = redisCacheClient.hget(memGuid, "query_new" + JSONObject.toJSONString(query));
        if (StringUtils.isNotBlank(hget)) {
            favoriteList = JSONObject.parseArray(hget, FavoriteEntity.class);
        } else {
            //查询数据库收藏夹信息
            if (query.getType() == 0) {
                // type=3,自营和商城的商品一起查询
                query.setType(3);
                favoriteList = favoriteMapper.query(query);
                query.setType(0);
            } else if (query.getType() == 1) {
                query.setType(2);
                Integer offSet = query.getOffset();
                Integer limit = query.getLimit();
                // 查询只有新品的店铺
                if (query.getNewListShop() == 1) {
                    query.setOffset(null);
                    query.setLimit(null);
                }
                favoriteList = favoriteMapper.query(query);
                query.setType(1);
                if (query.getNewListShop() == 1) {
                    query.setOffset(offSet);
                    query.setLimit(limit);
                }
            } else if (query.getType() == 5) {// 自营商品
                query.setType(0);// 自营商品
                favoriteList = favoriteMapper.query(query);
                query.setType(5);
            } else if (query.getType() == 6) {// 商城商品
                query.setType(1);// 商城商品
                favoriteList = favoriteMapper.query(query);
                query.setType(6);
            }
            redisCacheClient.hset(memGuid, "query_new" + JSONObject.toJSONString(query),
                    JSONObject.toJSONString(favoriteList));
            redisCacheClient.expire(memGuid, redisTime);
        }
        List<ProductVo> list = new ArrayList<>();

        if (favoriteList == null || favoriteList.size() == 0) {
            return new Result(Code.RESULT_STATUS_SUCCESS, list, "收藏夹无收藏商品或店铺");
        }
        Map<String, FavoriteEntity> favoriteMap = new HashMap<>();
        List<String> list2 = new ArrayList<>(); // 卖场id信息
        JSONArray arrayOfMall = new JSONArray(); // 商城卖场id信息
        JSONArray a = new JSONArray(); // 行销活动组装品信息
        // 花呗免息
        JSONArray isFlowersJSONArray = new JSONArray();
        // 多倍积分
        StringBuilder isMultipleScoreBuilder = new StringBuilder();
        // 秒杀商品
        StringBuilder isSeckillStringBuilder = new StringBuilder();
        for (FavoriteEntity entity : favoriteList) {
            JSONObject skuListobj = new JSONObject();
            JSONObject huabeiParam = new JSONObject();
            huabeiParam.put("sell_id", entity.getFavoriteSku());
            huabeiParam.put("category_id", entity.getKindId());
            if (entity.getType() == 0) { // 自营商品
                isMultipleScoreBuilder.append(entity.getFavoriteSku()).append(",");
                huabeiParam.put("goods_type", 2);
                isFlowersJSONArray.add(huabeiParam);
                isSeckillStringBuilder.append(entity.getFavoriteSku()).append(",");
                arrayOfMall.add(entity.getFavoriteSku());
                skuListobj.put("skuId", entity.getFavoriteSku());
                // 行销活动自营商品添加spuId,规格品ID
                skuListobj.put("spuId", entity.getFavoriteSpu());
                skuListobj.put("merchantId", "-1");
                a.add(skuListobj);
            } else if (entity.getType() == 1) { // 商城商品
                huabeiParam.put("goods_type", 1);
                isFlowersJSONArray.add(huabeiParam);
                isSeckillStringBuilder.append(entity.getFavoriteSku()).append(",");
                arrayOfMall.add(entity.getFavoriteSku());
                skuListobj.put("skuId", entity.getFavoriteSku());
                skuListobj.put("merchantId", String.valueOf(entity.getMerchantId()));
                a.add(skuListobj);
            }
            favoriteMap.put(entity.getFavoriteSku(), entity);
            list2.add(entity.getFavoriteSku());
        }

        // 商品显示
        if (query.getType() != 1) {
            // 返回列表数据接口
            Map<Integer, ProductVo> listOfProduct = new HashMap<>();
            // 收藏夹商城商品,请求参数
            MultiValueMap<String, Object> formDataShoppingMall = new LinkedMultiValueMap<>();
            // 收藏夹行销商品,请求参数
            MultiValueMap<String, Object> dataActivity = new LinkedMultiValueMap<>();
            // 批量查询商城商品info
            JSONObject jsonMallReturn = new JSONObject();
            // 行销活动查询信息
            JSONObject activityParseObject = new JSONObject();
            // 花呗信息
            JSONObject isFlowersReturn = new JSONObject();
            Map<String, String> huaBeiReturnMap = new HashMap<>();
            // 秒杀信息
            JSONObject dataSeckillDrp = new JSONObject();
            Map<String, String> isSeckillReturnMap = new HashMap<>();
            // 多倍积分
            JSONObject multipleScoreparseObject = new JSONObject();
            JSONObject multipleScore = new JSONObject();

            NumberFormat nf = new DecimalFormat("###.##");
            //查询自营商城商品信息
            if (arrayOfMall.size() > 0) {
                StringBuilder itnoBuilder1 = new StringBuilder();
                for (Object object : arrayOfMall) {
                    itnoBuilder1.append(object.toString()).append(",");
                }
                itnoBuilder1.delete(itnoBuilder1.length() - 1, itnoBuilder1.length());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("skuSeqs", itnoBuilder1.toString());
                if ("3".equals(query.getActivityQd())) {
                    jsonObject.put("isWireless", 1);
                    jsonObject.put("isStatus", 1);
                } else if ("2".equals(query.getActivityQd())) {
                    jsonObject.put("isWireless", 1);
                } else {
                    jsonObject.put("isWireless", 0);
                }
                jsonObject.put("areaCode", JSONObject.parse(query.getAreaCode()));
                // 店铺列表
                formDataShoppingMall.add("token", "member");
                formDataShoppingMall.add("data", jsonObject.toJSONString());
                RequestNoGen.setNo(requestNo);
                jsonMallReturn = asyncLoadService.post(mallProductNew, formDataShoppingMall, requestNo);
            }
            //查询商品行销活动信息
            if (a.size() > 0) {
                // 调用行销service搜索页接口
                JSONObject dataObject = new JSONObject();
                JSONObject areaCode = JSONObject.parseObject(query.getAreaCode());
                String provinceCode = areaCode.getString("provinceCode"); // 省
                String cityCode = areaCode.getString("cityCode"); // 省
                String areaCode2 = areaCode.getString("areaCode"); // 省
                dataObject.put("skuList", a);
                dataObject.put("provinceCode", provinceCode);
                dataObject.put("cityCode", cityCode);
                dataObject.put("areaCode", areaCode2);
                dataObject.put("activityQd", query.getActivityQd());
                dataActivity.add("param", dataObject.toJSONString());
                activityParseObject = asyncLoadService.post(
                        searchPageNormalActivityUrl, dataActivity, requestNo);
            }
            //查询商品花呗信息
            if (isFlowersJSONArray.size() > 0) {
                // 调用商品花呗service接口
                MultiValueMap<String, Object> req = new LinkedMultiValueMap<>();
                req.add("data", isFlowersJSONArray.toJSONString());
                isFlowersReturn = asyncLoadService.post(ipayParseItemsUseHBUrl,
                        req, requestNo);
            }
            //查询自营多倍积分信息
            if (isMultipleScoreBuilder.length() > 0) {
                MultiValueMap<String, Object> req = new LinkedMultiValueMap<>();
                JSONObject reqJsonObj = new JSONObject();
                reqJsonObj.put("itnos", isMultipleScoreBuilder);
                req.add("data", reqJsonObj.toJSONString());
                multipleScoreparseObject = asyncLoadService.post(
                        integralBatchCheckSendIntegralUrl, req, requestNo);
            }
            //查询秒杀信息
            if (isSeckillStringBuilder.length() > 0) {
                MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
                JSONObject jo = new JSONObject();
                jo.put("goodList", isSeckillStringBuilder);
                JSONObject provincePgseq = JSONObject.parseObject(provincePgseqMap);
                JSONObject areaCode = JSONObject.parseObject(query.getAreaCode());
                String provinceCode = areaCode.getString("provinceCode"); // 省
                String bigArea = provincePgseq.getString(provinceCode);
                jo.put("pgSeq", bigArea);
                formData.add("data", jo.toString());
                dataSeckillDrp = asyncLoadService.post(javaSeckillDrp, formData, requestNo);

            }

            //查询自营商城商品信息
            if (arrayOfMall.size() > 0) {
                try {
                    if (!jsonMallReturn.isEmpty() && jsonMallReturn.getString("success") != null
                            && "1".equals(jsonMallReturn.getString("success"))) {
                        JSONArray mallarr = jsonMallReturn.getJSONArray("data");
                        if (mallarr != null && mallarr.size() > 0) {
                            for (int i = 0, len = mallarr.size(); i < len; i++) {
                                JSONObject item = mallarr.getJSONObject(i);
                                String skuSeq = item.getString("skuSeq");
                                ProductVo vo = new ProductVo();
                                vo.setId(favoriteMap.get(skuSeq).getId());
                                vo.setMember_guid((favoriteMap.get(skuSeq).getMemGuid()));
                                // 无线组需求 新增merchantId 字段
                                vo.setMerchantId(favoriteMap.get(skuSeq).getMerchantId().toString());
                                vo.setType(favoriteMap.get(skuSeq).getType());

                                BigDecimal price = item.getBigDecimal("price");
                                String priceStr = favoriteMap.get(skuSeq).getPrice();
                                if (StringUtils.isNotBlank(priceStr) && price != null) {
                                    BigDecimal favoritePrice = new BigDecimal(priceStr);
                                    vo.setFavoritePrice(favoritePrice);
                                    double x = favoritePrice.subtract(price).doubleValue();
                                    vo.setPriceDifference((int) x);
                                } else {
                                    vo.setFavoritePrice(BigDecimal.ZERO);
                                    vo.setPriceDifference(0);
                                }
                                vo.setFavoriteChannel(favoriteMap.get(skuSeq).getChannel().toUpperCase());
                                vo.setCreate_time(myFmt.format(favoriteMap.get(skuSeq).getCreateTime()));
                                // 是否是多规格品
                                vo.setIs_combine(item.getInteger("specType"));
                                //0是原生，1是组合，2是单品多件
                                if ("1".equals(item.getString("isSpecial"))) {
                                    vo.setTag("限时特惠");
                                } else if (StringUtils.isNotBlank(item.getString("skuType"))
                                        && "2".equals(item.getString("skuType"))) {
                                    vo.setIs_combine(1);
                                    vo.setTag("单品多件");
                                } else if (StringUtils.isNotBlank(item.getString("skuType"))
                                        && "1".equals(item.getString("skuType"))) {
                                    vo.setIs_combine(1);
                                    vo.setTag("组合商品");
                                } else {
                                    vo.setTag("");
                                }
                                vo.setIsSpecial(item.getString("isSpecial"));
                                vo.setSource(item.getString("source"));
                                vo.setIsMobilePrice(item.getString("isMobilePrice"));
                                vo.setName(item.getString("title"));
                                vo.setIt_pic(item.getString("pic"));
                                vo.setSell_no(skuSeq);
                                vo.setSource_url(itemProductDomainNew + "/" + vo.getSell_no());
                                vo.setAvl_qty(item.getInteger("qty"));
                                vo.setPrice(nf.format(price));
                                // 是否预售
                                vo.setIsPreSale(item.getInteger("isPreSale"));
                                // 是否团购
                                vo.setIsGroup(item.getInteger("isGrouping"));
                                // 是否上下架
                                if ("1".equals(item.getString("skuStatus"))) {
                                    vo.setOff(false);
                                } else {
                                    vo.setOff(true);
                                    //不在售货范围切价格为0时 显示收藏价 如果收藏价小于0 则显示暂不报价
                                    if (price != null && price.compareTo(BigDecimal.ZERO) == 0) {
                                        BigDecimal favoritePrice = vo.getFavoritePrice();
                                        if (favoritePrice.compareTo(BigDecimal.ZERO) > 0) {
                                            vo.setPrice(favoritePrice.toString());
                                            vo.setPriceDifference(0);
                                        } else {
                                            vo.setPrice("暂无报价");
                                            vo.setPriceDifference(0);
                                        }
                                    }
                                }
                                String status = item.getString("status");
                                if (StringUtils.isNotBlank(status)
                                        && ("1".equals(item.getString("source")) || "3".equals(item
                                        .getString("source")))) {
                                    vo.setStatus(status);
                                } else {
                                    vo.setStatus("");
                                }
                                vo.setCpSeq(item.getString("cpSeq"));
                                vo.setActivitys(new HashMap<String, Object>());
                                int indexOf = list2.indexOf(skuSeq);
                                listOfProduct.put(indexOf, vo);
                            }
                        }

                    } else {
                        log.error(" 查询自营商城商品信息接口错误！url : " + mallProductNew + " parameters error :"
                                + formDataShoppingMall + "," + " return text : " + jsonMallReturn);
                    }
                } catch (Exception e) {
                    log.error("批量查询自营商城商品info接口Read timed out,url:" + mallProductNew + " parameters error :"
                            + formDataShoppingMall, e);
                }
            }
            // 行销内容组合
            try {
                if (!activityParseObject.isEmpty() && activityParseObject.getString("code") != null
                        && "200".equals(activityParseObject.getString("code"))) {
                    JSONObject body = activityParseObject.getJSONObject("body");
                    JSONArray skuActivityList = body.getJSONArray("skuActivityList");
                    if (null != skuActivityList && skuActivityList.size() > 0) {
                        // 每个商品遍历且结果集取第一个活动
                        for (int i = 0; i < skuActivityList.size(); i++) {
                            JSONObject skuActivity = skuActivityList.getJSONObject(i);
                            String skuId = skuActivity.getString("skuId");
                            String searchShowStr = skuActivity.getString("searchShowStr");
                            JSONArray urlList = skuActivity.getJSONArray("urlList");
                            String showTjzpTag = skuActivity.getString("showTjzpTag");
                            JSONArray reduceTypeList = skuActivity.getJSONArray("reduceTypeList");
                            Map<String, Object> actMap = new HashMap<String, Object>();
                            actMap.put("activity_name",
                                    !searchShowStr.isEmpty() ? (searchShowStr.split(";")[0].equals("-1") ? ""
                                            : searchShowStr.split(";")[0]) : "");
                            actMap.put("activity_url", !urlList.isEmpty() ? urlList.get(0) : "");
                            // 满减>折扣>满赠>优惠>换购
                            actMap.put("reduceType", !reduceTypeList.isEmpty() ? reduceTypeList
                                    : new JSONArray());
                            // 赠
                            actMap.put("showTjzpTag", showTjzpTag);
                            int indexOf = list2.indexOf(skuId);
                            ProductVo productVo = listOfProduct.get(indexOf);
                            if (productVo != null) {
                                if (StringUtils.isBlank(productVo.getTag()) && "1".equals(showTjzpTag)) {
                                    productVo.setTag("赠品");
                                }
                                productVo.setActivitys(actMap);
                            }
                        }
                    }
                } else {
                    log.error("调用行销活动service接口错误！url : " + searchPageNormalActivityUrl
                            + " parameters error: " + dataActivity + "  return text : " + activityParseObject);
                }
            } catch (Exception e) {
                log.error("调用行销活动接口Read timed out,url:" + searchPageNormalActivityUrl + " parameters error: "
                        + dataActivity, e);
            }
            // 花呗内容组合
            try {
                if (!isFlowersReturn.isEmpty() && !isFlowersReturn.getJSONArray("data").isEmpty()) {
                    JSONArray jsonArray = isFlowersReturn.getJSONArray("data");
                    for (int i = 0 + jsonArray.size() / 2, len = jsonArray.size(); i < len; i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        if (o.getInteger("code") == 200 && o.getString("hb_percent").equals("100")) {
                            huaBeiReturnMap.put(o.getString("sell_id"), o.getString("hb_num") + "期免息");
                        }
                    }
                } else {
                    log.error("调用花呗内容组合service接口错误！url : " + ipayParseItemsUseHBUrl + " parameters error: "
                            + isFlowersJSONArray + "  return text : " + isFlowersReturn);
                }
            } catch (Exception e) {
                log.error("调用花呗内容接口Read timed out,url:" + ipayParseItemsUseHBUrl + " parameters error: "
                        + isFlowersJSONArray, e);
            }
            // 自营多倍积分
            try {
                if (!multipleScoreparseObject.isEmpty()
                        && multipleScoreparseObject.getJSONObject("data") != null
                        && multipleScoreparseObject.getJSONObject("data").size() > 0
                        && multipleScoreparseObject.getString("success").equals("1")) {
                    multipleScore = multipleScoreparseObject.getJSONObject("data");
                } else {
                    log.error("调用自营多倍积分service接口错误！url : " + integralBatchCheckSendIntegralUrl
                            + " parameters error: " + isMultipleScoreBuilder + "  return text : "
                            + multipleScoreparseObject);
                }
            } catch (Exception e) {
                log.error("调用自营多倍积分Read timed out,url:" + integralBatchCheckSendIntegralUrl
                        + " parameters error: " + isMultipleScoreBuilder, e);
            }
            // 秒杀内容组合
            try {
                if (!dataSeckillDrp.isEmpty() && "1".equals(dataSeckillDrp.getString("success"))
                        && dataSeckillDrp.getJSONObject("data") != null
                        && dataSeckillDrp.getJSONObject("data").getJSONArray("list").size() > 0) {
                    JSONArray jsonArray = dataSeckillDrp.getJSONObject("data").getJSONArray("list");
                    for (int i = 0, len = jsonArray.size(); i < len; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String skuSeq = jsonObject.getString("skuSeq");
                        String isSeckill = jsonObject.getString("isSeckill");
                        if ("1".equals(isSeckill)) {
                            Long skStDT = jsonObject.getLongValue("skStDT");
                            Long displayEndTime = jsonObject.getLongValue("displayEndTime");
                            Long nowTime = System.currentTimeMillis();
                            if (nowTime < skStDT || nowTime > displayEndTime) {
                                isSeckill = "0";
                            }
                        }
                        isSeckillReturnMap.put(skuSeq, isSeckill);
                    }
                } else {
                    log.error("调用秒杀活动service接口错误！url : " + javaSeckillDrp + " parameters error: "
                            + isSeckillStringBuilder + "  return text : " + dataSeckillDrp);
                }
            } catch (Exception e) {
                log.error("调用秒杀活动接口Read timed out,url:" + javaSeckillDrp + " parameters error: "
                        + isSeckillStringBuilder, e);
            }
            // 自营及商城商品
            List<ProductVo> listOfProducts = new ArrayList<ProductVo>();
            for (int i = 0; i < list2.size(); i++) {
                ProductVo good = listOfProduct.get(i);
                if (null != good) {
                    // 环球购打标
                    String icon = mStaticUrl + "/assets/images/my/member/icon_haiwaigou2_2x.png";
                    // 商城
                    String icon_business = mStaticUrl + "/assets/images/my/member/icon_business2_2x.png";
                    // 商家直送
                    String icon_directbusiness = mStaticUrl
                            + "/assets/images/my/member/icon_directbusiness2_2x.png";
                    // 自营
                    String icon_global = mStaticUrl + "/assets/images/my/member/icon_selfsupport2_2x.png";
                    // 1自营,2商城,3商家直送,4环球购
                    String source = good.getSource();
                    String type = "1";
                    if ("1".equals(source)) {
                        good.setRlink(icon_global);
                    } else if ("2".equals(source)) {
                        good.setRlink(icon_business);
                        type = "2";
                    } else if ("3".equals(source)) {
                        good.setRlink(icon_directbusiness);
                    } else if ("4".equals(source)) {
                        good.setRlink(icon);
                        type = "2";
                    }
                    String it_pic = good.getIt_pic();
                    it_pic = PicUtil.picTransform(it_pic, storeUrl, "120x120", type, PicRandomUtil.random(imgInsideUrl), true);
                    // 触屏图片
                    good.setTouch_pic(it_pic);
                    String sell_no = good.getSell_no();
                    // 自营商品多倍积分
                    if (0 == good.getType()) {
                        int score = multipleScore.getIntValue(sell_no);
                        if (score > 1) {
                            good.setName("【" + score + "倍积分】" + good.getName());
                        }
                    }
                    // 自定义商城商品status
                    if (1 == good.getType()) {
                        if (good.getOff()) {
                            good.setStatus("6");
                        } else if (good.getAvl_qty() > 0 && 1 == good.getIs_combine()) {
                            good.setStatus("2");
                        } else if (good.getAvl_qty() > 0 && 0 == good.getIs_combine()) {
                            good.setStatus("1");
                        } else if (good.getAvl_qty() <= 0) {
                            good.setStatus("3");
                        }
                    }
                    List<String> tags = new ArrayList<String>();
                    String isMobilePrice = good.getIsMobilePrice();
                    String isFlowers = huaBeiReturnMap.get(sell_no);
                    if (StringUtils.isNotBlank(isFlowers)) {
                        good.setIsFlowers("1");
                    } else {
                        good.setIsFlowers("0");
                    }
                    int avl_qty = good.getAvl_qty();
                    if ("1".equals(isMobilePrice) && avl_qty > 0) {
                        tags.add("手机专享价");
                    }
                    String isGroup = good.getIsGroup().toString();
                    if ("1".equals(isGroup) && avl_qty > 0) {
                        tags.add("团");
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        listOfProducts.add(good);
                        continue;
                    }
                    String isSeckill = isSeckillReturnMap.get(sell_no);
                    if ("1".equals(isSeckill) && avl_qty > 0) {
                        tags.add("秒");
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        listOfProducts.add(good);
                        continue;
                    }
                    String isSpecial = good.getIsSpecial();
                    if ("1".equals(isSpecial) && avl_qty > 0) {
                        tags.add("限时特惠");
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        listOfProducts.add(good);
                        continue;
                    }
                    Map<String, Object> activitys = good.getActivitys();
                    JSONArray reduceType = (JSONArray) activitys.get("reduceType");
                    String status = good.getStatus();
                    //预售类型没有行销活动标
                    if (reduceType != null && !"8".equals(status)) {
                        if (reduceType.contains("1")) {
                            tags.add("满减");
                        }
                        if (tags.size() >= 2) {
                            good.setTags(tags);
                            listOfProducts.add(good);
                            continue;
                        }
                        if (reduceType.contains("2") || reduceType.contains("6")) {
                            tags.add("折扣");
                        }
                        if (tags.size() >= 2) {
                            good.setTags(tags);
                            listOfProducts.add(good);
                            continue;
                        }
                        // 环球购商品屏蔽满赠打标
                        if (!"4".equals(source)) {
                            if (reduceType.contains("3") || reduceType.contains("7")) {
                                tags.add("满赠");
                            }
                        }
                        if (tags.size() >= 2) {
                            good.setTags(tags);
                            listOfProducts.add(good);
                            continue;
                        }
                        if (reduceType.contains("5") || reduceType.contains("8") || reduceType.contains("9")
                                || reduceType.contains("10")) {
                            tags.add("优惠");
                        }
                        if (tags.size() >= 2) {
                            good.setTags(tags);
                            listOfProducts.add(good);
                            continue;
                        }
                        if (reduceType.contains("11")) {
                            tags.add("换购");
                        }
                        if (tags.size() >= 2) {
                            good.setTags(tags);
                            listOfProducts.add(good);
                            continue;
                        }
                    }

                    // 花呗免息
                    if (StringUtils.isNotBlank(isFlowers)) {
                        tags.add(isFlowers);
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        if ("1".equals(good.getStatus()) || "2".equals(good.getStatus())
                                || "11".equals(good.getStatus()) || "12".equals(good.getStatus())
                                || "13".equals(good.getStatus()) || "14".equals(good.getStatus())) {
                            good.setStatus("15");
                        }
                        listOfProducts.add(good);
                        continue;
                    }
                    String showTjzpTag = (String) activitys.get("showTjzpTag");
                    if ("1".equals(showTjzpTag)) {
                        tags.add("赠");
                    }
                    good.setTags(tags);
                    listOfProducts.add(good);
                } else {
                    if ("2".equals(query.getActivityQd())) {
                        ProductVo productVo1 = new ProductVo();
                        String string = list2.get(i);
                        FavoriteEntity favoriteEntity = favoriteMap.get(string);
                        String favoriteSku = favoriteEntity.getFavoriteSku();
                        String price = favoriteEntity.getPrice();
                        productVo1.setId(favoriteMap.get(favoriteSku).getId());
                        productVo1.setSell_no(favoriteSku);
                        if (favoriteMap.get(favoriteSku).getMerchantId() != null) {
                            productVo1.setMerchantId(Integer.toString(favoriteMap.get(favoriteSku)
                                    .getMerchantId()));
                        }
                        if (StringUtils.isNotBlank(price)) {
                            productVo1.setFavoritePrice(new BigDecimal(price));
                        } else {
                            productVo1.setFavoritePrice(BigDecimal.ZERO);
                        }
                        listOfProducts.add(productVo1);
                    }
                }
            }
            return new Result(Code.RESULT_STATUS_SUCCESS, listOfProducts, "查询收藏夹商品请求成功");
        } else {
            // 店铺列表
            List<StoreVo> storeList = new ArrayList<StoreVo>();
            // 店铺基础信息
            Map<String, StoreVo> mapStore = new HashMap<String, StoreVo>();
            // 店铺新品数据
            Map<String, List<ProductVo>> mapProductVos = new HashMap<String, List<ProductVo>>();
            // 店铺积分数据
            Map<String, List<StoreGradeVo>> mapStoreGradeVos = new HashMap<String, List<StoreGradeVo>>();
            // 请求店铺新品，数据实例
            // MultiValueMap<String, Object> formDataShopNew = new LinkedMultiValueMap<String,
            // Object>();
            // 请求店铺基础信息，数据实例
            MultiValueMap<String, Object> formDataBasicInformation = new LinkedMultiValueMap<String, Object>();
            // 请求店铺积分信息，数据实例
            MultiValueMap<String, Object> formDataGrade = new LinkedMultiValueMap<String, Object>();
            // 返回店铺新品，数据实例
            JSONObject jsonObjectShopNew = new JSONObject();
            // 返回店铺基础信息，数据实例
            JSONObject jsonObjectBasicInformation = new JSONObject();
            // 返回店铺评分，数据实例
            JSONObject jsonObjectGrade = new JSONObject();
            StringBuilder merchantIds = new StringBuilder();
            for (String merchantId : list2) {
                merchantIds.append(merchantId).append(",");
            }
            merchantIds.delete(merchantIds.length() - 1, merchantIds.length());
            //批量查询店铺新品
            JSONObject dataObject = new JSONObject();
            dataObject.put("merchantIds", merchantIds.toString());
            Calendar calendar = Calendar.getInstance();// 日历对象
            calendar.setTime(new Date());// 设置当前日期
            calendar.add(Calendar.DAY_OF_WEEK, -15);// 月份减一
            String provinceCode = "CS000016";
            if (StringUtils.isNotBlank(query.getAreaCode())) {
                provinceCode = JSONObject.parseObject(query.getAreaCode()).getString("provinceCode");
            }
            MultiValueMap<String, Object> formParam = new LinkedMultiValueMap<>();
            formParam.add("shopid", merchantIds.toString());
            formParam.add("province", provinceCode);
            formParam.add("facet", "shopid");
            formParam.add("resnum", "10");
            formParam.add("group_field", "store_id");
            formParam.add("fl", "pic,sell_slogan,suggestion_price");
            formParam.add("first_on_dt", calendar.getTimeInMillis() / 1000 + "_" + System.currentTimeMillis() / 1000);
            jsonObjectShopNew = asyncLoadService.post(searchproductMerchantIdUrl, formParam, requestNo);

            //批量查询店铺基础信息
            formDataBasicInformation.add("merchantIds", merchantIds.toString());
            jsonObjectBasicInformation = asyncLoadService.post(mallStoreUrl,
                    formDataBasicInformation, requestNo);
            //批量查询店铺评分
            formDataGrade.add("params", dataObject.toJSONString());
            jsonObjectGrade = asyncLoadService.post(mallStoreGradeUrl,
                    formDataGrade, requestNo);
            try {
                if (!jsonObjectShopNew.isEmpty()) {
                    JSONArray groups = jsonObjectShopNew.getJSONObject("grouped").getJSONObject("store_id").getJSONArray("groups");
                    if (groups != null && groups.size() > 0) {
                        // 新品list-- 无线不需要新品列表-- 只需要新品总数(字段命名为 newProductsSize)
                        Set<String> skus = new HashSet<>();
                        for (int i = 0; i < groups.size(); i++) {
                            JSONObject group = groups.getJSONObject(i);
                            String merchantId = group.getString("groupValue");
                            JSONArray newGoods = group.getJSONObject("doclist").getJSONArray("docs");
                            List<ProductVo> ps = new ArrayList<>();
                            if (newGoods != null && newGoods.size() > 0) {
                                for (int j = 0; j < newGoods.size(); j++) {
                                    //只显示5个新品
                                    if (j >= 5) {
                                        break;
                                    }
                                    JSONObject newGood = newGoods.getJSONObject(j);
                                    ProductVo p = new ProductVo();
                                    String skuSeq = newGood.getString("sku_seq");
                                    skus.add(skuSeq);
                                    p.setIt_pic(newGood.getString("pic"));
                                    p.setSell_no(skuSeq);
                                    double cent = newGood.getDoubleValue(provinceCode + "_price");
                                    p.setPrice(cent / 100 + "");
                                    p.setName(newGood.getString("sell_slogan"));
                                    p.setSource_url(itemProductDomainNew + "/" + skuSeq);
                                    ps.add(p);
                                }
                            }
                            mapProductVos.put(merchantId, ps);
                        }
                        //取实时价格
                        if (skus.size() > 0) {
                            Map<String, JSONObject> allProducts = new HashMap<>();
                            Set<String> limit = new HashSet<>();
                            for (String sku : skus) {
                                limit.add(sku);
                                if (limit.size() == 20) {
                                    Map<String, JSONObject> products = soaProductService.getProducts(query.getAreaCode(), query.getActivityQd(), limit);
                                    allProducts.putAll(products);
                                    limit.clear();
                                }
                            }
                            if (limit.size() > 0) {
                                Map<String, JSONObject> products = soaProductService.getProducts(query.getAreaCode(), query.getActivityQd(), limit);
                                allProducts.putAll(products);
                            }
                            Collection<List<ProductVo>> values = mapProductVos.values();
                            for (List<ProductVo> productVoList : values) {
                                for (ProductVo productVo : productVoList) {
                                    JSONObject item = allProducts.get(productVo.getSell_no());
                                    if (item != null) {
                                        String price = item.getString("price");
                                        if (price.indexOf(".") > 0) {
                                            //正则表达
                                            price = price.replaceAll("0+?$", "");//去掉后面无用的零
                                            price = price.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
                                        }
                                        productVo.setPrice(price);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("查询店铺新品异常,url:" + searchproductMerchantIdUrl + "?shopid=" + merchantIds, e);
            }
            try {
                if (!jsonObjectBasicInformation.isEmpty()) {
                    if ("1".equals(jsonObjectBasicInformation.getString("flag"))) {
                        JSONArray datas = jsonObjectBasicInformation.getJSONArray("datas");
                        if (datas != null && datas.size() > 0) {
                            for (int i = 0; i < datas.size(); i++) {
                                JSONObject newGood = datas.getJSONObject(i);
                                StoreVo storeVo = new StoreVo();
                                storeVo.setStoreLogoUrl(newGood.getString("storeLogoUrl"));
                                storeVo.setStoreName(newGood.getString("storeName"));
                                // 多地多仓beta2环境处理
                                storeVo.setUrl(newGood.getString("url").replace("beta1", "beta2"));
                                if ("4".equals(newGood.getString("status"))) {
                                    storeVo.setPunished("1");
                                } else {
                                    storeVo.setPunished("0");
                                }
                                mapStore.put(newGood.getString("merchantId"), storeVo);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("查询店铺基础信息Read timed out,url:" + mallStoreUrl + ", parameters error: "
                        + formDataBasicInformation, e);
            }

            try {
                if (!jsonObjectGrade.isEmpty()) {
                    if ("200".equals(jsonObjectGrade.getString("code"))) {
                        JSONObject jsonObjectData = jsonObjectGrade.getJSONObject("data");
                        for (String merchantId : list2) {
                            JSONArray jsonArray = jsonObjectData.getJSONArray(merchantId);
                            List<StoreGradeVo> parseArray = JSONObject.parseArray(jsonArray.toJSONString(),
                                    StoreGradeVo.class);
                            mapStoreGradeVos.put(merchantId, parseArray);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("查询店铺评分信息Read timed out,url:" + mallStoreGradeUrl + ", parameters error: "
                        + formDataGrade, e);
            }

            for (String merchantId : list2) {
                StoreVo storeVo = mapStore.get(merchantId);
                if (storeVo != null) {
                    List<ProductVo> list3 = mapProductVos.get(merchantId);
                    List<StoreGradeVo> list5 = mapStoreGradeVos.get(merchantId);
                    if (null != list3) {
                        storeVo.setNewProducts(list3);
                        storeVo.setNewProductSize(list3.size());
                    } else {
                        List<ProductVo> list4 = new ArrayList<ProductVo>();
                        storeVo.setNewProducts(list4);
                        storeVo.setNewProductSize(list4.size());
                    }
                    if (null != list5) {
                        storeVo.setStoreGrades(list5);
                    }
                    storeVo.setMerchantId(merchantId);
                    storeVo.setId(favoriteMap.get(merchantId).getId());
                    storeList.add(storeVo);
                }
            }

            if (storeList.size() > 0) {
                return new Result(Code.RESULT_STATUS_SUCCESS, storeList, "查询收藏夹店铺请求成功!");
            } else {
                return new Result(Code.RESULT_STATUS_SUCCESS, storeList, "收藏夹无收藏商品或店铺");
            }
        }
    }

    /**
     * 查询收藏夹商品或店铺的ID列表
     */
    @Override
    @HystrixCommand
    public Result getIdList(QueryVo query) {
        String memGuid = query.getMemGuid();
        List<FavoriteIdList> favoriteList = null;
        JSONArray result = new JSONArray();
        String hget = redisCacheClient.hget(memGuid, "idList_new" + JSONObject.toJSONString(query));
        if (StringUtils.isNotBlank(hget)) {
            favoriteList = JSONObject.parseArray(hget, FavoriteIdList.class);
        } else {
            favoriteList = favoriteMapper.getIdList(query);
            redisCacheClient.hset(memGuid, "idList_new" + JSONObject.toJSONString(query),
                    JSONObject.toJSONString(favoriteList));
            redisCacheClient.expire(memGuid, redisTime);
        }
        if (favoriteList == null || favoriteList.size() == 0) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "收藏夹无收藏商品或店铺");
        }
        Map<String, List<String>> merchantIds = new HashMap<String, List<String>>();
        if (query.getType() == 1) {
            // 商城商品ID列表
            for (FavoriteIdList entity : favoriteList) {
                if (!merchantIds.keySet().contains(entity.getMerchantId().toString())) {
                    List<String> ids = new ArrayList<String>();
                    ids.add(entity.getFavoriteSku());
                    merchantIds.put(entity.getMerchantId().toString(), ids);
                } else {
                    List<String> list = merchantIds.get(entity.getMerchantId().toString());
                    list.add(entity.getFavoriteSku());
                    merchantIds.put(entity.getMerchantId().toString(), list);
                }
            }
            for (String key : merchantIds.keySet()) {
                List<String> list = merchantIds.get(key);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(key, list);
                result.add(jsonObject);
            }
            return new Result(Code.RESULT_STATUS_SUCCESS, result, "查询收藏夹商城商品ID请求成功!");
        } else {
            // 自营商品ID列表
            for (FavoriteIdList entity : favoriteList) {
                result.add(entity.getFavoriteSku());
            }
            return new Result(Code.RESULT_STATUS_SUCCESS, result, "查询收藏夹ID请求成功!");
        }
    }

    /**
     * 逻辑删除用户收藏夹中的商品或店铺，可批量删除 如果用户的收藏夹中不存在该商品或店铺，删除失败并提示调用者
     */
    @Override
    @HystrixCommand
    public Result delete(String memGuid, String favoriteId) {

        String[] favoriteIds = favoriteId.split(",");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("memGuid", memGuid);
        map.put("favoriteIds", favoriteIds);
        favoriteMapper.delete(map);
        return new Result(Code.RESULT_STATUS_SUCCESS, "删除收藏夹成功！");

    }

    @Override
    @HystrixCommand
    public Result deleteBySku(String memGuid, String sku) {
        // 执行删除redis缓存
        redisCacheClient.del(memGuid);
        int count = favoriteMapper.deleteBySku(memGuid, sku);
        // 商品则调用异步批量推送kafka
        if (count > 0) {
            sendDelKafkaMsg(memGuid, sku);
        }
        return new Result(Code.RESULT_STATUS_SUCCESS, "success");
    }

    private void sendDelKafkaMsg(String memGuid, String sku) {
        Map<String, Object> smseq = new HashMap<>();
        smseq.put("memGuid", memGuid);
        smseq.put("smSeq", sku);
        smseq.put("action", "delete");
        smseq.put("time", System.currentTimeMillis());
        kafKaService.pushFavoriteData(smseq);
    }


    @Override
    @HystrixCommand
    public Result deleteById(String memGuid, String favoriteId) {
        // 执行删除redis缓存
        redisCacheClient.del(memGuid);
        List<String> skus = kafKaService.getSmseqsByIds(favoriteId);
        String[] favoriteIds = favoriteId.split(",");
        Map<String, Object> map = new HashMap<>();
        map.put("memGuid", memGuid);
        map.put("favoriteIds", favoriteIds);
        int count = favoriteMapper.delete(map);
        // 商品则调用异步批量推送kafka
        if (count > 0) {
            for (String sku : skus) {
                sendDelKafkaMsg(memGuid, sku);
            }
        } else {
            return new Result(Code.RESULT_STATUS_EXCEPTION, "删除失败count=" + count);
        }
        return new Result(Code.RESULT_STATUS_SUCCESS, "删除收藏夹成功！");
    }

    @Override
    @HystrixCommand
    public Result deleteAllByMemGuid(String memGuid) {
        favoriteMapper.deleteAllByMemGuid(memGuid);
        return new Result(Code.RESULT_STATUS_SUCCESS, "删除收藏夹成功!");
    }

    /**
     * 按条件查询收藏夹中商品或店铺数量
     */
    @Override
    @HystrixCommand
    public Result count(QueryVo query) {
        Long count = 0L;
        String memGuid = query.getMemGuid();
        if (query.getIsCrossborder() == null || query.getIsCrossborder() == 1) {
            query.setIsCrossborder(1); // 设置默认 1为所有 0 非跨境商品
        }
        if (StringUtils.isNotBlank(memGuid)) {
            String hget = redisCacheClient.hget(memGuid, "count_new" + JSONObject.toJSONString(query));
            if (StringUtils.isNotBlank(hget)) {
                if ("null".equals(hget)) {
                    return new Result(Code.RESULT_STATUS_SUCCESS, count, "收藏夹查询成功!");
                } else {
                    count = Long.parseLong(hget);
                }
                return new Result(Code.RESULT_STATUS_SUCCESS, count, "收藏数查询成功");
            }
        }
        if (query.getType() == null) {
            count = favoriteMapper.count(query);
        } else if (query.getType() == 0) {
            query.setType(3);
            count = favoriteMapper.count(query);
            query.setType(0);
        } else if (query.getType() == 1) {
            query.setType(2);
            count = favoriteMapper.count(query);
            query.setType(1);
        } else if (query.getType() == 5) {// 自营商品
            query.setType(0);// 自营商品
            count = favoriteMapper.count(query);
            query.setType(5);
        } else if (query.getType() == 6) {// 商城商品
            query.setType(1);// 商城商品
            count = favoriteMapper.count(query);
            query.setType(6);
        }
        if (StringUtils.isNotBlank(memGuid)) {
            redisCacheClient.hset(memGuid, "count_new" + JSONObject.toJSONString(query), Long.toString(count));
            redisCacheClient.expire(memGuid, redisTime);
        }
        return new Result(Code.RESULT_STATUS_SUCCESS, count, "收藏数查询成功");
    }

    /**
     * 判断某用户，是否收藏商品（自营、商城），店铺（商城）
     */
    @Override
    public Result haveCollectGoodsOrShop(QueryVo query) {
        Long id = 0L;
        String memGuid = query.getMemGuid();
        Map<String, Object> data = new HashMap<String, Object>();
        String hget = redisCacheClient.hget(memGuid,
                "haveCollectGoodsOrShop_new" + JSONObject.toJSONString(query));
        if (StringUtils.isNotBlank(hget)) {
            if ("null".equals(hget)) {
                data.put("have", false);
                return new Result(Code.RESULT_STATUS_SUCCESS, data, "收藏夹查询成功!");
            } else {
                id = Long.parseLong(hget);
            }
        } else {
            id = favoriteMapper.haveCollectGoodsOrShop(query);
            if (null == id) {
                redisCacheClient.hset(memGuid, "haveCollectGoodsOrShop_new" + JSONObject.toJSONString(query),
                        "null");
            } else {
                redisCacheClient.hset(memGuid, "haveCollectGoodsOrShop_new" + JSONObject.toJSONString(query),
                        Long.toString(id));
            }
            redisCacheClient.expire(memGuid, redisTime);
        }
        if (id != null) {
            data.put("have", true);
            data.put("id", id);
        } else {
            data.put("have", false);
        }
        return new Result(Code.RESULT_STATUS_SUCCESS, data, "收藏夹查询成功!");

    }

    @Override
    @HystrixCommand
    public Result countBySkuIds(String skuIds) {
        List<FavoriteSumVO> flist = favoriteMapper.countBySkuIds(Arrays.asList(skuIds.split(",")));
        JSONArray resultArray = new JSONArray();
        for (FavoriteSumVO favoriteSumVO : flist) {
            JSONObject skuObj = new JSONObject();
            skuObj.put("skuId", favoriteSumVO.getSmSeq());
            skuObj.put("favoriteNum", favoriteSumVO.getTotalNum());
            resultArray.add(skuObj);
        }
        return new Result(Code.RESULT_STATUS_SUCCESS, resultArray, "商城商品收藏数查询成功");

    }

    @Override
    public void upMerchanId() {
        List<FavoriteEntity> findMerchanId = favoriteMapper.findMerchanId();
        System.out.println(findMerchanId.size());
        for (FavoriteEntity favoriteEntity : findMerchanId) {
            String favoriteSeq = favoriteEntity.getFavoriteSeq();
            String mallInfo = restTemplate.getForObject(mallProMerchantId + favoriteSeq + ".html",
                    String.class);
            JSONObject mallInfoObj = JSONObject.parseObject(mallInfo);
            if (mallInfoObj == null || "-100".equals(mallInfoObj.getString("merchantId"))) {
                log.error("用商城商品信息接口返回请求体数据异常,找不到该商品商家id！ url : " + mallProMerchantId + "return text : "
                        + mallInfo);
            } else {
                // 获取商家ID
                Integer merchantIdTemp = mallInfoObj.getInteger("merchantId");
                int merchantId = merchantIdTemp;
                System.out.println(favoriteEntity.getMemGuid());
                System.out.println(favoriteEntity.getId());
                redisCacheClient.del(favoriteEntity.getMemGuid());
                favoriteMapper.upMerchanId(merchantId, favoriteEntity.getId());
            }
        }
    }

    @Override
    @HystrixCommand
    public List<String> getGoodIdsByMemGuid(String memGuid, Integer pageSize, Integer pageIndex) {
        Integer pageAll = (pageIndex - 1) * pageSize;
        return favoriteMapper.getGoodIdsByMemGuid(memGuid, pageSize, pageAll);
    }

    @Override
    @HystrixCommand
    public Result getFavoriteByIdsAndKindsIds(String memGuid, String ids, String kindId, Integer pageSize,
                                              Integer pageIndex, String provinceCode, String activityQd) {
        String requestNo = RequestNoGen.getNo();
        List<FavoriteEntity> favoriteList;
        String hget = redisCacheClient.hget(memGuid, "getFavoriteByIdsAndKindsIds_new:" + "ids:" + ids
                + ",kindId:" + kindId + ",pageSize:" + pageSize + ",pageIndex" + pageIndex);
        if (StringUtils.isNotBlank(hget)) {
            favoriteList = JSONObject.parseArray(hget, FavoriteEntity.class);
        } else {
            favoriteList = favoriteMapper.getFavoriteByIdsAndKindsIds(memGuid, kindId);
            redisCacheClient.hset(memGuid, "getFavoriteByIdsAndKindsIds_new:" + "ids:" + ids + ",kindId:"
                            + kindId + ",pageSize:" + pageSize + ",pageIndex" + pageIndex,
                    JSONObject.toJSONString(favoriteList));
            redisCacheClient.expire(memGuid, redisTime);
        }
        List<ProductVo> list = new ArrayList<>();
        if (favoriteList == null || favoriteList.size() == 0) {
            JSONObject js = new JSONObject();
            js.put("list", list);
            js.put("count", 0);
            return new Result(Code.RESULT_STATUS_SUCCESS, js, "收藏夹无收藏商品或店铺");
        } else {
            Set<String> setIds = new HashSet<>();
            List<FavoriteEntity> hitIds = new ArrayList<>();
            String[] split = ids.split(",");
            for (String string : split) {
                setIds.add(string);
            }
            for (FavoriteEntity string : favoriteList) {
                if (setIds.contains(Long.toString(string.getId()))) {
                    hitIds.add(string);
                }
            }
            if (hitIds.size() == 0) {
                JSONObject js = new JSONObject();
                js.put("list", list);
                js.put("count", 0);
                return new Result(Code.RESULT_STATUS_SUCCESS, js, "收藏夹无收藏商品或店铺");
            }
            List<FavoriteEntity> subList;
            if (pageIndex * pageSize > hitIds.size()) {
                subList = hitIds.subList((pageIndex - 1) * pageSize, hitIds.size());
            } else {
                subList = hitIds.subList((pageIndex - 1) * pageSize, pageIndex * pageSize);
            }
            Result resultList = getFavoriteBySkuid(subList, provinceCode, activityQd, requestNo);
            JSONObject result = new JSONObject();
            result.put("list", resultList.getData());
            result.put("count", hitIds.size());
            return new Result(Code.RESULT_STATUS_SUCCESS, result, "success");

        }
    }

    @Override
    @HystrixCommand
    public Result getFavoritePriceBySkuIds(String memGuid, String skuIds, String provinceCode,
                                           String activityQd) {
        String requestNo = RequestNoGen.getNo();
        List<FavoriteEntity> favoriteList;
        String hget = redisCacheClient.hget(memGuid, "getFavoritePriceBySkuIds_new:" + skuIds);
        String[] split = skuIds.split(",");
        if (StringUtils.isNotBlank(hget)) {
            favoriteList = JSONObject.parseArray(hget, FavoriteEntity.class);
        } else {
            favoriteList = favoriteMapper.getFavoritePriceBySkuIds(memGuid, split);
            redisCacheClient.hset(memGuid, "getFavoritePriceBySkuIds_new:" + skuIds,
                    JSONObject.toJSONString(favoriteList));
            redisCacheClient.expire(memGuid, redisTime);
        }
        return getFavoriteBySkuid(favoriteList, provinceCode, activityQd, requestNo);
    }

    @Override
    public void upKindName() {
        List<FavoriteEntity> findMerchanId = favoriteMapper.findKindName();
        System.out.println(findMerchanId.size());
        for (FavoriteEntity favoriteEntity : findMerchanId) {
            String kindId = favoriteEntity.getKindId();
            String kindName = favoriteEntity.getKindName();
            MultiValueMap<String, Object> formData2 = new LinkedMultiValueMap<String, Object>();
            JSONObject dataObject1 = new JSONObject();
            dataObject1.put("gcSeq", kindId);
            formData2.add("data", dataObject1.toJSONString());
            // 接口返回数据
            String respHotString;
            JSONObject activityResp;
            respHotString = restTemplate.postForObject(apiCategoryGetAppNameByGcSeq, formData2, String.class);
            activityResp = JSONObject.parseObject(respHotString);
            if (activityResp == null
                    || StringUtils.isBlank(activityResp.getJSONObject("data").getJSONObject("result")
                    .getString("appName"))) {
                log.error("用商品kindId查询信息接口返回请求体数据异常,找不到该商品kindName！ url : " + apiCategoryGetAppNameByGcSeq
                        + "?gcSeq=" + kindId + " ,return text : " + respHotString);
            } else {
                // 获取商品kindName
                String appName = activityResp.getJSONObject("data").getJSONObject("result")
                        .getString("appName");
                String validateAppName = validate(appName);
                if (validateAppName.equals(kindName)) {
                    continue;
                }
                favoriteMapper.upKindName(validateAppName, kindId);
            }
        }
    }

    /**
     * 查询商城店铺新品信息
     */
    private Result getFavoriteBySkuid(List<FavoriteEntity> favoriteList, String areaCode, String activityQd,
                                      String requestNo) {
        Map<String, FavoriteEntity> favoriteMap = new HashMap<>();
        List<String> list2 = new ArrayList<>(); // 卖场id信息
        JSONArray arrayOfMall = new JSONArray(); // 商城卖场id信息
        JSONArray a = new JSONArray(); // 行销活动组装品信息
        // 花呗免息
        JSONArray isFlowersJSONArray = new JSONArray();
        // 多倍积分
        StringBuilder isMultipleScoreBuilder = new StringBuilder();
        // 秒杀商品
        StringBuilder isSeckillStringBuilder = new StringBuilder();
        for (FavoriteEntity entity : favoriteList) {
            JSONObject skuListobj = new JSONObject();
            JSONObject huabeiParam = new JSONObject();
            huabeiParam.put("sell_id", entity.getFavoriteSku());
            huabeiParam.put("category_id", entity.getKindId());
            if (entity.getType() == 0) {
                // 自营商品
                isMultipleScoreBuilder.append(entity.getFavoriteSku()).append(",");
                huabeiParam.put("goods_type", 2);
                isFlowersJSONArray.add(huabeiParam);
                isSeckillStringBuilder.append(entity.getFavoriteSku()).append(",");
                arrayOfMall.add(entity.getFavoriteSku());
                skuListobj.put("skuId", entity.getFavoriteSku());
                // 行销活动自营商品添加spuId,规格品ID
                skuListobj.put("spuId", entity.getFavoriteSpu());
                skuListobj.put("merchantId", "-1");
                a.add(skuListobj);
            } else if (entity.getType() == 1) {
                // 商城商品
                huabeiParam.put("goods_type", 1);
                isFlowersJSONArray.add(huabeiParam);
                isSeckillStringBuilder.append(entity.getFavoriteSku()).append(",");
                arrayOfMall.add(entity.getFavoriteSku());
                skuListobj.put("skuId", entity.getFavoriteSku());
                skuListobj.put("merchantId", String.valueOf(entity.getMerchantId()));
                a.add(skuListobj);
            }
            favoriteMap.put(entity.getFavoriteSku(), entity);
            list2.add(entity.getFavoriteSku());
        }

        // 返回列表数据接口
        Map<Integer, ProductVo> listOfProduct = new HashMap<>();
        // 收藏夹商城商品,请求参数
        MultiValueMap<String, Object> formDataShoppingMall = new LinkedMultiValueMap<>();
        // 收藏夹行销商品,请求参数
        MultiValueMap<String, Object> dataActivity = new LinkedMultiValueMap<>();
        // 批量查询商城商品info
        JSONObject jsonMallReturn = new JSONObject();
        // 行销活动查询信息
        JSONObject activityParseObject = new JSONObject();
        // 花呗信息
        JSONObject isFlowersReturn = new JSONObject();
        Map<String, String> huaBeiReturnMap = new HashMap<>();
        // 秒杀信息
        JSONObject dataSeckillDrp = new JSONObject();
        Map<String, String> isSeckillReturnMap = new HashMap<>();
        // 多倍积分
        JSONObject multipleScoreparseObject = new JSONObject();
        JSONObject multipleScore = new JSONObject();

        NumberFormat nf = new DecimalFormat("###.##");
        //查询自营商城商品信息
        if (arrayOfMall.size() > 0) {
            StringBuilder itnoBuilder1 = new StringBuilder();
            for (Object object : arrayOfMall) {
                itnoBuilder1.append(object.toString()).append(",");
            }
            itnoBuilder1.delete(itnoBuilder1.length() - 1, itnoBuilder1.length());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("skuSeqs", itnoBuilder1.toString());
            if ("3".equals(activityQd)) {
                jsonObject.put("isWireless", 1);
                jsonObject.put("isStatus", 1);
            } else if ("2".equals(activityQd)) {
                jsonObject.put("isWireless", 1);
            } else {
                jsonObject.put("isWireless", 0);
            }
            jsonObject.put("areaCode", areaCode);
            // 店铺列表
            formDataShoppingMall.add("token", "member");
            formDataShoppingMall.add("data", jsonObject.toJSONString());
            jsonMallReturn = asyncLoadService.post(mallProductNew,
                    formDataShoppingMall, requestNo);
        }
        //查询商品行销活动信息
        if (a.size() > 0) {
            // 调用行销service搜索页接口
            JSONObject dataObject = new JSONObject();
            JSONObject areaCode1 = JSONObject.parseObject(areaCode);
            String provinceCode = areaCode1.getString("provinceCode"); // 省
            String cityCode = areaCode1.getString("cityCode"); // 省
            String areaCode2 = areaCode1.getString("areaCode"); // 省
            dataObject.put("skuList", a);
            dataObject.put("provinceCode", provinceCode);
            dataObject.put("cityCode", cityCode);
            dataObject.put("areaCode", areaCode2);
            dataObject.put("activityQd", activityQd);
            dataActivity.add("param", dataObject.toJSONString());
            activityParseObject = asyncLoadService.post(
                    searchPageNormalActivityUrl, dataActivity, requestNo);
        }
        //查询商品花呗信息
        if (isFlowersJSONArray.size() > 0) {
            // 调用商品花呗service接口
            MultiValueMap<String, Object> req = new LinkedMultiValueMap<String, Object>();
            req.add("data", isFlowersJSONArray.toJSONString());
            isFlowersReturn = asyncLoadService.post(ipayParseItemsUseHBUrl, req,
                    requestNo);
        }
        // 查询自营多倍积分信息
        if (isMultipleScoreBuilder.length() > 0) {
            MultiValueMap<String, Object> req = new LinkedMultiValueMap<String, Object>();
            JSONObject reqJsonObj = new JSONObject();
            reqJsonObj.put("itnos", isMultipleScoreBuilder);
            req.add("data", reqJsonObj.toJSONString());
            multipleScoreparseObject = asyncLoadService.post(
                    integralBatchCheckSendIntegralUrl, req, requestNo);
        }
        // 查询秒杀信息
        if (isSeckillStringBuilder.length() > 0) {
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
            JSONObject jo = new JSONObject();
            jo.put("goodList", isSeckillStringBuilder);
            JSONObject provincePgseq = JSONObject.parseObject(provincePgseqMap);
            JSONObject areaCode1 = JSONObject.parseObject(areaCode);
            String provinceCode = areaCode1.getString("provinceCode"); // 省
            String bigArea = provincePgseq.getString(provinceCode);
            jo.put("pgSeq", bigArea);
            formData.add("data", jo.toString());
            dataSeckillDrp = asyncLoadService.post(javaSeckillDrp, formData,
                    requestNo);
        }

        //查询自营商城商品信息
        if (arrayOfMall.size() > 0) {
            try {
                if (!jsonMallReturn.isEmpty() && jsonMallReturn.getString("success") != null
                        && "1".equals(jsonMallReturn.getString("success"))) {
                    JSONArray mallarr = jsonMallReturn.getJSONArray("data");
                    if (mallarr != null && mallarr.size() > 0) {
                        for (int i = 0, len = mallarr.size(); i < len; i++) {
                            JSONObject item = mallarr.getJSONObject(i);
                            String skuSeq = item.getString("skuSeq");
                            ProductVo vo = new ProductVo();
                            vo.setId(favoriteMap.get(skuSeq).getId());
                            vo.setMember_guid((favoriteMap.get(skuSeq).getMemGuid()));
                            // 无线组需求 新增merchantId 字段
                            vo.setMerchantId(favoriteMap.get(skuSeq).getMerchantId().toString());
                            vo.setType(favoriteMap.get(skuSeq).getType());
                            BigDecimal price = item.getBigDecimal("price");
                            String favoritePriceStr = favoriteMap.get(skuSeq).getPrice();
                            if (StringUtils.isNotBlank(favoritePriceStr) && price != null) {
                                BigDecimal favoritePrice = new BigDecimal(favoritePriceStr);
                                vo.setFavoritePrice(favoritePrice);
                                double x = favoritePrice.subtract(price).doubleValue();
                                vo.setPriceDifference((int) x);
                            } else {
                                vo.setFavoritePrice(BigDecimal.ZERO);
                                vo.setPriceDifference(0);
                            }
                            vo.setFavoriteChannel(favoriteMap.get(skuSeq).getChannel().toUpperCase());
                            vo.setCreate_time(myFmt.format(favoriteMap.get(skuSeq).getCreateTime()));
                            // 是否是多规格品
                            vo.setIs_combine(item.getInteger("specType"));
                            if ("1".equals(item.getString("isSpecial"))) {
                                vo.setTag("限时特惠");
                            } else if (StringUtils.isNotBlank(item.getString("skuType"))
                                    && "2".equals(item.getString("skuType"))) {
                                vo.setIs_combine(1);
                                vo.setTag("单品多件");
                            } else if (StringUtils.isNotBlank(item.getString("skuType"))
                                    && "1".equals(item.getString("skuType"))) {
                                vo.setIs_combine(1);
                                vo.setTag("组合商品");
                            } else {
                                vo.setTag("");
                            }
                            vo.setIsSpecial(item.getString("isSpecial"));
                            vo.setSource(item.getString("source"));
                            vo.setIsMobilePrice(item.getString("isMobilePrice"));
                            vo.setName(item.getString("title"));
                            vo.setIt_pic(item.getString("pic"));
                            vo.setSell_no(skuSeq);
                            vo.setSource_url(itemProductDomainNew + "/" + vo.getSell_no());
                            vo.setAvl_qty(item.getInteger("qty"));
                            vo.setPrice(nf.format(price));
                            // 是否预售
                            vo.setIsPreSale(item.getInteger("isPreSale"));
                            // 是否团购
                            vo.setIsGroup(item.getInteger("isGrouping"));
                            // 是否上下架
                            if ("1".equals(item.getString("skuStatus"))) {
                                vo.setOff(false);
                            } else {
                                vo.setOff(true);
                                //不在售货范围切价格为0时 显示收藏价 如果收藏价小于0 则显示暂不报价
                                if (price != null && price.compareTo(BigDecimal.ZERO) == 0) {
                                    BigDecimal favoritePrice = vo.getFavoritePrice();
                                    if (favoritePrice.compareTo(BigDecimal.ZERO) > 0) {
                                        vo.setPrice(favoritePrice.toString());
                                        vo.setPriceDifference(0);
                                    } else {
                                        vo.setPrice("暂无报价");
                                        vo.setPriceDifference(0);
                                    }
                                }
                            }
                            String status = item.getString("status");
                            if (StringUtils.isNotBlank(status)
                                    && ("1".equals(item.getString("source")) || "3".equals(item
                                    .getString("source")))) {
                                vo.setStatus(status);
                            } else {
                                vo.setStatus("");
                            }
                            vo.setCpSeq(item.getString("cpSeq"));
                            vo.setActivitys(new HashMap<String, Object>());
                            int indexOf = list2.indexOf(skuSeq);
                            listOfProduct.put(indexOf, vo);
                        }
                    }

                } else {
                    log.error(" 查询自营商城商品信息接口错误！url : " + mallProductNew + " parameters error :"
                            + formDataShoppingMall + "," + " return text : " + jsonMallReturn);
                }
            } catch (Exception e) {
                log.error("批量查询自营商城商品info接口Read timed out,url:" + mallProductNew + " parameters error :"
                        + formDataShoppingMall, e);
            }
        }
        // 行销内容组合
        try {
            if (!activityParseObject.isEmpty() && activityParseObject.getString("code") != null
                    && "200".equals(activityParseObject.getString("code"))) {
                JSONObject body = activityParseObject.getJSONObject("body");
                JSONArray skuActivityList = body.getJSONArray("skuActivityList");
                if (null != skuActivityList && skuActivityList.size() > 0) {
                    // 每个商品遍历且结果集取第一个活动
                    for (int i = 0; i < skuActivityList.size(); i++) {
                        JSONObject skuActivity = skuActivityList.getJSONObject(i);
                        String skuId = skuActivity.getString("skuId");
                        String searchShowStr = skuActivity.getString("searchShowStr");
                        JSONArray urlList = skuActivity.getJSONArray("urlList");
                        String showTjzpTag = skuActivity.getString("showTjzpTag");
                        JSONArray reduceTypeList = skuActivity.getJSONArray("reduceTypeList");
                        Map<String, Object> actMap = new HashMap<String, Object>();
                        actMap.put("activity_name",
                                !searchShowStr.isEmpty() ? (searchShowStr.split(";")[0].equals("-1") ? ""
                                        : searchShowStr.split(";")[0]) : "");
                        actMap.put("activity_url", !urlList.isEmpty() ? urlList.get(0) : "");
                        // 满减>折扣>满赠>优惠>换购
                        actMap.put("reduceType", !reduceTypeList.isEmpty() ? reduceTypeList : new JSONArray());
                        // 赠
                        actMap.put("showTjzpTag", showTjzpTag);
                        int indexOf = list2.indexOf(skuId);
                        ProductVo productVo = listOfProduct.get(indexOf);
                        if (productVo != null) {
                            if (StringUtils.isBlank(productVo.getTag()) && "1".equals(showTjzpTag)) {
                                productVo.setTag("赠品");
                            }
                            productVo.setActivitys(actMap);
                        }
                    }
                }
            } else {
                log.error("调用行销活动service接口错误！url : " + searchPageNormalActivityUrl + " parameters error: "
                        + dataActivity + "  return text : " + activityParseObject);
            }
        } catch (Exception e) {
            log.error("调用行销活动接口Read timed out,url:" + searchPageNormalActivityUrl + " parameters error: "
                    + dataActivity, e);
        }
        // 花呗内容组合
        try {
            if (!isFlowersReturn.isEmpty() && !isFlowersReturn.getJSONArray("data").isEmpty()) {
                JSONArray jsonArray = isFlowersReturn.getJSONArray("data");
                for (int i = 0 + jsonArray.size() / 2, len = jsonArray.size(); i < len; i++) {
                    JSONObject o = jsonArray.getJSONObject(i);
                    if (o.getInteger("code") == 200 && o.getString("hb_percent").equals("100")) {
                        huaBeiReturnMap.put(o.getString("sell_id"), o.getString("hb_num") + "期免息");
                    }
                }
            } else {
                log.error("调用花呗内容组合service接口错误！url : " + ipayParseItemsUseHBUrl + " parameters error: "
                        + isFlowersJSONArray + "  return text : " + isFlowersReturn);
            }
        } catch (Exception e) {
            log.error("调用花呗内容接口Read timed out,url:" + ipayParseItemsUseHBUrl + " parameters error: "
                    + isFlowersJSONArray, e);
        }
        // 自营多倍积分
        try {
            if (!multipleScoreparseObject.isEmpty() && multipleScoreparseObject.getJSONObject("data") != null
                    && multipleScoreparseObject.getJSONObject("data").size() > 0
                    && multipleScoreparseObject.getString("success").equals("1")) {
                multipleScore = multipleScoreparseObject.getJSONObject("data");
            } else {
                log.error("调用自营多倍积分service接口错误！url : " + integralBatchCheckSendIntegralUrl
                        + " parameters error: " + isMultipleScoreBuilder + "  return text : "
                        + multipleScoreparseObject);
            }
        } catch (Exception e) {
            log.error("调用自营多倍积分Read timed out,url:" + integralBatchCheckSendIntegralUrl
                    + " parameters error: " + isMultipleScoreBuilder, e);
        }
        // 秒杀内容组合
        try {
            if (!dataSeckillDrp.isEmpty() && "1".equals(dataSeckillDrp.getString("success"))
                    && dataSeckillDrp.getJSONObject("data") != null
                    && dataSeckillDrp.getJSONObject("data").getJSONArray("list").size() > 0) {
                JSONArray jsonArray = dataSeckillDrp.getJSONObject("data").getJSONArray("list");
                for (int i = 0, len = jsonArray.size(); i < len; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String skuSeq = jsonObject.getString("skuSeq");
                    String isSeckill = jsonObject.getString("isSeckill");
                    if ("1".equals(isSeckill)) {
                        Long skStDT = jsonObject.getLongValue("skStDT");
                        Long displayEndTime = jsonObject.getLongValue("displayEndTime");
                        Long nowTime = System.currentTimeMillis();
                        if (nowTime < skStDT || nowTime > displayEndTime) {
                            isSeckill = "0";
                        }
                    }
                    isSeckillReturnMap.put(skuSeq, isSeckill);
                }
            } else {
                log.error("调用秒杀活动service接口错误！url : " + javaSeckillDrp + " parameters error: "
                        + isSeckillStringBuilder + "  return text : " + dataSeckillDrp);
            }
        } catch (Exception e) {
            log.error("调用秒杀活动接口Read timed out,url:" + javaSeckillDrp + " parameters error: "
                    + isSeckillStringBuilder, e);
        }
        // 自营及商城商品
        List<ProductVo> listOfProducts = new ArrayList<ProductVo>();
        for (int i = 0; i < list2.size(); i++) {
            ProductVo good = listOfProduct.get(i);
            if (null != good) {
                // 环球购打标
                String icon = mStaticUrl + "/assets/images/my/member/icon_haiwaigou2_2x.png";
                // 商城
                String icon_business = mStaticUrl + "/assets/images/my/member/icon_business2_2x.png";
                // 商家直送
                String icon_directbusiness = mStaticUrl
                        + "/assets/images/my/member/icon_directbusiness2_2x.png";
                // 自营
                String icon_global = mStaticUrl + "/assets/images/my/member/icon_selfsupport2_2x.png";
                // 1自营,2商城,3商家直送,4环球购
                String source = good.getSource();
                String type = "1";
                if ("1".equals(source)) {
                    good.setRlink(icon_global);
                } else if ("2".equals(source)) {
                    good.setRlink(icon_business);
                    type = "2";
                } else if ("3".equals(source)) {
                    good.setRlink(icon_directbusiness);
                } else if ("4".equals(source)) {
                    good.setRlink(icon);
                    type = "2";
                }
                String it_pic = good.getIt_pic();
                it_pic = PicUtil.picTransform(it_pic, storeUrl, "120x120", type, PicRandomUtil.random(imgInsideUrl), true);
                // 触屏图片
                good.setTouch_pic(it_pic);
                String sell_no = good.getSell_no();
                // 自营商品多倍积分
                if (0 == good.getType()) {
                    int score = multipleScore.getIntValue(sell_no);
                    if (score > 1) {
                        good.setName("【" + score + "倍积分】" + good.getName());
                    }
                }
                // 自定义商城商品status
                if (1 == good.getType()) {
                    if (good.getOff()) {
                        good.setStatus("6");
                    } else if (good.getAvl_qty() > 0 && 1 == good.getIs_combine()) {
                        good.setStatus("2");
                    } else if (good.getAvl_qty() > 0 && 0 == good.getIs_combine()) {
                        good.setStatus("1");
                    } else if (good.getAvl_qty() <= 0) {
                        good.setStatus("3");
                    }
                }
                List<String> tags = new ArrayList<String>();
                String isMobilePrice = good.getIsMobilePrice();
                String isFlowers = huaBeiReturnMap.get(sell_no);
                if (StringUtils.isNotBlank(isFlowers)) {
                    good.setIsFlowers("1");
                } else {
                    good.setIsFlowers("0");
                }
                int avl_qty = good.getAvl_qty();
                if ("1".equals(isMobilePrice) && avl_qty > 0) {
                    tags.add("手机专享价");
                }
                String isGroup = good.getIsGroup().toString();
                if ("1".equals(isGroup) && avl_qty > 0) {
                    tags.add("团");
                }
                if (tags.size() >= 2) {
                    good.setTags(tags);
                    listOfProducts.add(good);
                    continue;
                }
                String isSeckill = isSeckillReturnMap.get(sell_no);
                if ("1".equals(isSeckill) && avl_qty > 0) {
                    tags.add("秒");
                }
                if (tags.size() >= 2) {
                    good.setTags(tags);
                    listOfProducts.add(good);
                    continue;
                }
                String isSpecial = good.getIsSpecial();
                if ("1".equals(isSpecial) && avl_qty > 0) {
                    tags.add("限时特惠");
                }
                if (tags.size() >= 2) {
                    good.setTags(tags);
                    listOfProducts.add(good);
                    continue;
                }
                Map<String, Object> activitys = good.getActivitys();
                JSONArray reduceType = (JSONArray) activitys.get("reduceType");
                String status = good.getStatus();
                if (reduceType != null && !"8".equals(status)) {
                    if (reduceType.contains("1")) {
                        tags.add("满减");
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        listOfProducts.add(good);
                        continue;
                    }
                    if (reduceType.contains("2") || reduceType.contains("6")) {
                        tags.add("折扣");
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        listOfProducts.add(good);
                        continue;
                    }
                    // 环球购商品屏蔽满赠打标
                    if (!"4".equals(source)) {
                        if (reduceType.contains("3") || reduceType.contains("7")) {
                            tags.add("满赠");
                        }
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        listOfProducts.add(good);
                        continue;
                    }
                    if (reduceType.contains("5") || reduceType.contains("8") || reduceType.contains("9")
                            || reduceType.contains("10")) {
                        tags.add("优惠");
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        listOfProducts.add(good);
                        continue;
                    }
                    if (reduceType.contains("11")) {
                        tags.add("换购");
                    }
                    if (tags.size() >= 2) {
                        good.setTags(tags);
                        listOfProducts.add(good);
                        continue;
                    }
                }

                // 花呗免息
                if (StringUtils.isNotBlank(isFlowers)) {
                    tags.add(isFlowers);
                }
                if (tags.size() >= 2) {
                    good.setTags(tags);
                    if ("1".equals(good.getStatus()) || "2".equals(good.getStatus())
                            || "11".equals(good.getStatus()) || "12".equals(good.getStatus())
                            || "13".equals(good.getStatus()) || "14".equals(good.getStatus())) {
                        good.setStatus("15");
                    }
                    listOfProducts.add(good);
                    continue;
                }
                String showTjzpTag = (String) activitys.get("showTjzpTag");
                if ("1".equals(showTjzpTag)) {
                    tags.add("增");
                }
                good.setTags(tags);
                listOfProducts.add(good);
            } else {
                if ("2".equals(activityQd)) {
                    ProductVo productVo1 = new ProductVo();
                    String string = list2.get(i);
                    FavoriteEntity favoriteEntity = favoriteMap.get(string);
                    String favoriteSku = favoriteEntity.getFavoriteSku();
                    String price = favoriteEntity.getPrice();
                    productVo1.setId(favoriteMap.get(favoriteSku).getId());
                    productVo1.setSell_no(favoriteSku);
                    if (favoriteMap.get(favoriteSku).getMerchantId() != null) {
                        productVo1.setMerchantId(Integer.toString(favoriteMap.get(favoriteSku)
                                .getMerchantId()));
                    }
                    if (StringUtils.isNotBlank(price)) {
                        productVo1.setFavoritePrice(new BigDecimal(price));
                    } else {
                        productVo1.setFavoritePrice(BigDecimal.ZERO);
                    }
                    listOfProducts.add(productVo1);
                }
            }
        }
        return new Result(Code.RESULT_STATUS_SUCCESS, listOfProducts, "查询收藏夹商品请求成功");

    }

    /**
     * 根据商城商品favoriteSeq查询店铺merchant_id
     */
    private Result getMerchantIdByFavoriteSeq(String favoriteSeq) {
        String mallInfo;
        JSONObject mallInfoObj;
        try {
            mallInfo = restTemplate.getForObject(mallProMerchantId + favoriteSeq + ".html", String.class);
            mallInfoObj = JSONObject.parseObject(mallInfo);
        } catch (RestClientException e) {
            log.error("查询商城商品所属店铺id信息Read timed out,url:" + mallProMerchantId, e);
            return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, "查询商城商品所属店铺id信息Read timed out,url:"
                    + mallProMerchantId);
        }
        if (mallInfoObj == null || (-100 == mallInfoObj.getInteger("merchantId"))) {
            log.error("用商城商品所属店铺id接口信息返回数据异常! url : " + mallProMerchantId + "return text : " + mallInfo);
            return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, mallInfoObj,
                    "用商城商品所属店铺id接口信息返回数据异常!url:" + mallProMerchantId + ",return text : " + mallInfo);
        } else {
            // 获取商家ID
            Integer merchantIdTemp = mallInfoObj.getInteger("merchantId");
            return new Result(Code.RESULT_STATUS_SUCCESS, merchantIdTemp, "");
        }
    }

    /**
     * 根据自营商品favoriteSeq查询商品类别
     */
    private Result getKindIdAndKindNameByFavoriteSeqSelfSupport(String favoriteSeq) {

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
        JSONObject dataObject = new JSONObject();
        dataObject.put("skuSeqs", favoriteSeq);
        formData.add("data", dataObject.toJSONString());
        formData.add("token", "member");
        String respHotString;
        JSONObject activityResp;
        try {
            respHotString = restTemplate.postForObject(productCategoryUrl, formData, String.class);
            activityResp = JSONObject.parseObject(respHotString);
        } catch (RestClientException e) {
            log.error("查询自营商品所属商品类别信息Read timed out,url:" + productCategoryUrl, e);
            return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, "查询自营商品所属商品类别信息Read timed out,url:"
                    + productCategoryUrl);
        }
        if (activityResp != null && activityResp.getJSONArray("data") != null) {
            JSONObject result = activityResp.getJSONArray("data").getJSONObject(0);
            String kindId = result.getString("cpSeq");
            String spuSeq = result.getString("spuSeq");
            MultiValueMap<String, Object> formData2 = new LinkedMultiValueMap<String, Object>();
            JSONObject dataObject1 = new JSONObject();
            dataObject1.put("gcSeq", kindId);
            formData2.add("data", dataObject1.toJSONString());
            // 接口返回数据
            JSONObject activityResp1;
            String respHotString1;
            try {
                respHotString1 = restTemplate.postForObject(apiCategoryGetAppNameByGcSeq, formData2,
                        String.class);
                activityResp1 = JSONObject.parseObject(respHotString1);
            } catch (RestClientException e) {
                log.error("查询自营商品kindId所属商品kindName类别信息Read timed out,url:" + apiCategoryGetAppNameByGcSeq
                        + "?gcSeq=" + kindId, e);
                return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION,
                        "查询自营商品kindId所属商品kindName类别信息Read timed out,url:" + apiCategoryGetAppNameByGcSeq
                                + "?gcSeq=" + kindId);
            }
            if (activityResp1 == null
                    || StringUtils.isBlank(activityResp1.getJSONObject("data").getJSONObject("result")
                    .getString("appName"))) {
                log.error("用自营商品kindId查询kindName信息接口返回请求体数据异常,找不到该商品kindName！ url : "
                        + apiCategoryGetAppNameByGcSeq + "?gcSeq=" + kindId + " ,return text : "
                        + respHotString1);
                return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, activityResp1,
                        "调用自营商品类目service接口错误,url:" + apiCategoryGetAppNameByGcSeq + "?gcSeq=" + kindId
                                + ",return text : " + respHotString1);
            } else {
                String appName = activityResp1.getJSONObject("data").getJSONObject("result")
                        .getString("appName");
                String kindName = validate(appName);
                /*
                 * String kindName = result.getString("siName"); kindName = validate(kindName);
                 */
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("spuId", spuSeq);
                jsonObject.put("kindId", kindId);
                jsonObject.put("kindName", kindName);
                return new Result(Code.RESULT_STATUS_SUCCESS, jsonObject, "");
            }

        } else {
            log.error("调用自营商品类目查询接口错误！url : " + productCategoryUrl + "return text:" + respHotString);
            return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, activityResp, "调用自营商品类目service接口错误,url:"
                    + productCategoryUrl + ",return text : " + respHotString);
        }

    }

    /**
     * 根据商城商品favoriteSeq查询商品类别
     */
    private Result getMallKindIdAndName(String favoriteSeq) {
        try {
            //根据商品ID取末级分类
            MultiValueMap<String, Object> lastCategoryParam = new LinkedMultiValueMap<>();
            JSONObject lastCategoryData = new JSONObject();
            lastCategoryData.put("skuId", favoriteSeq);
            lastCategoryParam.add("data", lastCategoryData.toJSONString());
            String lastCategory = restTemplate.postForObject(malltCategoryUrl, lastCategoryParam, String.class);
            JSONObject lastCategoryJson = JSONObject.parseObject(lastCategory);
            if (!"1".equals(lastCategoryJson.getString("success"))) {
                return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, "查询商城商品末级类别异常");
            }

            String kindId = lastCategoryJson.getJSONObject("data").getString("catId");
            if (StringUtils.isEmpty(kindId)) {
                return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, "查询商城商品末级类别catId为空");
            }
            //根据末级分类取顶级分类
            MultiValueMap<String, Object> firstCategoryParam = new LinkedMultiValueMap<>();
            JSONObject firstCategoryData = new JSONObject();
            firstCategoryData.put("gcSeq", kindId);
            firstCategoryParam.add("data", firstCategoryData.toJSONString());
            String firstCategory = restTemplate.postForObject(apiCategoryGetAppNameByGcSeq, firstCategoryParam, String.class);

            JSONObject firstCategoryJson = JSONObject.parseObject(firstCategory);
            if (!"1".equals(firstCategoryJson.getString("success"))) {
                return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, "查询商城商品顶级分类异常");
            }
            String appName = firstCategoryJson.getJSONObject("data").getJSONObject("result").getString("appName");
            String kindName = validate(appName);
            JSONObject jsonObject = new JSONObject();
            //这里返回的是末级分类ID和顶级分类名称
            jsonObject.put("kindId", kindId);
            jsonObject.put("kindName", kindName);
            return new Result(Code.RESULT_STATUS_SUCCESS, jsonObject, "success");

        } catch (RestClientException e) {
            log.error("查询商城商品分类信息异常", e);
            return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, "查询商城商品分类信息异常");
        }
    }

    /**
     * 根据商城商品favoriteSeq查询商品类别
     */
    private Result getKindIdAndKindNameByFavoriteSeqShop(String favoriteSeq) {
        String respHotString;
        JSONObject activityResp;
        try {
            respHotString = restTemplate.getForObject(storeCategoryUrl + favoriteSeq, String.class);
            activityResp = JSONObject.parseObject(respHotString);
        } catch (RestClientException e) {
            log.error("查询商城店铺所属商品类别信息Read timed out,url:" + storeCategoryUrl, e);
            return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, "查询商城店铺所属商品类别信息Read timed out,url:"
                    + storeCategoryUrl);
        }
        if (activityResp != null && activityResp.getJSONObject("body") != null
                && activityResp.getJSONObject("body").getJSONObject("data") != null) {
            JSONObject data = ((JSONObject) activityResp.get("body")).getJSONObject("data");
            String kindId = data.getString("catid");
            String kindName = data.getString("catname");
            kindName = validate(kindName);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("kindId", kindId);
            jsonObject.put("kindName", kindName);
            return new Result(Code.RESULT_STATUS_SUCCESS, jsonObject, "");
        } else {
            log.error("查询商城店铺所属商品类别错误!url : " + storeCategoryUrl + "return text : " + respHotString);
            return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, activityResp, "查询商城店铺所属商品类别!url:"
                    + storeCategoryUrl + ",return text : " + respHotString);
        }
    }

    /**
     * 分类名称处理
     */
    private static String validate(String kindName) {
        try {
            if ("台湾精品美食".equals(kindName)) {
                return "台湾美食";
            }
            String[] cateArr = kindName.split("、");
            if (cateArr.length > 0) {
                if (cateArr[0].length() <= 2) {
                    if (cateArr.length > 1 && cateArr[1].length() <= 2) {
                        kindName = cateArr[0] + cateArr[1];
                    } else {
                        kindName = cateArr[0];
                    }

                } else if (cateArr[0].length() <= 4) {
                    kindName = cateArr[0];
                } else {
                    kindName = kindName.substring(0, 3) + "..";
                }
            }
        } catch (Exception e) {
            log.error("分类名称处理错误  kindName=" + kindName, e);
        }
        return kindName;
    }

    @Override
    public void upLoad() {
        int start = 0;
        for (; ; ) {
            List<FavoriteEntity> findMerchanId = favoriteMapper.findUpLoad(start, 100);
            log.info("清洗商品数:" + findMerchanId.size());
            if (findMerchanId.size() == 0) {
                break;
            }
            Set<String> smSeqs = new HashSet<>();
            for (FavoriteEntity favoriteEntity : findMerchanId) {
                String smSeq = favoriteEntity.getFavoriteSeq();
                smSeqs.add(smSeq);
            }
            JSONArray jsonArray = tryGet(smSeqs);
            if (jsonArray == null || jsonArray.size() <= 0) {
                start += 100;
            } else {
                // 获取商品kindName
                int len = jsonArray.size();
                start = start + len;
                for (int i = 0; i < len; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String skuSeq = jsonObject.getString("skuSeq");
                    String smSeq = jsonObject.getString("smSeq");
                    String spuSeq = jsonObject.getString("spuSeq");
                    log.info("skuSeq:" + skuSeq);
                    favoriteMapper.upLoad(skuSeq, smSeq, spuSeq);
                }
            }
        }

    }

    @Override
    public void upLoadAll() {
        int start = 0;
        for (; ; ) {
            List<FavoriteEntity> findMerchanId = favoriteMapper.findAll(start, 100);
            log.info("清洗商品数:" + findMerchanId.size());
            if (findMerchanId.size() == 0) {
                break;
            }
            Set<String> smSeqs = new HashSet<>();
            for (FavoriteEntity favoriteEntity : findMerchanId) {
                String smSeq = favoriteEntity.getFavoriteSeq();
                smSeqs.add(smSeq);
            }
            JSONArray jsonArray = tryGet(smSeqs);
            if (jsonArray.size() > 0) {
                int len = jsonArray.size();
                for (int i = 0; i < len; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String skuSeq = jsonObject.getString("skuSeq");
                    String smSeq = jsonObject.getString("smSeq");
                    String spuSeq = jsonObject.getString("spuSeq");
                    log.info("skuSeq:" + skuSeq);
                    favoriteMapper.upLoad(skuSeq, smSeq, spuSeq);
                }
            }
            start += 100;
        }
    }

    private JSONArray tryGet(Set<String> smSeqs) {
        MultiValueMap<String, Object> requestParam = new LinkedMultiValueMap<>();
        JSONObject data = new JSONObject();
        data.put("smSeq", StringUtils.join(smSeqs, ","));
        requestParam.add("data", data.toJSONString());
        // 接口返回数据
        for (int i = 0; i < 3; i++) {
            try {
                String resStr = restTemplate.postForObject(findSkuInfoByItnosUrl, requestParam, String.class);
                JSONObject json = JSONObject.parseObject(resStr);
                if ("1".equals(json.getString("success"))) {
                    return json.getJSONArray("data");
                }
            } catch (Exception e) {
                log.error("查询失败i=" + i, e);
            }
        }
        return new JSONArray();
    }

    @Override
    public void upLoadError() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("errorSmSeq.properties");
        Properties properties = new Properties();
        properties.load(resourceAsStream);

        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        for (Map.Entry<Object, Object> entity : entries) {
            Object oldSku = entity.getKey();
            String newSkuAndSpu = entity.getValue().toString();
            String[] split = newSkuAndSpu.split(",");
            String spu = split[0];
            String newSku = split[1];
            int count = favoriteMapper.updateError(oldSku, spu, newSku);
            log.info(oldSku + ":" + newSku + ",count:" + count);
        }
    }

    @Override
    public void upLoadBySku(String skuSeq) {
        int start = 0;
        for (; ; ) {
            List<FavoriteEntity> entities = favoriteMapper.getSmseqsBySkuSeq(skuSeq, start, 20);
            log.info("start:" + start + "  entities:" + entities);
            if (entities.size() == 0) {
                break;
            }
            Set<String> smSeqs = new HashSet<>();
            for (FavoriteEntity favoriteEntity : entities) {
                String smSeq = favoriteEntity.getFavoriteSeq();
                smSeqs.add(smSeq);
            }
            JSONArray jsonArray = tryGet(smSeqs);
            if (jsonArray.size() > 0) {
                int len = jsonArray.size();
                for (int i = 0; i < len; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String newSkuSeq = jsonObject.getString("skuSeq");
                    String smSeq = jsonObject.getString("smSeq");
                    String spuSeq = jsonObject.getString("spuSeq");
                    log.info("skuSeq:" + newSkuSeq);
                    favoriteMapper.upLoad(newSkuSeq, smSeq, spuSeq);
                }
            }
            start += 20;
        }

    }

    @Override
    public void upLoadBySmSeq() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("error_smseq");
        Scanner scanner = new Scanner(resourceAsStream);
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine().trim();
            JSONArray jsonArray = tryGet(Collections.singleton(s));
            if (jsonArray.size() > 0) {
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String newSkuSeq = jsonObject.getString("skuSeq");
                String smSeq = jsonObject.getString("smSeq");
                String spuSeq = jsonObject.getString("spuSeq");
                log.info("skuSeq:" + newSkuSeq);
                favoriteMapper.upLoad(newSkuSeq, smSeq, spuSeq);
            }
        }
        try {
            scanner.close();
            resourceAsStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upLoadType() {
        favoriteMapper.upLoadType();
    }

    @Override
    public void returnGo() {
        Boolean next = true;
        while (next) {
            List<FavoriteEntity> findMerchanId = favoriteMapper.findUpLoadNext();
            log.info("清洗商品数:" + findMerchanId.size());
            for (FavoriteEntity favoriteEntity : findMerchanId) {
                String smSeq = favoriteEntity.getFavoriteSeq();
                favoriteMapper.upLoad("", smSeq, "");
            }
            if (findMerchanId.size() < 100) {
                next = false;
            }
        }
    }

    @Override
    public void deleteSmSeq() {
        //删除无法匹配的自营商品
        favoriteMapper.deleteSmSeq();
    }
}