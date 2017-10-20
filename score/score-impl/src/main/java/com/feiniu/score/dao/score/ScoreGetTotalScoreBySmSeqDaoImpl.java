package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class ScoreGetTotalScoreBySmSeqDaoImpl implements ScoreGetTotalScoreBySmSeqDao {
    @Autowired
    @Qualifier("restTemplateSmallTimeout")
    private RestTemplate restTemplateSmallTimeout;
    @Value("${pointInfoBySmSeqs.api}")
    private String pointInfoByApiSmSeqs;
    @Value("${bill.token}")
    private String token;

    @Autowired
    private ScoreGetBillDao scoreGetBillDao;

    private static final String SMTYPE_OF_DPDJ = "2";

    @Override
    public JSONObject getScoreListBySmSeqList(Set<String> skuSeqSet, String areaCodeStr, Integer isFast, String whSeq) {
        Map<String, Object> info = new HashMap<>();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("token", token);
        String json;
        //去重后的字符串
        String skuSeqStr = StringUtils.join(skuSeqSet.iterator(), ",");
        info.put("skuSeqs", skuSeqStr);
        info.put("areaCode", areaCodeStr);
        info.put("isFast", isFast);
        info.put("whSeq", whSeq);
        map.add("data", JSONObject.toJSONString(info));
        //{ "code": "0", "sn": "0253", "msg": null, "data": { "result": { "100002537": [ { "itno": "100002537", "showPrice": null, "point": 0.1, "grade": null, "itType": null, "isItnoMain": null, "itSpecSeq": null, "sno": null, "smType": null } ], "100002538": [ { "itno": "100002538", "showPrice": null, "point": 0.1, "grade": null, "itType": null, "isItnoMain": null, "itSpecSeq": null, "sno": null, "smType": null } ] } } }
        json = restTemplateSmallTimeout.postForObject(pointInfoByApiSmSeqs, map, String.class);

        JSONArray returnArr = new JSONArray();
        //全部卖场正确才加入缓存
        Boolean allSuccessFlag = null;
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            String success = jsonObject.getString("success");
            if ("1".equals(success)) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                JSONObject itBillMap = new JSONObject();
                JSONObject resultObj = reFormatData(dataArray, itBillMap);
                for (String skuSeq : skuSeqSet) {
                    JSONObject returnObj = new JSONObject();
                    try {
                        JSONArray getArr = resultObj.getJSONArray(skuSeq);
                        returnObj = getPointBySmSeq(getArr, skuSeq, itBillMap);
                        returnArr.add(returnObj);
                    } catch (Exception e) {
                        allSuccessFlag = false;
                        returnObj.put("skuSeq", skuSeq);
                        returnObj.put("code", "-1");
                        returnObj.put("msg", "查询卖场积分失败。");
                        returnArr.add(returnObj);
                    }
                }
                if (allSuccessFlag == null) {
                    allSuccessFlag = true;
                }
            } else {
                allSuccessFlag = false;
                JSONObject returnObj = new JSONObject();
                returnObj.put("code", "-1");
                returnObj.put("msg", "查询卖场积分失败。");
                returnArr.add(returnObj);
            }
        } catch (Exception e) {
            allSuccessFlag = false;
            JSONObject returnObjExc = new JSONObject();
            returnObjExc.put("code", "-1");
            returnObjExc.put("msg", "查询卖场积分失败。");
            returnArr.add(returnObjExc);
        }
        JSONObject data = new JSONObject();
        data.put("resultList", returnArr);
        data.put("allSuccessFlag", allSuccessFlag);
        return data;
    }


    /*原数据的格式
     [
            {
              "cpSeq": "CC201042",
              "grade": null,
              "isItnoMain": null,
              "orgiSkuSeq": "KZ01161290300000454",
              "point": null,
              "showPrice": 90,
              "skuSeq": "KZ01161290300000454",
              "skuType": "0",
              "sno": null,
              "spuSeq": "PZ011612300000362"
            },
            {
              "cpSeq": "CC201042",
              "grade": null,
              "isItnoMain": null,
              "orgiSkuSeq": "KZ01161290300000455",
              "point": null,
              "showPrice": 20,
              "skuSeq": "KZ01161290300000455",
              "skuType": "0",
              "sno": null,
              "spuSeq": "PZ011612300000363"
            }
      ]
     */
     /*改造后，为方便组合卖场计算
       {
            "KZ01161290300000456": [
                {
                    "cpSeq": "CC201042",
                    "orgiSkuSeq": "KZ01161290300000456",
                    "showPrice": 50,
                    "skuSeq": "KZ01161290300000456",
                    "skuType": "0",
                    "spuSeq": "PZ011612300000363"
                }
            ],
            "KZ01161290300000458": [
                {
                    "cpSeq": "CC201042",
                    "orgiSkuSeq": "KZ01161290300000458",
                    "showPrice": 50,
                    "skuSeq": "KZ01161290300000458",
                    "skuType": "0",
                    "spuSeq": "PZ011612300000363"
                }
            ]
        }
    */
    private JSONObject reFormatData(JSONArray dataArray, JSONObject itBillMap) {
        JSONObject result = new JSONObject();
        HashMap<String, String> itNoMapToCpSeq = new HashMap<>();
        if (dataArray != null && !dataArray.isEmpty()) {
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject skuObj = dataArray.getJSONObject(i);
                JSONArray skuArr = result.getJSONArray(skuObj.getString("orgiSkuSeq"));
                if (skuArr == null || skuArr.isEmpty()) {
                    skuArr = new JSONArray();
                }
                skuArr.add(skuObj);
                result.put(skuObj.getString("orgiSkuSeq"), skuArr);
                itNoMapToCpSeq.put(skuObj.getString("skuSeq"), skuObj.getString("cpSeq"));
            }
        }
        //不能用等于，getBillsInfo函数返回的是新建的hashMap引用
        itBillMap.putAll(scoreGetBillDao.getBillsInfo(itNoMapToCpSeq, true));
        return result;
    }


    private JSONObject getPointBySmSeq(JSONArray getArr, String smSeq, JSONObject itBillMap) {
        JSONObject returnObj = new JSONObject();
        Integer totalScore = 0;
        Map<String, Double> priceGroupByItSpecSeq = new HashMap<>();
        Map<String, Double> totalPriceOfItNo = new HashMap<>();
        String returnCode = "0";
        String returnMsg = "";

        //积分多倍送默认倍数为1
        Double multiple = 1D;
        JSONArray itLists = new JSONArray();

        //默认不是单品多件，传空，是的时候传2
        returnObj.put("skuType", "");

        for (int i = 0; i < getArr.size(); i++) {

            JSONObject pointObj = getArr.getJSONObject(i);
            String grade = pointObj.getString("grade");
            String skuSeq = pointObj.getString("skuSeq");
            Double showPrice = pointObj.getDouble("showPrice");
            BigDecimal pointBigD;
            if (itBillMap.get(skuSeq) != null) {
                pointBigD = itBillMap.getJSONObject(skuSeq).getBigDecimal(ScoreGetBillDao.BILL_KEY);
            } else {
                pointBigD = new BigDecimal(0);
            }
            String skuType = pointObj.getString("skuType");
            String sno = pointObj.getString("sno");
            Double smType2Qty = pointObj.getDouble("smType2Qty");
            JSONObject outJson = new JSONObject();
            outJson.put("skuSeq", skuSeq);
            outJson.put("skuType", skuType);
            outJson.put("multiple", itBillMap.getJSONObject(skuSeq).getDouble(ScoreGetBillDao.MULTIPLE_KEY));
            itLists.add(outJson);

            if (showPrice == null) {
                showPrice = 0D;
            }
            Double point;
            if (pointBigD == null) {
                //预约商品可能售价为空，则商详页不显示积分
                if (StringUtils.equals(skuType, Constant.IT_TYPE_OF_YBSP)) {
                    returnCode = "0";
                    returnMsg = "success";
                    //计算卖场总积分的map列.为空则卖场积分为0
                    priceGroupByItSpecSeq.clear();
                    break;
                } else {
                    point = 0D;
                }
            } else {
                point = pointBigD.doubleValue();
            }

            //单品多件的在计算积分时不舍小数（等同于先想加后乘系数）。其他商品先乘系数，舍去小数位后相加
            Double pointOfItNo = totalPriceOfItNo.get(skuSeq);
            if (StringUtils.equals(skuType, SMTYPE_OF_DPDJ)) {
                returnObj.put("skuType", skuType);
                if (pointOfItNo != null) {
                    pointOfItNo += showPrice * point * smType2Qty;
                } else {
                    pointOfItNo = showPrice * point * smType2Qty;
                }
            } else {
                pointOfItNo = Math.floor(showPrice * point);
            }
            totalPriceOfItNo.put(skuSeq, pointOfItNo);

            //促销等级
            if (!StringUtils.equals(grade, Constant.PROMOTION_GRADE_OF_1)
                    && !StringUtils.equals(grade, Constant.PROMOTION_GRADE_OF_3)
                    && !StringUtils.equals(grade, Constant.PROMOTION_GRADE_OF_4)) {
                if (StringUtils.equals(skuType, SMTYPE_OF_DPDJ)) {
                    outJson.put("getScore", Math.floor(showPrice * point * smType2Qty));
                    outJson.put("smType2Qty", smType2Qty);
                } else {
                    outJson.put("getScore", Math.floor(showPrice * point));
                }
                if (skuType != null) {
                    //是一般商品
                    //有规则品群组号且itNo不同为规格品，取最大的。beta数据中,多规格品会出现只有一个有积分系数，取积分系数最大的
                    //有规则品群组号且itNo相同且smType为2为单品多件，相加
                    //有规则品群组号且itNo相同但smType不为2为同种商品的组合商品
                    if (!StringUtils.isEmpty(sno)) {
                        Double priceOfItSpecSeq = priceGroupByItSpecSeq.get(sno);
                        if (priceOfItSpecSeq != null) {
                            priceOfItSpecSeq = Math.max(pointOfItNo, priceOfItSpecSeq);
                        } else {
                            priceOfItSpecSeq = pointOfItNo;
                        }
                        priceGroupByItSpecSeq.put(sno, priceOfItSpecSeq);
                    }
                    //没有规则品群组号
                    else {
                        priceGroupByItSpecSeq.put("noItSpecSeq_" + skuSeq, pointOfItNo);
                    }
                    //自营多倍只计算一般商品，组合卖场取最大的
                    multiple = Math.max(multiple, itBillMap.getJSONObject(skuSeq).getDouble(ScoreGetBillDao.MULTIPLE_KEY));
                } else {
                    //商城商品
                    multiple = Math.max(multiple, itBillMap.getJSONObject(skuSeq).getDouble(ScoreGetBillDao.MULTIPLE_KEY));
                    priceGroupByItSpecSeq.put("noItType_" + skuSeq, pointOfItNo);
                }
            } else {
                outJson.put("getScore", 0);
            }
            returnCode = "0";
            returnMsg = "success";
        }
        for (Double value : priceGroupByItSpecSeq.values()) {
            totalScore += value.intValue();
        }
        returnObj.put("skuLists", itLists);
        returnObj.put("skuSeq", smSeq);
        returnObj.put("multiple", multiple.intValue());
        if (StringUtils.equals(returnCode, "0")) {
            returnObj.put("totalScore", totalScore);
        } else {
            returnObj.put("totalScore", 0);
        }
        returnObj.put("code", returnCode);
        returnObj.put("msg", returnMsg);
        return returnObj;
    }
}