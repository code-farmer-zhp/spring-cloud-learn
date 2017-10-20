package com.feiniu.score.dao.score;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.vo.StoreInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Repository
public class ScoreGetStoreInfoDaoImpl implements ScoreGetStoreInfoDao {

    private static final Log log = LogFactory.getLog(ScoreGetStoreInfoDaoImpl.class);

    @Autowired
    @Qualifier("restTemplateBigTimeout")
    private RestTemplate restTemplate;

    @Value("${score.supseq.api}")
    private String supSeqUrl;

    @Value("${score.zhaoshang.api}")
    private String zhaoShangInfoUrl;

    @Value("${score.mallName.api}")
    private String mallNameUrl;

    @Value("${get.new.ogseq.api}")
    private String getNewOgSeqUrl;

    private String getNewOgSeq(String ogSeq, String memGuid) {
        Map<String, String> info = new HashMap<>();
        info.put("ogSeq", ogSeq);
        info.put("memGuid", memGuid);

        Map<String, Object> data = new HashMap<>();
        data.put("ogSeqList", Collections.singletonList(info));

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("data", JSONObject.toJSONString(data));
        try {
            String jsonStr = restTemplate.postForObject(getNewOgSeqUrl, param, String.class);
            JSONObject json = JSONObject.parseObject(jsonStr);
            int code = json.getIntValue("code");
            if (code == 200) {
                JSONArray array = json.getJSONArray("data");
                return array.getJSONObject(0).getString("ono");
            }
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询新订单号失败");
        } catch (Exception e) {
            log.error(e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询新订单号异常");
        }
    }

    @Override
    public Map<String, StoreInfoVo> getStoreNoByOgSeq(String ogSeq, String memGuid) {
        //根据老订单号查询出新订单号
        String newOgSeq = getNewOgSeq(ogSeq, memGuid);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("ono", newOgSeq);
        try {
            String jsonStr = restTemplate.postForObject(supSeqUrl, param, String.class);
            JSONObject json = JSONObject.parseObject(jsonStr);
            int code = json.getIntValue("code");
            if (code == 200) {
                JSONArray array = json.getJSONArray("data");
                Map<String, Set<String>> seqMap = new HashMap<>();
                Set<String> supSeqSet = new HashSet<>();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject data = array.getJSONObject(i);
                    String skuSeq = data.getString("skuSeq");
                    String supSeq = data.getString("supSeq");
                    if (StringUtils.isNotEmpty(supSeq)) {
                        Set<String> skuSet = seqMap.get(supSeq);
                        if (skuSet == null) {
                            skuSet = new HashSet<>();
                            seqMap.put(supSeq, skuSet);
                        }
                        skuSet.add(skuSeq);
                        supSeqSet.add(supSeq);
                    }
                }
                if (supSeqSet.size() > 0) {
                    return getZhaoShangInfo(supSeqSet, seqMap);
                } else {
                    return new HashMap<>();
                }
            }
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询门店编号失败");
        } catch (Exception e) {
            log.error(e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, e.getMessage());
        }
    }

    private Map<String, StoreInfoVo> getZhaoShangInfo(Set<String> supSeqSet, Map<String, Set<String>> seqMap) {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("supSeqs", StringUtils.join(supSeqSet, ","));
        try {
            String jsonStr = restTemplate.postForObject(zhaoShangInfoUrl, param, String.class);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            int code = jsonObject.getIntValue("code");
            if (code == 200) {
                JSONArray array = jsonObject.getJSONArray("data");
                Map<String, StoreInfoVo> result = new HashMap<>();
                for (int i = 0; i < array.size(); i++) {
                    JSONObject data = array.getJSONObject(i);
                    String supplierType = data.getString("supplierType");
                    if ("3".equals(supplierType)) {
                        String supId = data.getString("supId");
                        String storeNo = data.getString("storeNo");
                        String supSeq = data.getString("supSeq");
                        Set<String> skuSet = seqMap.get(supSeq);
                        for (String sku : skuSet) {
                            if (StringUtils.isEmpty(storeNo) || "_".equals(storeNo)) {
                                storeNo = "";
                            }
                            result.put(sku, new StoreInfoVo(supId, storeNo));
                        }
                    }
                }
                return result;
            }
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询招商门店编号失败");
        } catch (Exception e) {
            log.error(e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询招商门店编号异常");
        }
    }


    @Override
    public Map<String, String> getStoreNameBySellerNos(Set<String> sellerNos) {
        Map<String, String> result = new HashMap<>();
        if (sellerNos.size() == 0) {
            return result;
        }
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("supIds", StringUtils.join(sellerNos, ","));
        try {
            String jsonStr = restTemplate.postForObject(zhaoShangInfoUrl, param, String.class);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            int code = jsonObject.getIntValue("code");
            if (code == 200) {
                JSONArray array = jsonObject.getJSONArray("data");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject data = array.getJSONObject(i);
                    String supId = data.getString("supId");
                    String shortname = data.getString("shortname");
                    result.put(supId, shortname);
                }
                return result;
            }
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询招商门店名称失败");
        } catch (Exception e) {
            log.error(e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询招商门店名称异常");
        }
    }

    @Override
    public Map<String, Boolean> isStore(List<String> sellerNos) {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("supIds", StringUtils.join(sellerNos, ","));
        String jsonStr = restTemplate.postForObject(zhaoShangInfoUrl, param, String.class);
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        int code = jsonObject.getIntValue("code");
        if (code == 200) {
            JSONArray array = jsonObject.getJSONArray("data");
            Map<String, Boolean> result = new HashMap<>();
            for (int i = 0; i < array.size(); i++) {
                JSONObject data = array.getJSONObject(i);
                String supId = data.getString("supId");
                String supplierType = data.getString("supplierType");
                if ("3".equals(supplierType)) {
                    result.put(supId, true);
                } else {
                    result.put(supId, false);
                }
            }
            return result;
        }
        throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "查询招商门店异常");
    }

    @Override
    public Map<String, String> getMallNameBySellerNos(Set<String> sellerNos) {
        Map<String, String> result = new HashMap<>();
        if (sellerNos.size() == 0) {
            return result;
        }
        List<String> lists = new ArrayList<>(sellerNos);
        List<String> batch = new ArrayList<>(500);
        for (int i = 0; i < lists.size(); i++) {
            batch.add(lists.get(i));
            if (i % 500 == 0) {
                result.putAll(getMallNameBatch(batch));
                batch.clear();
            }
        }
        if (batch.size() > 0) {
            result.putAll(getMallNameBatch(batch));
        }
        return result;
    }

    private Map<String, String> getMallNameBatch(List<String> lists) {
        Map<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append("?merchantIds=");
        for (String str : lists) {
            sb.append(str).append(",");
        }
        String param = sb.deleteCharAt(sb.length() - 1).toString();
        String resStr = restTemplate.getForObject(mallNameUrl + param, String.class);
        JSONObject jsonObj = JSONObject.parseObject(resStr);
        int code = jsonObj.getIntValue("code");
        if (code == 200) {
            String body = jsonObj.getString("body");
            String[] names = body.split(",");
            for (int i = 0; i < lists.size(); i++) {
                result.put(lists.get(i), names[i]);
            }
        } else {
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "商城卖家名称查询失败");
        }
        return result;
    }
}
