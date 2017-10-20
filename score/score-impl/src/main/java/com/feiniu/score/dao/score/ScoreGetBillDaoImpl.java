package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;

@Repository
public class ScoreGetBillDaoImpl implements ScoreGetBillDao {

	@Autowired
	@Qualifier("restTemplateSmallTimeout")
	private RestTemplate smallRestTemplate;

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${bill.token}")
	private String token;

	@Value("${bill.and.multiple.urls}")
	private String billAndMultipleUrls;

	/*
		"201311CG050000114":{
			"bill":"0.1",
			"multiple":"1.0"
		}
	 */
	@Override
	public JSONObject getBillsInfo(HashMap<String, String> itNoMapToCpSeq,boolean isFront) {
		JSONObject ItNoBillDtoMaps=new JSONObject();
		JSONArray itNoAndCpSeqArrs=new JSONArray();
		for (String itNo : itNoMapToCpSeq.keySet()) {
			JSONObject obj = new JSONObject();
			obj.put("skuSeq", itNo);
			obj.put("cpSeq", itNoMapToCpSeq.get(itNo));
			itNoAndCpSeqArrs.add(obj);
			if (itNoAndCpSeqArrs.size() >= 20) {
				ItNoBillDtoMaps.putAll(getBillsInfo(itNoAndCpSeqArrs,isFront));
				itNoAndCpSeqArrs.clear();
			}
		}
		if(!itNoAndCpSeqArrs.isEmpty()){
			ItNoBillDtoMaps.putAll(getBillsInfo(itNoAndCpSeqArrs,isFront));
		}
		return ItNoBillDtoMaps;
	}

	//isFront，是否前台调用接口。是的话用较小的超时时间，否则用较大的
	private  JSONObject getBillsInfo(JSONArray itNoAndCpSeqArrs,boolean isFront){
		JSONObject listObj=new JSONObject();
		listObj.put("list",itNoAndCpSeqArrs);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("data", JSONObject.toJSONString(listObj));
		String json;
		if(isFront) {
			json=smallRestTemplate.postForObject(billAndMultipleUrls, map, String.class);
		}else{
			json=restTemplate.postForObject(billAndMultipleUrls, map, String.class);
		}
		JSONObject jsonObject = JSONObject.parseObject(json);
		String successFlag = jsonObject.getString("success");
		JSONObject billMap=new JSONObject();

		if("1".equals(successFlag)){
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for(int i=0;i<jsonArray.size();i++){
				JSONObject jsonBillItno = jsonArray.getJSONObject(i);
				String skuSeq = jsonBillItno.getString("skuSeq");
				BigDecimal bill = jsonBillItno.getBigDecimal("bill");
				if(bill == null){
					bill = BigDecimal.valueOf(0.0000);
				}
				//必须在里面，不然put是前一个的引用
				JSONObject inbd=new JSONObject();
				inbd.put(BILL_KEY,bill);
				inbd.put(MULTIPLE_KEY,jsonBillItno.get("multiple"));
				billMap.put(skuSeq,inbd);
			}
		}else if("0".equals(successFlag)){
			throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"根据itNo查询商品返点比例失败。");
		}else {
			throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION,"根据itNo查询商品返点比例发生错误。");
		}
		return billMap;
	}
}
