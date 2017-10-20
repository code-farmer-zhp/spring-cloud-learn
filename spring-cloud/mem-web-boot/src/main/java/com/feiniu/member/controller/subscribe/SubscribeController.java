package com.feiniu.member.controller.subscribe;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.common.Constant;
import com.feiniu.member.controller.common.CommonController;
import com.feiniu.member.dto.MessageInfoAll;
import com.feiniu.member.dto.MessageInfoPart;
import com.feiniu.member.dto.SubscribeEnum.Gender;
import com.feiniu.member.httpclient.HttpClientUtil;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.util.PageUtil;
import com.feiniu.member.util.SignUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping(value = "/subscribe")
public class SubscribeController extends CommonController {

    private static long count = 0;

    private static String pageSize = "20";

    public static final CustomLog log = CustomLog.getLogger(SubscribeController.class);

    // 商城商品的可售状态URl
    private  static ExecutorService executorService = Executors.newFixedThreadPool(50);
    
    @Value("${service.innermsg.url}")
    private String serviceInnermsgUrl;

    @Value("${service.setRead.url}")
    private String servicesetReadUrl;
    
    @Value("${service.mail.key}")
    private String mailKey;

    @Value("${service.mail.token}")
    private String mailToken;

    @Value("${service.mem.searchSubscribe}")
    private String searchSubscribeUrl;

    @Value("${service.mem.updataSubscribe}")
    private String updataSubscribeUrl;

    @Value("${service.fcm.deleteSubscribe}")
    private String serviceFcmDeleteSubscribe;

    @Value("${service.fcm.setSubscribe}")
    private String serviceFcmSetSubscribe;

    @Value("${service.mem.getUnreadAmountAndTitleByType}")
    private String getUnreadAmountAndTitleByTypeUrl;
    
    
    @Value("${service.mem.getUnreadAmountAndTitleByMemGuid}")
    private String getUnreadAmountAndTitleByMemGuidUrl;
    
    @Value("${my.url}")
    private String myUrl;

    @Value("${www.url}")
    private String wwwUrl;

    @Value("${buy.fn}")
    private String buyFn;
    
    @Value("${safe.url}")
    private String safeUrl;
    
    @Value("${vip.url}")
    private String vipUrl;

    @RequestMapping(value = {"/systemList","/allList"}, method = RequestMethod.GET)
    public ModelAndView systemList(HttpServletRequest request,
                                   @RequestParam(value = "messageStatus", defaultValue = "2") String messageStatus) {
        ModelAndView mav = getModel(request, "subscribe/systemList");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        if (mav.getModel().isEmpty()) {
            return new ModelAndView("redirect:" + loginUrl);
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        String basePath = "";
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        if ("2".equals(messageStatus)) {
            messageStatus = "";
        }
        initUnRead(1, memGuid);
        String pageIndex = "1";
        String beginTime = "";
        String endTime = "";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memGuid", memGuid);
        jsonObject.put("bigKind", 1);
        params.add("data", jsonObject.toString());
        List<String> messageTypes = new ArrayList<String>();
        try {
            String postForObject = restTemplate.postForObject(searchSubscribeUrl, params, String.class);
            JSONObject parseObject = JSONObject.parseObject(postForObject);
            String code = parseObject.getString("code");
            if ("100".equals(code)) {
                JSONArray jsonArray = parseObject.getJSONArray("data");
                if (jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        Integer isSubscribe = object.getInteger("isSubscribe");
                        if (0 == isSubscribe) {
                            String bigKind = object.getString("bigKind");
                            String smallKind = object.getString("smallKind");
                            messageTypes.add(bigKind + "-" + smallKind);
                        }
                    }
                } else {
                    for (Gender gender : Gender.values()) {
                        if (0 == gender.ordinal()) {
                            String value = gender.getValue();
                            String[] split = value.split(",");
                            for (String string : split) {
                                if (StringUtils.isNotBlank(string)) {
                                    messageTypes.add(gender.ordinal() + 1 + "-" + Integer.parseInt(string));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询站内信订阅接口报错", e);
        }
        MessageInfoAll messageInfo = new MessageInfoAll();
        buildHeader(messageInfo);
        buildBodyAll(messageInfo, memGuid, "1", messageTypes, pageSize, pageIndex, messageStatus, beginTime,
                endTime);
        String sendPostJson;
        try {
            sendPostJson = HttpClientUtil.sendPostJson(serviceInnermsgUrl,
                    JSONObject.toJSONString(messageInfo));
            JSONObject parseObject = JSONObject.parseObject(sendPostJson);
            String code = parseObject.getString("statusCode");
            if ("100".equals(code)) {
                JSONArray jsonArray = parseObject.getJSONArray("messageList");
                if (null != jsonArray) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        String messageType = (String) object.get("messageType");
                        String urlType = object.getString("url_type");
                        String openUrl = object.getString("open_url");
                        String returnUrl = null;
                        if (StringUtils.isBlank(openUrl)) {
                            returnUrl = returnUrl(messageType + "-" + urlType, openUrl);
                        } else {
                            if (Constant.LOGISTICSAll.contains(messageType)) {
                                returnUrl = returnUrl(messageType + "-" + urlType, openUrl);
                            } else {
                                returnUrl = returnUrl(messageType, openUrl);
                            }
                        }
                        object.put("open_url", returnUrl);
                        jsonArray.set(i, object);
                    }
                }
                parseObject.put("messageList", jsonArray);
                Integer amount = parseObject.getInteger("amount");
                Map<String, Object> beforeMonthMap = getSubscribe(memGuid, pageIndex, pageSize, basePath
                        + "subscribe/pageTurn", amount);
                mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
                mav.addObject("messageList", parseObject);
            }
        } catch (Exception e) {
            log.error("查询站内信详情接口报错", e);
        }
        mav.addObject("type", "systemList");
        return mav;
    }

    @RequestMapping(value = "/memberList", method = RequestMethod.GET)
    public ModelAndView memberList(HttpServletRequest request,
                                   @RequestParam(value = "messageStatus", defaultValue = "2") String messageStatus) {
        ModelAndView mav = getModel(request, "subscribe/memberList");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        if (mav.getModel().isEmpty()) {
            return new ModelAndView("redirect:" + loginUrl);
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        String basePath = "";
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        if ("2".equals(messageStatus)) {
            messageStatus = "";
        }
        initUnRead(2, memGuid);
        String pageIndex = "1";
        String beginTime = "";
        String endTime = "";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memGuid", memGuid);
        jsonObject.put("bigKind", 2);
        params.add("data", jsonObject.toString());
        List<String> messageTypes = new ArrayList<String>();
        try {
            String postForObject = restTemplate.postForObject(searchSubscribeUrl, params, String.class);
            JSONObject parseObject = JSONObject.parseObject(postForObject);
            String code = parseObject.getString("code");
            if ("100".equals(code)) {
                JSONArray jsonArray = parseObject.getJSONArray("data");
                if (jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        Integer isSubscribe = object.getInteger("isSubscribe");
                        if (0 == isSubscribe) {
                            String bigKind = object.getString("bigKind");
                            String smallKind = object.getString("smallKind");
                            messageTypes.add(bigKind + "-" + smallKind);
                        }
                    }
                } else {
                    for (Gender gender : Gender.values()) {
                        if (1 == gender.ordinal()) {
                            String value = gender.getValue();
                            String[] split = value.split(",");
                            for (String string : split) {
                                if (StringUtils.isNotBlank(string)) {
                                    messageTypes.add(gender.ordinal() + 1 + "-" + Integer.parseInt(string));
                                }
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            log.error("查询站内信订阅接口报错", e);
        }
        MessageInfoAll messageInfo = new MessageInfoAll();
        buildHeader(messageInfo);
        buildBodyAll(messageInfo, memGuid, "2", messageTypes, pageSize, pageIndex, messageStatus, beginTime,
                endTime);
        String sendPostJson;
        try {
            sendPostJson = HttpClientUtil.sendPostJson(serviceInnermsgUrl,
                    JSONObject.toJSONString(messageInfo));
            JSONObject parseObject = JSONObject.parseObject(sendPostJson);
            String code = parseObject.getString("statusCode");
            if ("100".equals(code)) {
                JSONArray jsonArray = parseObject.getJSONArray("messageList");
                if (null != jsonArray) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        String messageType = (String) object.get("messageType");
                        String urlType = object.getString("url_type");
                        String openUrl = object.getString("open_url");
                        String returnUrl = null;
                        if (StringUtils.isBlank(openUrl)) {
                            returnUrl = returnUrl(messageType + "-" + urlType, openUrl);
                        } else {
                            if (Constant.LOGISTICSAll.contains(messageType)) {
                                returnUrl = returnUrl(messageType + "-" + urlType, openUrl);
                            } else {
                                returnUrl = returnUrl(messageType, openUrl);
                            }
                        }
                        object.put("open_url", returnUrl);
                        jsonArray.set(i, object);
                    }
                }
                parseObject.put("messageList", jsonArray);
                Integer amount = parseObject.getInteger("amount");
                Map<String, Object> beforeMonthMap = getSubscribe(memGuid, pageIndex, pageSize, basePath
                        + "subscribe/pageTurn", amount);
                mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
                mav.addObject("messageList", parseObject);
            }
        } catch (Exception e) {
            log.error("查询站内信详情接口报错", e);
        }
        mav.addObject("type", "memberList");
        return mav;
    }

    @RequestMapping(value = "/activityList", method = RequestMethod.GET)
    public ModelAndView activityList(HttpServletRequest request,
                                     @RequestParam(value = "messageStatus", defaultValue = "2") String messageStatus) {
        ModelAndView mav = getModel(request, "subscribe/activityList");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        if (mav.getModel().isEmpty()) {
            return new ModelAndView("redirect:" + loginUrl);
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        String basePath = "";
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        if ("2".equals(messageStatus)) {
            messageStatus = "";
        }
        initUnRead(3, memGuid);
        String pageIndex = "1";
        String beginTime = "";
        String endTime = "";
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memGuid", memGuid);
        jsonObject.put("bigKind", 3);
        params.add("data", jsonObject.toString());
        List<String> messageTypes = new ArrayList<String>();
        try {
            String postForObject = restTemplate.postForObject(searchSubscribeUrl, params, String.class);
            JSONObject parseObject = JSONObject.parseObject(postForObject);
            String code = parseObject.getString("code");
            if ("100".equals(code)) {
                JSONArray jsonArray = parseObject.getJSONArray("data");
                if (jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        Integer isSubscribe = object.getInteger("isSubscribe");
                        if (0 == isSubscribe) {
                            String bigKind = object.getString("bigKind");
                            String smallKind = object.getString("smallKind");
                            messageTypes.add(bigKind + "-" + smallKind);
                        }
                    }
                } else {
                    for (Gender gender : Gender.values()) {
                        if (2 == gender.ordinal()) {
                            String value = gender.getValue();
                            String[] split = value.split(",");
                            for (String string : split) {
                                if (StringUtils.isNotBlank(string)) {
                                    messageTypes.add(gender.ordinal() + 1 + "-" + Integer.parseInt(string));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询站内信订阅接口报错", e);
        }
        MessageInfoAll messageInfo = new MessageInfoAll();
        buildHeader(messageInfo);
        buildBodyAll(messageInfo, memGuid, "3", messageTypes, pageSize, pageIndex, messageStatus, beginTime,
                endTime);
        String sendPostJson;
        try {
            sendPostJson = HttpClientUtil.sendPostJson(serviceInnermsgUrl,
                    JSONObject.toJSONString(messageInfo));
            JSONObject parseObject = JSONObject.parseObject(sendPostJson);
            String code = parseObject.getString("statusCode");
            if ("100".equals(code)) {
                JSONArray jsonArray = parseObject.getJSONArray("messageList");
                if (null != jsonArray) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        if (StringUtils.isNotBlank(object.getString("extraField"))) {
                            JSONArray extraField = JSONArray.parseArray(object.getString("extraField"));
                            Boolean showPic = true;
                            if (extraField.size() > 0) {
                                for (int j = 0; j < extraField.size(); j++) {
                                    JSONObject jsonObject2 = extraField.getJSONObject(j);
                                    Date date = jsonObject2.getDate("endTime");
                                    String Pic = jsonObject2.getString("Pic");
                                    if (new Date().getTime() > date.getTime()) {
                                        jsonObject2.put("effective", false);
                                    } else {
                                        jsonObject2.put("effective", true);
                                    }
                                    if (StringUtils.isNotBlank(Pic)) {
                                        showPic = true;
                                    } else {
                                        showPic = false;
                                    }
                                }
                            }
                            object.put("showPic", showPic);
                            object.put("extraField", extraField);
                        }
                    }
                }
                Integer amount = parseObject.getInteger("amount");
                Map<String, Object> beforeMonthMap = getSubscribe(memGuid, pageIndex, pageSize, basePath
                        + "subscribe/pageTurn", amount);
                mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
                mav.addObject("messageList", parseObject);
            }
        } catch (Exception e) {
            log.error("查询站内信详情接口报错", e);
        }
        mav.addObject("type", "activityList");
        return mav;
    }

    @RequestMapping(value = "/msgSets", method = RequestMethod.GET)
    public ModelAndView msgSets(HttpServletRequest request) {
        ModelAndView mav = getModel(request, "subscribe/msgSets");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        if (mav.getModel().isEmpty()) {
            return new ModelAndView("redirect:" + loginUrl);
        }
   
        return mav;
    }

    @RequestMapping(value = "/updataKind", method = RequestMethod.POST)
    @ResponseBody
    public String commit(HttpServletRequest request, @RequestParam(value = "kind") String kind,
                         @RequestParam(value = "unkind") String unkind) {
        // 新增未订阅的类别信息
        List<Map<String, Object>> list = new ArrayList<>();
        List<Map<String, Object>> list1 = new ArrayList<>();
        if (StringUtils.isNotBlank(kind)) {
            String[] split = kind.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String[] split2 = split[i].split("-");
                    map.put("bigKind", split2[0]);
                    map.put("smallKind", split2[1]);
                    map.put("isSubscribe", 1);
                    list.add(map);
                }
            }
        }
        if (StringUtils.isNotBlank(unkind)) {
            String[] split = unkind.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String[] split2 = split[i].split("-");
                    map.put("bigKind", split2[0]);
                    map.put("smallKind", split2[1]);
                    map.put("isSubscribe", 0);
                    list1.add(map);
                }
            }
        }
        Cookie[] cookies = request.getCookies();
        String loginCookie = "";
        String memGuid = "";
        for (Cookie c : cookies) {
            if (c.getName().equals(loginCookieName)) {
                loginCookie = c.getValue();
            }
        }
        String userInfo = restTemplate.postForObject(userApi + "?cookie=" + loginCookie, null, String.class);
        JSONObject userInfoJson = JSONObject.parseObject(userInfo);
        String json = (String) userInfoJson.get("data");
        JSONObject userJson = JSONObject.parseObject(json);
        memGuid = userJson.getString("MEM_GUID");
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("kind", list);
        jsonObject.put("unkind", list1);
        jsonObject.put("memGuid", memGuid);
        params.add("data", jsonObject.toString());
        String resStr1 = "";
        try {
            resStr1 = restTemplate.postForObject(updataSubscribeUrl, params, String.class);
        } catch (Exception e) {
            log.error("设置消息中心订阅失败", e);
        }
        return resStr1;
    }

    @RequestMapping(method = RequestMethod.POST, value = "pageTurn")
    public ModelAndView prodslistAll(HttpServletRequest request, @RequestParam("pageno") String pNo,
                                     @RequestParam("type") String type,
                                     @RequestParam(value = "messageStatus", defaultValue = "2") String messageStatus) {
        ModelAndView mav = getModel(request, "subscribe/sublist");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        String memGuid = "";
        String basePath = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        String pageIndex = pNo;
        List<String> messageTypes = new ArrayList<String>();
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memGuid", memGuid);
        if ("1".equals(type)) {
            jsonObject.put("bigKind", 1);
        } else if ("2".equals(type)) {
            jsonObject.put("bigKind", 2);
        } else if ("3".equals(type)) {
            jsonObject.put("bigKind", 3);
        }
        params.add("data", jsonObject.toString());
        try {
            String postForObject = restTemplate.postForObject(searchSubscribeUrl, params, String.class);
            JSONObject parseObject = JSONObject.parseObject(postForObject);
            String code = parseObject.getString("code");
            if ("100".equals(code)) {
                JSONArray jsonArray = parseObject.getJSONArray("data");
                if (jsonArray.size() > 0) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        Integer isSubscribe = object.getInteger("isSubscribe");
                        if (0 == isSubscribe) {
                            String bigKind = object.getString("bigKind");
                            String smallKind = object.getString("smallKind");
                            messageTypes.add(bigKind + "-" + smallKind);
                        }
                    }
                } else {
                        for (Gender gender : Gender.values()) {
                            if (Integer.parseInt(type) - 1 == gender.ordinal()) {
                                String value = gender.getValue();
                                String[] split = value.split(",");
                                for (String string : split) {
                                    if (StringUtils.isNotBlank(string)) {
                                        messageTypes.add(gender.ordinal() + 1 + "-"
                                                + Integer.parseInt(string));
                                    }
                                }
                            }
                        }

                }

            }
          
        } catch (Exception e) {
            log.error("查询站内信订阅接口报错", e);
        }
        String beginTime = "";
        String endTime = "";
        if ("2".equals(messageStatus)) {
            messageStatus = "";
        }
        MessageInfoAll messageInfo = new MessageInfoAll();
        buildHeader(messageInfo);
        buildBodyAll(messageInfo, memGuid, type, messageTypes, pageSize, pageIndex, messageStatus, beginTime,
                endTime);
        String sendPostJson;
        try {
            sendPostJson = HttpClientUtil.sendPostJson(serviceInnermsgUrl,
                    JSONObject.toJSONString(messageInfo));
            JSONObject parseObject = JSONObject.parseObject(sendPostJson);
            String code = parseObject.getString("statusCode");
            if ("100".equals(code)) {
                JSONArray jsonArray = parseObject.getJSONArray("messageList");
                if (null != jsonArray) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        if("3".equals(type)){
                            if (StringUtils.isNotBlank(object.getString("extraField"))) {
                                JSONArray extraField = JSONArray.parseArray(object.getString("extraField"));
                                Boolean showPic = true;
                                if (extraField.size() > 0) {
                                    for (int j = 0; j < extraField.size(); j++) {
                                        JSONObject jsonObject2 = extraField.getJSONObject(j);
                                        Date date = jsonObject2.getDate("endTime");
                                        String Pic = jsonObject2.getString("Pic");
                                        if (new Date().getTime() > date.getTime()) {
                                            jsonObject2.put("effective", false);
                                        } else {
                                            jsonObject2.put("effective", true);
                                        }
                                        if (StringUtils.isNotBlank(Pic)) {
                                            showPic = true;
                                        } else {
                                            showPic = false;
                                        }
                                    }
                                }
                                object.put("showPic", showPic);
                                object.put("extraField", extraField);
                            }
                        }else{
                            String messageType = object.getString("messageType");
                            String urlType = object.getString("url_type");
                            String openUrl = object.getString("open_url");
                            String returnUrl = null;
                            if (StringUtils.isBlank(openUrl)) {
                                returnUrl = returnUrl(messageType + "-" + urlType, openUrl);
                            } else {
                                if (Constant.LOGISTICSAll.contains(messageType)) {
                                    returnUrl = returnUrl(messageType + "-" + urlType, openUrl);
                                } else {
                                    returnUrl = returnUrl(messageType, openUrl);
                                }
                            }
                            object.put("open_url", returnUrl);
                        }
                    }
                }
                parseObject.put("messageList", jsonArray);
                Integer amount = parseObject.getInteger("amount");
                Map<String, Object> beforeMonthMap = getSubscribe(memGuid, pageIndex, pageSize, basePath
                        + "subscribe/pageTurn", amount);
                mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
                mav.addObject("messageList", parseObject);
            } 
        } catch (Exception e) {
            log.error("查询站内信接口报错", e);
        }
        if ("1".equals(type)) {
            mav.addObject("type", "systemList");
        } else if ("2".equals(type)) {
            mav.addObject("type", "memberList");
        } else if ("3".equals(type)) {
            mav.addObject("type", "activityList");
        }
        return mav;
    }

    @RequestMapping(value = "/findKind", method = RequestMethod.GET)
    @ResponseBody
    public String findKind(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String loginCookie = "";
        String memGuid = "";
        for (Cookie c : cookies) {
            if (c.getName().equals(loginCookieName)) {
                loginCookie = c.getValue();
            }
        }
        String userInfo = restTemplate.postForObject(userApi + "?cookie=" + loginCookie, null, String.class);
        JSONObject userInfoJson = JSONObject.parseObject(userInfo);
        String json = (String) userInfoJson.get("data");
        JSONObject userJson = JSONObject.parseObject(json);
        memGuid = userJson.getString("MEM_GUID");
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memGuid", memGuid);
        params.add("data", jsonObject.toString());
        String resStr1="";
        try {
            resStr1 = restTemplate.postForObject(searchSubscribeUrl, params, String.class);
        } catch (RestClientException e) {
            log.error("查询订阅消息类别报错", e);
        }
        return resStr1;
    }

    @RequestMapping(value = "/findUnReadNum", method = RequestMethod.GET)
    @ResponseBody
    public String findUnReadNum(HttpServletRequest request, @RequestParam("bigKind") String bigKind) {
        Cookie[] cookies = request.getCookies();
        String loginCookie = "";
        String memGuid = "";
        for (Cookie c : cookies) {
            if (c.getName().equals(loginCookieName)) {
                loginCookie = c.getValue();
            }
        }
        String userInfo = restTemplate.postForObject(userApi + "?cookie=" + loginCookie, null, String.class);
        JSONObject userInfoJson = JSONObject.parseObject(userInfo);
        String json = (String) userInfoJson.get("data");
        JSONObject userJson = JSONObject.parseObject(json);
        memGuid = userJson.getString("MEM_GUID");
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        JSONObject jsonObject = new JSONObject();
        String resStr1="";
        if("0".equals(bigKind)){
            jsonObject.put("memGuid", memGuid);
            params.add("data", jsonObject.toString());
            try {
                resStr1 = restTemplate.postForObject(getUnreadAmountAndTitleByMemGuidUrl, params, String.class);
            } catch (RestClientException e) {
                log.error("查询订阅订阅未读数量报错", e);
            }
        }else{
            jsonObject.put("memGuid", memGuid);
            jsonObject.put("bigKind", bigKind);
            params.add("data", jsonObject.toString());
           
            try {
                resStr1 = restTemplate.postForObject(getUnreadAmountAndTitleByTypeUrl, params, String.class);
            } catch (RestClientException e) {
                log.error("查询订阅订阅未读数量报错", e);
            }
        }
     
        return resStr1;
    }
    
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(HttpServletRequest request, @RequestParam(value = "kindIds") String kindIds) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(kindIds)) {
            String[] split = kindIds.split(",");
            if (split.length > 0) {
                for (int i = 0; i < split.length; i++) {
                    list.add(split[i]);
                }
            }
        }
        Cookie[] cookies = request.getCookies();
        String loginCookie = "";
        String memGuid = "";
        for (Cookie c : cookies) {
            if (c.getName().equals(loginCookieName)) {
                loginCookie = c.getValue();
            }
        }
        String userInfo = restTemplate.postForObject(userApi + "?cookie=" + loginCookie, null, String.class);
        JSONObject userInfoJson = JSONObject.parseObject(userInfo);
        String json = (String) userInfoJson.get("data");
        JSONObject userJson = JSONObject.parseObject(json);
        memGuid = userJson.getString("MEM_GUID");
        MessageInfoPart messageInfo = new MessageInfoPart();
        buildHeader(messageInfo);
        buildBodyPart(messageInfo, memGuid, list);
        String sendPostJson = "";
        try {
            sendPostJson = HttpClientUtil.sendPostJson(serviceFcmDeleteSubscribe,
                    JSONObject.toJSONString(messageInfo));
        } catch (Exception e) {
            log.error("删除站内信接口报错", e);
        }
        return sendPostJson;
    }


    private void buildHeader(MessageInfoAll messageInfo) {

        MessageInfoAll.Header header = new MessageInfoAll().new Header();
        header.setKey(mailKey);
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = format.format(now);
        header.setTimestamp(timestamp);
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("key", header.getKey());
        params.put("timestamp", timestamp);
        String sign = SignUtil.generate(params, mailToken);
        header.setSign(sign);
        header.setSerialNumber(String.valueOf(getSerialNumber()));
        header.setInterfaceVersion("1.0");
        messageInfo.setHeader(header);
    }

    private void buildHeader(MessageInfoPart messageInfo) {
        MessageInfoPart.Header header = new MessageInfoPart().new Header();
        header.setKey(mailKey);
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = format.format(now);
        header.setTimestamp(timestamp);
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("key", header.getKey());
        params.put("timestamp", timestamp);
        String sign = SignUtil.generate(params, mailToken);
        header.setSign(sign);
        header.setSerialNumber(String.valueOf(getSerialNumber()));
        header.setInterfaceVersion("1.0");
        messageInfo.setHeader(header);
    }

    private void buildBodyAll(MessageInfoAll messageInfo, String memGuid, String firstMsgType,
                              List<String> messageTypes, String pageSize, String pageIndex, String messageStatus,
                              String beginTime, String endTime) {
        MessageInfoAll.Body body = new MessageInfoAll().new Body();
        body.setMessageStatus(messageStatus);
        body.setMessageTypes(messageTypes);
        body.setUserID(memGuid);
        body.setFirstMsgType(firstMsgType);
        body.setReadChannel("1");
        body.setRmc("1");
        body.setPageIndex(pageIndex);
        body.setPageSize(pageSize);
        body.setBeginTime(beginTime);
        body.setEndTime(endTime);
        messageInfo.setBody(body);

    }

    private void buildBodyAll(MessageInfoAll messageInfo, String memGuid, List<String> messageTypes) {
        MessageInfoAll.Body body = new MessageInfoAll().new Body();
        body.setMessageTypes(messageTypes);
        body.setUserID(memGuid);
        body.setReadChannel("1");
        body.setRmc("1");
        messageInfo.setBody(body);

    }
    private void buildBodyPart(MessageInfoPart messageInfo, String memGuid, List<String> messageIDs) {
        MessageInfoPart.Body bodyPart = new MessageInfoPart().new Body();
        bodyPart.setMessageIDs(messageIDs);
        bodyPart.setUserID(memGuid);
        messageInfo.setBody(bodyPart);

    }

    public Map<String, Object> getSubscribe(String memGuid, String pageNo, String pageSize, String url,
                                            Integer totalSum) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Double totalPage = Math.ceil(totalSum.doubleValue() / Double.valueOf(pageSize));
        Integer total = totalPage.intValue();
        if (Integer.parseInt(pageNo) > total) {
            pageNo = total.toString();
        }

        PageUtil pagedata = new PageUtil(Integer.parseInt(pageNo), totalSum, Integer.parseInt(pageSize), url);
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
                pageMap.put("pre_href", data.getUrl() + "?pageno=" + (data.getPageNo() - 1));
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
                pageMap.put("next_href", data.getUrl() + "?pageno=" + (data.getPageNo() + 1));
            } else {
                pageMap.put("next_href", "javascript:void(0);");
            }
            pageMap.put("goUrl", data.getUrl());
        }
        return pageMap;
    }

    private synchronized long getSerialNumber() {
        return count++;
    }

    private String returnUrl(String messageType, String openUrl) {
        if (Constant.SCORE.contains(messageType)) {
            return myUrl + "/point/pointlist";
        } else if (Constant.INDEX.contains(messageType)) {
            return wwwUrl;
        } else if (Constant.VOUCHER.contains(messageType)) {
            return safeUrl + "/voucher/voucherBonusList?firstOpen=0";
        } else if (Constant.BALANCE.contains(messageType)) {
            return safeUrl + "/balance/myBalance";
        } else if (Constant.GROWTH_1.contains(messageType)) {
            return vipUrl + "/gift_privilege.html";
        }else if (Constant.GROWTH_2.contains(messageType)) {
            return vipUrl + "/grade_privilege.html";
        }  else if (Constant.LOGISTICS_1.contains(messageType)) {
            return memberUrl + "/order/orderDetail/" + openUrl;
        } else if (Constant.LOGISTICS_2.contains(messageType)) {
            return memberUrl + "/order/orderList?searchCondition=" + openUrl;
        } else if(Constant.VOUCHER_1.contains(messageType)){
            return buyFn+"cart/index";
        }else{
            return openUrl;
        }
    }
    
    
    //异步添加其他规格产品
    private void initUnRead(final int type,final String memGuid) {
        executorService.execute(new Runnable() {
                @Override
                public void run() {
                    List<String> messageTypes = new ArrayList<String>();
                    for (Gender gender : Gender.values()) {
                        if (type - 1 == gender.ordinal()) {
                            String value = gender.getValue();
                            String[] split = value.split(",");
                            for (String string : split) {
                                if (StringUtils.isNotBlank(string)) {
                                    messageTypes.add(gender.ordinal() + 1 + "-"
                                            + Integer.parseInt(string));
                                }
                            }
                        }
                    }
                    MessageInfoAll messageInfo = new MessageInfoAll();
                    buildHeader(messageInfo);
                    buildBodyAll(messageInfo, memGuid, messageTypes);
                    try {
                     String sendPostJson = HttpClientUtil.sendPostJson(servicesetReadUrl,
                                JSONObject.toJSONString(messageInfo));
                        JSONObject parseObject = JSONObject.parseObject(sendPostJson);
                        if(!("100".equals(parseObject.getString("statusCode")))){
                            log.error("更新已读接口失败:"+parseObject.getString("message"));
                        }
                    } catch (Exception e) {
                        log.error("更新已读接口失败", e);
                    }
                       
            }
       });
     }

}
