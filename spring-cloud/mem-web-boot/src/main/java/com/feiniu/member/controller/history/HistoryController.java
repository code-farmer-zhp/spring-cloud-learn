package com.feiniu.member.controller.history;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.common.BaseFunction;
import com.feiniu.member.controller.common.CommonController;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.HistoryService;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Iterator;

/***********************************************************************************************************************
 * Created by Shirley
 * 会员中心-我的足迹
 **********************************************************************************************************************/
@Controller
@RequestMapping(value = "history")
public class HistoryController extends CommonController {

    /**log信息*********************************************************************************************************/
    public static final CustomLog log = CustomLog.getLogger(HistoryController.class);

    /**配置文件常量*****************************************************************************************************/
    //飞牛网首页
    @Value("${www.url}")
    private String wwwUrl;
    //图片服务器替换
    @Value("${store.url}")
    private String storeUrl;

    /**程序常量*********************************************************************************************************/
    //图片地址
    @Value("${imgInside.url}")
    private String imgInside;
    //每一页显示的数据个数
    private static String pageSize = "12";
    //变量名－分类|降价|促销列表
    private static String kindList = "kindList";
    //变量名－分类列表
    private static String typeList = "typeList";
    //变量名－记录列表
    private static String historyList = "historyList";
    //当前页
    private static String pageIdx = "pageNo";
    //总页数
    private static String pageCount = "pageCount";
    //当前最后时间名
    private static String lastDate = "lastDate";

    @Autowired
    private HistoryService historyService;

    /*******************************************************************************************************************
     * 我的足迹－列表页
     ******************************************************************************************************************/
    @RequestMapping(value = {"/list"})
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response)
                             throws Exception {

        /**设置打开页面，没有登陆的情况下打开登陆页面***********************************************************************/
        ModelAndView mav =getModel(request, "history/list");
        if(mav.getViewName().equals("redirect:"+loginUrl))
        {
            return mav;
        }
        if(mav.getModel().isEmpty()){
            return new ModelAndView("redirect:" + loginUrl);
        }

        /**取得基本信息*************************************************************************************************/
        //用户guid
        String memGuid="";
        if(mav.getModel().get("memGuid")!=null){
            memGuid=mav.getModel().get("memGuid").toString();
        }
        //飞牛网首页
        mav.addObject("wwwUrl", wwwUrl);
        //地区信息
        String areaCode="";
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("C_dist_area")) {
                areaCode = c.getValue();
                break;
            }
        }

        /**取得分类信息*************************************************************************************************/
        JSONObject objKind = getHistoryType(memGuid, areaCode);
        mav.addObject(typeList, objKind);

        /**打开打开我的足迹列表页****************************************************************************************/
        mav.setViewName("history/historyList");
        return mav;
    }

    /*******************************************************************************************************************
     * 向下拉｜选择分类｜选择促销｜选择降价——取得我的足迹列表信息
     * @param pageNo:当前页
     * @param type:查询类型(kind:分类 moreAct:促销 lowPrice:降价 为空查询全部)
     * @param ids:查询条件
     ******************************************************************************************************************/
    @RequestMapping(method = RequestMethod.GET, value = "getList")
    @ResponseBody
    public JSONObject getList(HttpServletRequest request
                               ,@RequestParam(value = "pageNo", defaultValue = "1") String pageNo
                               ,@RequestParam(value = "type", defaultValue = "") String type
                               ,@RequestParam(value = "ids", defaultValue = "") String ids) {

        JSONObject retObj = new JSONObject();

        /**获取登陆信息*************************************************************************************************/
        JSONObject objLogin = getLogInData(request);
        String code = objLogin.getString("code");

        retObj.put("code",code);
        if (code.equals("0")) {
            //取得登陆信息成功

        } else {
            //取得登陆信息失败
            retObj.put("code","login");
            retObj.put("loginUrl",loginUrl);
            return retObj;
        }

        /**取得基本信息*************************************************************************************************/
        //用户guid
        String memGuid="";
        if(objLogin.get("memGuid") != null){
            memGuid = objLogin.get("memGuid").toString();
        }
        //飞牛网首页
        retObj.put("wwwUrl", wwwUrl);
        //地区信息
        String area_code="";
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("C_dist_area")) {
                area_code = c.getValue();
                break;
            }
        }

        /**取得列表信息*************************************************************************************************/
        JSONArray objList = null;
        Integer listCount = 0;
        Integer pCount = 0;
        String lastName = "";
        JSONObject objData = getHistory(memGuid, area_code, pageNo, type, ids);
        if (BaseFunction.jsonObjectHasData(objData)) {
            lastName = objData.getString("lastName");
            objList = objData.getJSONArray("itemList");
            listCount = BaseFunction.jsonGetInteger(objData, "count");
            pCount = BaseFunction.ceil(listCount, Integer.valueOf(pageSize));
        }

        retObj.put(historyList, objList);
        retObj.put("pageNo", pageNo);
        retObj.put(pageCount, pCount);
        retObj.put(lastDate, lastName);
        retObj.put("imgInside", PicRandomUtil.random(imgInside));

        retObj.put("name", 1);

        return retObj;

    }

    /*******************************************************************************************************************
     * 取得用户降价，促销信息
     ******************************************************************************************************************/
    @RequestMapping(method = RequestMethod.GET, value = "getKind")
    @ResponseBody
    public JSONObject getKind(HttpServletRequest request) {

        JSONObject retObj = new JSONObject();

        /**获取登陆信息*************************************************************************************************/
        JSONObject objLogin = getLogInData(request);
        String code = objLogin.getString("code");

        retObj.put("code",code);
        if (code.equals("0")) {
            //取得登陆信息成功

        } else {
            //取得登陆信息失败
            retObj.put("code","login");
            retObj.put("loginUrl",loginUrl);
            return retObj;
        }

        /**取得基本信息*************************************************************************************************/
        //用户guid
        String memGuid="";
        if(objLogin.get("memGuid") != null){
            memGuid = objLogin.get("memGuid").toString();
        }
        //飞牛网首页
        retObj.put("wwwUrl", wwwUrl);
        //地区信息
        String area_code="";
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("C_dist_area")) {
                area_code = c.getValue();
                break;
            }
        }

        /**取得分类信息*************************************************************************************************/
        JSONObject objKind = getHistoryKind(memGuid, area_code);
        retObj.put(kindList, objKind);

        return retObj;

    }

    /*******************************************************************************************************************
     * 取得我的足迹分类|降价|促销信息
     * memGuid:用户guid
     * areaCode:地区信息
     ******************************************************************************************************************/
    private JSONObject getHistoryKind(String memGuid, String areaCode) {

        JSONObject resultObj = null;

        /**返回信息处理*************************************************************************************************/
        try {

            JSONObject jsonObject = historyService.historyKind(memGuid, areaCode, 1, "member");
            if (BaseFunction.jsonObjectHasData(jsonObject)) {
                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");
                if (code.equals("200")) {
                    resultObj = jsonObject.getJSONObject("data");
                }
            }

        } catch (Exception e) {

        }

        return resultObj;

    }

    /*******************************************************************************************************************
     * 取得我的足迹分类信息
     * memGuid:用户guid
     * areaCode:地区信息
     ******************************************************************************************************************/
    private JSONObject getHistoryType(String memGuid, String areaCode) {

        JSONObject resultObj = null;

        /**返回信息处理*************************************************************************************************/
        try {

            JSONObject jsonObject = historyService.historyType(memGuid, areaCode, 1, "member");

            if (BaseFunction.jsonObjectHasData(jsonObject)) {
                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");
                if (code.equals("200")) {
                    resultObj = jsonObject.getJSONObject("data");
                }
            }

        } catch (Exception e) {

        }

        return resultObj;

    }

    /*******************************************************************************************************************
     * 取得我的足迹列表信息
     * @param memGuid:用户guid
     * @param areaCode:地区信息
     * @param pageNo:当前页
     * @param type:查询类型(kind:分类 moreAct:促销 lowPrice:降价 为空查询全部)
     * @param ids:查询条件
     ******************************************************************************************************************/
    private JSONObject getHistory(String memGuid, String areaCode, String pageNo, String type, String ids) {

        JSONObject resultObj = new JSONObject();

        /**查询信息处理*************************************************************************************************/
        //当前页处理
        pageNo = BaseFunction.doPage(pageNo);

        /**返回信息处理*************************************************************************************************/
        try {

            Integer pageIndex = Integer.parseInt(pageNo);
            Integer pageS = Integer.parseInt(pageSize);
            JSONObject jsonObject = historyService.historyListByPage(memGuid, areaCode, pageIndex, pageS, type, ids, "member");

            if (BaseFunction.jsonObjectHasData(jsonObject)) {
                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");
                if (code.equals("200")) {
                    JSONObject object = jsonObject.getJSONObject("data");

                    int count = 0;
                    String lastName = "";
                    JSONArray itemList = new JSONArray();
                    JSONArray packageList = new JSONArray();

                    if (BaseFunction.jsonObjectHasData(object)) {
                        count = object.getInteger("packageSize");
                        packageList = object.getJSONArray("packageList");
                    }

                    if (BaseFunction.jsonArrayHasData(packageList)) {

                        //今天
                        Date dateNow = new Date();
                        int intNow = Integer.valueOf(BaseFunction.formatDate(dateNow, "yyyyMMdd"));

                        //昨天
                        Date dateYesterday = BaseFunction.getYesterday(dateNow);
                        int intYesterday = Integer.valueOf(BaseFunction.formatDate(dateYesterday, "yyyyMMdd"));

                        //上周一
                        Date preWeekFirstDay = BaseFunction.getWeekDay(dateNow, -1, 2);
                        int intPreWeekFirstDay = Integer.valueOf(BaseFunction.formatDate(preWeekFirstDay, "yyyyMMdd"));

                        //上周六
                        Date preWeekLastDay = BaseFunction.getWeekDay(dateNow, -1, 7);
                        int intPreWeekLastDay = Integer.valueOf(BaseFunction.formatDate(preWeekLastDay, "yyyyMMdd"));

                        Iterator iterator = packageList.iterator();

                        while (iterator.hasNext()) {

                            JSONObject item = (JSONObject)iterator.next();


                            //判断日期
                            String time = item.getString("update_time");
                            String ymd = "";
                            String weekName = "";

                            if (time.trim().length() > 10) {

                                ymd = time.substring(0,10);
                                JSONObject objItem = new JSONObject();

                                //需要判断的日期
                                Date date = BaseFunction.stringToDate(time);
                                int intDt = Integer.valueOf(BaseFunction.formatDate(date, "yyyyMMdd"));

                                if (intDt == intNow) {
                                    weekName = "今天";
                                } else if (intDt == intYesterday) {
                                    weekName = "昨天";
                                } else if (intDt < intYesterday && intDt > intPreWeekLastDay) {
                                    String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
                                    weekName = BaseFunction.getWeekName(date, weekDays);
                                } else if (intDt <= intPreWeekLastDay && intDt >= intPreWeekFirstDay) {
                                    String[] weekDays = {"上周日", "上周一", "上周二", "上周三", "上周四", "上周五", "上周六"};
                                    weekName = BaseFunction.getWeekName(date, weekDays);
                                } else if (intDt < intPreWeekFirstDay) {
                                    weekName = "更早以前";
                                } else {
                                    weekName = "";
                                }

                                String name = item.getString("name");
                                if (BaseFunction.isEmptyNUllString(name)) {
                                    item.put("name","");
                                }
                                String price = item.getString("price");
                                if (BaseFunction.isEmptyNUllString(price)) {
                                    item.put("price","");
                                }

                                lastName = weekName;

                                objItem.put("ymd",ymd);
                                objItem.put("weekName",weekName);
                                objItem.put("item",item);

                                itemList.add(objItem);

                            }

                            //图片服务器替换
                            String itPic = item.getString("it_pic");
                            String item_type = item.getString("type");
                            if(!BaseFunction.isEmptyNUllString(itPic) && !BaseFunction.isEmptyNUllString(item_type)){
                                String picTransform = BaseFunction.picTransform(itPic, item_type, storeUrl ,PicRandomUtil.random(imgInside));
                                item.put("it_pic", picTransform);
                            } else {
                                item.put("it_pic", "");
                            }

                        }

                    }

                    resultObj.put("lastName",lastName);
                    resultObj.put("count",count);
                    resultObj.put("itemList",itemList);

                } else {

                }
            }

        } catch (Exception e) {

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
                                     ,@RequestParam("type") String type
                                     ,@RequestParam("text") String text) {

        JSONObject retObj = new JSONObject();
        String code = "";
        String msg = "";

        /**查询信息处理*************************************************************************************************/
        JSONObject objLogin = getLogInData(request);
        String logCode = objLogin.getString("code");

        retObj.put("code",logCode);
        if (logCode.equals("0")) {
            //取得登陆信息成功

        } else {
            //取得登陆信息失败
            retObj.put("code","login");
            retObj.put("loginUrl",loginUrl);
            return retObj;
        }

        //用户guid
        String memGuid="";
        if(objLogin.get("memGuid") != null){
            memGuid = objLogin.get("memGuid").toString();
        }

        Date nowDate;
        Date workDate;
        String fromDate;
        String toDate;

        String ids = "";

        switch(type) {
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
                toDate = text;
                ids = fromDate + ";" + toDate;
                break;
            case "3":
                //按时间删除商品-某个日期以前
                nowDate = BaseFunction.stringToDate(text);
                workDate = BaseFunction.getDateAdd(nowDate, -31);
                fromDate  = BaseFunction.formatDate(workDate, "yyyy-MM-dd");
                workDate = BaseFunction.getDateAdd(nowDate, 1);
                toDate  = BaseFunction.formatDate(workDate, "yyyy-MM-dd");
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

        } catch(Exception e) {

        }

        retObj.put("code",code);
        retObj.put("msg",msg);

        return retObj;

    }

    /*******************************************************************************************************************
     * 取得登陆信息
     * @param request
     ******************************************************************************************************************/
    private JSONObject getLogInData(HttpServletRequest request) {

        JSONObject retObj;

        //0:成功 1:失败
        String code = "1";
        //用户guid
        String memGuid = "";
        //地区信息
        String areaCode = "";

        Cookie[] cookies = request.getCookies();
        if(cookies==null||cookies.length<1){
            retObj = setLoginData(code,memGuid,areaCode);
            return retObj;
        }

        String loginCookie = "";
        for (Cookie c : cookies) {
            if (c.getName().equals(loginCookieName)) {
                loginCookie = c.getValue();
            }

            if (c.getName().equals("C_dist")) {
                areaCode = c.getValue();
                areaCode = areaCode.substring(areaCode.lastIndexOf('_')+1);
            }
        }
        if (loginCookie == "") {
            retObj = setLoginData(code,memGuid,areaCode);
            return retObj;
        } else {
            String userInfo = restTemplate.postForObject(userApi + "?cookie=" + loginCookie, null, String.class);
            JSONObject userInfoJson;
            try {
                userInfoJson = JSONObject.parseObject(userInfo);
            } catch (Exception e) {
                retObj = setLoginData(code,memGuid,areaCode);
                return retObj;
            }

            if (userInfoJson.get("code")==null||!userInfoJson.get("code").equals(200)) {
                retObj = setLoginData(code,memGuid,areaCode);
                return retObj;
            } else {
                String json = (String) userInfoJson.get("data");
                JSONObject userJson = JSONObject.parseObject(json);
                memGuid = userJson.getString("MEM_GUID");
            }
        }

        code = "0";
        retObj = setLoginData(code,memGuid,areaCode);
        return retObj;

    }

    private JSONObject setLoginData(String code, String memGuid, String areaCode) {

        JSONObject retObj = new JSONObject();

        retObj.put("code",code);
        if (code.equals("0")) {
            retObj.put("memGuid",memGuid);
            retObj.put("areaCode",areaCode);
        } else {
            retObj.put("loginUrl",loginUrl);
        }

        return retObj;

    }

}
