package com.feiniu.favorite.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.favorite.asyncload.domain.AsyncLoadService;
import com.feiniu.favorite.utils.RequestNoGen;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class SoaProductService {

    private static final Log log = LogFactory.getLog(SoaProductService.class);

    @Value("${api.mall.product.new}")
    private String mallProductNew;

    @Autowired
    private AsyncLoadService asyncLoadService;

    public Map<String, JSONObject> getProducts(String area, String activityQd, Set<String> skus) {
        Map<String, JSONObject> result = new HashMap<>();
        JSONObject data = new JSONObject();
        data.put("skuSeqs", StringUtils.join(skus, ","));
        if ("3".equals(activityQd)) {
            data.put("isStatus", 1);
            data.put("isWireless", 1);
        } else if ("2".equals(activityQd)) {
            data.put("isWireless", 1);
        } else {
            data.put("isWireless", 0);
        }
        data.put("areaCode", JSONObject.parse(area));
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("token", "member");
        param.add("data", data.toJSONString());
        try {
            JSONObject jsonMallReturn = asyncLoadService.post(mallProductNew, param, RequestNoGen.getNo());
            if ("1".equals(jsonMallReturn.getString("success"))) {
                JSONArray mallarr = jsonMallReturn.getJSONArray("data");
                for (int i = 0, len = mallarr.size(); i < len; i++) {
                    JSONObject item = mallarr.getJSONObject(i);
                    String skuSeq = item.getString("skuSeq");
                    result.put(skuSeq, item);
                }
            }
        } catch (Exception e) {
            log.error("商品信息查询异常。", e);
        }
        return result;

    }
}
