package com.feiniu.member.controller.favorite;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.controller.common.CommonController;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.favorite.FavoriteAPIService;
import com.feiniu.member.util.PageUtil;
import com.feiniu.member.util.PicRandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 收藏夹控制层
 */
@Controller
@RequestMapping(value = "favorite")
public class FavoriteController extends CommonController {

    private static final CustomLog log = CustomLog.getLogger(FavoriteController.class);

    @Value("${imgInside.url}")
    private String imgInside;

    @Autowired
    protected FavoriteAPIService favoriteAPIService;

    /**
     * 店铺收藏列表
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "storesFavorite")
    public ModelAndView storeslist(HttpServletRequest request) {
        ModelAndView mav = getModel(request, "favorite/store_list");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        String basePath = "";
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        String areaCode = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("C_dist_area")) {
                areaCode = c.getValue();
                String[] split = areaCode.split("_");
                JSONObject js = new JSONObject();
                js.put("provinceCode", split[0]);
                js.put("cityCode", split[1]);
                js.put("areaCode", split[2]);
                areaCode = js.toJSONString();
                break;
            }
        }
        String url = basePath + "favorite/favPageTurn";
        // 获取店铺收藏数据
        String hasStoresView = favoriteAPIService.hasStoresView(memGuid, areaCode, 10, 0);
        JSONObject hasStoresViewInfoReslut = JSONObject.parseObject(hasStoresView);
        if (hasStoresViewInfoReslut.getInteger("code") != 1) {
            log.error("查询店铺收藏接口报错" + hasStoresViewInfoReslut.getInteger("code"));
            return mav;
        }
        // 获取店铺收藏数量
        Integer hasStoresNum = favoriteAPIService.hasStoresNum(memGuid);
        // 收藏店铺收藏类别
        String hasStoresType = favoriteAPIService.hasStoresType(memGuid, areaCode);

        mav.addObject("category", JSONObject.parseObject(hasStoresType));
        mav.addObject("memHasCommentCount", hasStoresViewInfoReslut);
        Map<String, Object> beforeMonthMap = getFavoritePage(1, 10, url, hasStoresNum);
        mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
        mav.addObject("imgInside", PicRandomUtil.random(imgInside));
        return mav;

    }

    /**
     * 商品收藏列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "prodsFavorite")
    public ModelAndView prodslist(HttpServletRequest request) {
        ModelAndView mav = getModel(request, "favorite/prods_list");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        String basePath = "";
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        String areaCode = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("C_dist_area")) {
                areaCode = c.getValue();
                String[] split = areaCode.split("_");
                JSONObject js = new JSONObject();
                js.put("provinceCode", split[0]);
                js.put("cityCode", split[1]);
                js.put("areaCode", split[2]);
                areaCode = js.toJSONString();
                break;
            }
        }
        String url = basePath + "favorite/favPageTurn";
        // 获取商品收藏数据
        String hasProdsView = favoriteAPIService.hasProdsView(memGuid, areaCode, 10, 0, 1);
        JSONObject hasProdsViewInfoReslut = JSONObject.parseObject(hasProdsView);
        if (hasProdsViewInfoReslut.getInteger("code") != 1) {
            log.error("查询商品收藏接口报错" + hasProdsViewInfoReslut.getInteger("code"));
            return mav;
        }
        // 获取商品收藏数量
        Integer hasProdsNum = favoriteAPIService.hasProdsNum(memGuid);
        // 获取商品收藏类别
        String hasStoresType = favoriteAPIService.hasProdsType(memGuid, areaCode);
        JSONArray data = hasProdsViewInfoReslut.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            boolean off = jsonObject.getBooleanValue("off");
            if (!off) {
                jsonObject.put("price", jsonObject.getBigDecimal("price"));
            }
        }
        mav.addObject("category", JSONObject.parseObject(hasStoresType));
        mav.addObject("memHasCommentCount", hasProdsViewInfoReslut);
        Map<String, Object> beforeMonthMap = getFavoritePage(1, 10, url, hasProdsNum);
        mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
        mav.addObject("imgInside", PicRandomUtil.random(imgInside));
        return mav;

    }

    /**
     * 删除收藏信息
     *
     * @param request
     * @param fIds
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "deleteFavorite")
    @ResponseBody
    public String deleteFavoriteByIds(HttpServletRequest request, @RequestParam("fIds") String fIds) {
        Cookie[] cookies = request.getCookies();
        String memGuid = "";
        String loginCookie = "";
        for (Cookie c : cookies) {
            if (c.getName().equals(loginCookieName)) {
                loginCookie = c.getValue();
                break;
            }
        }
        String userInfo = restTemplate.postForObject(userApi + "?cookie=" + loginCookie, null, String.class);
        JSONObject userInfoJson = JSONObject.parseObject(userInfo);
        String json = userInfoJson.getString("data");
        JSONObject userJson = JSONObject.parseObject(json);
        memGuid = userJson.getString("MEM_GUID");
        String deleteFavorite = favoriteAPIService.deleteFavorite(memGuid, fIds);
        return deleteFavorite;
    }

    @RequestMapping(method = RequestMethod.POST, value = "favPageTurn")
    public ModelAndView prodsListAll(HttpServletRequest request, @RequestParam(value = "type") String type,
                                     @RequestParam("pageno") Integer pageNo,
                                     @RequestParam(value = "ids", defaultValue = "") String ids,
                                     @RequestParam(value = "kindId", defaultValue = "") String kindId) {
        ModelAndView mav = new ModelAndView();
        String memGuid = "";
        String basePath = "";
        String areaCode = "";
        if ("prod".equals(type)) {
            mav = getModel(request, "favorite/plist");
        } else {
            mav = getModel(request, "favorite/slist");
        }
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("C_dist_area")) {
                areaCode = c.getValue();
                String[] split = areaCode.split("_");
                JSONObject js = new JSONObject();
                js.put("provinceCode", split[0]);
                js.put("cityCode", split[1]);
                js.put("areaCode", split[2]);
                areaCode = js.toJSONString();
                break;
            }
        }
        Integer offset = (pageNo - 1) * 10;
        String ajaxListAll = favoriteAPIService.ajaxListViewAll(memGuid, areaCode, 10, offset, type, ids,
                kindId);
        Integer ajaxListNumAll = favoriteAPIService.ajaxListNumAll(memGuid, type, ids, kindId);
        mav.addObject("memHasCommentCount", JSONObject.parseObject(ajaxListAll));
        String url = basePath + "favorite/favPageTurn";
        if (StringUtils.isNotBlank(kindId)) {
            mav.addObject("kindId", kindId);
        }
        if (StringUtils.isNotBlank(ids)) {
            mav.addObject("ids", ids);
        }
        Map<String, Object> beforeMonthMap = getFavoritePage(pageNo, 10, url, ajaxListNumAll);
        mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
        mav.addObject("imgInside", PicRandomUtil.random(imgInside));
        return mav;

    }

    /**
     * 点击类别时异步加载收藏商品或收藏店铺信息
     *
     * @param request
     * @param type
     * @param kindId
     * @param ids
     * @return
     */
    @RequestMapping(value = "propertyFavorite")
    public ModelAndView propertyFavorite(HttpServletRequest request,
                                         @RequestParam(value = "type") String type,
                                         @RequestParam(value = "kindId", defaultValue = "") String kindId,
                                         @RequestParam(value = "ids", defaultValue = "") String ids) {
        ModelAndView mav = new ModelAndView();
        String memGuid = "";
        String basePath = "";
        String areaCode = "";
        if ("prod".equals(type)) {
            mav = getModel(request, "favorite/plist");
        } else {
            mav = getModel(request, "favorite/slist");
        }
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("C_dist_area")) {
                areaCode = c.getValue();
                String[] split = areaCode.split("_");
                JSONObject js = new JSONObject();
                js.put("provinceCode", split[0]);
                js.put("cityCode", split[1]);
                js.put("areaCode", split[2]);
                areaCode = js.toJSONString();
                break;
            }
        }
        String ajaxListAll = favoriteAPIService.ajaxListViewAll(memGuid, areaCode, 10, 0, type, ids, kindId);
        Integer ajaxListNumAll = favoriteAPIService.ajaxListNumAll(memGuid, type, ids, kindId);
        mav.addObject("memHasCommentCount", JSONObject.parseObject(ajaxListAll));
        String url = basePath + "favorite/favPageTurn";
        if (StringUtils.isNotBlank(kindId)) {
            mav.addObject("kindId", kindId);
        }
        if (StringUtils.isNotBlank(ids)) {
            mav.addObject("ids", ids);
        }
        Map<String, Object> beforeMonthMap = getFavoritePage(1, 10, url, ajaxListNumAll);
        mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
        mav.addObject("imgInside", PicRandomUtil.random(imgInside));
        return mav;

    }

    /**
     * 店铺或商品异步加载类别及促销信息
     *
     * @param request
     * @param type
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "property")
    public ModelAndView propertyType(HttpServletRequest request, @RequestParam(value = "fType") String type) {
        ModelAndView mav = new ModelAndView();
        String memGuid = "";
        String areaCode = "";
        if ("prod".equals(type)) {
            mav = getModel(request, "favorite/pylist");
        } else {
            mav = getModel(request, "favorite/sylist");
        }
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("C_dist_area")) {
                areaCode = c.getValue();
                String[] split = areaCode.split("_");
                JSONObject js = new JSONObject();
                js.put("provinceCode", split[0]);
                js.put("cityCode", split[1]);
                js.put("areaCode", split[2]);
                areaCode = js.toJSONString();
                break;
            }
        }
        String propertyType = favoriteAPIService.propertyType(memGuid, areaCode, type);
        mav.addObject("category", JSONObject.parseObject(propertyType));
        return mav;

    }

    /**
     * 查询店铺优惠券信息
     */
    @RequestMapping(method = RequestMethod.GET, value = "findCouponcs")
    @ResponseBody
    public String findCouponsForFavorite(@RequestParam("storeId") String storeId) {
        return favoriteAPIService.findCouponForFavorite(storeId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "findActivitys")
    @ResponseBody
    public String findActivitysForFavorite(@RequestParam("storeId") String storeId) {
        return favoriteAPIService.findActivitysForFavorite(storeId);

    }

    public Map<String, Object> getFavoritePage(Integer pageNo, Integer pageSize, String url,
                                               Integer totalSum) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Double totalPage = Math.ceil(totalSum.doubleValue() / Double.valueOf(pageSize));
        Integer total = totalPage.intValue();
        if (pageNo > total) {
            pageNo = total;
        }
        PageUtil pagedata = new PageUtil(pageNo, totalSum, pageSize, url);
        returnMap.put("pageData", paging(pagedata));
        return returnMap;
    }

    public Map<String, Object> paging(PageUtil data) {
        Map<String, Object> pageMap = new HashMap<String, Object>();
        if (data.getPageSize() != 0 && data.getTotalSum() > 0) {
            if (data.getPageNo() == null) {
                data.setPageNo(1);
            }
            if (data.getPageSize() == null) {
                data.setPageSize(10);
            }
            Double totalPage = Math.ceil(data.getTotalSum().doubleValue() / data.getPageSize().doubleValue());
            data.setTotalPage(totalPage.intValue());

            if (data.getPageNo() > totalPage.intValue()) {
                data.setPageNo(totalPage.intValue());
            }
            if (data.getPageNo() == 1) {
                pageMap.put("fn_prve", "fn_prve off");
            } else {
                pageMap.put("fn_prve", "fn_prve");
            }

            if (data.getPageNo() > 1) {
                if (data.getUrl().contains("id")) {
                    pageMap.put("pre_href", data.getUrl() + "&pageno=" + (data.getPageNo() - 1));

                } else if (data.getUrl().contains("actId")) {

                    pageMap.put("pre_href", data.getUrl() + "&pageno=" + (data.getPageNo() - 1));
                } else {
                    pageMap.put("pre_href", data.getUrl() + "?pageno=" + (data.getPageNo() - 1));
                }
            } else {
                pageMap.put("pre_href", "javascript:void(0);");
            }

            pageMap.put("pageNo", data.getPageNo());
            pageMap.put("totalpage", data.getTotalPage());

            if (data.getPageNo() >= data.getTotalPage()) {
                pageMap.put("fn_next", "fn_next off");
            } else {
                pageMap.put("fn_next", "fn_next");
            }
            if (data.getPageNo() < data.getTotalPage()) {
                if (data.getUrl().contains("id")) {
                    pageMap.put("next_href", data.getUrl() + "&pageno=" + (data.getPageNo() + 1));

                } else if (data.getUrl().contains("actId")) {

                    pageMap.put("next_href", data.getUrl() + "&pageno=" + (data.getPageNo() + 1));

                } else {
                    pageMap.put("next_href", data.getUrl() + "?pageno=" + (data.getPageNo() + 1));
                }
            } else {
                pageMap.put("next_href", "javascript:void(0);");
            }
            pageMap.put("goUrl", data.getUrl());
        }
        return pageMap;
    }
}
