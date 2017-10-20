package com.feiniu.score.dao.growth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Repository
public class GrowthCommonDaoImpl implements GrowthCommonDao {
	

    @Autowired
    private RestTemplate restTemplate;

    @Value("${getCardInfo.api}")
    private String getCardInfoApi;

    private final static int SUCCESS_CODE = 100;
    private final static String SEARCH_TYPE = "5";

    @Override
    public Double getUsePriceByActSeq(String actSeq) {
        //[{ "card_type":"2", "card_id":"201509C2200000003"}]
        JSONObject paramJson = new JSONObject();
        Double returnPrice = null;
        paramJson.put("card_type", "2");
        paramJson.put("card_id", actSeq);
        JSONArray paramArr = new JSONArray();
        paramArr.add(paramJson);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        params.add("data", paramArr.toString());

        String returnStr = restTemplate.postForObject(getCardInfoApi, params, String.class);

        if (returnStr != null) {
            JSONObject returnObj = null;
            try {
                returnObj = (JSONObject) JSONObject.parse(returnStr);
            } catch (Exception e) {
                throw new ScoreException(ResultCode.RESULT_TYPE_CONV_ERROR, "查询卡券返回值转换为json格式错误");
            }
            if (returnObj != null && returnObj.get("Body") != null) {
                JSONObject body = (JSONObject) returnObj.get("Body");
                String returnData = body.getString("data");
               /* String returnDataDe;
                try {
                    returnDataDe = new String(returnData.getBytes(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new ScoreException(ResultCode.RESULT_TYPE_CONV_ERROR, "查询卡券返回值转换为UTF-8错误");
                }*/
                JSONArray restArr = JSONArray.parseArray(returnData);
                for (int j = 0; j < restArr.size(); j++) {
                    JSONObject restOnj = restArr.getJSONObject(j);
                    String restActSeq = restOnj.getString("seq");
                    if (actSeq.equals(restActSeq)) {
                        returnPrice = restOnj.getDouble("price");
                    }
                }
            }
        }
        return returnPrice;
    }
}
