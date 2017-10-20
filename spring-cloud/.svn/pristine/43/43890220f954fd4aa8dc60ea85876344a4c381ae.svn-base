package com.feiniu.member.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by xiaoyan.wu on 2016/10/28.
 */
public interface HistoryService {

    //获取足迹分类信息
    JSONObject historyType(String memGuid, String areaCode, Integer channel, String token);

    //获取足迹促销降价信息
    JSONObject historyKind(String memGuid, String areaCode, Integer channel, String token);

    //根据页数获取足迹信息
    JSONObject historyListByPage(String memGuid, String areaCode, Integer pageIndex, Integer pageSize, String type, String ids, String token);

    //根据条数获取足迹信息
    JSONObject historyListByIdx(String memGuid, String areaCode, Integer start, Integer size, String type, String ids, String token);

    //删除足迹信息
    JSONObject delHistory(String memGuid, String type, String ids, String token);

    //获取商品行销活动信息
    JSONObject searchPageNormalActivity(String activityQd, String areaCode, JSONArray skuList);

    //获取秒杀活动信息
    JSONObject getSeckill(String provinceId, String pgSeq, JSONArray seckillSmSeqs);

}