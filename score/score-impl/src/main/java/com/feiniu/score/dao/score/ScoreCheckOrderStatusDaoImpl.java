package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.log.CustomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class ScoreCheckOrderStatusDaoImpl implements ScoreCheckOrderStatusDao {

    private static final CustomLog log = CustomLog.getLogger(ScoreCheckOrderStatusDaoImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${get.order.status.url}")
    private String url;

    private static final int SUCCESS_CODE = 200;

    /**
     * 订单失败
     */
    private static final int IS_ERROR = 2;

    @Override
    public boolean getOrderSubmitErrorStatus(String ogNo) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("data", JSONObject.toJSONString(Arrays.asList(ogNo)));
            String resStr = restTemplate.postForObject(url, params, String.class);
            JSONObject resJson = JSONObject.parseObject(resStr);
            int code = resJson.getIntValue("code");
            if (SUCCESS_CODE == code) {
                JSONArray dataArray = resJson.getJSONArray("data");
                JSONObject orderData = dataArray.getJSONObject(0);
                int succ = orderData.getIntValue("succ");
                if (succ == IS_ERROR) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("查询订单状态出错", "getOrderSubmitErrorStatus", e);
            return false;
        }

    }
}
