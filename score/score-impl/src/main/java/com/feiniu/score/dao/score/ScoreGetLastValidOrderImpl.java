package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by yue.teng on 2016/5/31.
 */


@Repository
public class ScoreGetLastValidOrderImpl implements ScoreGetLastValidOrder {

    private static final CustomLog log = CustomLog.getLogger(ScoreGetOrderDetailImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    private static final Integer SUCCESS = 200;

    @Value("${getLastValidOrder.api}")
    private String url;

    @Override
    public Date getLastValidOrder(String memGuid) {
        Map<String, Object> param = new HashMap<>();
        param.put("memGuid", memGuid);
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("data", JSONObject.toJSONString(param));
        String orderJson = restTemplate.postForObject(url, map, String.class);
        JSONObject jsonObj = JSONObject.parseObject(orderJson);
        Integer code = jsonObj.getInteger("code");
        if (Objects.equals(SUCCESS, code)) {
            JSONObject data = jsonObj.getJSONObject("data");
            return data.getDate("payDt");
        } else {
            String msg = jsonObj.getString("msg");
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询最近一笔订的时间异常。msg:" + msg);
        }
    }
}