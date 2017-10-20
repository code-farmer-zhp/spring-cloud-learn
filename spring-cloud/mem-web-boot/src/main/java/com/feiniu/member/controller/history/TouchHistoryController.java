package com.feiniu.member.controller.history;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.common.BaseFunction;
import com.feiniu.member.controller.common.TouchCommonController;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.CommonService;
import com.feiniu.member.service.HistoryService;
import com.feiniu.member.service.score.MutilScoreService;
import com.feiniu.member.util.PicRandomUtil;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


@Controller
@RequestMapping(value = "/touch/touchHistory")
public class TouchHistoryController extends TouchCommonController {

    public static final CustomLog log = CustomLog.getLogger(TouchHistoryController.class);

    //图片服务器替换
    @Value("${store.url}")
    private String storeUrl;

    //图片地址
    @Value("${imgInside.url}")
    private String imgInside;

    //触屏登陆
    @Value("${m.login.url}")
    private String mLoginUrl;

    //静态资源
    @Value("${m.staticDomain.url}")
    private String mStaticUrl;

    //变量名－记录列表
    private static String historyList = "historyList";

    //变量名－记录总个数
    private static String historyCount = "count";

    //行销活动标签
    private static String activityList = "activityList";

    @Autowired
    private CommonService commonService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private MutilScoreService mutilScoreService;

    @RequestMapping(value = {"/list"})
    public ModelAndView list(HttpServletRequest request)
            throws Exception {

        //设置打开页面，没有登陆的情况下打开登陆页面
        ModelAndView mav = getModel(request, "history/touchHistoryList");

        if (mav == null || mav.getModel().isEmpty() || mav.getViewName().equals("redirect:" + mLoginUrl)) {
            return new ModelAndView("redirect:" + mLoginUrl + "?gotourl=" + buildGotoUrl(request));
        }

        mav.addObject("imgInside", PicRandomUtil.random(imgInside));
        mav.setViewName("history/touchHistoryList");
        return mav;
    }

    private String buildGotoUrl(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        StringBuilder builder = new StringBuilder();
        builder.append(noProtocol(requestURL));
        Map parameterMap = request.getParameterMap();
        if (parameterMap != null && parameterMap.size() > 0) {
            @SuppressWarnings("unchecked")
            Set<Map.Entry> set = parameterMap.entrySet();
            int i = 0;
            for (Map.Entry entry : set) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (i == 0) {
                    builder.append("?");
                } else {
                    builder.append("&");
                }
                builder.append(key).append("=").append(value);
                i++;
            }
        }
        try {
            return URLEncoder.encode(builder.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("编码错误", e);
            return requestURL;
        }
    }

    private String noProtocol(String url) {
        if (url.startsWith("http:")) {
            return url.substring(5);
        }
        if (url.startsWith("https:")) {
            return url.substring(6);
        }
        return url;
    }

    /**
     * 向下拉——取得我的足迹列表信息
     */
    @RequestMapping(method = RequestMethod.GET, value = "getTouchHistoryList")
    @ResponseBody
    public JSONObject getTouchHistoryList(HttpServletRequest request
            , @RequestParam(value = "start", defaultValue = "1") Integer start
            , @RequestParam(value = "size", defaultValue = "10") Integer size) {

        JSONObject retObj = new JSONObject();

        //获取登陆信息
        JSONObject objLogin = getLogInData(request);
        String code = objLogin.getString("code");

        retObj.put("code", code);
        JSONObject data;
        if (code.equals("200")) {
            //取得登陆信息成功
            String json = objLogin.getString("data");
            data = JSONObject.parseObject(json);
        } else {
            //取得登陆信息失败
            retObj.put("loginUrl", mLoginUrl);
            retObj.put("code", "login");
            return retObj;
        }

        String memGuid = data.getString("MEM_GUID");

        //地区信息
        String areaCode = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("th5_siteid")) {
                areaCode = c.getValue();
                break;
            }
        }

        //取得列表信息
        Integer listCount = 0;
        JSONArray objList = null;
        String activity = "";
        JSONObject objData = getHistory(memGuid, areaCode, start, size);
        if (BaseFunction.jsonObjectHasData(objData)) {
            objList = objData.getJSONArray("itemList");
            listCount = BaseFunction.jsonGetInteger(objData, "count");
            activity = BaseFunction.jsonGetString(objData, "activityList");
        }

        retObj.put(historyList, objList);
        retObj.put(historyCount, listCount);
        retObj.put(activityList, activity);

        return retObj;

    }

    protected JSONObject getLogInData(HttpServletRequest request) {
        return commonService.checkLogin(request, true);
    }

    /*******************************************************************************************************************
     * 取得我的足迹列表信息
     * @param memGuid:用户guid
     * @param areaCode:地区信息
     * @param start:从第几页开始
     * @param size:显示几条
     ******************************************************************************************************************/
    private JSONObject getHistory(String memGuid, String areaCode, Integer start, Integer size) {

        JSONObject resultObj = new JSONObject();

        /**返回信息处理*************************************************************************************************/
        try {

            JSONObject jsonObject = historyService.historyListByIdx(memGuid, areaCode, start, size, "", "", "member");

            if (BaseFunction.jsonObjectHasData(jsonObject)) {

                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");
                if (code.equals("200")) {
                    JSONObject object = jsonObject.getJSONObject("data");

                    int count = 0;
                    String lastName = "";
                    String activity = "";
                    JSONArray itemList = new JSONArray();
                    JSONArray packageList = new JSONArray();

                    if (BaseFunction.jsonObjectHasData(object)) {
                        count = object.getInteger("packageSize");
                        packageList = object.getJSONArray("packageList");
                    }

                    if (BaseFunction.jsonArrayHasData(packageList)) {
                        //查询多倍积分信息
                        Set<String> skus = new HashSet<>();
                        for (int i = 0; i < packageList.size(); i++) {
                            JSONObject json = packageList.getJSONObject(i);
                            String sellNo = json.getString("sell_no");
                            skus.add(sellNo);
                        }
                        JSONObject areaCodeInfo = BaseFunction.areaJson(areaCode);
                        String provinceCode = areaCodeInfo.getString("provinceCode");
                        String cityCode = areaCodeInfo.getString("cityCode");
                        String areaCodeStr = areaCodeInfo.getString("areaCode");
                        Map<String, Integer> mutilInfo = mutilScoreService.mutilScore(provinceCode, cityCode, areaCodeStr, skus);

                        //今天
                        Date dateNow = new Date();
                        int intNow = Integer.valueOf(BaseFunction.formatDate(dateNow, "yyyyMMdd"));

                        //昨天
                        Date dateYesterday = BaseFunction.getYesterday(dateNow);
                        int intYesterday = Integer.valueOf(BaseFunction.formatDate(dateYesterday, "yyyyMMdd"));

                        Iterator iterator = packageList.iterator();

                        Integer index = 0;
                        while (iterator.hasNext()) {

                            JSONObject item = (JSONObject) iterator.next();

                            //判断日期
                            String time = item.getString("update_time");
                            String ymd = "";
                            String weekName = "";

                            if (time.trim().length() > 10) {

                                ymd = time.substring(0, 10);
                                JSONObject objItem = new JSONObject();

                                //需要判断的日期
                                Date date = BaseFunction.stringToDate(time);
                                int intDt = Integer.valueOf(BaseFunction.formatDate(date, "yyyyMMdd"));

                                if (intDt == intNow) {
                                    weekName = "今天";
                                } else if (intDt == intYesterday) {
                                    weekName = "昨天";
                                } else {
                                    weekName = BaseFunction.formatDate(date, "MM月dd日");
                                }

                                Integer type = BaseFunction.jsonGetInteger(item, "type");
                                Integer itemType = BaseFunction.jsonGetInteger(item, "itemType");
                                Integer isFreshPord = BaseFunction.jsonGetInteger(item, "isFreshPord");
                                String typeName = BaseFunction.itemTypeName(type, itemType, isFreshPord);
                                String typeNameUrl = "";
                                String picArray[] = mStaticUrl.split(",");
                                String picUrl = picArray[0];
                                if (BaseFunction.strEquals(typeName, "商家直送")) {
                                    typeNameUrl = picUrl + "/assets/images/my/member/icon_directbusiness2_2x.png";
                                } else if (BaseFunction.strEquals(typeName, "自营")) {
                                    typeNameUrl = picUrl + "/assets/images/my/member/icon_selfsupport2_2x.png";
                                } else if (BaseFunction.strEquals(typeName, "商城")) {
                                    typeNameUrl = picUrl + "/assets/images/my/member/icon_business2_2x.png";
                                } else if (BaseFunction.strEquals(typeName, "环球购")) {
                                    typeNameUrl = picUrl + "/assets/images/my/member/icon_haiwaigou2_2x.png";
                                }
                                item.put("typeName", typeName);
                                item.put("typeNameUrl", typeNameUrl);
                                item.put("index", index + start);

                                String name = item.getString("name");
                                if (BaseFunction.isEmptyNUllString(name)) {
                                    item.put("name", "");
                                }
                                String price = item.getString("price");
                                if (BaseFunction.isEmptyNUllString(price)) {
                                    item.put("price", "");
                                }
                                index += 1;

                                lastName = weekName;

                                String merchantId = BaseFunction.jsonGetString(item, "merchantId");
                                String sellNo = BaseFunction.jsonGetString(item, "sell_no");
                                String isMobilePrice = BaseFunction.jsonGetString(item, "isMobilePrice");
                                String isGroup = BaseFunction.jsonGetString(item, "isGroup");
                                String isActivity = BaseFunction.jsonGetString(item, "isActivity");
                                String isInfant = BaseFunction.jsonGetString(item, "isInfant");
                                String tag = BaseFunction.jsonGetString(item, "tag");
                                if (activity != "") {
                                    activity += ";";
                                }
                                activity += merchantId + "," + sellNo + "," + isMobilePrice + "," + isGroup + "," + isActivity + "," + isInfant + "," + tag;

                                objItem.put("ymd", ymd);
                                objItem.put("weekName", weekName);
                                objItem.put("groupName", ymd);
                                //是否是多倍积分
                                Integer mutil = mutilInfo.get(sellNo);
                                if (mutil != null) {
                                    item.put("name", String.format("【%d倍积分】", mutil) + item.getString("name"));
                                }
                                objItem.put("item", item);

                                itemList.add(objItem);

                            }

                            //图片服务器替换
                            String itPic = item.getString("it_pic");
                            String item_type = item.getString("type");
                            if (!BaseFunction.isEmptyNUllString(itPic) && !BaseFunction.isEmptyNUllString(item_type)) {
                                String picTransform = BaseFunction.picTransformTouch(itPic, item_type, storeUrl, PicRandomUtil.random(imgInside));
                                item.put("it_pic", picTransform);
                            } else {
                                item.put("it_pic", "");
                            }

                        }

                    }

                    resultObj.put("lastName", lastName);
                    resultObj.put("count", count);
                    resultObj.put("activityList", activity);
                    resultObj.put("itemList", itemList);

                }

            }

        } catch (Exception e) {
            log.error("调用我的足迹列表错误！memGuid:" + memGuid + "; areaCode:" + areaCode + "; start:" + start + "; size:" + size, e);
        }

        return resultObj;

    }

    /*******************************************************************************************************************
     * 删除我的足迹信息
     * @param request
     * @param type:删除类型(0:全部删除　1:按卖场ＩＤ删除　2:按年月日删除  3:删除某个日期以前的数据)
     * @param text:删除记录
     ******************************************************************************************************************/
    @RequestMapping(method = RequestMethod.GET, value = "delete")
    @ResponseBody
    public JSONObject deleteHistory(HttpServletRequest request
            , @RequestParam("type") String type
            , @RequestParam("text") String text) {

        JSONObject retObj = new JSONObject();
        String code = "";
        String msg = "";

        /**查询信息处理*************************************************************************************************/
        JSONObject objLogin = getLogInData(request);
        String logCode = objLogin.getString("code");

        retObj.put("code", logCode);
        JSONObject data;
        if (logCode.equals("200")) {
            //取得登陆信息成功
            String json = objLogin.getString("data");
            data = JSONObject.parseObject(json);
        } else {
            //取得登陆信息失败
            retObj.put("code", "login");
            retObj.put("loginUrl", mLoginUrl);
            return retObj;
        }

        //用户guid
        String memGuid = data.getString("MEM_GUID");

        JSONObject jsonObj = new JSONObject();
        //用户id
        jsonObj.put("memGuid", memGuid);

        Date nowDate;
        Date workDate;
        String fromDate;
        String toDate;

        String ids = "";

        switch (type) {
            case "0":
                break;
            case "1":
                //按卖场ＩＤ删除记录
                ids = text;
                break;
            case "2":
                //按时间删除商品-年月日
                nowDate = BaseFunction.stringToDate(text);
                workDate = BaseFunction.getDateAdd(nowDate, 1);
                fromDate = text;
                toDate = BaseFunction.formatDate(workDate, "yyyy-MM-dd");
                ids = fromDate + ";" + toDate;
                break;
            case "3":
                //按时间删除商品-某个日期以前
                nowDate = BaseFunction.stringToDate(text);
                workDate = BaseFunction.getDateAdd(nowDate, -31);
                fromDate = BaseFunction.formatDate(workDate, "yyyy-MM-dd");
                workDate = BaseFunction.getDateAdd(nowDate, 1);
                toDate = BaseFunction.formatDate(workDate, "yyyy-MM-dd");
                ids = fromDate + ";" + toDate;
                break;
            default:
                break;
        }

        /**返回信息处理*************************************************************************************************/
        try {

            JSONObject jsonObject = historyService.delHistory(memGuid, type, ids, "member");
            if (BaseFunction.jsonObjectHasData(jsonObject)) {
                code = jsonObject.getString("code");
                msg = jsonObject.getString("msg");
            }

        } catch (Exception e) {
            log.error("删除我的足迹失败！ memGuid:" + memGuid + "; type:" + type + "; ids:" + ids, e);
        }

        retObj.put("code", code);
        retObj.put("msg", msg);

        return retObj;

    }

    /*******************************************************************************************************************
     * 获得行销活动信息
     * @param request
     * @param text:商品信息
     ******************************************************************************************************************/
    @RequestMapping(method = RequestMethod.GET, value = "getActivity")
    @ResponseBody
    public JSONObject getActivity(HttpServletRequest request, @RequestParam("text") String text) {

        JSONObject retObj = new JSONObject();
        String code = "500";
        String msg = "";

        /**查询信息处理*************************************************************************************************/
        JSONObject objLogin = getLogInData(request);
        String logCode = objLogin.getString("code");

        retObj.put("code", logCode);
        if (logCode.equals("200")) {
            //取得登陆信息成功
            String json = objLogin.getString("data");
        } else {
            //取得登陆信息失败
            retObj.put("code", "login");
            retObj.put("loginUrl", mLoginUrl);
            return retObj;
        }

        if (!BaseFunction.isEmptyNUllString(text)) {
            String[] split = text.split(";");

            JSONArray skuList = new JSONArray();
            if (split.length > 0) {

                //手机专享价
                JSONArray mobilePriceSku = new JSONArray();
                //团购
                JSONArray groupSku = new JSONArray();
                //秒杀
                JSONArray seckillList = new JSONArray();
                JSONArray seckillSku = new JSONArray();
                //限时特惠
                JSONArray saleSku = new JSONArray();
                //母婴专享价
                JSONArray motherSku = new JSONArray();
                //行销活动
                JSONArray activityList = new JSONArray();
                JSONObject activitySku = new JSONObject();
                //赠品
                JSONArray giftSku = new JSONArray();

                for (int i = 0; i < split.length; i++) {
                    String[] item = split[i].split(",");

                    if (item.length >= 6) {

                        String merchantId = item[0];
                        String sku = item[1];
                        String isMobilePrice = item[2];
                        String isGroup = item[3];
                        String isActivity = item[4];
                        String isInfant = item[5];
                        String tag = "";
                        if (item.length == 7) {
                            tag = item[6];
                        }

                        skuList.add(sku);

                        if (BaseFunction.strEquals(isMobilePrice, "1")) {
                            mobilePriceSku.add(sku);
                        } else if (BaseFunction.strEquals(isGroup, "1")) {
                            groupSku.add(sku);
                        } else if (BaseFunction.strEquals(isInfant, "1")) {
                            motherSku.add(sku);
                        } else if (BaseFunction.strEquals(isActivity, "1")) {
                            saleSku.add(sku);
                            seckillList.add(sku);
                        } else {
                            seckillList.add(sku);

                            JSONObject skuListobj = new JSONObject();
                            skuListobj.put("merchantId", merchantId);
                            skuListobj.put("skuId", sku);
                            activityList.add(skuListobj);
                        }

                        if (BaseFunction.strEquals(tag, "赠品")) {
                            giftSku.add(sku);
                        }

                    }
                }

                if (skuList.size() > 0) {

                    //地区信息
                    String areaCode = "";
                    Cookie[] cookies = request.getCookies();
                    for (Cookie c : cookies) {
                        if (c.getName().equals("th5_siteid")) {
                            areaCode = c.getValue();
                            areaCode = areaCode.split("-")[0];
                            break;
                        }
                    }
                    String pgSeq = BaseFunction.getPgSeq(areaCode);

                    activitySku = getActivityItem(areaCode, activityList);
                    seckillSku = getSeckill(areaCode, pgSeq, seckillList);
                    JSONArray itemList = new JSONArray();

                    Iterator iterator = skuList.iterator();
                    while (iterator.hasNext()) {
                        String sku = (String) iterator.next();
                        JSONArray skuActivityList = new JSONArray();

                        if (mobilePriceSku.contains(sku)) {
                            skuActivityList.add("手机专享");
                        } else if (groupSku.contains(sku)) {
                            skuActivityList.add("团");
                        } else if (seckillSku.contains(sku)) {
                            skuActivityList.add("秒");
                        } else if (motherSku.contains(sku)) {
                            skuActivityList.add("母婴专享");
                        } else if (saleSku.contains(sku)) {
                            skuActivityList.add("限时特惠");
                        } else if (activitySku.containsKey(sku)) {
                            skuActivityList.addAll(activitySku.getJSONArray(sku));
                        }

                        if (giftSku.contains(sku)) {
                            skuActivityList.add("赠");
                        }

                        if (skuActivityList.size() > 0) {
                            JSONObject skuItem = new JSONObject();
                            skuItem.put("sku", sku);
                            skuItem.put("activity", skuActivityList);
                            itemList.add(skuItem);
                        }
                    }

                    if (itemList.size() > 0) {
                        code = "200";
                        retObj.put("itemList", itemList);
                    }

                }

            }

        }

        retObj.put("code", code);
        retObj.put("msg", msg);

        return retObj;

    }

    private JSONObject getActivityItem(String areaCode, JSONArray skuList) {

        JSONObject itemList = new JSONObject();

        JSONObject activityResp = historyService.searchPageNormalActivity("3", areaCode, skuList);

        if (BaseFunction.jsonObjectHasData(activityResp)) {

            if ("200".equals(activityResp.getString("code"))) {

                JSONObject body = BaseFunction.jsonGetJSONObject(activityResp, "body");
                JSONArray skuActivityList = BaseFunction.jsonGetJSONArray(body, "skuActivityList");
                if (BaseFunction.jsonArrayHasData(skuActivityList)) {
                    Iterator activityIte = skuActivityList.iterator();
                    while (activityIte.hasNext()) {

                        JSONObject activityObj = (JSONObject) activityIte.next();
                        String skuId = activityObj.getString("skuId");
                        if (skuId.contains("_")) {
                            skuId = skuId.split("_")[0];
                        }
                        JSONArray activityList = new JSONArray();
                        JSONArray reduceTypeList = activityObj.getJSONArray("reduceTypeList");

                        Iterator skuIte = reduceTypeList.iterator();
                        while (skuIte.hasNext()) {
                            String activityType = (String) skuIte.next();
                            String activityTypeName = BaseFunction.activityTypeName(activityType);
                            if (!activityList.contains(activityTypeName)) {
                                activityList.add(activityTypeName);
                            }
                        }

                        if (activityList.size() > 0) {
                            itemList.put(skuId, activityList);
                        }

                    }
                }

            }

        }

        return itemList;

    }

    private JSONArray getSeckill(String areaCode, String pgSeq, JSONArray skuList) {

        JSONArray seckillSku = new JSONArray();

        JSONObject activityResp = historyService.getSeckill(areaCode, pgSeq, skuList);

        if (BaseFunction.jsonObjectHasData(activityResp)) {

            if ("1".equals(activityResp.getString("success"))) {

                JSONObject data = BaseFunction.jsonGetJSONObject(activityResp, "data");
                JSONArray secKillList = BaseFunction.jsonGetJSONArray(data, "secKill");
                if (BaseFunction.jsonArrayHasData(secKillList)) {
                    Iterator secKillIte = secKillList.iterator();
                    while (secKillIte.hasNext()) {

                        JSONObject secKillObj = (JSONObject) secKillIte.next();
                        String smSeq = secKillObj.getString("smSeq");
                        String isSeckill = secKillObj.getString("isSeckill");

                        if (BaseFunction.strEquals(isSeckill, "1")) {
                            seckillSku.add(smSeq);
                        }

                    }
                }

            }

        }

        return seckillSku;

    }

}
