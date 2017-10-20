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

import java.util.HashMap;
import java.util.Map;

@Repository
public class ScoreGetCommmentDetailDaoImpl implements ScoreGetCommmentDetailDao {

    private static final CustomLog LOG = CustomLog.getLogger(ScoreGetCommmentDetailDaoImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    @Value("${comment.url}")
    private String url;

    //图片路径前缀
    @Value("${comment.img.prefix}")
    private String imgPrefix;

    //站点路径前缀
    @Value("${comment.websit.prefix}")
    private String webSitePrefix;

    @Override
    public Map<String, Object> getCommentDetail(Long commentSeq) {
        try {
            //params= {"commentIds": "12073049" }
            Map<String, Object> req = new HashMap<>();
            req.put("commentIds", commentSeq + "");
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            param.add("params", JSONObject.toJSONString(req));
            String resultJson = restTemplate.postForObject(url, param, String.class);
            JSONObject jsonObj = JSONObject.parseObject(resultJson);
            Map<String, Object> data = new HashMap<>();
            if (jsonObj.getIntValue("code") == 200) {
                JSONObject jsonData = jsonObj.getJSONArray("data").getJSONObject(0);
                String goodsName = jsonData.getString("goodsName");
                String picUrl = jsonData.getString("picUrl");
                String smSeq = jsonData.getString("smSeq");
                data.put("name", goodsName);
                data.put("sourcleUrl", "/" + smSeq);
                data.put("picUrl", picUrl);
                data.put("smSeq", smSeq);
                return data;
            }
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "请求评论详细信息失败。");
        } catch (Exception e) {
            LOG.error("请求评论详细信息失败", "getCommentDetail", e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "请求评论详细信息失败。");
        }

    }

    @Override
    public Map<Long, Object> getCommentDetails(String commentSeqs) {
        try {
            Map<String, Object> req = new HashMap<>();
            req.put("commentIds", commentSeqs);
            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            param.add("params", JSONObject.toJSONString(req));
            String resultJson = restTemplate.postForObject(url, param, String.class);
            JSONObject jsonObj = JSONObject.parseObject(resultJson);
            Map<Long, Object> data = new HashMap<>();
            if (jsonObj.getIntValue("code") == 200) {
                JSONArray jsonArray = jsonObj.getJSONArray("data");
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map<String, Object> info = new HashMap<>();
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    Long id = jsonData.getLong("id");
                    String goodsName = jsonData.getString("goodsName");
                    String picUrl = jsonData.getString("picUrl");
                    String smSeq = jsonData.getString("smSeq");
                    info.put("goodsName", goodsName);
                    info.put("sourceUrl", webSitePrefix+"/" + smSeq);
                    info.put("smSeq", smSeq);
                    info.put("picUrl", imgPrefix + picUrl);
                    data.put(id, info);
                }
                return data;
            } else {
                throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "批量请求评论详细信息失败。");
            }
        } catch (Exception e) {
            LOG.error("批量请求评论详细信息失败.", "getCommentDetails", e);
            throw new ScoreException(ResultCode.RESULT_STATUS_EXCEPTION, "批量请求评论详细信息失败。");
        }

    }
}
