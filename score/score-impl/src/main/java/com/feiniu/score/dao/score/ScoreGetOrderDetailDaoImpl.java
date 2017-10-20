package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONArray;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ScoreGetOrderDetailDaoImpl implements ScoreGetOrderDetailDao {

	private static final CustomLog log = CustomLog.getLogger(ScoreGetOrderDetailDaoImpl.class);
	@Autowired
	private RestTemplate restTemplate;

	@Value("${order.url}")
	private String url;

	private static final String SUCCESS = "100";

	@Override
	public List<Map<String, Object>> getOrderDetailByOgSeq(String memGuid,
			String ogSeq) {
		Map<String, Object> mapParam = new HashMap<>();
		mapParam.put("memberId", memGuid);
		mapParam.put("ogSeq", ogSeq);
		MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
		info.add("request", JSONObject.toJSONString(mapParam));
		String resultJson = restTemplate.postForObject(url, info, String.class);
		/*try {
			resultJson = new String(resultJson.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.info("转码失败。",e);
		}*/
		JSONObject parseObj = JSONObject.parseObject(resultJson);
		String code = parseObj.getString("code");
		List<Map<String, Object>> packageList = new ArrayList<>();
		if (SUCCESS.equals(code)) {
			JSONObject resultObj = parseObj.getJSONObject("result");
			if (resultObj != null) {
				JSONArray packageArray = resultObj.getJSONArray("packages");
				for (int i = 0; i < packageArray.size(); i++) {
					JSONObject apackage = packageArray.getJSONObject(i);
					Map<String, Object> packageMap = new HashMap<>();
					String packNo = apackage.getString("packNo");
					String supplierType = apackage.getString("supplierType");
					JSONArray itemArray = apackage.getJSONArray("itemList");
					List<Map<String, Object>> itemList = new ArrayList<>();
					for (int index = 0; index < itemArray.size(); index++) {
						JSONObject item = itemArray.getJSONObject(index);
						Map<String, Object> itemMap = new HashMap<>();
						String smSeq = item.getString("smSeq");
						String name = item.getString("name");
						String sourcleUrl = item.getString("sourcleUrl");
						String picUrl = item.getString("picUrl");
						String kind = item.getString("kind");
						itemMap.put("smSeq", smSeq);
						itemMap.put("name", name);
						itemMap.put("sourcleUrl", sourcleUrl);
						itemMap.put("picUrl", picUrl);
						itemMap.put("kind", kind);
						itemList.add(itemMap);
					}
					packageMap.put("dataList", itemList);
					packageMap.put("packNo", packNo);
					packageMap.put("supplierType",supplierType);
					packageList.add(packageMap);
				}
			} else {
				log.info("没有查到订单的详细信息。","getOrderDetailByOgSeq");
			}
		} else {
			log.error("请求订单详细信息失败。","getOrderDetailByOgSeq");
			throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION,"请求订单详细信息失败。");
		}
		return packageList;
	}
	public static void main(String[] args) {
		ScoreGetOrderDetailDaoImpl sgd = new ScoreGetOrderDetailDaoImpl();
		List<Map<String, Object>> orderDetailByOgSeq = sgd
				.getOrderDetailByOgSeq("B03D348D-6F7A-3405-80EF-C74271279EA0",
						"201503CO14000070");
		System.out.println(JSONObject.toJSONString(orderDetailByOgSeq));
	}

}
