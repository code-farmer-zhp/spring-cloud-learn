package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.vo.OrderJsonVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Repository
public class ScoreGetOrderDetailImpl implements ScoreGetOrderDetail {

    @Autowired
    private RestTemplate restTemplate;

    private static final Integer SUCCESS = 200;

    @Value("${orderDetail.url}")
    private String url;

    @Override
    public OrderJsonVo getOrderDetail(String memGuid, String ogSeq) {
        Map<String, Object> param = new HashMap<>();
        param.put("memGuid", memGuid);
        param.put("ogSeq", ogSeq);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("data", JSONObject.toJSONString(param));
        String orderJson = restTemplate.postForObject(url, map, String.class);
        JSONObject jsonObj = JSONObject.parseObject(orderJson);
        Integer code = jsonObj.getInteger("code");
        if (Objects.equals(SUCCESS, code)) {
            JSONObject data = jsonObj.getJSONObject("data");
            JSONObject orderInfo = data.getJSONObject("orderInfo");
            return OrderJsonVo.convertJson(orderInfo);
        } else {
            String msg = jsonObj.getString("msg");
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询订单信息异常。msg:" + msg);
        }


    }
}
