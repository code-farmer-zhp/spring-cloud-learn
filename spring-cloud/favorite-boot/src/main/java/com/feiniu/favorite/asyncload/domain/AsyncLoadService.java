package com.feiniu.favorite.asyncload.domain;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.MultiValueMap;

/**
 * 一个asyncLoad的对象服务
 */
public interface AsyncLoadService {

    JSONObject post(String url, MultiValueMap<String, Object> formData, String requestNo);

    JSONObject get(String url, String requestNo);
}
