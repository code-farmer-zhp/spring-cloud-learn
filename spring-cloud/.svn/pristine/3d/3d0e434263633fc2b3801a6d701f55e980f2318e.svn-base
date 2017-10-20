package com.feiniu.member.service.favorite;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.score.MutilScoreService;
import com.feiniu.member.util.PicRandomUtil;
import com.feiniu.member.util.PicUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.util.*;

/**
 * 收藏夹控制层
 */
@Service
public class FavoriteAPIService {

    private static final CustomLog log = CustomLog.getLogger(FavoriteAPIService.class);

    @Value("${imgInside.url}")
    private String imgInsideUrl;

    @Value("${m.feiniu.url}")
    private String mUrl;

    @Value("${m.staticDomain.url}")
    private String mStaticUrl;

    @Value("${favAdd.api}")
    private String favoriteBasicsUrl;

    @Value("${store.url}")
    private String storeUrl;

    @Value("${findAllByPage.api}")
    private String findAllByPageApi;

    @Value("${findActivitys.api}")
    private String findActivitysApi;

    @Value("${getActivityListByMerchantBatch.api}")
    private String getActivityListByMerchantBatchApi;

    @Value("${feiniusearch.searchproduct.api}")
    private String feiniusearchSearchproductApi;

    @Value("${getUserScoreInfo.api}")
    private String getUserScoreInfoApi;

    @Value("${queryCouponMountByFavorite.api}")
    private String queryCouponMountByFavoriteApi;

    @Value("${queryCouponInfoByFavorite.api}")
    private String queryCouponInfoByFavoriteApi;

    @Value("${store.to.url}")
    private String storeToUrl;

    @Value("${pmapiservice.api.url}")
    private String pmapiservice_api_url;

    @Value("${hua.bei.url}")
    private String hua_bei_url;

    @Value("${java.seckill.drp}")
    private String javaSeckillDrp;

    // 秒杀配置的大区
    @Value("${province.pgseq.map}")
    private String provincePgseqMap;

    @Autowired
    @Qualifier("loadBalancedRestTemplate")
    protected RestTemplate restTemplate;

    @Autowired
    private MutilScoreService mutilScoreService;

    /**
     * 获取店铺收藏列表数据
     */
    @HystrixCommand
    public String hasStoresView(String memGuid, String areaCode, Integer pageSize, Integer offset) {
        try {
            String memHasCommentCount = restTemplate.getForObject(favoriteBasicsUrl
                    + "/favorite?type=1&active=true&activity_qd=1&area_code={areaCode}" + "&mem_guid="
                    + memGuid + "&limit=" + pageSize + "&offset=" + offset, String.class, areaCode);
            JSONObject memHasCommentCountJson = JSONObject.parseObject(memHasCommentCount);
            JSONArray jsonData = memHasCommentCountJson.getJSONArray("data");
            if (null != jsonData && jsonData.size() > 0) {
                for (int i = 0, len = jsonData.size(); i < len; i++) {
                    JSONObject jo = jsonData.getJSONObject(i);
                    String storeLogoUrl = jo.getString("storeLogoUrl");
                    if (StringUtils.isNotBlank(storeLogoUrl)) {
                        String picTransform = PicUtil.picTransform(storeLogoUrl, storeUrl);
                        jo.put("storeLogoUrl", picTransform);
                    }
                    JSONArray newProducts = jo.getJSONArray("newProducts");
                    if (null != newProducts && newProducts.size() > 0) {
                        for (int j = 0, size = newProducts.size(); j < size; j++) {
                            JSONObject newProduct = newProducts.getJSONObject(j);
                            String itPic = newProduct.getString("it_pic");
                            if (StringUtils.isNotBlank(itPic)) {
                                String picTransform = PicUtil.picTransform(itPic, storeUrl);
                                picTransform = picTransform.replace("_80x80.", "_120x120.");
                                newProduct.put("it_pic", picTransform);
                            }
                        }
                    }
                }
            }
            return memHasCommentCountJson.toJSONString();
        } catch (Exception e) {
            log.error("获取店铺收藏列表数据出错", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 店铺收藏类别信息
     */
    @HystrixCommand
    public String hasStoresType(String memGuid, String areaCode) {

        try {
            String category = restTemplate.getForObject(favoriteBasicsUrl
                    + "/favorite/category?type=1&active=true&activity_qd=1&area_code={areaCode}"
                    + "&mem_guid=" + memGuid, String.class, areaCode);
            return category;
        } catch (Exception e) {
            log.error("店铺收藏类别信息查询出错", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 店铺收藏数量
     */
    @HystrixCommand
    public Integer hasStoresNum(String memGuid) {

        try {
            String totalSum = restTemplate.getForObject(favoriteBasicsUrl
                    + "/favorite/count?type=1&active=true&mem_gu" + "id=" + memGuid, String.class);
            Integer num = JSONObject.parseObject(totalSum).getInteger("data");
            return num;
        } catch (Exception e) {
            log.error("查询店铺收藏数量错误", e);
            return 0;
        }

    }

    /**
     * 商品收藏列表
     */
    @HystrixCommand
    public String hasProdsView(String memGuid, String areaCode, Integer pageSize, Integer offset,
                               Integer activity_qd) {

        try {
            String memHasCommentCount = restTemplate.getForObject(favoriteBasicsUrl
                            + "/favorite?type=0&active=true&area_code={areaCode}" + "&mem_guid=" + memGuid
                            + "&limit=" + pageSize + "&offset=" + offset + "&activity_qd=" + activity_qd,
                    String.class, areaCode);
            JSONObject memHasCommentCountJson = JSONObject.parseObject(memHasCommentCount);
            JSONArray jsonData = memHasCommentCountJson.getJSONArray("data");
            if (null != jsonData && jsonData.size() > 0) {
                for (int i = 0, len = jsonData.size(); i < len; i++) {
                    JSONObject jo = jsonData.getJSONObject(i);
                    String type = jo.getString("type");
                    if ("1".equals(type)) {
                        String itPic = jo.getString("it_pic");
                        String picTransform = PicUtil.picTransform(itPic, storeUrl);
                        jo.put("it_pic", picTransform);
                    }
                }
            }
            return memHasCommentCountJson.toJSONString();
        } catch (Exception e) {
            log.error("商品收藏列表查询出错", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 商品收藏列表类别及促销
     */
    @HystrixCommand
    public String hasProdsType(String memGuid, String areaCode) {

        try {
            String category = restTemplate.getForObject(favoriteBasicsUrl
                    + "/favorite/category?type=0&active=true&activity_qd=1&area_code={areaCode}"
                    + "&mem_guid=" + memGuid, String.class, areaCode);
            return category;
        } catch (Exception e) {
            log.error("商品收藏列表类别及促销查询错误", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 商品收藏数量
     */
    @HystrixCommand
    public Integer hasProdsNum(String memGuid) {
        try {
            String totalSum = restTemplate.getForObject(favoriteBasicsUrl
                    + "/favorite/count?type=0&active=true&mem_guid=" + memGuid, String.class);
            Integer num = JSONObject.parseObject(totalSum).getInteger("data");
            return num;
        } catch (Exception e) {
            log.error("查询商品收藏数量错误", e);
            return 0;
        }

    }

    /**
     * 删除收藏信息
     */
    @HystrixCommand
    public String deleteFavorite(String memGuid, String fIds) {
        try {
            String forObject = restTemplate.getForObject(favoriteBasicsUrl + "/" + memGuid + "/" + fIds,
                    String.class);
            return forObject;
        } catch (Exception e) {
            log.error("删除收藏信息出错", e);
            return "{\"code\":\"505\"}";
        }
    }

    @HystrixCommand
    public String ajaxListViewAll(String memGuid, String areaCode, Integer pageSize, Integer offset,
                                  String type, String ids, String kindId) {
        JSONObject memHasCommentCountJson = new JSONObject();
        String memHasCommentCount = new String();
        if ("prod".equals(type)) {
            try {
                if (StringUtils.isNotBlank(kindId)) {
                    memHasCommentCount = restTemplate.getForObject(favoriteBasicsUrl
                            + "/favorite?type=0&active=true&activity_qd=1&area_code={areaCode}"
                            + "&mem_guid=" + memGuid + "&limit=" + pageSize + "&offset=" + offset
                            + "&kind_id=" + kindId, String.class, areaCode);
                } else if (StringUtils.isNotBlank(ids)) {
                    memHasCommentCount = restTemplate.getForObject(favoriteBasicsUrl
                            + "/favorite?type=0&active=true&activity_qd=1&area_code={areaCode}"
                            + "&mem_guid=" + memGuid + "&limit=" + pageSize + "&offset=" + offset + "&ids="
                            + ids, String.class, areaCode);
                } else {
                    memHasCommentCount = restTemplate.getForObject(favoriteBasicsUrl
                                    + "/favorite?type=0&active=true&activity_qd=1&area_code={areaCode}"
                                    + "&mem_guid=" + memGuid + "&limit=" + pageSize + "&offset=" + offset,
                            String.class, areaCode);
                }

                memHasCommentCountJson = JSONObject.parseObject(memHasCommentCount);
                JSONArray jsonData = memHasCommentCountJson.getJSONArray("data");
                if (null != jsonData && jsonData.size() > 0) {
                    for (int i = 0, len = jsonData.size(); i < len; i++) {
                        JSONObject jo = jsonData.getJSONObject(i);
                        String joType = jo.getString("type");
                        if ("1".equals(joType)) {
                            String itPic = jo.getString("it_pic");
                            String picTransform = PicUtil.picTransform(itPic, storeUrl);
                            jo.put("it_pic", picTransform);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("商品列表查询失败", e);
                return "{\"code\":\"505\"}";
            }

        } else {
            try {
                if (StringUtils.isNotBlank(kindId)) {
                    memHasCommentCount = restTemplate.getForObject(favoriteBasicsUrl
                            + "/favorite?type=1&active=true&activity_qd=1&area_code={areaCode}"
                            + "&mem_guid=" + memGuid + "&limit=" + pageSize + "&offset=" + offset
                            + "&kind_id=" + kindId, String.class, areaCode);
                } else {
                    memHasCommentCount = restTemplate.getForObject(favoriteBasicsUrl
                                    + "/favorite?type=1&active=true&activity_qd=1&area_code={areaCode}"
                                    + "&mem_guid=" + memGuid + "&limit=" + pageSize + "&offset=" + offset,
                            String.class, areaCode);
                }
                memHasCommentCountJson = JSONObject.parseObject(memHasCommentCount);
                JSONArray jsonData = memHasCommentCountJson.getJSONArray("data");
                if (null != jsonData && jsonData.size() > 0) {
                    for (int i = 0, len = jsonData.size(); i < len; i++) {
                        JSONObject jo = jsonData.getJSONObject(i);
                        String storeLogoUrl = jo.getString("storeLogoUrl");
                        if (StringUtils.isNotBlank(storeLogoUrl)) {
                            String picTransform = PicUtil.picTransform(storeLogoUrl, storeUrl);
                            jo.put("storeLogoUrl", picTransform);
                        }
                        JSONArray newProducts = jo.getJSONArray("newProducts");
                        if (null != newProducts && newProducts.size() > 0) {
                            for (int j = 0, size = newProducts.size(); j < size; j++) {
                                JSONObject newProduct = newProducts.getJSONObject(j);
                                String itPic = newProduct.getString("it_pic");
                                if (StringUtils.isNotBlank(itPic)) {
                                    String picTransform = PicUtil.picTransform(itPic, storeUrl);
                                    picTransform = picTransform.replace("_80x80.", "_120x120.");
                                    newProduct.put("it_pic", picTransform);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("店铺列表查询失败", e);
                return "{\"code\":\"505\"}";
            }

        }
        return memHasCommentCountJson.toJSONString();

    }

    @HystrixCommand
    public Integer ajaxListNumAll(String memGuid, String type, String ids, String kindId) {
        String totalSum = new String();
        if ("prod".equals(type)) {
            if (StringUtils.isNotBlank(kindId)) {
                totalSum = restTemplate.getForObject(favoriteBasicsUrl
                                + "/favorite/count?type=0&active=true&mem_guid=" + memGuid + "&kind_id=" + kindId,
                        String.class);
            } else if (StringUtils.isNotBlank(ids)) {
                totalSum = restTemplate.getForObject(favoriteBasicsUrl
                                + "/favorite/count?type=0&active=true&mem_guid=" + memGuid + "&ids=" + ids,
                        String.class);
            } else {
                totalSum = restTemplate.getForObject(favoriteBasicsUrl
                        + "/favorite/count?type=0&active=true&mem_guid=" + memGuid, String.class);
            }
            try {
                Integer num = JSONObject.parseObject(totalSum).getInteger("data");
                return num;
            } catch (Exception e) {
                log.error("商品收藏查询数量出错", e);
                return 0;
            }
        } else {
            if (StringUtils.isNotBlank(kindId)) {
                totalSum = restTemplate.getForObject(favoriteBasicsUrl
                                + "/favorite/count?type=1&active=true&mem_guid=" + memGuid + "&kind_id=" + kindId,
                        String.class);
            } else {
                totalSum = restTemplate.getForObject(favoriteBasicsUrl
                        + "/favorite/count?type=1&active=true&mem_guid=" + memGuid, String.class);
            }
            try {
                Integer num = JSONObject.parseObject(totalSum).getInteger("data");
                return num;
            } catch (Exception e) {
                log.error("店铺收藏查询数量出错", e);
                return 0;
            }

        }

    }

    /**
     * 店铺或商品异步加载类别及促销信息
     */
    @HystrixCommand
    public String propertyType(String memGuid, String areaCode, String type) {
        String category = "";
        try {
            if ("prod".equals(type)) {
                category = restTemplate.getForObject(favoriteBasicsUrl
                        + "/favorite/category?type=0&active=true&activity_qd=1&area_code={areaCode}"
                        + "&mem_guid=" + memGuid, String.class, areaCode);
            } else {
                category = restTemplate.getForObject(favoriteBasicsUrl
                        + "/favorite/category?type=1&active=true&activity_qd=1&area_code={areaCode}"
                        + "&mem_guid=" + memGuid, String.class, areaCode);
            }
        } catch (Exception e) {
            log.error("查询店铺或商品收藏类别有误", e);
            return "{\"code\":\"505\"}";
        }

        return category;

    }

    /**
     * 查询店铺优惠券信息
     */
    @HystrixCommand
    public String findCouponForFavorite(String storeId) {
        MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId", storeId);
        jsonObject.put("pageNo", 1);
        jsonObject.put("pageSize", 50);
        jsonObject.put("activityStatus", 2);
        params1.add("param", jsonObject.toString());
        try {
            String memHasCommentCount = restTemplate.postForObject(findAllByPageApi, params1, String.class);
            return memHasCommentCount;
        } catch (RestClientException e) {
            log.error("查询店铺优惠券信息出错", e);
            return "{\"code\":\"200\",\"data\":\"{\\\"list\\\":[]}\"}";
        }
    }

    /**
     * 促销活动查询
     */
    @HystrixCommand
    public String findActivitysForFavorite(String storeId) {
        MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
        params1.add("merchantId", storeId);
        try {
            String memHasCommentCount = restTemplate.postForObject(findActivitysApi, params1, String.class);
            return memHasCommentCount;
        } catch (RestClientException e) {
            log.error("查询店铺活动信息出错", e);
            return "{\"flag\":\"10006\"}";
        }
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String listGoods(String guid, String cityCode, String areaCode, Integer pageIndex, int pageSize,
                            String searchContent, JSONArray cates) {
        // 返回总数
        Integer total = 0;
        // 分类数量
        int total1 = 0;

        // 搜索结果卖场ID存放字段
        StringBuilder smSeqArray = new StringBuilder();

        // 请求接口的分页下脚坐标,搜索接口使用
        int offset = (pageIndex - 1) * pageSize;

        // 是否组合查询标志字段（活动+类别）
        boolean multiple = false;
        // 分类删选id标识(只可单选)
        String kindId = "";

        // 活动筛选的id存放字段
        StringBuilder ids = new StringBuilder();

        // 如果搜索不为空执行收藏夹搜索逻辑
        if (StringUtils.isNotBlank(searchContent)) {
            try {
                searchContent = URLDecoder.decode(searchContent, "UTF-8");
            } catch (Exception e) {
                log.error("URLDecoder解密失败!");
            }
            StringBuilder smSeqs = new StringBuilder();
            // 循环获取该用户所有收藏商品的卖场ID
            String seqsString = restTemplate.getForObject(favoriteBasicsUrl + "/getFavoriteId?memGuid="
                    + guid + "&pageSize=200&pageIndex=1", String.class);
            JSONObject seqs = JSONObject.parseObject(seqsString);
            JSONArray datas = seqs.getJSONArray("data");
            for (int i = 0, len = datas.size(); i < len; i++) {
                smSeqs.append(datas.getString(i) + ",");
            }
            if (smSeqs.length() > 0) {
                smSeqs.delete(smSeqs.length() - 1, smSeqs.length());
            } else {
                return "{\"code\":\"200\",\"msg\":\"未订阅商品收藏\",\"goodsList\":[]}";
            }
            // 调用搜索接口,每页最多加载10条数据
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            params1.add("q", searchContent);
            params1.add("sm_seq", String.valueOf(smSeqs));
            params1.add("pageIndex", String.valueOf(pageIndex));
            params1.add("province", cityCode);
            params1.add("resnum", "10");
            String forObject = restTemplate
                    .postForObject(feiniusearchSearchproductApi, params1, String.class);
            JSONObject solr = JSONObject.parseObject(forObject);
            if (solr != null) {
                JSONObject responseHeader = solr.getJSONObject("responseHeader");
                if (responseHeader.getInteger("status") != 0) {
                    log.error("收藏搜索商品solr内部接口调用失败");
                    return "{\"code\":\"505\",\"msg\":\"大数据查询为空\"}";
                } else {
                    // 获取搜索结果总数
                    total = solr.getJSONObject("grouped").getJSONObject("NEW_SPEC_SEQ").getInteger("ngroups");
                    JSONArray groups = solr.getJSONObject("grouped").getJSONObject("NEW_SPEC_SEQ")
                            .getJSONArray("groups");
                    if (groups != null) {
                        for (int i = 0, len = groups.size(); i < len; i++) {
                            JSONObject v = groups.getJSONObject(i);
                            JSONArray docs = v.getJSONObject("doclist").getJSONArray("docs");
                            smSeqArray.append(docs.getJSONObject(0).getString("SM_SEQ") + ",");
                        }
                        if (smSeqArray.length() > 0) {
                            smSeqArray.delete(smSeqArray.length() - 1, smSeqArray.length());
                        }
                    }
                }
            } else {
                log.error("收藏搜索商品solr内部接口调用失败");
                return "{\"code\":\"505\",\"msg\":\"大数据查询为空\",\"goodsList\":[]}";
            }
            // 执行分类及促销标签选择
        } else if (null != cates && cates.size() > 0) {
            // 筛选分类查询(单)
            Set<String> kindsSet = new HashSet<String>();
            // 筛选分类查询(双)
            Set<String> overlappingKindsSet = new HashSet<String>();
            // 筛选分类查询(三)
            Set<String> overlappingKindsSetThree = new HashSet<String>();
            // 是否取交集还是取单个分类
            int overlapping = 0;
            for (int i = 0, len = cates.size(); i < len; i++) {
                JSONObject cate = cates.getJSONObject(i);
                if (StringUtils.isBlank(cate.getString("cateType"))) {
                    log.error("收藏夹筛选参数错误");
                    return "{\"code\":\"505\",\"msg\":\"cateType入参有误\"}";
                }
                // 类别属性单选
                if ("0".equals(cate.getString("cateType"))) {
                    // 分类类目条件(只可单选)
                    kindId = cate.getString("kindId");
                    // 获取分类总数
                    total1 = cate.getInteger("favoriteCount");
                } else if ("1".equals(cate.getString("cateType"))) {
                    overlapping++;
                    // 活动类目条件
                    if (StringUtils.isNotBlank(cate.getString("kindId"))) {
                        String[] split = cate.getString("kindId").split(",");
                        if (overlapping == 3) {
                            for (String string : split) {
                                if (overlappingKindsSet.contains(string)) {
                                    overlappingKindsSetThree.add(string);
                                }
                            }
                        } else if (overlapping == 2) {
                            for (String string : split) {
                                if (kindsSet.contains(string)) {
                                    overlappingKindsSet.add(string);
                                }
                            }
                        } else {
                            for (String string : split) {
                                kindsSet.add(string);
                            }
                        }
                    } else {
                        return "{\"code\":\"200\",\"msg\":\"kindId为空\",\"goodsList\":[]}";
                    }
                }
            }
            if (overlapping == 3) {
                if (StringUtils.isNotBlank(kindId) && overlappingKindsSetThree.size() > 0) {
                    // 组合查询(分类类目+活动类目)
                    multiple = true;
                } else if (StringUtils.isBlank(kindId) && overlappingKindsSetThree.size() > 0) {
                    total = overlappingKindsSetThree.size();
                }

                if (overlappingKindsSetThree.size() > 0) {
                    // 促销，降价，有货查询
                    for (String string : overlappingKindsSetThree) {
                        ids.append(string + ",");
                    }
                    ids.delete(ids.length() - 1, ids.length());
                } else {
                    return "{\"code\":\"200\",\"msg\":\"交集kindId为空\",\"goodsList\":[]}";
                }
            } else if (overlapping == 2) {
                if (StringUtils.isNotBlank(kindId) && overlappingKindsSet.size() > 0) {
                    // 组合查询(分类类目+活动类目)
                    multiple = true;
                } else if (StringUtils.isBlank(kindId) && overlappingKindsSet.size() > 0) {
                    total = overlappingKindsSet.size();
                }

                if (overlappingKindsSet.size() > 0) {
                    // 促销，降价，有货查询
                    for (String string : overlappingKindsSet) {
                        ids.append(string + ",");
                    }
                    ids.delete(ids.length() - 1, ids.length());
                } else {
                    return "{\"code\":\"200\",\"msg\":\"交集kindId为空\",\"goodsList\":[]}";
                }
            } else {
                if (StringUtils.isNotBlank(kindId) && kindsSet.size() > 0) {
                    // 组合查询(分类类目+活动类目)
                    multiple = true;
                } else if (StringUtils.isBlank(kindId) && kindsSet.size() > 0) {
                    total = kindsSet.size();
                } else if (StringUtils.isNotBlank(kindId) && kindsSet.size() == 0) {
                    total = total1;
                }

                if (kindsSet.size() > 0) {
                    // 促销，降价，有货查询
                    for (String string : kindsSet) {
                        ids.append(string + ",");
                    }
                    ids.delete(ids.length() - 1, ids.length());
                }
            }

        }

        JSONObject listRes = new JSONObject();
        JSONArray raw = new JSONArray();
        if (StringUtils.isNotBlank(searchContent)) {
            // 搜索情况下调用新接口
            if (total > 0 && smSeqArray.length() > 0) {
                MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
                params1.add("memGuid", guid);
                params1.add("skuIds", String.valueOf(smSeqArray));
                params1.add("activityQd", String.valueOf(3));
                params1.add("areaCode", areaCode);
                String listResString = restTemplate.postForObject(favoriteBasicsUrl
                        + "/getFavoritePriceBySkuIds", params1, String.class);
                listRes = JSONObject.parseObject(listResString);
                JSONObject solrdata = listRes.getJSONObject("data");
                raw = solrdata.getJSONArray("list");
            } else {
                return "{\"code\":\"200\",\"goodsList\":[]}";
            }
        } else if (multiple) {
            // 组合查询调用新接口

            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<String, Object>();
            // JSONObject formData=new JSONObject();
            formData.add("memGuid", guid);
            formData.add("ids", String.valueOf(ids));
            formData.add("kindId", kindId);
            formData.add("pageSize", String.valueOf(10));
            formData.add("pageIndex", pageIndex.toString());
            formData.add("areaCode", areaCode);
            formData.add("activityQd", String.valueOf(3));
            String listResString = restTemplate.postForObject(favoriteBasicsUrl
                    + "/getFavoriteByIdsAndKindId", formData, String.class);
            listRes = JSONObject.parseObject(listResString);
            try {
                //接口返回两种数据
                JSONObject multipledata = listRes.getJSONObject("data");
                // 组合查询则通过接口返回字段获取总数
                total = multipledata.getInteger("count");
                raw = multipledata.getJSONArray("list");
            } catch (Exception e) {
                log.error("", e);
                JSONArray multipledata = listRes.getJSONArray("data");
                // 组合查询则通过接口返回字段获取总数
                total = multipledata.size();
                raw = multipledata;
            }

        } else {
            String url = favoriteBasicsUrl;
            url += "/favorite?mem_guid=" + guid + "&type=" + 0 + "&offset=" + offset + "&limit=" + 10
                    + "&activity_qd=" + 3 + "&is_crossborder=1&active=true&area_code={areaCode}";
            if (StringUtils.isNotBlank(kindId)) {
                url += "&kind_id=" + kindId;
            }
            if (ids.length() > 0) {
                url += "&ids=" + ids;
            }
            if (StringUtils.isBlank(kindId) && StringUtils.isBlank(ids)) {
                total = hasProdsNum(guid);
            }
            String listResString = restTemplate.getForObject(url, String.class, areaCode);
            listRes = JSONObject.parseObject(listResString);
            raw = listRes.getJSONArray("data");
        }
        //添加多倍积分标签
        if (raw != null && raw.size() > 0) {
            Set<String> skus = new HashSet<>();
            for (int i = 0; i < raw.size(); i++) {
                JSONObject json = raw.getJSONObject(i);
                String sellNo = json.getString("sell_no");
                skus.add(sellNo);
            }
            JSONObject areaCodeInfo = JSONObject.parseObject(areaCode);
            String provinceCode = areaCodeInfo.getString("provinceCode");
            String cityCodeStr = areaCodeInfo.getString("cityCode");
            String areaCodeStr = areaCodeInfo.getString("areaCode");
            Map<String, Integer> mutilScore = mutilScoreService.mutilScore(provinceCode, cityCodeStr, areaCodeStr, skus);

            for (int i = 0; i < raw.size(); i++) {
                JSONObject json = raw.getJSONObject(i);
                String sellNo = json.getString("sell_no");
                Integer mutil = mutilScore.get(sellNo);
                if (mutil != null) {
                    json.put("name", String.format("【%d倍积分】", mutil) + json.getString("name"));
                }
            }
        }
        Integer totalPageCount = (int) Math.ceil(total.doubleValue() / 10);
        JSONObject response = new JSONObject();
        response.put("count", total);
        response.put("totalPageCount", totalPageCount);
        response.put("goodsList", raw);
        response.put("pageIndex", pageIndex);
        response.put("code", 200);
        return response.toJSONString();
    }


    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    public String categoryGoods(String memGuid, String areaCode) {
        // 获得促销和分类类目
        String url = favoriteBasicsUrl + "/favorite/category";
        url += "?mem_guid=" + memGuid + "&activity_qd=" + 3 + "&type=0&active=true&area_code={areaCode}";
        String forObject = restTemplate.getForObject(url, String.class, areaCode);

        JSONObject res = JSONObject.parseObject(forObject);
        // 获取到货和降级类目
        String provinceCode = JSONObject.parseObject(areaCode).getString("provinceCode");
        String url1 = favoriteBasicsUrl + "/getPriceAndArrival";
        url1 += "?memGuid=" + memGuid + "&provinceCode=" + provinceCode;

        String forObject1 = restTemplate.getForObject(url1, String.class);

        JSONObject res1 = JSONObject.parseObject(forObject1);

        JSONObject redata = res.getJSONObject("data");

        JSONObject redata1 = res1.getJSONObject("data");
        JSONObject response = new JSONObject();
        if (redata != null) {
            // 每个类目下的数量
            Integer count = redata.getInteger("cateCounts");
            if (count != null && count >= 0) {
                JSONArray actcates = new JSONArray();
                JSONArray cates = new JSONArray();

                JSONObject actcate = new JSONObject();
                actcate.put("favoriteCount", count);
                actcate.put("kindId", "");
                actcate.put("kindName", "全部");
                actcate.put("cateType", -1);
                JSONObject cate = new JSONObject();
                cate.put("favoriteCount", count);
                cate.put("kindId", "");
                cate.put("kindName", "全部");
                cate.put("cateType", -1);

                cates.add(cate);
                actcates.add(actcate);

                JSONArray reactcates = redata.getJSONArray("actcates");
                if (reactcates != null && reactcates.size() > 0) {
                    actcates.addAll(reactcates);
                }
                JSONArray recates = redata.getJSONArray("cates");
                if (recates != null && recates.size() > 0) {
                    cates.addAll(recates);
                }

                JSONObject classify = new JSONObject();
                classify.put("kindId", "");
                classify.put("kindName", "分类");
                classify.put("cates", cates);

                actcates.add(classify);

                actcates.addAll(2, redata1.getJSONArray("arrivalAndPriceCates"));

                response.put("actcates", actcates);
            }
            response.put("code", 200);
        } else {
            log.error("类目查询接口报错");
            response.put("code", 505);
        }

        return response.toJSONString();
    }

    @HystrixCommand
    public String listStores(String memGuid, String areaCode, Integer pageIndex, int pageSize,
                             JSONArray catesArray) {
        // 总数
        Integer total = 0;
        // 店铺id(StringBuilder)
        StringBuilder merchantIds = new StringBuilder();
        // 店铺id(List)
        List<String> merchantIdsList = new ArrayList<String>();
        // 收藏夹唯一表示id
        StringBuilder ids = new StringBuilder();
        // 是否为组合查询(0否 1是)
        int overlapping = 0;
        // 筛选分类查询(单)
        Set<String> kindsSet = new HashSet<String>();
        // 筛选分类查询 (双)
        Set<String> overlappingKindsSet = new HashSet<String>();
        // 筛选分类查询 (三)
        Set<String> overlappingKindsThree = new HashSet<String>();
        // 返回的数据列表
        JSONArray basicsStoreInfoJsonArray = new JSONArray();
        // 接口访问链接
        String url = "";
        // 类目选择内容
        if (null != catesArray && catesArray.size() > 0) {
            for (int i = 0, len = catesArray.size(); i < len; i++) {
                overlapping++;
                JSONObject jsonObject = catesArray.getJSONObject(i);
                String kindId = jsonObject.getString("kindId");
                if (StringUtils.isNotBlank(kindId)) {
                    String[] split = kindId.split(",");
                    if (overlapping == 3) {
                        for (String string : split) {
                            if (overlappingKindsSet.contains(string)) {
                                overlappingKindsThree.add(string);
                            }
                        }
                    } else if (overlapping == 2) {
                        for (String string : split) {
                            if (kindsSet.contains(string)) {
                                overlappingKindsSet.add(string);
                            }
                        }
                    } else {
                        for (String string : split) {
                            kindsSet.add(string);
                        }
                    }
                } else {
                    return "{\"code\":\"200\",\"storesList\":[]}";
                }
            }
            if (overlapping == 3) {
                // 有劵,上新,促销(联合查询)
                for (String string : overlappingKindsThree) {
                    ids.append(string + ",");
                }
                if (ids.length() == 0) {
                    return "{\"code\":\"200\",\"storesList\":[]}";
                }
                total = overlappingKindsThree.size();
                url = favoriteBasicsUrl + "/favorite?active=true&limit=" + pageSize
                        + "&activity_qd=3&mem_guid=" + memGuid + "&type=1&area_code={areaCode}"
                        + "&offset=" + (pageIndex - 1) * pageSize + "&ids=" + ids;
            } else if (overlapping == 2) {
                // 有劵,上新,促销(联合查询)
                for (String string : overlappingKindsSet) {
                    ids.append(string + ",");
                }
                if (ids.length() == 0) {
                    return "{\"code\":\"200\",\"storesList\":[]}";
                }
                total = overlappingKindsSet.size();
                url = favoriteBasicsUrl + "/favorite?active=true&limit=" + pageSize
                        + "&activity_qd=3&mem_guid=" + memGuid + "&type=1&area_code={areaCode}"
                        + "&offset=" + (pageIndex - 1) * pageSize + "&ids=" + ids;
            } else {
                // 有劵,上新,促销(单查询)
                for (String string : kindsSet) {
                    ids.append(string + ",");
                }
                if (ids.length() == 0) {
                    return "{\"code\":\"200\",\"storesList\":[]}";
                }
                total = kindsSet.size();
                url = favoriteBasicsUrl + "/favorite?active=true&limit=" + pageSize
                        + "&activity_qd=3&mem_guid=" + memGuid + "&type=1&area_code={areaCode}"
                        + "&offset=" + (pageIndex - 1) * pageSize + "&ids=" + ids;
            }
            String basicsStoreInfo = restTemplate.getForObject(url, String.class, areaCode);
            JSONObject basicsStoreInfoResult = JSONObject.parseObject(basicsStoreInfo);
            String code = basicsStoreInfoResult.getString("code");
            if (!"1".equals(code)) {
                log.error("查询店铺基础信息出错");
                return "{\"code\":\"505\",\"storesList\":[]}";
            }
            basicsStoreInfoJsonArray = basicsStoreInfoResult.getJSONArray("data");
        } else {
            url = favoriteBasicsUrl + "/favorite?active=true&limit=" + pageSize + "&activity_qd=3&mem_guid="
                    + memGuid + "&type=1&area_code={areaCode}" + "&offset=" + (pageIndex - 1) * pageSize;
            String basicsStoreInfo = restTemplate.getForObject(url, String.class, areaCode);
            total = hasStoresNum(memGuid);
            JSONObject basicsStoreInfoResult = JSONObject.parseObject(basicsStoreInfo);
            String code = basicsStoreInfoResult.getString("code");
            if (!"1".equals(code)) {
                log.error("查询店铺基础信息出错");
                return "{\"code\":\"505\",\"storesList\":[]}";
            }
            basicsStoreInfoJsonArray = basicsStoreInfoResult.getJSONArray("data");
        }
        // 店铺活动组装数据实例
        Map<String, JSONArray> allActivityMap = new HashMap<String, JSONArray>();
        // 店铺卡券组装数据实例
        Map<String, String> allCouponMountMap = new HashMap<String, String>();
        if (null != basicsStoreInfoJsonArray && basicsStoreInfoJsonArray.size() > 0) {
            for (int i = 0, len = basicsStoreInfoJsonArray.size(); i < len; i++) {
                JSONObject jo = basicsStoreInfoJsonArray.getJSONObject(i);
                String merchantId = jo.getString("merchantId");
                merchantIdsList.add(merchantId);
                merchantIds.append(merchantId + ",");
                String storego = jo.getString("url");
                String[] split = storeToUrl.split(",");
                storego = storego.replace(split[0], split[1]);
                jo.put("url", storego);
                String storeLogoUrl = jo.getString("storeLogoUrl");
                if (StringUtils.isNotBlank(storeLogoUrl)) {
                    String picTransform = PicUtil.picTransform(storeLogoUrl, storeUrl, "120x120", "2",
                            PicRandomUtil.random(imgInsideUrl), true);
                    jo.put("storeLogoUrl", picTransform);
                }
                JSONArray newProducts = jo.getJSONArray("newProducts");
                if (null != newProducts && newProducts.size() > 0) {
                    for (int j = 0, size = newProducts.size(); j < size; j++) {
                        JSONObject newProduct = newProducts.getJSONObject(j);
                        String itPic = newProduct.getString("it_pic");
                        if (StringUtils.isNotBlank(itPic)) {
                            String picTransform = PicUtil.picTransform(itPic, storeUrl, "120x120", "2",
                                    PicRandomUtil.random(imgInsideUrl), true);
                            newProduct.put("it_pic", picTransform);
                        }
                    }
                }
            }
        }
        if (merchantIds.length() > 0) {
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
            JSONObject formData = new JSONObject();
            formData.put("merchantId", merchantIds);
            formData.put("activityNumber", 3);
            param.add("param", formData.toJSONString());
            String listResString = restTemplate.postForObject(getActivityListByMerchantBatchApi, param,
                    String.class);
            JSONObject allActivityJSONObject = JSONObject.parseObject(listResString);
            JSONArray jsonArrayData = allActivityJSONObject.getJSONArray("body");
            if (null != jsonArrayData && jsonArrayData.size() > 0) {
                for (int i = 0, len = jsonArrayData.size(); i < len; i++) {
                    JSONObject jsonObject = jsonArrayData.getJSONObject(i);
                    String merchantId = jsonObject.getString("merchantId");
                    JSONArray activityInfoList = jsonObject.getJSONArray("ActivityInfoList");
                    allActivityMap.put(merchantId, activityInfoList);
                }
            }
            MultiValueMap<String, Object> param1 = new LinkedMultiValueMap<String, Object>();
            JSONObject formData1 = new JSONObject();
            formData1.put("merchantIds", merchantIdsList);
            param1.add("data", formData1.toJSONString());
            String listResString1 = restTemplate.postForObject(queryCouponMountByFavoriteApi, param1,
                    String.class);
            JSONObject allCouponMount = JSONObject.parseObject(listResString1);
            JSONArray allCouponMountJSONArray = allCouponMount.getJSONArray("data");
            if (null != allCouponMountJSONArray && allCouponMountJSONArray.size() > 0) {
                for (int i = 0, len = allCouponMountJSONArray.size(); i < len; i++) {
                    JSONObject jsonObject = allCouponMountJSONArray.getJSONObject(i);
                    String merchantId = jsonObject.getString("merchantId");
                    String canReceiveMount = jsonObject.getString("canReceiveMount");
                    allCouponMountMap.put(merchantId, canReceiveMount);
                }
            }
        }

        if (basicsStoreInfoJsonArray != null && basicsStoreInfoJsonArray.size() > 0) {
            for (int i = 0, len = basicsStoreInfoJsonArray.size(); i < len; i++) {
                JSONObject oneScore = basicsStoreInfoJsonArray.getJSONObject(i);
                String merchantId = oneScore.getString("merchantId");
                // 设置店铺活动

                getActivitiesByMerchantId(oneScore, merchantId, allActivityMap.get(merchantId));

                String mount = allCouponMountMap.get(merchantId);
                if (StringUtils.isNotBlank(mount)) {
                    oneScore.put("couponAmount", mount);
                } else {
                    oneScore.put("couponAmount", 0);
                }
                // 设置店铺奖券
            }
        }

        JSONObject dataJson = new JSONObject();
        dataJson.put("storesList", basicsStoreInfoJsonArray);
        dataJson.put("count", total);
        dataJson.put("pageIndex", pageIndex);
        Integer totalPageCount = (int) Math.ceil(total.doubleValue() / 10);
        dataJson.put("totalPageCount", totalPageCount);
        dataJson.put("code", 200);
        return dataJson.toJSONString();

    }

    /**
     * 注意： type=4 这种情况已取消，不会出现
     */
    public static JSONObject getActivityModelByType(int type, boolean isGlobal) {
        JSONObject obj = new JSONObject();

        String name = null;
        switch (type) {
            case 1:
                name = "满减";
                break;
            case 2:
            case 6:
                name = "折扣";
                break;
            case 3:
            case 7:
                if (!isGlobal) {
                    // 环球购商品屏蔽满赠打标
                    name = "满赠";
                }
                break;
            case 5:
            case 8:
            case 9:
            case 10:
                name = "优惠";
                break;
            case 11:
                name = "换购";
                break;
            case 4:
                // 此情况忽略，不考虑
                break;
            default:
                break;
        }

        if (null == name) {
            return null;
        }

        obj.put("name", name);
        obj.put("form", 3);
        obj.put("color", "#e60012");
        obj.put("bordercolor", "#e60012");
        obj.put("bgcolor", "#ffffff");

        return obj;
    }

    /**
     * 有店铺ID获取店铺活动信息（最多展示3个）
     */
    private void getActivitiesByMerchantId(JSONObject datas, String merchantId, JSONArray activities) {
        if (null == activities || activities.size() == 0) {
            return;
        }
        Integer totalNum = activities.size();

        JSONArray resArray = new JSONArray();
        for (int i = 0, len = activities.size(); i < len; i++) {
            if (i > 3) {
                // 最多展示3个店铺活动
                break;
            }
            JSONObject act = activities.getJSONObject(i);
            JSONObject res = new JSONObject();
            res.put("title", act.getString("desc"));
            res.put("url", mUrl + "/campaign/index/" + act.getString("activityId"));
            res.put("campSeq", act.getString("activityId"));
            // 设置标签类型
            JSONObject typeTag = getActivityModelByType(act.getIntValue("favorType"), false);
            res.put("type_tags", new JSONArray());
            if (null != typeTag)
                res.getJSONArray("type_tags").add(typeTag);

            resArray.add(res);
        }

        datas.put("activities", resArray);
        datas.put("activitySize", totalNum);

    }

    @HystrixCommand
    public String listCoupons(String memGuid, Integer pageIndex, int i, String merchantId) {

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
        JSONObject formData = new JSONObject();
        formData.put("merchantId", merchantId);
        formData.put("memGuid", memGuid);
        formData.put("pageNo", pageIndex);
        formData.put("pageSize", 10);

        param.add("data", formData.toJSONString());
        String listResString = restTemplate.postForObject(queryCouponInfoByFavoriteApi, param, String.class);

        JSONObject datas = JSONObject.parseObject(listResString).getJSONObject("data");
        // 接口拿到的卡券信息
        JSONArray resCouponList = datas.getJSONArray("couponList");

        JSONObject joResult = new JSONObject();// 返回数据
        JSONArray couponList = new JSONArray();
        joResult.put("code", 200);
        joResult.put("couponList", couponList);
        joResult.put("totalRows", datas.getInteger("totalRows"));
        joResult.put("totalPage", datas.getInteger("totalPage"));
        JSONObject coupon = new JSONObject();
        JSONObject resCoupon = new JSONObject();
        if (null != resCouponList && resCouponList.size() > 0)
            for (Object obj : resCouponList) {
                resCoupon = (JSONObject) obj;

                boolean isGlobal = false;
                boolean isProxy = false;
                String merchantType = resCoupon.getString("merchantType");
                if (null != merchantType && merchantType.indexOf(CouponUtils.MERCHANT_TYPE_ACROSS) >= 0) {
                    // 判断环球购券
                    isGlobal = true;
                }
                if (null != merchantType && merchantType.indexOf(CouponUtils.MERCHANT_TYPE_PROXY) >= 0) {
                    // 判断代运营券
                    isProxy = true;
                }
                // 转化为API通用的卡券类型，进行处理
                String apiCouponType = CouponUtils.transformCenterCouponType(
                        resCoupon.getString("couponType"), isGlobal, isProxy);

                coupon = new JSONObject();
                coupon.put("id", resCoupon.get("couponId"));
                coupon.put("title", CouponUtils.getViewCouponName(apiCouponType));
                coupon.put("voucherType", resCoupon.get("couponType"));
                //券的总共还可以领取的券数量
                int canCouponReceiveCount = resCoupon.getIntValue("canCouponReceiveCount");
                //券的当天还可以领取的券数量
                int canTodayCouponReceiveCount = resCoupon.getIntValue("canTodayCouponReceiveCount");

                //用户还可以领取的券数量
                int userCanReceiveCouponCount = resCoupon.getIntValue("canUserReceiveCouponCount");
                //用户当天还可以领取的券数量
                int userDayRemainCount = resCoupon.getIntValue("userDayRemainCount");
                //用户可领取领取券总数量
                int userReceiveCouponCount = resCoupon.getIntValue("userReceiveCouponCount");
                int status;
                String[] split = mStaticUrl.split(",");
                //已领完
                if (canCouponReceiveCount <= 0 || canTodayCouponReceiveCount <= 0) {
                    status = -1;
                } else if (userCanReceiveCouponCount <= 0 || userDayRemainCount <= 0 || userReceiveCouponCount <= 0) {
                    //已领取
                    status = 1;
                } else {
                    //未领取
                    status = 0;
                    coupon.put("picUrl", split[new Random().nextInt(split.length)] + "/assets/images/my/member/lq.png");
                }

                coupon.put("status", status);
                String useStartTime = resCoupon.getString("useStartTime");
                String useEndTime = resCoupon.getString("useEndTime");

                coupon.put("validTime", useStartTime + "~" + useEndTime);
                coupon.put("threshold",
                        CouponUtils.getThreshold(apiCouponType, resCoupon.getString("dkjMan"), 508));
                coupon.put("source", 1);
                coupon.put("amount", resCoupon.getString("dkjJian"));
                coupon.put("voucherColor", "#FEB039");
                coupon.put("scope_description",
                        null != resCoupon.get("scopeDescription") ? resCoupon.getString("scopeDescription")
                                : "");// 适用范围描述
                coupon.put("slogan", null != resCoupon.get("promotion") ? resCoupon.getString("promotion")
                        : "");// 促销口号
                coupon.put("display_status",
                        null != resCoupon.get("displayStatus") ? resCoupon.getString("displayStatus") : "");

                couponList.add(coupon);
            }

        return joResult.toJSONString();
    }

    @HystrixCommand
    public String categoryStores(String memGuid, String areaCode) {
        try {
            String category = restTemplate.getForObject(favoriteBasicsUrl
                    + "/favorite/category?type=1&active=true&activity_qd=1&area_code={areaCode}"
                    + "&mem_guid=" + memGuid, String.class, areaCode);
            JSONObject parseObject = JSONObject.parseObject(category);
            JSONArray actcates = parseObject.getJSONObject("data").getJSONArray("actcates");
            int hasStoresNum = hasStoresNum(memGuid);
            JSONObject actcate = new JSONObject();
            actcate.put("kindName", "全部");
            actcate.put("favoriteCount", hasStoresNum);
            actcate.put("cateType", -1);
            actcate.put("kindId", "");
            actcates.add(0, actcate);
            JSONObject js = new JSONObject();
            js.put("code", 200);
            js.put("actcates", actcates);
            return js.toJSONString();
        } catch (Exception e) {
            log.error("店铺收藏类别信息查询出错", e);
            return "{\"code\":\"505\"}";
        }
    }

}
