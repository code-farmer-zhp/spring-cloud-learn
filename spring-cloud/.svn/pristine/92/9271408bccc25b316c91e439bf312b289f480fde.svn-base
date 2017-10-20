package com.feiniu.favorite.asyncload.domain;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.favorite.utils.RequestNoGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class AsyncLoadServiceImpl implements AsyncLoadService {

    @Autowired
    private RestTemplate restTemplate;


    public JSONObject post(String url, MultiValueMap<String, Object> formData, String requestNo) {
        RequestNoGen.setNo(requestNo);
        String text = restTemplate.postForObject(url, formData, String.class);
        return JSONObject.parseObject(text);
    }

    public JSONObject get(String url, String requestNo) {
        RequestNoGen.setNo(requestNo);
        String text = restTemplate.getForObject(url, String.class);
        return JSONObject.parseObject(text);
    }

}
