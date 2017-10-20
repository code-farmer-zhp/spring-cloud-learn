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

@Service
public class FeedBackService {
    public static final CustomLog log = CustomLog.getLogger(FeedBackService.class);
    @Autowired
    protected RestTemplate restTemplate;
    @Value("${feedback.add.url}")
    private String feedbackAddUrl;

    /**
     * 用户意见反馈提交 addFeedback
     *
     *
     * @param token
     * @return JSONObject
     */
    public JSONObject addFeedback(String picUrls, String content, int type, String contact,
                                  String phoneType, String memberGuid,
                                  String sysVersion, String cityCode, String token) {
        JSONObject data = new JSONObject();
        data.put("picUrls", picUrls);
        data.put("content", content);
        data.put("type", type);
        data.put("contact", contact);
        data.put("phoneType", phoneType);
        if (StringUtils.isNotBlank(memberGuid)) {
            data.put("memGuid", memberGuid);
        }
        //app version
        data.put("version", "");
        //api version
        data.put("source", "");
        //系统类型
        data.put("system", sysVersion);
        //省份
        data.put("province", cityCode);
        //来源	（IOS：1/ANDROID：2/触屏：3）
        //写死触屏3
        data.put("through", 3);

        MultiValueMap<String, String> req = new LinkedMultiValueMap<String, String>();
        req.add("data", data.toJSONString());
        req.add("token", token);
        String res = restTemplate.postForObject(feedbackAddUrl, req,String.class);

        JSONObject resData = JSONObject.parseObject(res);
        return resData;
    }


    public static String filterStrNew(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        // 复制粘贴的时候在字符串的尾部会出现这个符号
        str = str.replaceAll("�", "");
        return filterEmoji(str);
    }

    public static String filterEmoji(String source) {
        if(StringUtils.isNotBlank(source)){
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
        }else{
            return source;
        }
    }
}
