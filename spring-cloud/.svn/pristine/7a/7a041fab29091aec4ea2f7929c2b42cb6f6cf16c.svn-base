package com.feiniu.favorite.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.favorite.entity.FavoriteSumVO;
import com.feiniu.favorite.mapper.FavoriteMapper;
import com.feiniu.favorite.service.KafKaService;
import com.feiniu.kafka.client.ProducerClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class KafkaServiceImpl implements KafKaService {

    public static final Log log = LogFactory.getLog(KafkaServiceImpl.class);

    @Value("${kafka.topic.1}")
    private String kafkaTopic1;

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private ProducerClient<String, String> producerClient;

    @Override
    public void pushAllFavoriteData() {
        final List<FavoriteSumVO> list = favoriteMapper.getAllFavoriteData();
        // 执行消息推送的功能
        JSONArray ja = new JSONArray();
        ja.addAll(list);
        String msg = ja.toJSONString();
        log.info("topic:" + kafkaTopic1 + ",msg:" + msg);
        producerClient.sendMessage(kafkaTopic1, System.currentTimeMillis() + "", msg);

    }

    @Override
    public void pushFavoriteData(Map<String, Object> smseqs) {
        String message = JSONObject.toJSONString(smseqs);
        log.info("topic:" + kafkaTopic1 + ",msg:" + message);
        producerClient.sendMessage(kafkaTopic1, System.currentTimeMillis() + "", message);
    }

    @Override
    public List<String> getSmseqsByIds(String ids) {
        String[] smseq = ids.split(",");
        List<String> asList = Arrays.asList(smseq);
        return favoriteMapper.getSmseqsByIds(asList);
    }

    @Override
    public List<String> getSmseqsByMemGuid(String memGuid) {
        return favoriteMapper.getSmseqsByMemGuid(memGuid);
    }

}
