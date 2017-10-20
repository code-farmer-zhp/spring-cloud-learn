package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import com.feiniu.score.vo.ReturnJsonVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ScoreGetReturnDetailImpl implements ScoreGetReturnDetail {

    private static final CustomLog log = CustomLog.getLogger(ScoreGetReturnDetailImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${orderReturn.url}")
    private String url;

    private static final Integer SUCCESS = 200;

    @Override
    public ReturnJsonVo getReturnDetail(String memGuid, String rgSeq, String rssSeq) {
        Map<String, Object> map = new HashMap<>();
        map.put("memGuid", memGuid);
        map.put("rgSeq", rgSeq);
        //rssSeq 参数已经废弃
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("data", JSONObject.toJSONString(map));
        String returnJson = restTemplate.postForObject(url, param, String.class);
        JSONObject jsonObject = JSONObject.parseObject(returnJson);
        Integer code = jsonObject.getInteger("code");
        if (Objects.equals(SUCCESS, code)) {
            JSONObject data = jsonObject.getJSONObject("data");
            return ReturnJsonVo.convertJson(data);
        } else {
            String msg = jsonObject.getString("msg");
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询退货信息异常。msg:" + msg);
        }
    }

    public static void main(String[] args) {
        ScoreGetReturnDetailImpl sd = new ScoreGetReturnDetailImpl();
        sd.getReturnDetail("37B1B68D-9560-3471-8B35-C74C348A6274", "201501CS28000016", "");
    }
}
