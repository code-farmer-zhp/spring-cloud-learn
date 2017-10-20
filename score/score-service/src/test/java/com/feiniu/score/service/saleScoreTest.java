package com.feiniu.score.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.Constant;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yue.teng on 2016/5/12.
 */

public class saleScoreTest {
    @Test
    public void totalScore(){

        String json="{ \"code\": \"0\", \"sn\": \"0170\", \"msg\": null, \"data\": { \"result\": [ { \"itno\": \"201601CG250000020\", \"showPrice\": 9, \"point\": 0.11, \"grade\": null, \"itType\": \"1\", \"isItnoMain\": \"1\", \"itSpecSeq\": \"201601C250000012\", \"sno\": null, \"smType\": null } ] } }";
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONObject returnObj = new JSONObject();
        JSONObject dataObj = jsonObject.getJSONObject("data");
        JSONArray getArr = dataObj.getJSONArray("result");
        Integer totalScore = 0;
        Map<String, Double> priceGroupByItSpecSeq = new HashMap<>();
        Map<String, Double> totalPriceOfItNo = new HashMap<>();
        String returnCode = "0";
        String returnMsg = "";
        for (int i = 0; i < getArr.size(); i++) {
            JSONObject resultObj = getArr.getJSONObject(i);
            String grade = resultObj.getString("grade");
            String itNo = resultObj.getString("itno");
            Double showPrice = resultObj.getDouble("showPrice");
            Double point = resultObj.getDouble("point");

            //商城没有以下三个属性
            String itType = resultObj.getString("itType");
            String itSpecSeq = resultObj.getString("itSpecSeq");
            String smType = resultObj.getString("smType");
            if (showPrice == null) {
                showPrice = 0D;
            }
            if (point == null) {
                //预约商品可能售价为空，则商详页不显示积分
                if(StringUtils.equals(itType, Constant.IT_TYPE_OF_YBSP)){
                    returnCode = "0";
                    returnMsg = "success";
                    //计算卖场总积分的map列.为空则卖场积分为0
                    priceGroupByItSpecSeq.clear();
                    break;
                }
                else{
                    point = 0D;
                }
            }
            //按itNo分组，同组的相加
            Double priceOfItNo = totalPriceOfItNo.get(itNo);
            if (StringUtils.equals(smType, "2")) {
                if (priceOfItNo != null) {
                    priceOfItNo += showPrice * point;
                } else {
                    priceOfItNo = showPrice * point;
                }
            } else {
                if (priceOfItNo != null) {
                    priceOfItNo += Math.floor(showPrice * point);
                } else {
                    priceOfItNo = Math.floor(showPrice * point);
                }
            }
            totalPriceOfItNo.put(itNo, priceOfItNo);

            //促销等级不为1,且不为3
            if (!StringUtils.equals(grade, Constant.PROMOTION_GRADE_OF_1)
                    && !StringUtils.equals(grade, Constant.PROMOTION_GRADE_OF_3)) {
                if (itType != null) {
                    //是一般商品
                    if (StringUtils.equals(itType, Constant.IT_TYPE_OF_YBSP)) {
                        //有规则品群组号且itNo不同为规格品，取最大的。beta数据中,多规格品会出现只有一个有积分系数，取积分系数最大的
                        //有规则品群组号且itNo相同且smType为2为单品多件，相加
                        //有规则品群组号且itNo相同但smType不为2为同种商品的组合商品
                        if (!StringUtils.isEmpty(itSpecSeq)) {
                            Double priceOfItSpecSeq = priceGroupByItSpecSeq.get(itSpecSeq);

                            if (priceOfItSpecSeq != null) {
                                priceOfItSpecSeq = Math.max(priceOfItNo, priceOfItSpecSeq);
                            } else {
                                priceOfItSpecSeq = priceOfItNo;
                            }
                            priceGroupByItSpecSeq.put(itSpecSeq, priceOfItSpecSeq);
                        }
                        //没有规则品群组号
                        else {
                            priceGroupByItSpecSeq.put("noItSpecSeq_" + itNo, priceOfItNo);
                        }
                    }
                } else {
                    priceGroupByItSpecSeq.put("noItType_" + itNo,priceOfItNo);
                }
            }
            returnCode = "0";
            returnMsg = "success";
        }
        for (Double value : priceGroupByItSpecSeq.values()) {
            totalScore += value.intValue();
        }
    }
}
