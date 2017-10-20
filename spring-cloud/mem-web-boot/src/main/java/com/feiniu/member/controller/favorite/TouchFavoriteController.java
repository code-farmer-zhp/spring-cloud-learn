package com.feiniu.member.controller.favorite;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.controller.common.TouchCommonController;
import com.feiniu.member.service.favorite.FavoriteAPIService;
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

/**
 * 收藏夹控制层
 * 
 *
 */
@Controller
@RequestMapping(value = "/touch/favorite")
public class TouchFavoriteController extends TouchCommonController {

    @Value("${m.login.url}")
    private String mLoginUrl;

    @Value("${getCoupon.api}")
    private String getCouponApi;
    
    @Value("${buy.fn}")
    private String buyFn;
 
    
    @Autowired
    private FavoriteAPIService favoriteAPIService;

    @RequestMapping(method = RequestMethod.GET, value = "index")
    public ModelAndView myTouchCommentView(HttpServletRequest request,
                                           @RequestParam(value = "status", defaultValue = "0") String status) {
        ModelAndView mav = getModel(request, "favorite/touch_favorite");
        if (mav == null || mav.getModel().isEmpty()) {
            return new ModelAndView("redirect:" + mLoginUrl);
        }
        if (mav.getViewName().contains("redirect")) {
            return mav;
        }
        //购物车和卡券充值
        mav.addObject("getCouponApi", getCouponApi);
        mav.addObject("buyFn", buyFn);
        return mav;
    }

    /**
     * 商品列表显示
     */
    @RequestMapping(method = RequestMethod.GET, value = "listGoods")
    @ResponseBody
    public String listGoods(HttpServletRequest request,
                            @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                            @RequestParam(value = "cates", defaultValue = "") String cates,
                            @RequestParam(value = "searchContent", defaultValue = "") String searchContent) {
        String memGuid = getGuid(request);
        Cookie[] cookies = request.getCookies();
        String areaCode;
        String provinceCode = "CS000016"; //省编码
        JSONObject js1=new JSONObject();
        js1.put("provinceCode","CS000016");
        js1.put("cityCode", "310100");
        js1.put("areaCode", "310101");
        areaCode=js1.toJSONString();
        for (Cookie c : cookies) {
            if (c.getName().equals("th5_siteid")) {
              if(c.getValue().split("-")[1].equals("0")){
                  break;
               }else{
                   provinceCode = c.getValue().split("-")[0];
                   JSONObject js=new JSONObject();
                   js.put("provinceCode", provinceCode);
                   js.put("cityCode", c.getValue().split("-")[1]);
                   js.put("areaCode", c.getValue().split("-")[2]);
                   areaCode=js.toJSONString();
                   break;
               }
            }
        }
        cates = cates.replace("&amp;quot;","\"");
        // 筛选数据
        JSONArray catesArray = JSONObject.parseArray(cates);
        return favoriteAPIService.listGoods(memGuid, provinceCode, areaCode, pageIndex, 10,
                searchContent, catesArray);
    }
    /**
     * 店铺显示列表
     */
    @RequestMapping(method = RequestMethod.GET, value = "listStores")
    @ResponseBody
    public String list(HttpServletRequest request,
                       @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                       @RequestParam(value = "cates", defaultValue = "") String cates) {
        String memGuid = getGuid(request);
        Cookie[] cookies = request.getCookies();
        //CS000016-310100-310101默认地址
        String areaCode;
        String provinceCode = "CS000016"; //省编码
        JSONObject js1=new JSONObject();
        js1.put("provinceCode","CS000016");
        js1.put("cityCode", "310100");
        js1.put("areaCode", "310101");
        areaCode=js1.toJSONString();
        for (Cookie c : cookies) {
            if (c.getName().equals("th5_siteid")) {
                JSONObject js=new JSONObject();
                js.put("provinceCode", provinceCode);
                js.put("cityCode", c.getValue().split("-")[1]);
                js.put("areaCode", c.getValue().split("-")[2]);
                areaCode=js.toJSONString();
                break;
            }
        }
        cates = cates.replace("&amp;quot;", "\"");
        // 筛选数据
        JSONArray catesArray = JSONObject.parseArray(cates);
        String result = favoriteAPIService.listStores(memGuid, areaCode, pageIndex, 10,
                catesArray);
        return result;
    }
    /**
     * 卡券显示数据
     * @param request
     * @param pageIndex
     * @param merchantId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "coupons")
    @ResponseBody
    public String listCoupons(HttpServletRequest request,
                              @RequestParam(value = "pageIndex", defaultValue = "1") Integer pageIndex,
                              @RequestParam(value = "merchantId") String merchantId) {
        String memGuid = getGuid(request);
        String result = favoriteAPIService.listCoupons(memGuid, pageIndex, 10, merchantId);
        return result;
    }
   

    /**
     * 商品类别查询
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "categoryGoods")
    @ResponseBody
    public String categoryGoods(HttpServletRequest request) {
        String memGuid = getGuid(request);
        // CS000016-310100-310101默认地址
        String provinceCode = "CS000016";// 省编码
        Cookie[] cookies = request.getCookies();
        //CS000016-310100-310101默认地址
        String areaCode;
        JSONObject js1=new JSONObject();
        js1.put("provinceCode","CS000016");
        js1.put("cityCode", "310100");
        js1.put("areaCode", "310101");
        areaCode=js1.toJSONString();
        for (Cookie c : cookies) {
            if (c.getName().equals("th5_siteid")) {
                JSONObject js=new JSONObject();
                js.put("provinceCode", provinceCode);
                js.put("cityCode", c.getValue().split("-")[1]);
                js.put("areaCode", c.getValue().split("-")[2]);
                areaCode=js.toJSONString();
                break;
            }
        }
        return favoriteAPIService.categoryGoods(memGuid, areaCode);
    }
    
     /**
     * 店铺类别查询
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "categoryStores")
    @ResponseBody
    public String categoryStore(HttpServletRequest request) {
        String memGuid = getGuid(request);
        // CS000016-310100-310101默认地址
        String provinceCode = "CS000016";// 省编码
        Cookie[] cookies = request.getCookies();
        
        //CS000016-310100-310101默认地址
        String areaCode;
        JSONObject js1=new JSONObject();
        js1.put("provinceCode","CS000016");
        js1.put("cityCode", "310100");
        js1.put("areaCode", "310101");
        areaCode=js1.toJSONString();
        for (Cookie c : cookies) {
            if (c.getName().equals("th5_siteid")) {
                JSONObject js=new JSONObject();
                js.put("provinceCode", provinceCode);
                js.put("cityCode", c.getValue().split("-")[1]);
                js.put("areaCode", c.getValue().split("-")[2]);
                areaCode=js.toJSONString();
                break;
            }
        }
        String deleteFavorite = favoriteAPIService.categoryStores(memGuid, areaCode);
        return deleteFavorite;
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
        String memGuid = getGuid(request);
        String deleteFavorite = favoriteAPIService.deleteFavorite(memGuid, fIds);
        return deleteFavorite;
    }

    
    

}
