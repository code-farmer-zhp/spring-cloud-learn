package com.feiniu.member.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.common.BaseFunction;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by xiaoyan.wu on 2016/10/28.
 * 我的足迹
 */
@Service
public class HistoryServiceImpl implements HistoryService {
    private CustomLog log = CustomLog.getLogger(HistoryServiceImpl.class);

    //我的足迹－分类信息
    @Value("${footprint.type.url}")
    private String typeUrl;
    //我的足迹－分类信息|降价促销信息
    @Value("${footprint.category.url}")
    private String categoryUrl;
    //我的足迹－列表信息
    @Value("${footprint.favorite.url}")
    private String historyListUrl;
    //我的足迹－删除信息
    @Value("${footprint.delete.url}")
    private String deleteHistoryUrl;
    //行销service搜索页接口url
    @Value("${api.mall.promotion.search.activity.url}")
    private String searchPageNormalActivityUrl;
    //获取秒杀活动信息
    @Value("${getSeckill.url}")
    private String getSeckillUrl;

    @Autowired
    private RestTemplate restTemplate;

    /*******************************************************************************************************************
     * 查询足迹分类信息
     * @param memGuid
     * @param areaCode
     * @param channel
     * @param token
     * @return
     ******************************************************************************************************************/
    public JSONObject historyType(String memGuid, String areaCode, Integer channel, String token) {

        JSONObject retJsonobj = null;

        String url = typeUrl;

        JSONObject areaJson = BaseFunction.areaJson(areaCode);

        JSONObject jsonObj = new JSONObject();
        //用户id
        jsonObj.put("memGuid", memGuid);
        //活动渠道(1:pc端 2:app端3:触屏)
        jsonObj.put("channel", channel);
        //区域代码
        jsonObj.put("areaCode", areaJson);
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("token", token);
        data.add("data", jsonObj.toJSONString());

        try {

            String resultJson = restTemplate.postForObject(url, data, String.class);
            retJsonobj = JSONObject.parseObject(resultJson);

        } catch (Exception e) {

        }

        return retJsonobj;

    }

    /*******************************************************************************************************************
     * 获取足迹促销降价信息
     * @param memGuid
     * @param areaCode
     * @param channel
     * @param token
     * @return
     ******************************************************************************************************************/
    public JSONObject historyKind(String memGuid, String areaCode, Integer channel, String token) {

        JSONObject retJsonobj = null;

        String url = categoryUrl;

        JSONObject areaJson = BaseFunction.areaJson(areaCode);

        JSONObject jsonObj = new JSONObject();
        //用户id
        jsonObj.put("memGuid", memGuid);
        //活动渠道(1:pc端 2:app端3:触屏)
        jsonObj.put("channel", channel);
        //区域代码
        jsonObj.put("areaCode", areaJson);
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("token", token);
        data.add("data", jsonObj.toJSONString());

        try {

            String resultJson = restTemplate.postForObject(url, data, String.class);
            retJsonobj = JSONObject.parseObject(resultJson);

        } catch (Exception e) {

        }

        return retJsonobj;

    }

    /*******************************************************************************************************************
     * 根据页数获取足迹信息
     * @param memGuid    用户主key
     * @param areaCode   地区信息
     * @param pageIndex  第几页
     * @param pageSize   每一个显示的条数
     * @param type       查询类型(kind:分类 moreAct:促销 lowPrice:降价 为空查询全部)
     * @param ids        查询条件
     * @param token
     * @return
     ******************************************************************************************************************/
    @Override
    public JSONObject historyListByPage(String memGuid, String areaCode, Integer pageIndex, Integer pageSize
                                        , String type, String ids, String token) {

        JSONObject retJsonobj = null;

        try {
            JSONObject jsonObj = new JSONObject();
            //每页数量
            jsonObj.put("pageSize", pageSize);
            //页码数
            jsonObj.put("pageIndex", pageIndex);

            retJsonobj = getHistory(jsonObj, memGuid, areaCode, type, ids, token);

        } catch (Exception e) {
            log.error( "调用historyListByPage异常, memGuid=" + memGuid + ", 失败原因:" + e.getMessage(), e);
        }

        return retJsonobj;

    }

    /*******************************************************************************************************************
     * 根据页数获取足迹信息
     * @param memGuid    用户主key
     * @param areaCode   地区信息
     * @param start      从第几条开始
     * @param size       显示到第几条
     * @param type       查询类型(kind:分类 moreAct:促销 lowPrice:降价 为空查询全部)
     * @param ids        查询条件
     * @param token
     * @return
     ******************************************************************************************************************/
    @Override
    public JSONObject historyListByIdx(String memGuid, String areaCode, Integer start, Integer size
                                       , String type, String ids, String token) {

        JSONObject retJsonobj = null;

        try {
            JSONObject jsonObj = new JSONObject();
            //从第几条开始
            jsonObj.put("size", size);
            //显示到第几条
            jsonObj.put("start", start);

            retJsonobj = getHistory(jsonObj, memGuid, areaCode, type, ids, token);

        } catch (Exception e) {
            log.error( "调用historyListByIdx异常, memGuid=" + memGuid + ", 失败原因:" + e.getMessage(), e);
        }

        return retJsonobj;

    }

    private JSONObject getHistory(JSONObject jsonObj, String memGuid, String areaCode, String type, String ids, String token) {

        JSONObject retJsonobj = null;

        try {

            JSONObject areaJson = BaseFunction.areaJson(areaCode);

            //用户id
            jsonObj.put("memGuid", memGuid);
            //活动渠道(1:pc端 2:app端3:触屏)
            jsonObj.put("channel", 1);
            //区域代码
            jsonObj.put("areaCode", areaJson);
            //促商品ids
            if (type.equals("moreAct")) {
                jsonObj.put("moreActProductIds", ids);
            }
            //降价商品ids
            if (type.equals("lowPrice")) {
                jsonObj.put("lowPriceProductIds", ids);
            }
            //类目编号
            if (type.equals("kind")) {
                jsonObj.put("kindId", ids);
            }

            MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
            data.add("token", token);
            data.add("data", jsonObj.toJSONString());

            String jsonStr = restTemplate.postForObject(historyListUrl, data, String.class);

            retJsonobj = JSONObject.parseObject(jsonStr);

        } catch (Exception e) {
            log.error("调用getHistory异常, memGuid=" + memGuid + ", 失败原因:" + e.getMessage(), e);
        }

        return retJsonobj;

    }

    /*******************************************************************************************************************
     * 根据页数获取足迹信息
     * @param memGuid    用户主key
     * @param type:删除类型(0:全部删除　1:按卖场ＩＤ删除　2:按年月日删除  3:删除某个日期以前的数据)
     * @param ids:删除记录
     * @param token
     * @return
     ******************************************************************************************************************/
    @Override
    public JSONObject delHistory(String memGuid, String type, String ids, String token ) {

        JSONObject retJsonobj = null;

        try {
            JSONObject jsonObj = new JSONObject();

            //用户id
            jsonObj.put("memGuid", memGuid);

            switch(type) {
                case "0":
                    break;
                case "1":
                    //按卖场ＩＤ删除记录
                    jsonObj.put("messageIds", ids);
                    break;
                case "2":
                    //按时间删除商品-年月日
                    jsonObj.put("time", ids);
                    break;
                case "3":
                    //按时间删除商品-某个日期以前
                    jsonObj.put("time", ids);
                    break;
                default:
                    break;
            }

            MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
            data.add("token", token);
            data.add("data", jsonObj.toJSONString());

            String jsonStr = restTemplate.postForObject(deleteHistoryUrl, data, String.class);

            retJsonobj = JSONObject.parseObject(jsonStr);

        } catch (Exception e) {
            log.error("调用delHistory异常, memGuid=" + memGuid + ", 失败原因:" + e.getMessage(), e);
        }

        return retJsonobj;

    }

    /*******************************************************************************************************************
     * 查询行销活动信息
     * @param activityQd
     * @param areaCode
     * @param skuList
     * @return
     ******************************************************************************************************************/
    public JSONObject searchPageNormalActivity (String activityQd, String areaCode, JSONArray skuList) {

        String retText = null;
        JSONObject retJsonobj = null;

        String url = searchPageNormalActivityUrl;

        JSONObject areaJson = BaseFunction.areaJson(areaCode);
        String provinceCode = areaJson.getString("provinceCode");

        JSONObject dataObject = new JSONObject();
        dataObject.put("skuList", skuList);
        dataObject.put("provinceCode", provinceCode);
        dataObject.put("activityQd", Integer.parseInt(activityQd));

        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("param", dataObject.toJSONString());

        try {

            retText = restTemplate.postForObject(url, data, String.class);
//            retText = new String(jsonStr.getBytes("ISO8859-1"), "UTF-8");
            retJsonobj = JSONObject.parseObject(retText);

        } catch (Exception e) {

            log.error("调用行销活动异常, skuId=" + skuList + ", 失败原因:" + e.getMessage(), e);

        }

        return retJsonobj;

    }

    /*******************************************************************************************************************
     * 查询秒杀活动信息
     * @param provinceId
     * @param pgSeq
     * @param seckillSmSeqs
     * @return
     ******************************************************************************************************************/
    public JSONObject getSeckill (String provinceId, String pgSeq, JSONArray seckillSmSeqs) {

        String retText = null;
        JSONObject retJsonobj = null;

        String url = getSeckillUrl;

        JSONObject dataObject = new JSONObject();
        dataObject.put("pgSeq", pgSeq);
        dataObject.put("provinceId", provinceId);
        dataObject.put("seckillSmSeqs", seckillSmSeqs);

        MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();
        data.add("data", dataObject.toJSONString());

        try {

            retText = restTemplate.postForObject(url, data, String.class);
            retJsonobj = JSONObject.parseObject(retText);

        } catch (Exception e) {

            log.error( "调用秒杀活动异常, 失败原因:" + e.getMessage(), e);

        }

        return retJsonobj;

    }

}
