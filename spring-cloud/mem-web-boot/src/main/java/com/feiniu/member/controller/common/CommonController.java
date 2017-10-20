package com.feiniu.member.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.CommonService;
import com.feiniu.member.util.CookieUtil;
import com.feiniu.member.util.HttpRequestUtil;
import com.feiniu.member.util.PicRandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

public class CommonController {
    public static final CustomLog log = CustomLog.getLogger(CommonController.class);
    @Autowired
    protected RestTemplate restTemplate;
    @Value("${user.api}")
    protected String userApi;
    @Value("${login.url}")
    protected String loginUrl;
    @Value("${member.url}")
    protected String memberUrl;
    @Value("${loginCookieName}")
    protected String loginCookieName;
    @Value("${my.url}")
    private String myUrl;
    @Value("${shop.url}")
    private String shopUrl;
    @Value("${safe.url}")
    private String safeUrl;
    @Value("${store.url}")
    private String storeUrl;
    @Value("${redEnvelope.get.count}")
    private String redEnvelopeGetCount;
    @Value("${vip.url}")
    private String vipUrl;
    @Value("${staticDomain.url}")
    private String staticDomain;
    @Value("${msgUrl}")
    private String msgUrl;
    @Value("${imgInside.url}")
    private String imgInsideUrl;
    @Value("${www.url}")
    private String wwwUrl;
    @Value("${storeDomain.url}")
    private String storeDomainUrl;
    @Value("${version.code}")
    private String versionCode;
    @Value("${mall.url}")
    private String mallUrl;
    @Value("${receiveCoupon.jsp}")
    private String receiveCouponJsp;

    @Value("${left.new.url}")
    private String leftNewUrl;

    // 公共头部url
    @Value("${header2.html.url}")
    private String header2HtmlUrl;

    // 导航头部头部url
    @Value("${header2.nav.html.url}")
    private String header2NavHtmlUrl;

    // 公共尾部url
    @Value("${footer2.html.url}")
    private String footer2HtmlUrl;

    @Autowired
    private CommonService commonService;

    protected ModelAndView getModel(HttpServletRequest request, String viewUrl) {
        ModelAndView mav = new ModelAndView(viewUrl);
        mav.addObject("version", versionCode);
        mav.addObject("basePath", myUrl + "/");
        mav.addObject("myUrl", myUrl);
        mav.addObject("shopUrl", shopUrl);
        mav.addObject("safeUrl", safeUrl);
        mav.addObject("storeUrl", storeUrl);
        mav.addObject("vipUrl", vipUrl);
        mav.addObject("imgInsideUrl", PicRandomUtil.random(imgInsideUrl));
        mav.addObject("wwwUrl", wwwUrl);
        mav.addObject("storeDomainUrl", storeDomainUrl);
        mav.addObject("receiveCouponJsp", receiveCouponJsp);
        mav.addObject("mallUrl", mallUrl);
        String[] splits = staticDomain.split(",");
        mav.addObject("staticDomain", splits[new Random().nextInt(splits.length)]);
        JSONObject userInfoJson = commonService.checkLogin(request);

        if (userInfoJson == null || userInfoJson.get("code") == null || !userInfoJson.get("code").equals(200)) {
            return new ModelAndView("redirect:" + loginUrl);
        } else {
            String json = userInfoJson.getString("data");
            JSONObject userJson = JSONObject.parseObject(json);
            String memName;
            if (userJson.getString("NICKNAME") != null && !StringUtils.isEmpty(userJson.getString("NICKNAME"))) {
                memName = userJson.getString("NICKNAME");
                memName = commonService.hideNickName(memName);
            } else {
                memName = userJson.getString("MEM_USERNAME");
            }
            mav.addObject("memGuid", userJson.getString("MEM_GUID"));
            mav.addObject("memName", memName);
            mav.addObject("memberUrl", memberUrl);
            mav.addObject("isEmployee", userJson.getInteger("IS_EMPLOYEE"));
            mav.addObject("isCompany", userJson.getInteger("MEM_TYPE"));
            mav.addObject("msgUrl", msgUrl);

            String leftHtml = HttpRequestUtil.sendGet(leftNewUrl, "", HttpRequestUtil.assemblyLoginCookie(request.getCookies()));
            request.setAttribute("leftHtml", leftHtml);
            // 头部页面html
            request.setAttribute("headerHtml", getHeaderHtml(request));
            // 导航头部html
            request.setAttribute("navHeaderHtml", getNavHeaderHtml(request));
            // 尾部页面html
            request.setAttribute("footerHtml", getFooterHtml(request));
        }
        return mav;
    }

    protected String getGuid(HttpServletRequest request) {
        return commonService.getGuid(request, false);
    }

    public String getNavHeaderHtml(HttpServletRequest request) {
        try {
            return HttpRequestUtil.sendGet(header2NavHtmlUrl, "", HttpRequestUtil.assemblyLoginCookie(request.getCookies()));
        } catch (RestClientException e) {
            log.error("获取导航头部请求出错", e);
        } catch (Exception e) {
            log.error("解析导航头部返回值出错", e);
        }
        return null;
    }

    public String getHeaderHtml(HttpServletRequest request) {
        StringBuilder sbf = new StringBuilder();
        try {
            String codeJsonStr = restTemplate.getForObject(header2HtmlUrl + "&pg_seq=" + getAreaCodeKey(request), String.class);
            JSONObject retJson = JSONObject.parseObject(codeJsonStr);
            if (2000 == retJson.getIntValue("code") || 0 == retJson.getIntValue("code")) {
                JSONObject dataJson = retJson.getJSONObject("data");
                //.append("<link rel='stylesheet' type='text/css' href='"+ dataJson.getString("css") + "'>")
                sbf.append("<script>").append(dataJson.getString("javascript")).append("</script>")
                        .append(dataJson.getString("toolbar"));
            }
        } catch (RestClientException e) {
            log.error("获取公共头部请求出错,url:" + header2HtmlUrl, e);
        } catch (Exception e) {
            log.error("解析公共头部返回值出错,url:" + header2HtmlUrl, e);
        }
        return sbf.toString();
    }

    /*
     * 获取尾部
     */
    public String getFooterHtml(HttpServletRequest request) {
        StringBuffer sbf = new StringBuffer();
        try {
            String codeJsonStr = restTemplate.getForObject(footer2HtmlUrl, String.class);
            JSONObject retJson = JSONObject.parseObject(codeJsonStr);
            if (2000 == retJson.getIntValue("code")) {
                JSONObject dataJson = retJson.getJSONObject("data");
                sbf//.append("<link rel='stylesheet' type='text/css' href='"+ dataJson.getString("css") + "'>")
                        // .append("<script>" + dataJson.getString("javascript") +
                        // "</script>")
                        .append(dataJson.getString("footer"));
            }
        } catch (RestClientException e) {
            log.error("获取公共尾部请求出错,url:" + footer2HtmlUrl, e);
        } catch (Exception e) {
            log.error("解析公共尾部返回值出错,url:" + footer2HtmlUrl, e);
        }
        return sbf.toString();
    }

    public String getAreaCodeKey(HttpServletRequest request) {
        String areaCode = "CPG1";
        String cDistValue = CookieUtil.getCookieValue(request, "C_dist");
        if (StringUtils.isNotBlank(cDistValue)) {
            String[] cDistArr = cDistValue.split("_");
            if (cDistArr.length == 2) {
                areaCode = cDistArr[0];
            }
        }
        return areaCode;
    }
}
