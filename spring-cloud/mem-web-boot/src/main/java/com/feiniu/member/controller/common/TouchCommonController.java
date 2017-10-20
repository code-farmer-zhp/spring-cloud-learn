package com.feiniu.member.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.CommonService;
import com.feiniu.member.util.HttpRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * Created by yue.teng on 2016-11-08.
 */
public class TouchCommonController {
    private static CustomLog log = CustomLog.getLogger(TouchCommonController.class);

    @Value("${version.code}")
    private String versionCode;
    @Value("${m.feiniu.url}")
    private String mUrl;
    @Value("${m.login.url}")
    private String mLoginUrl;
    @Value("${m.my.url}")
    private String mMyUrl;
    @Value("${m.seckill.url}")
    private String mSeckillUrl;
    @Value("${m.home.url}")
    private String mHomeUrl;
    @Value("${m.i.url}")
    private String mIUrl;
    @Value("${m.staticDomain.url}")
    private String mStaticUrl;
    @Value("${www.url}")
    private String wwwUrl;
    @Value("${m.top.nav.html.url}")
    private String mTopNavHtmlUrl;
    @Value("${m.footer.html.url}")
    private String mFooterHtmlUrl;
    @Value("${environment.version}")
    private String environmentVersion;
    @Value("${m.vip.url}")
    private String mVipUrl;
    @Value("${m.buy.url}")
    private String mBuyUrl;
    @Value("${m.item.url}")
    private String mItemUrl;
    @Autowired
    private CommonService commonService;

    protected ModelAndView getModel(HttpServletRequest request, String viewUrl) {
        ModelAndView mav = new ModelAndView(viewUrl);
        mav.addObject("version", versionCode);
        mav.addObject("mMyUrl", mMyUrl);
        mav.addObject("mHomeUrl", mHomeUrl);
        mav.addObject("mIUrl", mIUrl);
        mav.addObject("mUrl", mUrl);
        mav.addObject("mVipUrl", mVipUrl);
        mav.addObject("mItemUrl", mItemUrl);
        mav.addObject("mBuyUrl", mBuyUrl);
        mav.addObject("mSeckillUrl", mSeckillUrl);
        mav.addObject("wwwUrl", wwwUrl);
        mav.addObject("environmentVersion", environmentVersion);
        String[] msplits = mStaticUrl.split(",");
        mav.addObject("mStaticUrl", msplits[new Random().nextInt(msplits.length)]);
        mav.addObject("footHtml", getFooter());
        JSONObject userInfoJson = commonService.checkLogin(request, true);

        if (userInfoJson == null || userInfoJson.get("code") == null || !userInfoJson.get("code").equals(200)) {
            return new ModelAndView("redirect:" + mLoginUrl);
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
            mav.addObject("isEmployee", userJson.getInteger("IS_EMPLOYEE"));
            mav.addObject("isCompany", userJson.getInteger("MEM_TYPE"));
        }
        return mav;
    }

    protected String getGuid(HttpServletRequest request) {
        return commonService.getGuid(request, true);
    }

    /**
     * getIntegerVersion根据字符串版本号获得数字版本号
     *
     * @param version
     * @return int
     * @throws @since 1.0.0
     */
    protected int getIntegerVersion(String version) {
        int intVersion = -100;
        try {
            if (!StringUtils.isBlank(version) && version.length() >= 3) {
                intVersion = Integer.valueOf(version.substring(version.length() - 3, version.length()));
            }
        } catch (Exception e) {
            log.error("解析接口版本号异常:" + e.getMessage(), e);
        }
        return intVersion;
    }

    /*
    * 获取尾部
    */
    protected String getFooter() {
        try {
            return HttpRequestUtil.sendGet(mFooterHtmlUrl, "", "");
        } catch (Exception e) {
            log.error("获取公共尾部出错", e);
            return null;
        }
    }

    /*
        * 获取头部导航
        */
    protected String getTopNavHtmlUrl() {
        try {
            return HttpRequestUtil.sendGet(mTopNavHtmlUrl, "", "");
        } catch (Exception e) {
            log.error("获取头部导航出错", e);
            return null;
        }
    }
}
