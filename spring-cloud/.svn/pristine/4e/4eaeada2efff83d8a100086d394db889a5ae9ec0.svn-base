package com.feiniu.member.service.score;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class MutilScoreService {

    private static final Log log = LogFactory.getLog(MutilScoreService.class);

    @Value("${mutil.score.url}")
    private String mutilScoreUrl;

    @Autowired
    private RestTemplate restTemplate;


    public Map<String, Integer> mutilScore(String provinceCode, String cityCode, String areaCode, Set<String> skus) {
        Map<String, Integer> result = new HashMap<>();
        try {
            Map<String, Object> areaCodeInfo = new HashMap<>();
            areaCodeInfo.put("provinceCode", provinceCode);
            areaCodeInfo.put("cityCode", cityCode);
            areaCodeInfo.put("areaCode", areaCode);
            Map<String, Object> param = new HashMap<>();
            param.put("areaCode", areaCodeInfo);
            param.put("skuSeqs", StringUtils.join(skus, ","));

            String getStr = restTemplate.getForObject(mutilScoreUrl + "?data={data}", String.class, JSONObject.toJSONString(param));
            JSONObject jsonObject = JSONObject.parseObject(getStr);
            int code = jsonObject.getIntValue("code");
            if (100 == code) {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray resultList = data.getJSONArray("resultList");
                for (int i = 0; i < resultList.size(); i++) {
                    JSONObject skuInfo = resultList.getJSONObject(i);
                    int score = skuInfo.getIntValue("totalScore");
                    int multiple = skuInfo.getIntValue("multiple");
                    if (score > 0 && multiple > 1) {
                        String skuSeq = skuInfo.getString("skuSeq");
                        result.put(skuSeq, multiple);
                    }
                }

            }
        } catch (Exception e) {
            log.error("多倍积分查询异常", e);
        }
        return result;
    }
}
