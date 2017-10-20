package com.feiniu.score.rest;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dto.Result;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.service.GrowthMemService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Controller
@Path("/growth")
public class GrowthRestServiceImpl implements GrowthRestService {
    @Autowired
    private GrowthMemService growthMemService;

    /**
     * @api {post} /growth/mem/queryMemLevel queryMemLevel
     * @apiVersion 1.0.0
     * @apiName queryMemLevel
     * @apiGroup Growth
     * @apiDescription 查询给定GUID的会员的会员等级
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data 返回数据
     * @apiSuccess (Success 100)  {String}   data.memLevel  用户当前等级
     * @apiSuccess (Success 100)  {String}   data.memLevelDesc  用户当前等级描述
     * @apiSuccess (Success 100)  {String}   data.levelChangeTime  用户等级变更时间。（普通用户为升级时间合伙人用户为成为合伙人的时间）
     * @apiSuccess (Success 100)  {String}   data.nextLevel  用户下一等级。已经是最高等级是为空
     * @apiSuccess (Success 100)  {String}   data.nextLevelDesc  用户下一等级的描述。已经是最高等级是为空
     * @apiSuccess (Success 100)  {Number}   data.nextLevelNeed  用户升到下一等级所需成长值.已经是最高等级是为0
     * @apiSuccess (Success 100)  {Number}   data.growthValue  用户当前成长值
     * @apiSuccess (Success 100)  {Boolean}  data.isPartner  用户是否为合伙人
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 515   查询用户是否为合伙人异常
     * @apiError (Error 5xx) 516   查询是否为工会会员异常
     * @apiParamExample getUserScoreInfo-Example:
     * data={"memGuid":"631B41AD-0536-XXXX-XXXX-C0FBE57201DF"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "memLevel": "TP",
     * "growthValue": 0,
     * "levelChangeTime": "2016-02-23 10:58:59",
     * "isPartner": 1,
     * "memLevelDesc": "飞牛网合伙人",
     * "nextLevel": "",
     * "nextLevelNeed": 0,
     * "nextLevelDesc": ""
     * }
     * }
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "memLevel": "T3",
     * "growthValue": 4755,
     * "levelChangeTime": "2016/04/18 11:50:01",
     * "isPartner": 0,
     * "memLevelDesc": "白金卡会员",
     * "nextLevel": "",
     * "nextLevelNeed": 0,
     * "nextLevelDesc": ""
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 查询给定GUID的会员的会员等级
     */
    @Override
    @POST
    @Path("/mem/queryMemLevel")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMemLevel(@FormParam("data") String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        String memGuid = getMemGuid(data);
        return getResponse(growthMemService.getMemLevel(memGuid));
    }

    /**
     * @api {post} /growth/mem/queryMemInfo queryMemInfo
     * @apiVersion 1.0.0
     * @apiName queryMemInfo
     * @apiGroup Growth
     * @apiDescription 查询给定GUID的会员等级信息(成长值在有效期内超过的会员百分比)
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data 返回数据
     * @apiSuccess (Success 100)  {String}   data.memLevel  用户当前等级
     * @apiSuccess (Success 100)  {String}   data.memLevelDesc  用户当前等级描述
     * @apiSuccess (Success 100)  {String}   data.levelChangeTime  用户等级变更时间。（普通用户为升级时间合伙人用户为成为合伙人的时间）
     * @apiSuccess (Success 100)  {Number}   data.growthValue  用户当前成长值
     * @apiSuccess (Success 100)  {Boolean}  data.expiryDate  等级过期时间
     * @apiSuccess (Success 100)  {Boolean}  data.overPercent 成长值超过的会员百分比
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 515   查询用户是否为合伙人异常
     * @apiError (Error 5xx) 516   查询是否为工会会员异常
     * @apiParamExample getUserScoreInfo-Example:
     * data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "overPercent": "0%",
     * "memLevel": "TP",
     * "growthValue": 0,
     * "levelChangeTime": "2016-02-23 10:58:59",
     * "memGuid": "044A8094-7195-6620-AC07-BF75251F24B6",
     * "expiryDate": null,
     * "memLevelDesc": "飞牛网合伙人"
     * }
     * }
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "overPercent": "100%",
     * "memLevel": "T3",
     * "growthValue": 4755,
     * "levelChangeTime": "2016/04/18 11:50:01",
     * "memGuid": "08608E0B-9303-1B63-9234-48C85B9C61C1",
     * "expiryDate": "2099/12/31",
     * "memLevelDesc": "白金卡会员"
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 查询给定GUID的会员的成长值在有效期内超过的会员百分比
     */
    @Override
    @POST
    @Path("/mem/queryMemInfo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMemOverPercent(@FormParam("data") String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        String memGuid = getMemGuid(data);
        return getResponse(growthMemService.getMemOverPercent(memGuid));
    }

    /**
     * @api {post} /growth/mem/queryLevelCount queryLevelCount
     * @apiVersion 1.0.0
     * @apiName queryLevelCount
     * @apiGroup Growth
     * @apiDescription 查询会员成长值明细记录数量（按商品显示，ERP使用）
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {Date}   [data.startDate]   开始时间 格式 yyyy-MM-dd HH:mm:ss
     * @apiParam {Date}   [data.endDate]   结束时间   格式 yyyy-MM-dd HH:mm:ss
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data 返回数据
     * @apiSuccess (Success 100)  {String}   data.TotalItems  成长值明细记录数量
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample getUserScoreInfo-Example:
     * data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6","startDate":"2016-01-01 00:00:00","endDate":"2016-07-30 00:00:00"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "TotalItems": 55
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 查询会员成长值明细记录数量
     */
    @Override
    @POST
    @Path("/mem/queryLevelCount")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGrowthDetailCount(@FormParam("data") String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        String memGuid = getMemGuid(data);
        Integer totalSum = growthMemService.getGrowthDetailCount(memGuid, data);
        Map<String, Object> retuanData = new HashMap<String, Object>();
        retuanData.put("TotalItems", totalSum);
        return getResponse(new Result(ResultCode.RESULT_STATUS_SUCCESS, retuanData, "success"));
    }

    /**
     * @api {post} /growth/mem/queryLevel queryLevel
     * @apiVersion 1.0.0
     * @apiName queryLevel
     * @apiGroup Growth
     * @apiDescription 分页查询会员成长值明细（按商品显示，ERP使用）
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {Date}   	 data.PageIndex   页码，从1开始
     * @apiParam {Date}   	 data.RowCount   每页记录数
     * @apiParam {Date}   [data.startDate]   开始时间 格式 yyyy-MM-dd HH:mm:ss
     * @apiParam {Date}   [data.endDate]   结束时间   格式 yyyy-MM-dd HH:mm:ss
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data 返回数据
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList  成长值明细数组
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.growthType  成长值变更类型
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.growthValue  成长值变更值
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.growthDesc  成长值变更说明
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.growthChangeDate  成长值变更时间
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.ogsSeq  有则为商城订单。成长值变更与订单相关时返回
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.orderNo  订单号。成长值变更与订单相关时返回
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.itNo  商品id。成长值变更与订单相关时返回
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.smSeq  卖场号。成长值变更与订单相关时返回
     * @apiSuccess (Success 100)  {Number}   data.TotalItems  成长值明细记录数量
     * @apiSuccess (Success 100)  {Number}   data.PageIndex  页码
     * @apiSuccess (Success 100)  {Number}   data.totalPage  总页数
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample getUserScoreInfo-Example:
     * data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6","PageIndex":1,"RowCount":3,"startDate":"2016-01-01 00:00:00","endDate":"2016-07-30 00:00:00"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "growthDetailList": [
     * {
     * "growthType": "登录获得",
     * "growthValue": "+5",
     * "growthDesc": "PC端登录 登录时间 : 2016-07-28 11:37:53",
     * "growthChangeDate": "2016-07-28 11:37:53"
     * },
     * {
     * "growthType": "购物获得",
     * "ogsSeq": "_",
     * "orderNo": "201607CP27066496",
     * "growthValue": "+409",
     * "itNo": "201606CG280000002",
     * "smSeq": "201606CM280000002",
     * "growthDesc": "订单号 : 201607CP27066496 商品ID : 201606CG280000002",
     * "growthChangeDate": "2016-07-27 10:10:28"
     * },
     * {
     * "growthType": "购物获得",
     * "ogsSeq": "_",
     * "orderNo": "201607CP27066496",
     * "growthValue": "+91",
     * "itNo": "201510CG150000016",
     * "smSeq": "201510CM150000019",
     * "growthDesc": "订单号 : 201607CP27066496 商品ID : 201510CG150000016",
     * "growthChangeDate": "2016-07-27 10:10:28"
     * },
     * {
     * "growthType": "登录获得",
     * "growthValue": "+5",
     * "growthDesc": "PC端登录 登录时间 : 2016-07-27 08:42:12",
     * "growthChangeDate": "2016-07-27 08:42:12"
     * },
     * {
     * "growthType": "评论获得",
     * "growthValue": "+20",
     * "growthChangeDate": "2016-07-26 11:37:43"
     * }
     * ],
     * "TotalItems": 459,
     * "PageIndex": 1,
     * "totalPage": 92
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 分页查询会员成长值明细
     */
    @Override
    @POST
    @Path("/mem/queryLevel")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGrowthDetail(@FormParam("data") String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        String memGuid = getMemGuid(data);
        return getResponse(growthMemService.getGrowthDetail(memGuid, data));
    }

    /**
     * 查询会员等级种类
     */
    @Override
    @GET
    @Path("/mem/queryMemLevelList")
    @Produces({MediaType.APPLICATION_JSON})
    public Response queryMemLevelList() {
        return getResponse(growthMemService.queryMemLevelList());
    }

    /**
     * @api {post} /growth/mem/getMemScoreAndGrowthInfo getMemScoreAndGrowthInfo
     * @apiVersion 1.0.0
     * @apiName getMemScoreAndGrowthInfo
     * @apiGroup Growth
     * @apiDescription 显示用户成长值、权益、积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data 返回数据
     * @apiSuccess (Success 100)  {String}   data.memLevel  用户当前等级
     * @apiSuccess (Success 100)  {String}   data.memLevelDesc  用户当前等级描述
     * @apiSuccess (Success 100)  {String}   data.levelChangeTime  用户等级变更时间。（普通用户为升级时间合伙人用户为成为合伙人的时间）
     * @apiSuccess (Success 100)  {String}   data.nextLevel  用户下一等级。已经是最高等级是为空
     * @apiSuccess (Success 100)  {String}   data.nextLevelDesc  用户下一等级的描述。已经是最高等级是为空
     * @apiSuccess (Success 100)  {Number}   data.nextLevelNeed  用户升到下一等级所需成长值.已经是最高等级是为0
     * @apiSuccess (Success 100)  {Number}   data.growthValue  用户当前成长值
     * @apiSuccess (Success 100)  {Boolean}  data.isPartner  用户是否为合伙人
     * @apiSuccess (Success 100)  {Number}   data.availableScore  用户可用积分
     * @apiSuccess (Success 100)  {String[]} data.mrstUiList  用户已点亮权益
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 515   查询用户是否为合伙人异常
     * @apiParamExample getUserScoreInfo-Example:
     * data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "memLevel": "T3",
     * "growthValue": 35338,
     * "levelChangeTime": "2016/03/17 16:31:36",
     * "isPartner": 0,
     * "mrstUiList": [
     * "T5",
     * "TZ",
     * "T3",
     * "T2"
     * ],
     * "memLevelDesc": "白金卡会员",
     * "nextLevel": "",
     * "nextLevelNeed": 0,
     * "availableScore": 15247,
     * "nextLevelDesc": ""
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 显示用户成长值、权益、积分
     */
    @Override
    @POST
    @Path("/mem/getMemScoreAndGrowthInfo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getMemScoreAndGrowthInfo(@FormParam("data") String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        String memGuid = getMemGuid(data);
        return getResponse(growthMemService.getMemScoreAndGrowthInfo(memGuid));
    }

    /**
     * @api {post} /growth/mem/queryLevelWithGroupByKey queryLevelWithGroupByKey
     * @apiVersion 1.0.0
     * @apiName queryLevelWithGroupByKey
     * @apiGroup Growth
     * @apiDescription 分页查询会员成长值明细 (按订单显示,前台使用)
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {Date}   	 data.PageIndex   页码，从1开始
     * @apiParam {Date}   	 data.RowCount   每页记录数
     * @apiParam {Date}   [data.startDate]   开始时间 格式 yyyy-MM-dd HH:mm:ss
     * @apiParam {Date}   [data.endDate]   结束时间   格式 yyyy-MM-dd HH:mm:ss
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data 返回数据
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList  成长值明细数组
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.growthType  成长值变更类型
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.growthValue  成长值变更值
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.growthDesc  成长值变更说明
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.growthChangeDate  成长值变更时间
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.ogsSeq  有则为商城订单。成长值变更与订单相关时返回
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.orderNo  订单号。成长值变更与订单相关时返回
     * @apiSuccess (Success 100)  {Object}   data.growthDetailList.smSeq  卖场号。成长值变更与订单相关时返回
     * @apiSuccess (Success 100)  {Number}   data.TotalItems  成长值明细记录数量
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 515   查询用户是否为合伙人异常
     * @apiParamExample getUserScoreInfo-Example:
     * data={"memGuid":"1BF7FD73-5596-F99E-2444-D3F3BB097C9E","PageIndex":1,"RowCount":5,"startDate":"2016-01-01 00:00:00","endDate":"2016-07-30 00:00:00"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "growthDetailList": [
     * {
     * "growthType": "登录获得",
     * "growthValue": "+5",
     * "growthDesc": "PC端登录 登录时间 : 2016-07-28 11:37:53",
     * "growthChangeDate": "2016-07-28 11:37:53"
     * },
     * {
     * "growthType": "购物获得",
     * "ogsSeq": null,
     * "orderNo": "201607CP27066496",
     * "growthValue": "+500",
     * "growthDesc": "订单号 : 201607CP27066496",
     * "growthChangeDate": "2016-07-27 10:10:28"
     * },
     * {
     * "growthType": "登录获得",
     * "growthValue": "+5",
     * "growthDesc": "PC端登录 登录时间 : 2016-07-27 08:42:12",
     * "growthChangeDate": "2016-07-27 08:42:12"
     * },
     * {
     * "growthType": "评论获得",
     * "ogsSeq": null,
     * "orderNo": "201607CP25066336",
     * "growthValue": "+20",
     * "smSeq": "201508CM270000083",
     * "growthDesc": "订单号 : 201607CP25066336 商品ID : 201508CG270000080",
     * "growthChangeDate": "2016-07-26 11:37:43"
     * },
     * {
     * "growthType": "评论获得",
     * "ogsSeq": null,
     * "orderNo": "201607CP25066336",
     * "growthValue": "+20",
     * "smSeq": "201508CM270000132",
     * "growthDesc": "订单号 : 201607CP25066336 商品ID : 201508CG270000126",
     * "growthChangeDate": "2016-07-26 10:47:02"
     * }
     * ],
     * "PageIndex": 1
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 分页查询会员成长值明细，按订单显示
     */
    @Override
    @POST
    @Path("/mem/queryLevelWithGroupByKey")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGrowthDetailWithGroupByKey(@FormParam("data") String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        String memGuid = getMemGuid(data);
        return getResponse(growthMemService.getGrowthDetailGroupByOg(memGuid, data, 1));
    }

    /**
     * @api {post} /growth/mem/queryLevelCountWithGroupByKey queryLevelCountWithGroupByKey
     * @apiVersion 1.0.0
     * @apiName queryLevelCountWithGroupByKey
     * @apiGroup Growth
     * @apiDescription 分页查询会员成长值明细总数 (按订单显示,前台使用)
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {Date}   [data.startDate]   开始时间 格式 yyyy-MM-dd HH:mm:ss
     * @apiParam {Date}   [data.endDate]   结束时间   格式 yyyy-MM-dd HH:mm:ss
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data 返回数据
     * @apiSuccess (Success 100)  {Object}   data.TotalItems  成长值明细数组
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 515   查询用户是否为合伙人异常
     * @apiParamExample getUserScoreInfo-Example:
     * data={"memGuid":"1BF7FD73-5596-F99E-2444-D3F3BB097C9E","startDate":"2016-01-01 00:00:00","endDate":"2016-07-30 00:00:00"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "TotalItems": 159
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 分页查询会员成长值明细，按订单显示
     */
    @Override
    @POST
    @Path("/mem/queryLevelCountWithGroupByKey")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGrowthDetailCountWithGroupByKey(@FormParam("data") String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        String memGuid = getMemGuid(data);
        return getResponse(growthMemService.getGrowthDetailCountGroupByOg(memGuid, data, 1));
    }

    private String getMemGuid(String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
        }
        JSONObject jsonObj = JSONObject.parseObject(data);
        //会员ID
        String memGuid = jsonObj.getString("memGuid");
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        return memGuid;
    }

    private Response getResponse(Result result) {
        return Response
                .ok((result == null
                        ? new Result(ResultCode.RESULT_STATUS_EXCEPTION, "服务器异常！")
                        : result), MediaType.APPLICATION_JSON).build();
    }

    /**
     * 移除memcache缓存的值
     */
    @Override
    @POST
    @Path("/cleanCacheValue")
    @Produces({MediaType.APPLICATION_JSON})
    public Response clearCacheValue(@FormParam("key") String key) {
        return getResponse(growthMemService.clearCacheValue(key));
    }


    @Override
    @POST
    @Path("/putCacheValue")
    @Produces({MediaType.APPLICATION_JSON})
    public Response putCacheValue(@FormParam("key") String key, @FormParam("value") String value) {
        return getResponse(growthMemService.putCacheValue(key,value));
    }


    @Override
    @POST
    @Path("/showCacheValue")
    @Produces({MediaType.APPLICATION_JSON})
    public Response showCacheValue(@FormParam("key") String key, @FormParam("field") String field) {
        return getResponse(new Result(ResultCode.RESULT_STATUS_SUCCESS, growthMemService.showCacheValue(key, field), ""));
    }
}
