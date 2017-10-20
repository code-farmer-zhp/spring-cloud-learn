package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ScoreWhetherUsePointImpl implements ScoreWhetherUsePoint {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${whether.use.point.url}")
    private String url;

    private static final String SUCCESS = "0";

    /**
     * 可以使用积分
     */
    private static final String CAN_USE = "1";


    @Override
    public Map<String, Boolean> getWhetherCanUsePoint(String itNos) {
        Map<String, Object> info = new HashMap<>();
        info.put("itnos", itNos);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("data", JSONObject.toJSONString(info));
        String json = restTemplate.postForObject(url, map, String.class);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String code = jsonObject.getString("code");
        Map<String, Boolean> booleanMap = new HashMap<>();
        if (SUCCESS.equals(code)) {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            JSONObject result = dataObj.getJSONObject("result");
            Set<String> keys = result.keySet();
            for (String key : keys) {
                String value = result.getString(key);
                if (StringUtils.equals(CAN_USE, value)) {
                    booleanMap.put(key, true);
                } else {
                    booleanMap.put(key, false);
                }
            }

        } else {
            String msg = jsonObject.getString("msg");
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, " 根据itNo查询商品是否可以使用积分失败。message=" + msg);
        }
        return booleanMap;
    }
}
