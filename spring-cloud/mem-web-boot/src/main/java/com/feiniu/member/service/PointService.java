package com.feiniu.member.service;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.dto.Result;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.util.IsNumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by yue.teng on 2016-11-02.
 */
@Service
public class PointService {
    public static final CustomLog log = CustomLog.getLogger(PointService.class);

    @Autowired
    protected CommonService commonService;
    @Autowired
    protected RestTemplate restTemplate;
    @Value("${getUserScoreInfo.api}")
    private String getUserScoreInfoApi;

    @Value("${getUserScoreDetailList.api}")
    private String getUserScoreDetailListApi;

    private int maxPageSize=50;
    private int defaultPageSize=15;

    /**
     * 获取用户积分基本信息 getUserScoreInfo
     * @param memGuid
     * @return JSONObject
     */
    public JSONObject getUserScoreInfo(String memGuid) {
        try {
            String userScoreInfoJson = restTemplate.getForObject(getUserScoreInfoApi + "?memGuid=" + memGuid, String.class);
            JSONObject resultObj = JSONObject.parseObject(userScoreInfoJson);
            return resultObj.getJSONObject("data");
        } catch (Exception e) {
            log.error("查询用户积分信息出错", e);
            return null;
        }
    }

    /**
     * 获取用户积分列表信息 getScoreList
     * @param from 积分来源：0:全部;1:购物；2：评论;3.绑定手机;4.绑定邮箱;5.手机签到获得
     * @param type 积分消费获取类型：0:全部；1，获取；2，消费
     * @param pageNo 页码，从1开始
     * @param pageSize  每页大小
     * @param orderno   订单编号
     * @param start 开始日期 yyyy-MM-dd
     * @param end   结束日期 yyyy-MM-dd
     * @param commentno 评论编号
     * @param memGuid   用户GUID
     * @return Result
     */
    public Result getScoreList(String from, int type, int pageNo, int pageSize, String orderno, String start, String end, String commentno, String memGuid, boolean isDetail) {
        if (StringUtils.isBlank(from) || !IsNumberUtil.isInteger(from)) {
            from = "0";
        }
        if (StringUtils.isBlank(orderno) || orderno.length() > 50) {
            orderno = null;
        } else {
            //有用订单号查询，type应该与订单相关
            if (from.equals("3") || from.equals("4")) {
                from = "0";
            }
        }
        if (StringUtils.isBlank(commentno) || !IsNumberUtil.isInteger(commentno) || Integer.parseInt(commentno) < 1) {
            commentno = "0";
        }

        JSONObject paramJson = new JSONObject();
        paramJson.put("srcType", from);
        paramJson.put("directType", type);
        paramJson.put("pageNo", pageNo);
        paramJson.put("pageSize", pageSize);
        paramJson.put("memGuid", memGuid);
        paramJson.put("isDetail", isDetail);
        if (!StringUtils.isBlank(orderno)) {
            paramJson.put("ogNo", orderno);
        }
        if (!StringUtils.isBlank(commentno)) {
            paramJson.put("commentSeq", commentno);
        }
        if (!StringUtils.isBlank(start)) {
            paramJson.put("startTime", start);
        }
        if (!StringUtils.isBlank(end)) {
            paramJson.put("endTime", end);
        }
        String resultStr = restTemplate.getForObject(getUserScoreDetailListApi + "?data={data}", String.class, paramJson.toJSONString());
        return JSONObject.parseObject(resultStr,
                Result.class);
    }
}
