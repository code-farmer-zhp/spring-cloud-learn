package com.feiniu.member.controller.point.touch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.controller.common.TouchCommonController;
import com.feiniu.member.dto.Result;
import com.feiniu.member.dto.TouchResultDto;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.PointService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


@Controller
@RequestMapping(value = "/touch/points")
public class PointForeTouchController extends TouchCommonController {
    private static  CustomLog log=CustomLog.getLogger(PointForeTouchController.class);
    @Autowired
    private PointService pointService;

    @Value("${m.login.url}")
    private String mLoginUrl;
    @Value("${weixin.url}")
    private String weixinUrl;

    private int maxPageSize=50;
    private int defaultPageSize=10;

    @RequestMapping(value="",method = RequestMethod.GET)
    public ModelAndView points(HttpServletRequest request) throws Exception {
        ModelAndView mav=getModel(request, "point/touch/index");
        if(mav==null||mav.getModel().isEmpty()){
            return new ModelAndView("redirect:" + mLoginUrl);
        }
        if(mav.getViewName().contains("redirect"))
        {
            return mav;
        }

        String memGuid="";
        if(mav.getModel().get("memGuid")!=null){
            memGuid=mav.getModel().get("memGuid").toString();
        }
        mav.addObject("weixinUrl", weixinUrl);

        try {
            JSONObject userScoreInfo = pointService.getUserScoreInfo(memGuid);
            if (userScoreInfo != null) {
                mav.addObject("expireScore", userScoreInfo.get("expiringScore"));
                if (userScoreInfo.getString("expiringTime") != null) {
                    mav.addObject("expiringTime", userScoreInfo.getString("expiringTime").replace("/", "."));
                }
                mav.addObject("waitScore", userScoreInfo.get("lockedScore"));
                mav.addObject("allScore", userScoreInfo.get("availabeScore"));
                Date expiringTime;
                try {
                    expiringTime = FastDateFormat.getInstance("yyyy/MM/dd").parse(userScoreInfo.getString("expiringTime"));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(expiringTime);
                    mav.addObject("year", calendar.get(Calendar.YEAR) + 1);
                } catch (ParseException e) {
                    log.error("日期转化错误,expiringTime=" + userScoreInfo.getString("expiringTime"), e);
                }
            }
        }catch (RestClientException e){
            log.error("调用积分接口超时",e);
        }catch (Exception e){
            log.error("查询积分信息失败",e);
        }
        return mav;
    }

    /**
     * 获取积分信息
     *
     * @param data 入参
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/getPointInfo")
    @ResponseBody
    public TouchResultDto getPointInfo(HttpServletRequest request, @RequestParam("data") String data) {
        Long begin = System.currentTimeMillis();
        try {
            String dataBackXss = data.replace("&amp;quot;", "\"");
            JSONObject dataInJson = JSONObject.parseObject(dataBackXss);
            JSONObject bodyIn = dataInJson.getJSONObject("body");
            String callback = dataInJson.getString("callback");
            Integer type = bodyIn.getInteger("type");
            Integer pageIndex = bodyIn.getInteger("pageIndex");
            Integer onePageSize = bodyIn.getInteger("onePageSize");
            type = (type == null ? 1 : type);
            String memGuid = getGuid(request);
            if (StringUtils.isBlank(memGuid)) {
                return new TouchResultDto(begin, System.currentTimeMillis(), 2, "用户尚未登录", null);
            }
            JSONObject body = new JSONObject();

            body.put("type", type);

            int pageNoInt;
            if (pageIndex == null || pageIndex < 1) {
                pageNoInt = 1;
            } else {
                pageNoInt = pageIndex;
            }

            int pageSizeInt;
            if (onePageSize == null || onePageSize < 1) {
                pageSizeInt = defaultPageSize;
            } else if (onePageSize > maxPageSize) {
                pageSizeInt = maxPageSize;
            } else {
                pageSizeInt = onePageSize;
            }

            Result scoreListResult = pointService.getScoreList("0", type, pageNoInt, pageSizeInt, null, null, null, null, memGuid, false);

            if (scoreListResult != null && scoreListResult.getCode() == 100) {
                JSONObject dataJson = (JSONObject) scoreListResult.getData();
                body.put("totalNum", dataJson.get("totalNum"));
                //不能用getInteger,不然28/20=1
                body.put("total_page", Double.valueOf(Math.ceil(dataJson.getDouble("totalNum") / pageSizeInt)).intValue());

                JSONArray returnPointArr = new JSONArray();
                if (dataJson.getJSONArray("userScoreList") != null) {
                    for (int i = 0; i < dataJson.getJSONArray("userScoreList").size(); i++) {
                        JSONObject pointDetailJson = dataJson.getJSONArray("userScoreList").getJSONObject(i);

                        JSONObject returnJson = new JSONObject();
                        returnJson.put("score", Math.abs(pointDetailJson.getInteger("scoreNumber")));
                        //1.获得 2.使用
                        if (pointDetailJson.getInteger("scoreNumber") < 0) {
                            returnJson.put("score_type", 2);
                        } else {
                            returnJson.put("score_type", 1);
                        }
                        if (pointDetailJson.getString("insTime") != null) {
                            returnJson.put("time", pointDetailJson.getString("insTime").replace("/", "."));
                        }
                        returnJson.put("orderStr", pointDetailJson.getString("ogNo"));
                        returnJson.put("descr", pointDetailJson.getString("description"));

                        returnPointArr.add(returnJson);
                    }
                }
                body.put("pointList", returnPointArr);
            }
            return new TouchResultDto(begin, System.currentTimeMillis(), 0, "", body);
        } catch (RestClientException e) {
            log.error("调用积分明细接口超时", e);
            return new TouchResultDto(begin, System.currentTimeMillis(), 1000, "网络超时，请稍后重试", null);
        } catch (Exception e) {
            log.error("查询积分明细信息失败", e);
            return new TouchResultDto(begin, System.currentTimeMillis(), 1, "查询积分信息失败", null);
        }

    }
}
