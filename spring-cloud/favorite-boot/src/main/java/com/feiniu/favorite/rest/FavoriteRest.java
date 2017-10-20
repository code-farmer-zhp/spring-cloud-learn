package com.feiniu.favorite.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.favorite.dto.Code;
import com.feiniu.favorite.dto.Result;
import com.feiniu.favorite.service.FavoriteService;
import com.feiniu.favorite.service.KafKaService;
import com.feiniu.favorite.service.impl.PriceAndArrivalService;
import com.feiniu.favorite.vo.QueryVo;
import com.fn.cache.client.RedisCacheClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Path("/rest/v1")
public class FavoriteRest {

    private static final Log log = LogFactory.getLog(FavoriteRest.class);

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private KafKaService kafKaService;

    @Autowired
    private RedisCacheClient redisCacheClient;

    @Autowired
    private PriceAndArrivalService priceAndArrivalService;

    /**
     * 添加批量收藏商品
     */
    @POST
    @Path("/{mem_guid}/favoriteAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Result addAll(@PathParam("mem_guid") String memGuid, @FormParam("params") String params) {
        if (StringUtils.isBlank(memGuid)) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数mem_guid不允许为空!");
        }
        JSONArray dataJsonArray = JSONObject.parseArray(params);
        if (dataJsonArray == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数params不允许为空！");
        }
        int total = dataJsonArray.size();
        int alreadyCollect = 0;// 已收藏品数量
        int successCollect = 0;// 成功收藏数量
        int failCollect = 0;// 失败收藏数量
        try {
            // 执行删除redis缓存,在更新数据库之后
            redisCacheClient.del(memGuid);
            if (total > 0) {
                for (Object object : dataJsonArray) {
                    JSONObject dataJsonObj = (JSONObject) object;
                    //多地多仓商品唯一表示skuSeq
                    String favoriteSkuId = dataJsonObj.getString("favoriteSkuId");
                    //0:自营商品    1:商城商品    2:店铺
                    Integer type = dataJsonObj.getIntValue("type");
                    //是否多规格商品
                    Integer seqKind = dataJsonObj.getInteger("seqKind");
                    String areaCode = dataJsonObj.getString("areaCode");
                    //是否跨境商品
                    Integer isCrossborder = dataJsonObj.getIntValue("isCrossborder");
                    //收藏价
                    String price = dataJsonObj.getString("price");
                    //来源渠道
                    String channel = dataJsonObj.getString("channel");
                    if (StringUtils.isBlank(favoriteSkuId)) {
                        return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数favoriteSkuId不允许为空！");
                    }
                    if (type != 0 && type != 1 && type != 2) {
                        return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数type错误！");
                    }
                    Result result = favoriteService.add(memGuid, favoriteSkuId, type, seqKind, areaCode,
                            isCrossborder, price, channel);
                    // 商品则调用异步批量推送kafka
                    if (result.getCode() == Code.RESULT_STATUS_SUCCESS) {
                        successCollect++;
                        Map<String, Object> smseqs = new HashMap<String, Object>();
                        smseqs.put("memGuid", memGuid);
                        smseqs.put("smSeq", favoriteSkuId);
                        smseqs.put("smPrice", price);
                        smseqs.put("action", "add");
                        smseqs.put("time", System.currentTimeMillis());
                        kafKaService.pushFavoriteData(smseqs);
                    } else if (result.getCode() == Code.RESULT_STATUS_CALL_API_EXCEPTION) {
                        failCollect++;
                    } else if (result.getCode() == Code.RESULT_STATUS_DUPLICATE_FAVORITE) {
                        alreadyCollect++;
                    } else if (result.getCode() == Code.RESULT_STATUS_FAVORITE_COUNT_EXCEED) {
                        return result;
                    }
                }
                if (successCollect > 0) {
                    if (total == successCollect) {
                        return new Result(Code.RESULT_STATUS_SUCCESS, "成功移入收藏夹!");
                    } else {
                        return new Result(Code.RESULT_STATUS_SUCCESS_TWO, "成功移入收藏夹!");
                    }
                } else if (total == alreadyCollect) {
                    return new Result(Code.RESULT_STATUS_DUPLICATE_FAVORITE_ALL, "您已收藏过这些商品!");
                } else if (total == failCollect) {
                    return new Result(Code.RESULT_STATUS_CALL_API_EXCEPTION, "收藏失败,请稍后再试!");
                } else if (total == (alreadyCollect + failCollect)) {
                    return new Result(Code.RESULT_STATUS_DUPLICATE_FAVORITE, "您已收藏过这些商品!");
                } else {
                    return new Result(Code.RESULT_STATUS_SUCCESS, "成功移入收藏夹!");
                }
            } else {
                return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数错误！");
            }
        } catch (Exception e) {
            log.error("批量加入收藏夹失败！", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "批量加入收藏夹失败！");
        }
    }

    /**
     * 非批量收藏商品或店铺
     */
    @POST
    @Path("/{mem_guid}/favorite")
    @Produces(MediaType.APPLICATION_JSON)
    public Result add(@PathParam("mem_guid") String memGuid, @FormParam("favorite_skuId") String favoriteSkuId,
                      @FormParam("type") Integer type, @FormParam("seq_kind") Integer seqKind,
                      @FormParam("area_code") String areaCode, @FormParam("is_crossborder") Integer isCrossborder,
                      @FormParam("price") String price, @FormParam("channel") String channel) {

        if (StringUtils.isBlank(memGuid)) {
            return new Result(Code.RESULT_STATUS_MEM_GUID_NOT_EXISTS, "参数mem_guid不允许为空！");
        }

        if (StringUtils.isBlank(favoriteSkuId)) {
            return new Result(Code.RESULT_STATUS_FAVORITE_ID_NOT_EXISTS, "参数favoriteSkuId不允许为空！");
        }

        if (type == null || (type != 0 && type != 1 && type != 2)) {
            return new Result(Code.RESULT_STATUS_TYPE_NOT_EXISTS, "参数type错误！");
        }
        try {
            // 执行删除redis缓存
            redisCacheClient.del(memGuid);
            Result result = favoriteService.add(memGuid, favoriteSkuId, type, seqKind, areaCode, isCrossborder,
                    price, channel);
            // 商品则调用异步批量推送kafka
            if (result.getCode() == Code.RESULT_STATUS_SUCCESS && (type == 0 || type == 1)) {
                Map<String, Object> smseqs = new HashMap<String, Object>();
                smseqs.put("memGuid", memGuid);
                smseqs.put("smSeq", favoriteSkuId);
                smseqs.put("smPrice", price);
                smseqs.put("action", "add");
                smseqs.put("time", System.currentTimeMillis());
                kafKaService.pushFavoriteData(smseqs);
            }
            return result;
        } catch (Exception e) {
            log.error("加入收藏夹失败！", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "加入收藏夹失败！");
        }
    }

    /**
     * 收藏夹类别获取
     */
    @GET
    @Path("/favorite/category")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getCategory(@BeanParam QueryVo vo) {
        if (vo == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数不允许为空！");
        }
        if (StringUtils.isBlank(vo.getMemGuid())) {
            return new Result(Code.RESULT_STATUS_MEM_GUID_NOT_EXISTS, "用户mem_guid不能为空！");
        }
        // 0:商品,1：商场店铺
        if (vo.getType() == null) {
            return new Result(Code.RESULT_STATUS_TYPE_NOT_EXISTS, "收藏夹类型不允许为空！");
        }
        if (vo.getActive() == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "收藏夹参数active不允许为空！");
        }
        if ((vo.getType() == 0) && StringUtils.isBlank(vo.getAreaCode())) {
            return new Result(Code.RESULT_STATUS_AREACODE_NOT_EXISTS, "参数area_code不允许为空！");
        }
        if (StringUtils.isBlank(vo.getActivityQd())
                || (!"1".equals(vo.getActivityQd()) && !"2".equals(vo.getActivityQd()) && !"3".equals(vo
                .getActivityQd()))) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数 activity_qd错误");
        }
        try {
            return favoriteService.queryCategory(vo);
        } catch (Exception e) {
            log.error("收藏夹类别获取错误！", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "收藏夹类别获取错误！");
        }
    }

    /**
     * 查询收藏夹商品或店铺，一次最多查询20条数据
     */
    @GET
    @Path("/favorite")
    @Produces(MediaType.APPLICATION_JSON)
    public Result query(@BeanParam QueryVo vo) {
        if (vo == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数不允许为空！");
        }
        if (StringUtils.isBlank(vo.getMemGuid())) {
            return new Result(Code.RESULT_STATUS_MEM_GUID_NOT_EXISTS, "用户mem_guid不能为空！");
        }
        // 0:商品，1：商场店铺
        if (vo.getType() == null) {
            return new Result(Code.RESULT_STATUS_TYPE_NOT_EXISTS, "收藏夹类型不允许为空！");
        }
        if (vo.getActive() == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "收藏夹参数active不允许为空！");
        }
        if ((vo.getType() == 0) && StringUtils.isBlank(vo.getAreaCode())) {
            return new Result(Code.RESULT_STATUS_AREACODE_NOT_EXISTS, "参数area_code不允许为空！");
        }
        if (StringUtils.isBlank(vo.getActivityQd())
                || (!"1".equals(vo.getActivityQd()) && !"2".equals(vo.getActivityQd()) && !"3".equals(vo
                .getActivityQd()))) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数activity_qd错误");
        }
        if (vo.getIsCrossborder() == null) {
            vo.setIsCrossborder(1); // 设置默认 查处所有的
        }
        // 0:显示所有店铺 , 1:只显示有新品的店铺, 默认显示所有的店铺
        if (vo.getNewListShop() == null) {
            vo.setNewListShop(0);
        }
        try {
            return favoriteService.queryAsyncLoadService(vo);
        } catch (Exception e) {
            log.error("查询收藏夹商品或店铺错误！", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "查询收藏夹商品或店铺错误！");
        }

    }

    /**
     * 根据用户guid查询收藏夹商品或店铺的ID列表
     */
    @GET
    @Path("/favorite/IdList")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getIdList(@BeanParam QueryVo vo) {
        if (vo == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数不允许为空！");
        }
        if (StringUtils.isBlank(vo.getMemGuid())) {
            return new Result(Code.RESULT_STATUS_MEM_GUID_NOT_EXISTS, "参数mem_guid不允许为空！");
        }
        // 0:自营商品 1:商城商品 2:店铺
        if (vo.getType() == null || (vo.getType() != 0 && vo.getType() != 1 && vo.getType() != 2)) {
            return new Result(Code.RESULT_STATUS_TYPE_NOT_EXISTS, "参数type错误！");
        }
        try {
            return favoriteService.getIdList(vo);
        } catch (Exception e) {
            log.error("查询收藏夹商品或店铺的ID列表错误!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "查询收藏夹商品或店铺的ID列表错误!");
        }

    }

    /**
     * app端删除收藏夹商品或店铺
     */
    @DELETE
    @Path("/{mem_guid}/favorite/{favorite_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result delete(@PathParam("mem_guid") String memGuid, @PathParam("favorite_id") String favoriteId) {

        if (StringUtils.isBlank(memGuid)) {
            return new Result(Code.RESULT_STATUS_MEM_GUID_NOT_EXISTS, "参数mem_guid不允许为空！");
        }
        if (StringUtils.isBlank(favoriteId)) {
            return new Result(Code.RESULT_STATUS_FAVORITE_ID_NOT_EXISTS, "参数favorite_id不允许为空！");
        }
        try {
            return favoriteService.deleteById(memGuid, favoriteId);
        } catch (Exception e) {
            log.error("删除收藏夹错误!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "删除收藏夹错误!");
        }
    }

    /**
     * 根据商品sku删除收藏
     */
    @DELETE
    @Path("/deleteBySku/{mem_guid}/favorite/{sku}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result deleteBySku(@PathParam("mem_guid") String memGuid, @PathParam("sku") String sku) {
        if (StringUtils.isBlank(memGuid)) {
            return new Result(Code.RESULT_STATUS_MEM_GUID_NOT_EXISTS, "参数mem_guid不允许为空！");
        }
        if (StringUtils.isBlank(sku)) {
            return new Result(Code.RESULT_STATUS_FAVORITE_ID_NOT_EXISTS, "参数sku不允许为空！");
        }
        try {
            return favoriteService.deleteBySku(memGuid, sku);
        } catch (Exception e) {
            log.error("根据商品sku删除收藏错误!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "删除收藏夹错误!");
        }
    }

    /**
     * pc端删除收藏夹商品或店铺
     */
    @GET
    @Path("/{mem_guid}/{favorite_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result deleteFavorite(@PathParam("mem_guid") String memGuid,
                                 @PathParam("favorite_id") String favoriteId) {

        if (StringUtils.isBlank(memGuid)) {
            return new Result(Code.RESULT_STATUS_MEM_GUID_NOT_EXISTS, "参数'mem_guid'不允许为空！");
        }
        if (StringUtils.isBlank(favoriteId)) {
            return new Result(Code.RESULT_STATUS_FAVORITE_ID_NOT_EXISTS, "参数'favorite_id'不允许为空！");
        }
        try {
            return favoriteService.deleteById(memGuid, favoriteId);
        } catch (Exception e) {
            log.error("删除收藏夹错误!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "删除收藏夹错误!");
        }

    }

    /**
     * 根据用户id清空收藏夹（逻辑删除）
     */
    @DELETE
    @Path("/favorite/empty/{mem_guid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Result deleteAllByMemGuid(@PathParam("mem_guid") String memGuid) {

        if (StringUtils.isBlank(memGuid)) {
            return new Result(Code.RESULT_STATUS_MEM_GUID_NOT_EXISTS, "参数'mem_guid'不允许为空！");
        }
        try {
            // 执行删除redis缓存
            redisCacheClient.del(memGuid);
            List<String> smskuqs = kafKaService.getSmseqsByMemGuid(memGuid);
            Result result = favoriteService.deleteAllByMemGuid(memGuid);
            // 商品则调用异步批量推送kafka
            if (result.getCode() == Code.RESULT_STATUS_SUCCESS && smskuqs.size() > 0) {
                for (String string : smskuqs) {
                    Map<String, Object> smseq = new HashMap<String, Object>();
                    smseq.put("memGuid", memGuid);
                    smseq.put("smSeq", string);
                    smseq.put("action", "delete");
                    smseq.put("time", System.currentTimeMillis());
                    kafKaService.pushFavoriteData(smseq);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("清空收藏夹错误!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "清空收藏夹错误!");
        }
    }

    /**
     * 查询收藏夹中商品或店铺数量
     */
    @GET
    @Path("/favorite/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Result count(@BeanParam QueryVo vo) {
        if (vo == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数不允许为空！");
        }
        try {
            return favoriteService.count(vo);
        } catch (Exception e) {
            log.error("查询收藏夹中商品或店铺数量错误!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "查询收藏夹中商品或店铺数量错误!");
        }
    }

    /**
     * 判断某用户，是否收藏商品（自营、商城），店铺（商城）
     */
    @GET
    @Path("/favorite/have_collect_goods_or_shop")
    @Produces(MediaType.APPLICATION_JSON)
    public Result haveCollectGoodsOrShop(@BeanParam QueryVo vo) {
        if (vo == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数不允许为空！");
        }
        if (StringUtils.isBlank(vo.getMemGuid())) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数 mem_guid不能为空");
        }
        if (StringUtils.isBlank(vo.getFavoriteSku())) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数 favorite_sku不能为空");
        }
        // type 0表示自营的商品 1标识商城的商品 2 标识店铺
        if (vo.getType() == null || (vo.getType() != 0 && vo.getType() != 1 && vo.getType() != 2)) {
            return new Result(Code.RESULT_STATUS_TYPE_NOT_EXISTS, "参数type错误！");
        }
        try {
            return favoriteService.haveCollectGoodsOrShop(vo);
        } catch (Exception e) {
            log.error("判断某用户，是否收藏商品错误!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "判断某用户，是否收藏商品错误!");
        }
    }

    /**
     * 异步推送当前收藏夹中所有有效的商品信息到kafka中
     */
    @GET
    @Path("/favorite/pushAllFavoriteData")
    @Produces(MediaType.APPLICATION_JSON)
    public Result pushAllFavoriteData() {
        kafKaService.pushAllFavoriteData();
        return new Result(Code.RESULT_STATUS_SUCCESS, "推送当前所有商品收藏信息已启动。。。。");
    }

    /**
     * STORY #6183 店铺装修需要批量查询收藏量接口
     */
    @POST
    @Path("/favorite/getFavoriteNumsBySkuIds")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getFavoriteNumsBySkuIds(@FormParam("data") String data) {
        JSONObject dataObj = JSONObject.parseObject(data);
        try {
            if (dataObj != null) {
                String skuIds = dataObj.getString("skuIds");
                if (StringUtils.isEmpty(skuIds)) {
                    return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数 skuIds不能为空");
                } else {
                    return favoriteService.countBySkuIds(skuIds);
                }
            } else {
                return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数 data不能为空");
            }
        } catch (Exception e) {
            log.error("批量查询收藏量错误!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "批量查询收藏量错误!");
        }
    }

    /**
     * 查询商家数据报表里的收藏量
     */
    @GET
    @Path("/favorite/MRcount")
    @Produces(MediaType.APPLICATION_JSON)
    public Result MRcount(@BeanParam QueryVo vo) {
        if (vo == null) {
            return new Result(Code.RESULT_STATUS_PARAMETER_ERROR, "参数不允许为空！");
        }
        try {
            return favoriteService.count(vo);
        } catch (Exception e) {
            log.error("查询收藏量失败!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "查询收藏量失败!");
        }
    }

    /**
     * 清洗店铺MerchanId数据
     */
    @GET
    @Path("/upMerchanId")
    @Produces(MediaType.TEXT_PLAIN)
    public String upMerchanId(@BeanParam QueryVo vo) {
        try {
            favoriteService.upMerchanId();
        } catch (Exception e) {
            return "更新失败！";
        }
        return "更新成功！";

    }

    /**
     * 根据memGuid查询200条收藏数据
     */
    @GET
    @Path("/getFavoriteId")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getFavoriteId(@QueryParam("memGuid") String memGuid,
                                @QueryParam("pageSize") Integer pageSize, @QueryParam("pageIndex") Integer pageIndex) {
        try {
            List<String> goodIds = favoriteService.getGoodIdsByMemGuid(memGuid, pageSize, pageIndex);
            return new Result(Code.RESULT_STATUS_SUCCESS, goodIds, "查询收藏量成功!");
        } catch (Exception e) {
            log.error("根据memGuid查询200条收藏数据！", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "查询收藏量失败!");
        }

    }

    /**
     * 查询收藏商品的有货和降价
     */
    @GET
    @Path("/getPriceAndArrival")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getPriceAndArrival(@QueryParam("memGuid") String memGuid,
                                     @QueryParam("provinceCode") String provinceCode) {
        try {
            return priceAndArrivalService.getPriceAndArrival(memGuid, provinceCode);
        } catch (Exception e) {
            log.error("查询收藏商品的有货和降价失败!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "查询收藏商品的有货和降价失败!");
        }

    }

    /**
     * 根据类别和数据库唯一自增id查询收藏列表数据
     */
    @POST
    @Path("/getFavoriteByIdsAndKindId")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getFavoriteByIdsAndKindsIds(@FormParam("memGuid") String memGuid,
                                              @FormParam("ids") String ids, @FormParam("kindId") String kindId,
                                              @FormParam("pageSize") Integer pageSize, @FormParam("pageIndex") Integer pageIndex,
                                              @FormParam("areaCode") String areaCode, @FormParam("activityQd") String activityQd) {
        try {
            return favoriteService.getFavoriteByIdsAndKindsIds(memGuid, ids, kindId, pageSize, pageIndex,
                    areaCode, activityQd);
        } catch (Exception e) {
            log.error("根据列别和唯一键查询收藏夹列表失败!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "根据列别和唯一键查询收藏夹列表失败!");
        }

    }

    /**
     * 根据guid和skuid查询收藏列表数据
     */
    @POST
    @Path("/getFavoritePriceBySkuIds")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getFavoritePriceBySkuIds(@FormParam("memGuid") String memGuid,
                                           @FormParam("skuIds") String skuIds, @FormParam("areaCode") String areaCode,
                                           @FormParam("activityQd") String activityQd) {
        try {
            return favoriteService.getFavoritePriceBySkuIds(memGuid, skuIds, areaCode, activityQd);
        } catch (Exception e) {
            log.error("根据卖场id查询收藏价失败!", e);
            return new Result(Code.RESULT_STATUS_EXCEPTION, "根据卖场id查询收藏价失败!");
        }

    }


    /**
     * 清洗商品kindName数据
     */
    @GET
    @Path("/upKindName")
    @Produces(MediaType.TEXT_PLAIN)
    public String upKindName() {
        try {
            favoriteService.upKindName();
        } catch (Exception e) {
            return "更新失败！";
        }
        return "更新成功！";

    }

    /**
     * 多地多仓清洗自营商品
     */
    @GET
    @Path("/upLoad")
    public String upLoad() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String d1 = sdf.format(new Date(System.currentTimeMillis()));
            favoriteService.upLoad();
            String d2 = sdf.format(new Date(System.currentTimeMillis()));
            log.info("清洗成功success" + "开始时间:" + d1 + "结束时间:" + d2);
        } catch (Exception e) {
            log.error(e);
            return "更新失败！";
        }
        return "更新成功！";
    }

    @GET
    @Path("/upLoadAll")
    public String upLoadAll() {
        try {
            favoriteService.upLoadAll();
            return "更新成功！";
        } catch (Exception e) {
            log.error(e);
            return "更新失败！";
        }
    }


    @GET
    @Path("/upLoadError")
    public String upLoadError() {
        try {
            favoriteService.upLoadError();
            return "更新成功！";
        } catch (Exception e) {
            log.error(e);
            return "更新失败！";
        }
    }

    @GET
    @Path("/upLoadBySmSeq")
    public String upLoadBySmSeq() {
        try {
            favoriteService.upLoadBySmSeq();
            return "更新成功！";
        } catch (Exception e) {
            log.error(e);
            return "更新失败！";
        }
    }

    @GET
    @Path("/upLoadBySku")
    public String upLoadBySku(@QueryParam("skuSeq") String skuSeq) {
        try {
            if (StringUtils.isEmpty(skuSeq)) {
                return "skuSeq 为空";
            }
            favoriteService.upLoadBySku(skuSeq);
            return "更新成功！";
        } catch (Exception e) {
            log.error(e);
            return "更新失败！";
        }
    }

    /**
     * 多地多仓清洗商城商品及店铺
     */
    @GET
    @Path("/upLoadType")
    public String upLoadType() {
        try {
            favoriteService.upLoadType();
        } catch (Exception e) {
            return "更新失败！";
        }
        return "更新成功！";

    }

    /**
     * 多地多仓清洗还原
     */
    @GET
    @Path("/returnGo")
    public String returnGo() {
        try {
            favoriteService.returnGo();
        } catch (Exception e) {
            return "还原失败！";
        }
        return "还原成功！";

    }

    /**
     * 多地多仓删除无法匹配的SMSEQ
     */
    @GET
    @Path("/deleteSmSeq")
    public String deleteSmSeq() {
        try {
            favoriteService.deleteSmSeq();
        } catch (Exception e) {
            return "删除失败！";
        }
        return "删除成功！";

    }
}