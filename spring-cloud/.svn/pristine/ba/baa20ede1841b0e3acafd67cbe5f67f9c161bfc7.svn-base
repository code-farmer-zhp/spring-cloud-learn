package com.feiniu.member.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.log.CustomLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yue.teng on 2016-11-02.
 */
@Service
public class CommonService {
    public static final CustomLog log = CustomLog.getLogger(CommonService.class);

    @Autowired
    protected RestTemplate restTemplate;
    @Value("${loginCookieName}")
    protected String loginCookieName;
    @Value("${user.api}")
    protected String userApi;

    @Value("${loginCookieNameTouch}")
    protected String loginCookieNameTouch;
    @Value("${user.api.touch}")
    protected String userApiTouch;
    public JSONObject checkLogin(HttpServletRequest request) {
        return checkLogin(request,false);
    }

    public JSONObject checkLogin(HttpServletRequest request, boolean isTouch) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null || cookies.length < 1) {
                return null;
            }
            String loginCookie = "";
            for (Cookie c : cookies) {
                if(isTouch){
                    if (c.getName().equals(loginCookieNameTouch)) {
                        loginCookie = c.getValue();
                    }
                }else {
                    if (c.getName().equals(loginCookieName)) {
                        loginCookie = c.getValue();
                    }
                }
            }
            if (StringUtils.isBlank(loginCookie)) {
                return null;
            } else {
                String userInfo;

                MultiValueMap<String, String> req = new LinkedMultiValueMap<String, String>();
                req.add("cookie", loginCookie);
                if(isTouch) {
                    req.add("channel", "2");
                    userInfo = restTemplate.postForObject(userApiTouch, req, String.class);
                }else{
                    req.add("channel", "1");
                    userInfo = restTemplate.postForObject(userApi, req, String.class);
                }
                return JSONObject.parseObject(userInfo);
            }
        } catch (Exception e) {
            log.error("获取登录态出错", e);
            return null;
        }
    }

    public String getGuid(HttpServletRequest request, boolean isTouch) {
        JSONObject loginJson=checkLogin(request,isTouch);
        if(loginJson==null||loginJson.get("data")==null){
            return null;
        }else{
            JSONObject dataJson=JSONObject.parseObject(loginJson.getString("data"));
            return dataJson.getString("MEM_GUID");
        }
    }

    public String hideNickName(String nickname) {
        String returnNickName = "";
        String phoneRegex = "^1\\d{10}$";
        Pattern phonePattern = Pattern.compile(phoneRegex);
        Matcher phoneMatcher = phonePattern.matcher(nickname);
        if (phoneMatcher.matches()) {
            returnNickName = getMaskPhone(nickname);
        } else {
            String mailRegex = "^[a-zA-Z0-9_\\.\\-]+@[a-zA-Z0-9\\-]+\\.[a-zA-Z0-9\\.\\-]+$";
            Pattern mailPattern = Pattern.compile(mailRegex);
            Matcher mailMatcher = mailPattern.matcher(nickname);
            if (mailMatcher.matches()) {
                returnNickName = getMaskEmail(nickname);
            } else {
                returnNickName = nickname;
            }
        }
        return returnNickName;
    }

    /*
    * 返回遮罩形式的phone(前面3位和后面3位显示, 其余显示为*)
    */
    private String getMaskPhone(String phone) {
        return hideStr(phone, 2, 2);
    }

    /*
     * 返回遮罩形式的email
     */
    private String getMaskEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
            String[] emailArr = email.split("@");
            emailArr[0] = hideStr(emailArr[0], 1, 1);
            email = emailArr[0] + "@" + emailArr[1];
        }
        return email;
    }

    /*
     * 隐藏字符串的指定部分
     */
    private String hideStr(String str, int beginLen, int endLen) {
        if (StringUtils.isNotBlank(str)) {
            Pattern p = Pattern.compile("(.{" + beginLen + "})(.+)(.{" + endLen + "})");
            Matcher m = p.matcher(str);

            if (m.find()) {
                String maskStr = m.group(2);
                if (StringUtils.isNotBlank(maskStr)) {
                    maskStr = maskStr.replaceAll(".", "*");
                }
                str = m.group(1) + maskStr + m.group(3);
            }
        }
        return str;
    }
}
