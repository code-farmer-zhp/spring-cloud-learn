package com.feiniu.score.rest;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dto.Result;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.service.ScoreService;
import com.feiniu.score.service.ScoreSignQueueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Controller
@Path("/v1/score")
public class ScoreRestServiceImpl implements ScoreRestService {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private ScoreSignQueueService scoreSignQueueService;

    @GET
    @Path("/test")
    @Override
    public String test() {
        return "success";
    }


    /**
     * @api {post} /v1/score/getOrderScore getOrderScore
     * @apiVersion 1.0.0
     * @apiName getOrderScore
     * @apiGroup Score
     * @apiDescription 核对订单时显示积分信息
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {String}   data.areaSeq  区域流水号
     * @apiParam {Object[]}   [data.self]   自营商品
     * @apiParam {String}   data.self.smSeq  卖场ID
     * @apiParam {String}   data.self.itNo   商品ID
     * @apiParam {Number}   data.self.realPay   付款总金额
     * @apiParam {String}   [data.self.parentId]  父商品id
     * @apiParam {String}   [data.self.ssmGrade]   促销等级
     * @apiParam {String}   [data.self.ssmType]   团购类型
     * @apiParam {Object[]}   [data.mall]    商城商品
     * @apiParam {String}   data.mall.itNo   商品ID
     * @apiParam {Number}   data.mall.realPay   付款总金额
     * @apiParam {String}   [data.mall.parentId]    父商品id
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Number}   data.totalScore  可获得的积分
     * @apiSuccess (Success 100)  {Number}   data.maxUseScorePoints  最多可使用的积分。为兼容老版本，尚未取scoreUseSection的整数倍
     * @apiSuccess (Success 100)  {Number}   data.availableScore  用户当前可用积分
     * @apiSuccess (Success 100)  {Number[]}   data.useScoreList  可使用积分数组，按scoreUseSection分档。由大到小
     * @apiSuccess (Success 100)  {Number}   data.scoreUseSection  可使用积分的单个区间
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} getOrderScore-Example
     * data={"areaSeq":"CS000016","mall":[],"memGuid":"76B6CEC7-14C0-87F4-8E6C-ED1616D35968","self":[{"itNo":"201601CG040000041","parentId":"201601CM040000041","realPay":230.67,"smSeq":"201601CM040000041"}]}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "useScoreList": [
     * 7500,
     * 7000,
     * 6500,
     * 6000,
     * 5500,
     * 5000,
     * 4500,
     * 4000,
     * 3500,
     * 3000,
     * 2500,
     * 2000,
     * 1500,
     * 1000,
     * 500,
     * 0
     * ],
     * "availableScore": 7632,
     * "scoreUseSection": 500,
     * "totalScore": 50,
     * "maxUseScorePoints": 7632
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
     * 核对订单时显示积分信息
     */
    @Override
    @POST
    @Path("/getOrderScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getOrderScore(@FormParam("data") String productList) {
        String memGuid = getMemGuid(productList);
        return getResponse(scoreService.getOrderScore(memGuid, productList));
    }


    /**
     * @api {post} /v1/score/submitOrderScore submitOrderScore
     * @apiVersion 1.0.0
     * @apiName submitOrderScore
     * @apiGroup Score
     * @apiDescription 提交订单后确认订单页显示积分信息
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {String}   data.consumeScore  订单消费积分
     * @apiParam {String}   data.ogSeq   订单流水号
     * @apiParam {String}   data.memType  如果是企业用户就为1
     * @apiParam {String}   data.provinceId  省份
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 503   入参不合法
     * @apiParamExample {json} submitOrderScore-Example
     * data={"consumeScore":0.00,"memGuid":"95A0D397-BCD2-9FAE-DC58-B55E3F81741D","memType":0,"ogNo":"201607CP18065687","ogSeq":"201607CO18065687","provinceId":"310101","sourceMode":"1"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code":100,
     * "msg":"success"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 提交订单后确认订单页显示积分信息
     */
    @Override
    @POST
    @Path("/submitOrderScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response submitOrderScore(@FormParam("data") String orderInfo) {
        String memGuid = getMemGuid(orderInfo);
        return getResponse(scoreService.submitOrderScore(memGuid, orderInfo));
    }


    /**
     * @api {get} /v1/score/getOrderScoreInfo getOrderScoreInfo
     * @apiVersion 1.0.0
     * @apiName getOrderScoreInfo
     * @apiGroup Score
     * @apiDescription 查询单个订单积分信息
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {String} data.ogSeq   订单流水号
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Number}   data.getScore  可获得的积分
     * @apiSuccess (Success 100)  {Number}   data.getGrowth  可获得的成长值
     * @apiSuccess (Success 100)  {String}   data.nextLevel  用户下一等级
     * @apiSuccess (Success 100)  {String}   data.nextLevelDesc  用户下一等级的描述
     * @apiSuccess (Success 100)  {Number}   data.nextLevelNeed  用户升到下一等级所需成长值
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 503   入参不合法
     * @apiParamExample {json} getOrderScoreInfo-Example
     * data={"memGuid":"0978FBA6-CFDA-A7CA-489D-DC449A072BB3","ogSeq":"201601CO07035663"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "data": {
     * "getGrowth": 0,
     * "getScore": 0,
     * "nextLevel": "",
     * "nextLevelDesc": "",
     * "nextLevelNeed": 0
     * },
     * "msg": "success"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 查询单个订单积分信息
     */
    @Override
    @GET
    @Path("/getOrderScoreInfo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getOrderScoreInfo(@QueryParam("data") String order) {
        String memGuid = getMemGuid(order);
        return getResponse(scoreService.getOrderScoreInfo(memGuid, order));
    }


    /**
     * @api {get} /v1/score/getOrderConsumeScoreInfo getOrderConsumeScoreInfo
     * @apiVersion 1.0.0
     * @apiName getOrderConsumeScoreInfo
     * @apiGroup Score
     * @apiDescription 查询单个订单消费积分信息
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {String} data.ogSeq   订单流水号
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Number}   data.consumeScore  订单消费的积分
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} getOrderConsumeScoreInfo-Example
     * data={"memGuid":"0978FBA6-CFDA-A7CA-489D-DC449A072BB3","ogSeq":"201601CO07035663"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "data": {
     * "consumeScore": 0,
     * },
     * "msg": "success"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 查询单个订单消费积分信息
     */
    @Override
    @GET
    @Path("/getOrderConsumeScoreInfo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getOrderConsumeScoreInfo(@QueryParam("data") String order) {
        String memGuid = getMemGuid(order);
        return getResponse(scoreService.getOrderConsumeScoreInfo(memGuid, order));
    }


    /**
     * @api {post} /v1/score/addScore addScore
     * @apiVersion 1.0.0
     * @apiName addScore
     * @apiGroup Score
     * @apiDescription 订单付款后发放积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {String} data.ogSeq   订单流水号
     * @apiParam {String} [data.payDate]   支付时间
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} addScore-Example
     * data={"memGuid":"0978FBA6-CFDA-A7CA-489D-DC449A072BB3","ogSeq":"201601CO07035663"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 订单付款后发放积分
     */
    @Override
    @POST
    @Path("/addScore")
    @Produces({MediaType.APPLICATION_JSON})
    @Deprecated
    public Response addScore(@FormParam("data") String order) {
        //String memGuid = getMemGuid(order);
        return getResponse(new Result(ResultCode.RESULT_STATUS_EXCEPTION, "已废弃"));
    }

    /**
     * @api {post} /v1/score/addScoreBatch addScoreBatch
     * @apiVersion 1.0.0
     * @apiName addScoreBatch
     * @apiGroup Score
     * @apiDescription 订单付款后发放积分，批量
     * @apiParam {Object[]}   data[]       入参数据
     * @apiParam {String} data.memGuid   用户GUID
     * @apiParam {String} data.ogSeq   订单流水号
     * @apiParam {String} [data.payDate]   支付时间
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} addScoreBatch-Example
     * data=[{"memGuid":"3F84D587-D613-70D3-481D-51C5F3A8A0CA","ogSeq":"201607CO15333310","payDate":"2016\/07\/18 00:05:18"},{"memGuid":"B420D7E2-0532-DD6A-0882-62DEE907AF66","ogSeq":"201607CO16366521","payDate":"2016\/07\/18 00:05:20"}]
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 订单付款后发放积分，批量
     */
    @Override
    @POST
    @Path("/addScoreBatch")
    @Produces({MediaType.APPLICATION_JSON})
    @Deprecated
    public Response addScoreBatch(@FormParam("data") String order) {

      /*  if (StringUtils.isEmpty(order)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空。");
        }
        JSONArray orderArray = JSONObject.parseArray(order);
        Response result = null;
        for (int i = 0; i < orderArray.size(); i++) {
            JSONObject jsonOneOrder = orderArray.getJSONObject(i);
            result = addScore(jsonOneOrder.toJSONString());
        }*/
        return getResponse(new Result(ResultCode.RESULT_STATUS_EXCEPTION, "已废弃"));
    }


    /**
     * @api {GET} /v1/score/getReturnConsumeScore getReturnConsumeScore
     * @apiVersion 1.0.0
     * @apiName getReturnConsumeScore
     * @apiGroup Score
     * @apiDescription 查询退货单可返还的消费积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String} data.memGuid   用户GUID
     * @apiParam {String} data.rgSeq   退订单流水号
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Number}   data.returnConsumeScore  订单消费的积分
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} getReturnConsumeScore-Example
     * data={"memGuid":"87690AC7-1611-C204-1652-0F9ABAB9D009","rgSeq":"201607CS18000015"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "getReturnConsumeScore": 0,
     * },
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 查询退货单可返还的消费积分
     */
    @Override
    @GET
    @Path("/getReturnConsumeScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getReturnConsumeScore(@QueryParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.getReturnConsumeScore(memGuid, data));
    }


    /**
     * @api {post} /v1/score/confirmReturnScore confirmReturnScore
     * @apiVersion 1.0.0
     * @apiName confirmReturnScore
     * @apiGroup Score
     * @apiDescription 退货退款确认后回收发放的积分和返还消费的积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String} data.memGuid   用户GUID
     * @apiParam {String} data.rgSeq   退订单流水号
     * @apiParam {String} data.pay   付款状态
     * @apiParam {String} [data.rssSeq]   子退订单流水号
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} confirmReturnScore-Example
     * data={"memGuid":"87690AC7-1611-C204-1652-0F9ABAB9D009","rgSeq":"201607CS18000015","pay":"0"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 退货退款确认后回收发放的积分和返还消费的积分
     */
    @Override
    @POST
    @Path("/confirmReturnScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response confirmReturnScore(@FormParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.confirmReturnScore(memGuid, data));
    }


    /**
     * @api {post} /v1/score/cancelMallOrderConsumeScore cancelMallOrderConsumeScore
     * @apiVersion 1.0.0
     * @apiName cancelMallOrderConsumeScore
     * @apiGroup Score
     * @apiDescription 取消订单，只处理商城的订单，返回消费积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String} data.memGuid   用户GUID
     * @apiParam {String} data.ogSeq   订单流水号
     * @apiParam {String} [data.packageNo]   子退订单流水号
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} cancelMallOrderConsumeScore-Example
     * data={"memGuid":"76B6CEC7-14C0-87F4-8E6C-ED1616D35968","ogSeq":"201607CO18065687""}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 取消订单，只处理商城的订单，返回消费积分
     */
    @Override
    @POST
    @Path("/cancelMallOrderConsumeScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response cancelMallOrderConsumeScore(@FormParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.cancelMallOrderConsumeScore(memGuid, data));
    }


    /**
     * @api {get} /v1/score/getUserAvaliableScore getUserAvaliableScore
     * @apiVersion 1.0.0
     * @apiName getUserAvaliableScore
     * @apiGroup Score
     * @apiDescription 显示用户可用积分信息
     * @apiParam {String}   memGuid   用户GUID
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Number}   data.availabeScore  当前可用积分
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample getUserAvaliableScore-Example:
     * memGuid=044A8094-7195-6620-AC07-BF75251F24B6
     * @apiSuccessExample {json} Success-Response:
     * {
     *	"code": 100,
     *	"msg": "success",
     *	"data": {
     *	"availabeScore": 58530,
     *	}
     *}
     */
    /**
     * 显示用户可用积分信息
     *
     * @param memGuid 会员ID
     * @return 用户可用积分
     */
    @Override
    @GET
    @Path("/getUserAvaliableScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserAvaliableScore(@QueryParam("memGuid") String memGuid, @QueryParam("cache") String cache) {
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        return getResponse(scoreService.getUserAvaliableScore(memGuid, cache));
    }

    /**
     * @api {get} /v1/score/getUserScoreInfo getUserScoreInfo
     * @apiVersion 1.0.0
     * @apiName getUserScoreInfo
     * @apiGroup Score
     * @apiDescription 查询今天是否有签到获得积分记录并返回获得的积分
     * @apiParam {String}   memGuid   用户GUID
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {String}  data.expiringTime  过期日期（当天23:59:59过期）
     * @apiSuccess (Success 100)  {Number}   data.expiredScore  已过期积分
     * @apiSuccess (Success 100)  {Number}   data.availabeScore  当前可用积分
     * @apiSuccess (Success 100)  {Number}   data.lockedScore  冻结积分
     * @apiSuccess (Success 100)  {Number}   data.expiringScore  将要过期积分
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample getUserScoreInfo-Example:
     * memGuid=044A8094-7195-6620-AC07-BF75251F24B6
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "expiringTime": "2016/12/31",
     * "expiredScore": 62,
     * "availabeScore": 58530,
     * "lockedScore": 0,
     * "expiringScore": 49634
     * }
     * }
     */
    /**
     * 查询用户可用积分，待生效积分，即将过期积分，过期时间，已过期积分
     *
     * @param memGuid 会员ID
     * @return 状态信息
     */
    @Override
    @GET
    @Path("/getUserScoreInfo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserScoreInfo(@QueryParam("memGuid") String memGuid) {
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        return getResponse(scoreService.getUserScoreInfo(memGuid));
    }


    /**
     * @api {get} /v1/score/getUserScoreDetailList getUserScoreDetailList
     * @apiVersion 1.0.0
     * @apiName getUserScoreDetailList
     * @apiGroup Score
     * @apiDescription 查询用户积分详细信息
     * @apiParam {Object}   data       入参数据
     * @apiParam {String} data.memGuid   用户GUID
     * @apiParam {String} [data.ogSeq] 订单流水号
     * @apiParam {String} [data.ogNo] 订单编号
     * @apiParam {String} data.srcType 积分来源：0:全部;1:购物；2：评论;3.绑定手机;4.绑定邮箱;5.手机签到获得
     * @apiParam {String} data.directType 积分消费获取类型：0:全部；1，获取；2，消费
     * @apiParam {String} [data.startTime] 开始日期 yyyy-MM-dd
     * @apiParam {String} [data.endTime] 结束日期 yyyy-MM-dd
     * @apiParam {Number} data.pageNo 页码，从1开始
     * @apiParam {Number} data.pageSize 每页大小
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {String}   data.totalNum  总条数
     * @apiSuccess (Success 100)  {Object[]}   data.userScoreList  积分详情
     * @apiSuccess (Success 100)  {Number}   data.channel  后台积分变更类型
     * @apiSuccess (Success 100)  {String}   data.commentSeq  评论流水号。为0表示为空
     * @apiSuccess (Success 100)  {String}   data.description  前台描述
     * @apiSuccess (Success 100)  {String}   data.detail  评论流水号。为0表示为空
     * @apiSuccess (Success 100)  {Long}   data.insDate  插入时间
     * @apiSuccess (Success 100)  {String}   data.detail  评论流水号。为0表示为空
     * @apiSuccess (Success 100)  {String}   data.insTime  插入日期，yyyy/MM/dd
     * @apiSuccess (Success 100)  {String}   data.ogNo  订单号
     * @apiSuccess (Success 100)  {String}   data.ogSeq  订单流水号
     * @apiSuccess (Success 100)  {String}   data.remark  备注
     * @apiSuccess (Success 100)  {String}   data.rgSeq  退回流水号
     * @apiSuccess (Success 100)  {String}   data.scoreNumber  订单流水号
     * @apiSuccess (Success 100)  {String}   data.scoreStatus  备注
     * @apiSuccess (Success 100)  {String}   data.srcType  前台类型
     * @apiSuccess (Success 100)  {String}   data.type  前台查询类型
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample getUserScoreDetailList-Example:
     * data={srcType:"0",directType:"0",pageNo:"1",pageSize:"3",memGuid:"044A8094-7195-6620-AC07-BF75251F24B6",isDetail:"false",commentSeq:"0",startTime:"2016-03-01",endTime:"2016-07-18"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "data": {
     * "totalNum": 3,
     * "userScoreList": [
     * {
     * "channel": 0,
     * "commentSeq": 0,
     * "description": "购物赠点",
     * "detail": "购物",
     * "insDate": 1465056319000,
     * "insTime": "2016/06/05",
     * "limitTime": 1465056000000,
     * "lockJobStatus": true,
     * "ogNo": "201605CP26056945",
     * "ogSeq": "201605CO26056945",
     * "remark": "",
     * "rgSeq": "",
     * "scoreNumber": 41,
     * "scoreStatus": "已生效",
     * "srcType": 1,
     * "type": "购物"
     * },
     * {
     * "channel": 16,
     * "commentSeq": 0,
     * "description": "签到赠点",
     * "detail": "签到获得积分",
     * "insDate": 1464679703000,
     * "insTime": "2016/05/31",
     * "lockJobStatus": false,
     * "ogNo": "",
     * "ogSeq": "",
     * "remark": "",
     * "rgSeq": "",
     * "scoreNumber": 5,
     * "scoreStatus": "已生效",
     * "srcType": 5,
     * "type": "签到"
     * },
     * {
     * "channel": 16,
     * "commentSeq": 0,
     * "description": "签到赠点",
     * "detail": "签到获得积分",
     * "insDate": 1464244165000,
     * "insTime": "2016/05/26",
     * "lockJobStatus": false,
     * "ogNo": "",
     * "ogSeq": "",
     * "remark": "",
     * "rgSeq": "",
     * "scoreNumber": 5,
     * "scoreStatus": "已生效",
     * "srcType": 5,
     * "type": "签到"
     * }
     * ]
     * },
     * "msg": "success"
     * }
     */
    /**
     * 查询用户积分详细信息
     */
    @Override
    @GET
    @Path("/getUserScoreDetailList")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserScoreDetailList(@QueryParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.getUserScoreDetailList(memGuid, data));
    }

    /**
     * @api {post} /v1/score/saveScoreByBindPhone saveScoreByBindPhone
     * @apiVersion 1.0.0
     * @apiName saveScoreByBindPhone
     * @apiGroup Score
     * @apiDescription 绑定手机获得积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {String}   data.phoneNo   手机号
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 506   已经获得绑定手机的积分
     * @apiParamExample saveScoreByBindPhone-Example:
     * data={     "memGuid": "044A8094-7195-6620-AC07-BF75251F24B6",     "phoneNo": "11312124214"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 506,
     * "msg": "已经获得绑定手机的积分，不能重复提交。",
     * "data": null
     * }
     */
    /**
     * 绑定手机获得积分
     */
    @Override
    @POST
    @Path("/saveScoreByBindPhone")
    @Produces({MediaType.APPLICATION_JSON})
    public Response saveScoreByBindPhone(@FormParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.saveScoreByBindPhone(memGuid, data));
    }

    /**
     * @api {post} /v1/score/saveScoreByBindEmail saveScoreByBindEmail
     * @apiVersion 1.0.0
     * @apiName saveScoreByBindEmail
     * @apiGroup Score
     * @apiDescription 绑定邮箱获得积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {String}   data.email   邮箱地址
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 506   已经获得绑定邮箱的积分
     * @apiParamExample saveScoreByBindEmail-Example:
     * data={     "memGuid": "044A8094-7195-6620-AC07-BF75251F24B6",     "phoneNo": "1121@asdsf.com"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "email 不能为空",
     * "data": null
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 506,
     * "msg": "已经获得绑定邮箱的积分，不能重复提交。",
     * "data": null
     * }
     */
    /**
     * 绑定邮箱获得积分
     */
    @Override
    @POST
    @Path("/saveScoreByBindEmail")
    @Produces({MediaType.APPLICATION_JSON})
    public Response saveScoreByBindEmail(@FormParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.saveScoreByBindEmail(memGuid, data));
    }


    /**
     * @api {post} /v1/score/saveScoreByCommentProduct saveScoreByCommentProduct
     * @apiVersion 1.0.0
     * @apiName saveScoreByCommentProduct
     * @apiGroup Score
     * @apiDescription 评论商品获得积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {String}   data.ogSeq   订单流水号
     * @apiParam {String}   data.ogNo  订单号
     * @apiParam {String}   data.olSeq   订单明细流水号
     * @apiParam {String}   data.itNo   商品ID
     * @apiParam {String}   data.smSeq   卖场编号
     * @apiParam {String}   data.commentSeq 评论流水号
     * @apiParam {String}   data.getScore 评论获得的积分
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample saveScoreByCommentProduct-Example:
     * data={"memGuid":"631B41AD-0536-XXXX-XXXX-C0FBE57201DF","ogSeq":"201605COXXXXX1","ogNo":"201605CP25XXXXX1","smSeq":"201603CM300002178","olSeq":"201605C25XXXXX9","itNo":"201603CG3XXXXX","getScore":5,"commentSeq":4192XX}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "ogSeq 不能为空",
     * "data": null
     * }
     */
    /**
     * 评论商品获得积分
     */
    @Override
    @POST
    @Path("/saveScoreByCommentProduct")
    @Produces({MediaType.APPLICATION_JSON})
    public Response saveScoreByCommentProduct(@FormParam("data") String data) {
        return getResponse(scoreService.saveScoreByCommentProduct(data));
    }


    /**
     * @api {post} /v1/score/saveScoreBySetEssenceOrTop saveScoreBySetEssenceOrTop
     * @apiVersion 1.0.0
     * @apiName saveScoreBySetEssenceOrTop
     * @apiGroup Score
     * @apiDescription 评论置顶或设置精华获得积分
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {Number}   data.dirType   订单流水号 1.精华 2.置顶
     * @apiParam {String}   data.commentSeq  评论流水号
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample saveScoreBySetEssenceOrTop-Example:
     *data={"memGuid":"631B41AD-0536-XXXX-XXXX-C0FBE57201DF","commentSeq":4192XX,"dirType":1}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "ogSeq 不能为空",
     * "data": null
     * }
     */
    /**
     * 评论置顶或设置精华获得积分
     */
    @Override
    @POST
    @Path("/saveScoreBySetEssenceOrTop")
    @Produces({MediaType.APPLICATION_JSON})
    public Response saveScoreBySetEssenceOrTop(@FormParam("data") String data) {
        return getResponse(scoreService.saveScoreBySetEssenceOrTop(data));
    }


    @Override
    @POST
    @Path("/saveScoreByCustomerGive")
    @Produces({MediaType.APPLICATION_JSON})
    public Response saveScoreByCustomerGive(@FormParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.saveScoreByCustomerGive(memGuid, data));
    }

    @Override
    @POST
    @Path("/loadScoreSum")
    @Produces({MediaType.APPLICATION_JSON})
    public Response loadScoreSum(@FormParam("data") String data) {
        return getResponse(scoreService.loadScoreSum(data));
    }

    @Override
    @POST
    @Path("/loadOlSore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response loadOlScore(@FormParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.loadOlScore(memGuid, data));
    }


    @Override
    @GET
    @Path("/getUserScoreLogDetailList")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserScoreLogDetailList(@QueryParam("data") String data) {
        String memGuid = getMemGuid(data);
        return getResponse(scoreService.getUserScoreLogDetailList(memGuid, data));
    }


    @Override
    @GET
    @Path("/getDetail")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDetail(@QueryParam("data") String data) {
        if (StringUtils.isEmpty(data)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "data 不能为空");
        }
        return getResponse(scoreService.getDetail(data));
    }


    /**
     * @api {get} /v1/score/saveScoreBySign saveScoreBySign
     * @apiVersion 1.0.0
     * @apiName saveScoreBySign
     * @apiGroup Score
     * @apiDescription 签到获得积分
     * @apiParam {String}   memGuid   用户GUID
     * @apiParam {String}   from   来源 1.微信：2.IOS 3.安卓 4.H5 5.PC
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Number}  data  签到获得的积分
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参为空
     * @apiError (Error 5xx) 502   运行时错误
     * @apiError (Error 5xx) 503   入参不合法
     * @apiError (Error 5xx) 506   重复提交    (今天已经签过了，已领5积分（获得积分）或者今天已经签到过了，请再接再厉（不获得积分）)
     * @apiError (Error 5xx) 507   员工签到超过7次   员工签到超过7次不赠送积分
     * @apiError (Error 5xx) 508   最近X天无有效订单  合伙人、工会、金卡和白金卡会员,X=180。普通和银卡会员,x=30
     * @apiError (Error 5xx) 509   首单未完成    完成首单后签到可获得积分哦~
     * @apiParamExample {json} apiParamExample
     * data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6","from":"4"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "data": 5,
     * "msg": "签到成功，已领5积分"
     *}
     * @apiErrorExample {json} Error-Response:
     *{
     * "code": 507,
     * "data": 0,
     * "msg": "员工签到超过7次不赠送积分"
     *}
     *@apiErrorExample {json} Error-Response:
     * {
     * "code": 509,
     * "data": 0,
     * "msg": "完成首单后签到可获得积分哦~"
     *}
     *@apiErrorExample {json} Error-Response:
     * {
     * "code": 508,
     * "data": 0,
     * "msg": "银卡会员30天内无有效订单，签到不再赠送积分（金卡和白金卡会员为180天）"
     * }
     */
    /**
     * 签到获得积分
     */
    @Override
    @POST
    @Path("/saveScoreBySign")
    @Produces({MediaType.APPLICATION_JSON})
    public Response saveScoreBySign(@FormParam("data") String data) {

        return getResponse(scoreSignQueueService.saveScoreBySign(getMemGuid(data), data));
    }

    /**
     * @api {get} /v1/score/haveSign haveSign
     * @apiVersion 1.0.0
     * @apiName haveSign
     * @apiGroup Score
     * @apiDescription 查询今天是否有签到
     * @apiParam {String}   memGuid   用户GUID
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Boolean}  data  返回数据 是否已经签到
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} apiParamExample
     * memGuid=044A8094-7195-6620-AC07-BF75251F24B6
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "data": false,
     * "msg": "success"
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 查询是否已经签到
     */
    @Override
    @GET
    @Path("/haveSign")
    @Produces({MediaType.APPLICATION_JSON})
    public Response haveSign(@QueryParam("memGuid") String memGuid) {
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        return getResponse(scoreService.haveSign(memGuid));
    }

    /**
     * @api {get} /v1/score/haveSignReturnScore haveSignReturnScore
     * @apiVersion 1.0.0
     * @apiName haveSignReturnScore
     * @apiGroup Score
     * @apiDescription 查询今天是否有签到获得积分记录并返回获得的积分
     * @apiParam {String}   memGuid   用户GUID
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Boolean}  data.flag  是否已经签到
     * @apiSuccess (Success 100)  {Number}   data.getScore  签到获得的积分
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} haveSignReturnScore-Example
     * memGuid=044A8094-7195-6620-AC07-BF75251F24B6
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "flag": false,
     * "getScore": null
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
     * 查询今天是否有签到获得积分记录并返回获得的积分
     */
    @Override
    @GET
    @Path("/haveSignReturnScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response haveSignReturnScore(@QueryParam("memGuid") String memGuid) {
        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        return getResponse(scoreService.haveSignReturnScore(memGuid));
    }

    /**
     * 查询用户是否已绑定手机和邮箱
     */
    @Override
    @POST
    @Path("/haveBindPhoneAndEmail")
    @Produces({MediaType.APPLICATION_JSON})
    public Response haveBindPhoneAndEmail(@FormParam("memGuid") String memGuid) {

        if (StringUtils.isEmpty(memGuid)) {
            throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
        }
        return getResponse(scoreService.haveBindPhoneAndEmail(memGuid));
    }

    @Override
    @POST
    @Path("/getScoreDetailByOlSeqs")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getScoreDetailByOlSeqs(@FormParam("data") String data) {
        return getResponse(scoreService.getScoreDetailByOlSeqs(getMemGuid(data), data));
    }

    @Override
    @POST
    @Path("/mallScoreForCancel")
    @Produces({MediaType.APPLICATION_JSON})
    public Response mallScoreForCancel(@FormParam("data") String data) {
        return getResponse(scoreService.mallScoreForCancel(getMemGuid(data), data));
    }

    @Override
    @POST
    @Path("/mallScoreForRefund")
    @Produces({MediaType.APPLICATION_JSON})
    public Response mallScoreForRefund(@FormParam("data") String data) {
        return getResponse(scoreService.mallScoreForRefund(data));
    }

    @Override
    @POST
    @Path("/submitSomeCannelReturnOrderScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response submitSomeCannelReturnOrderScore(@FormParam("data") String data) {
        //遗留接口
        return getResponse(new Result(ResultCode.RESULT_STATUS_SUCCESS, "success"));
    }

    @Override
    @POST
    @Path("/submitReturnOrderScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response submitReturnOrderScore(@FormParam("data") String returnInfo) {
        //遗留接口
        return getResponse(new Result(ResultCode.RESULT_STATUS_SUCCESS, "success"));
    }

    /**
     * @api {get} /v1/score/changeImmediately changeImmediately
     * @apiVersion 1.0.0
     * @apiName changeImmediately
     * @apiGroup Score
     * @apiDescription 抽奖/兑换抵用券使用/消耗(回滚)积分
     * @apiParam {String}   memGuid   用户GUID
     * @apiParam {Number}   [scoreValue]   使用/获得的积分，正数。type=2时非必须，其他条件必须
     * @apiParam {String}   type   类型,0:获得积分 1:使用积分 2:回滚积分
     * @apiParam {String}   channel   1.抽奖获得/使用/回滚积分 2.兑换抵用券
     * @apiParam {String}   [smlSeq]   使用积分时会返回smlSeq，以便在兑换失败时按smlSeq回滚使用的积分.type=2时必传
     * @apiParam {String}   [exCardNo]   channel=2,type=2时必传
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Boolean}  data. smlSeq  积分记录ID
     * @apiSuccess (Success 100)  {Number}   data.roolBackScore  回滚的积分，type为2时返回
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 503   入参数错误
     * @apiError (Error 5xx) 514   按smlSeq找不到积分的记录
     * @apiParamExample {json} 抽奖使用积分
     * data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6", "scoreValue":"10", "type":"1", "channel":"1" }
     * @apiParamExample {json} 回滚积分
     * data={ "memGuid":"044A8094-7195-6620-AC07-BF75251F24B6", "smlSeq":"200”, “type”:”2”, “channel”:”2” }
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "抽奖使用积分成功",
     * "data": {
     * "smlSeq": 614
     * }
     * }
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "兑换抵用券使用积分成功",
     * "data": {
     * "smlSeq": 614
     * }
     * }
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "回滚抽奖积分成功",
     * "data": {
     * "roolBackScore": 10
     * }
     *}
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "memGuid 不能为空",
     * "data": null
     * }
     */
    /**
     * 抽奖/兑换抵用券使用/消耗(回滚)积分
     */
    @Override
    @POST
    @Path("/changeImmediately")
    @Produces({MediaType.APPLICATION_JSON})
    public Response changeImmediately(@FormParam("data") String data) {
        return getResponse(scoreService.changeScoreImmediatelyWithChannel(getMemGuid(data), data));
    }

    /**
     * @api {get} /v1/score/exchangeCard exchangeCard
     * @apiVersion 1.0.0
     * @apiName exchangeCard
     * @apiGroup Score
     * @apiDescription 兑换异业券
     * @apiParam {String}   memGuid   用户GUID
     * @apiParam {Number}   consumeScore   使用的积分
     * @apiParam {String}   exCardNo   券卡号
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiError (Error 5xx) 503   入参数错误
     * @apiParamExample {json} apiParamExample
     * data={"exCardNo":"201703DH240002","consumeScore":199,"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data":null
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 501,
     * "msg": "exCardNo不能为空",
     * "data": null
     * }
     */
    /**
     * 兑换异业券
     */
    @Override
    @POST
    @Path("/exchangeCard")
    @Produces({MediaType.APPLICATION_JSON})
    public Response ExchangeCard(@FormParam("data") String data) {
        return getResponse(scoreService.changeScoreImmediatelyByExchangeCard(getMemGuid(data), data));
    }

    /**
     * @api {get} /v1/score/getSaleScore getSaleScore
     * @apiVersion 1.0.0
     * @apiName getSaleScore
     * @apiGroup Score
     * @apiDescription 按卖场查询可获得的积分和积分倍数，支持批量
     * @apiParam {String}  data   数据
     * @apiParam {String}  data.smSeq   卖场号，批量的用逗号隔开
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Boolean}  data.allSuccessFlag  是否全部查询成功
     * @apiSuccess (Success 100)  {Object}   data.resultList  查询结果队列
     * @apiSuccess (Success 100)  {String}   data.resultList.code 查询结果 0成功
     * @apiSuccess (Success 100)  {String}   data.resultList.msg  查询结果描述
     * @apiSuccess (Success 100)  {String}   data.resultList.smSeq  卖场号
     * @apiSuccess (Success 100)  {Number}   data.resultList.totalScore  卖场总积分
     * @apiSuccess (Success 100)  {Number}   data.resultList.multiple  卖场主商品积分倍数
     * @apiSuccess (Success 100)  {Object}   data.resultList.itLists  商品列表
     * @apiSuccess (Success 100)  {String}   data.resultList.itLists.itType   1 一般商品, 2 加购品， 3 赠品， 4 配件
     * @apiSuccess (Success 100)  {String}   data.resultList.itLists.multiple  商品积分倍数
     * @apiSuccess (Success 100)  {String}   data.resultList.itLists.itNo  商品号
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 502   运行中异常
     * @apiParamExample {json} getSaleScore-Example
     * data={"smSeq":"100002537,100002538"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "allSuccessFlag": true,
     * "resultList": [
     * {
     * "msg": "success",
     * "smSeq": "201412CM110000271",
     * "code": "0",
     * "multiple": 1,
     * "itLists": [
     * {
     * "itType": "1",
     * "multiple": 1,
     * "itNo": "201312CG120000989"
     * },
     * {
     * "itType": "1",
     * "multiple": 1,
     * "itNo": "201312CG120001017"
     * },
     * {
     * "itType": "1",
     * "multiple": 1,
     * "itNo": "201312CG120000989"
     * },
     * {
     * "itType": "1",
     * "multiple": 1,
     * "itNo": "201312CG120001017"
     * }
     * ],
     * "totalScore": 54
     * },
     * {
     * "msg": "success",
     * "smSeq": "201601CM210000021",
     * "code": "0",
     * "multiple": 1,
     * "itLists": [
     * {
     * "itType": "1",
     * "multiple": 1,
     * "itNo": "201601CG210000020"
     * }
     * ],
     * "totalScore": 509
     * }
     * ]
     * }
     * }
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "allSuccessFlag": true,
     * "resultList": [
     * {
     * "msg": "根据SmSeq查询卖场积分失败。",
     * "code": "-1"
     * },
     * {
     * "msg": "success",
     * "smSeq": "201601CM210000021",
     * "code": "0",
     * "multiple": 1,
     * "itLists": [
     * {
     * "itType": "1",
     * "multiple": 1,
     * "itNo": "201601CG210000020"
     * }
     * ],
     * "totalScore": 509
     * }
     * ]
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 502,
     * "msg": "运行中异常:syntax error, position at 0, name smSeq",
     * "data": null
     * }
     */
    /**
     * @api {get} /v1/score/getSaleScore getSaleScore
     * @apiVersion 2.0.0
     * @apiName getSaleScore
     * @apiGroup Score
     * @apiDescription 按卖场查询可获得的积分和积分倍数，支持批量
     * @apiParam {String}  data   数据
     * @apiParam {String}  data.skuSeqs   卖场号，批量的用逗号隔开
     * @apiParam {Object}  data.areaCode   地区编码
     * @apiParam {String}  data.areaCode.areaCode   区编码
     * @apiParam {String}  data.areaCode.cityCode   市编码
     * @apiParam {String}  data.areaCode.provinceCode   省编码
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Boolean}  data.allSuccessFlag  是否全部查询成功
     * @apiSuccess (Success 100)  {Object}   data.resultList  查询结果队列
     * @apiSuccess (Success 100)  {String}   data.resultList.code 查询结果 0成功
     * @apiSuccess (Success 100)  {String}   data.resultList.msg  查询结果描述
     * @apiSuccess (Success 100)  {String}   data.resultList.skuSeq  卖场号
     * @apiSuccess (Success 100)  {Number}   data.resultList.totalScore  卖场总积分
     * @apiSuccess (Success 100)  {String}   data.resultList.skuType  默认传空 单品多件时传2
     * @apiSuccess (Success 100)  {Number}   data.resultList.multiple  卖场主商品积分倍数
     * @apiSuccess (Success 100)  {Object}   data.resultList.skuLists  商品列表
     * @apiSuccess (Success 100)  {String}   data.resultList.skuLists.skuType   0 普通SKU商品 1 虚拟组合 2 单品多件 3 单品搭配 4 手机套餐
     * @apiSuccess (Success 100)  {Number}   data.resultList.skuLists.multiple  商品积分倍数
     * @apiSuccess (Success 100)  {String}   data.resultList.skuLists.skuSeq  商品号
     * @apiSuccess (Success 100)  {String}   data.resultList.skuLists.getScore  商品获得的积分
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 502   运行中异常
     * @apiParamExample {json} getSaleScore-Example
     * data={"skuSeqs":"KS1170290300001700,KS5170290300001732","areaCode":{"areaCode":"310101","cityCode":"310100","provinceCode":"CS000016"}}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "allSuccessFlag": true,
     * "resultList": [
     * {
     * "totalScore": 46,
     * "skuType": "",
     * "skuLists": [
     * {
     * "skuType": "1",
     * "getScore": 23,
     * "skuSeq": "KS1170290300001700",
     * "multiple": 5
     * },
     * {
     * "skuType": "1",
     * "getScore": 23,
     * "skuSeq": "KS1170290300001700",
     * "multiple": 5
     * }
     * ],
     * "code": "0",
     * "skuSeq": "KS5170290300001732",
     * "multiple": 5,
     * "msg": "success"
     * },
     * {
     * "totalScore": 27,
     * "skuType": "",
     * "skuLists": [
     * {
     * "skuType": "0",
     * "getScore": 27,
     * "skuSeq": "KS1170290300001700",
     * "multiple": 5
     * }
     * ],
     * "code": "0",
     * "skuSeq": "KS1170290300001700",
     * "multiple": 5,
     * "msg": "success"
     * }
     * ]
     * }
     * }
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "allSuccessFlag": true,
     * "resultList": [
     * {
     * "msg": "查询卖场积分失败。",
     * "code": "-1"
     * },
     * {
     * "totalScore": 7,
     * "skuType": "",
     * "code": "0",
     * "skuLists": [
     * {
     * "skuType": "0",
     * "getScore": 7,
     * "skuSeq": "KZ01161290300000459",
     * "multiple": 1
     * }
     * ],
     * "skuSeq": "KZ01161290300000459",
     * "msg": "success",
     * "multiple": 1
     * }
     * ]
     * }
     * }
     * @apiErrorExample {json} Error-Response:
     * {
     * "code": 502,
     * "msg": "运行中异常:syntax error, position at 0, name skuSeqs",
     * "data": null
     * }
     */
    @Override
    @GET
    @Path("/getSaleScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getSaleScore(@QueryParam("data") String data) {
        return getResponse(scoreService.getScoreListBySmSeqList(data));
    }

    /**
     * 积分发放报表
     */
    @Override
    @POST
    @Path("/getScoreGrantDetail")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getScoreGrantDetail(@FormParam("data") String data) {
        return getResponse(scoreService.getScoreGrantDetail(data));
    }

    @Override
    @POST
    @Path("/getStoreScoreReportInfo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getStoreScoreReportInfo(@FormParam("data") String data) {
        return getResponse(scoreService.getStoreScoreReportInfo(data));
    }

    /**
     * 积分使用报表
     */
    @Override
    @POST
    @Path("/getScoreUseDetail")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getScoreUseDetail(@FormParam("data") String data) {
        return getResponse(scoreService.getScoreUseDetail(data));
    }

    @Override
    @POST
    @Path("/getScoreByOgsSeq")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getScoreByOgsSeq(@FormParam("data") String data) {
        return getResponse(scoreService.getScoreByOgsSeq(data));
    }

    /**
     * @api {post} /v1/score/getLastCurSignInfo getLastCurSignInfo
     * @apiVersion 1.0.0
     * @apiName getLastCurSignInfo
     * @apiGroup Score
     * @apiDescription 查询最后一次连续签到（包括签一次）的信息,包括今天
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiParam {Number}   data.durDaysForSel=7   持续多少天清0
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {Number}   data.enduranceDays  已持续签到天数，不清0
     * @apiSuccess (Success 100)  {String}   data.durBeginDate  持续签到开始时间
     * @apiSuccess (Success 100)  {String}   data.durEndDate  持续签到结束时间
     * @apiSuccess (Success 100)  {String}   data.durDaysAfterClean 已持续签到天数，连续（data.durDaysForSel）天后清0
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} getLastCurSignInfo-Example:
     * data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6","durDaysForSel":7}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "durBeginDate": "2016-03-30",
     * "durEndDate": "2016-03-30",
     * "enduranceDays": 1
     * "durDaysAfterClean": 1
     * }
     * }
     */
    /**
     * 查询最后一次连续签到（包括签一次）的信息,包括今天
     */
    @Override
    @POST
    @Path("/getLastCurSignInfo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getLastCurSignInfo(@FormParam("data") String data) {
        return getResponse(scoreService.getLastCurSignInfo(getMemGuid(data), data));
    }

    /**
     * @api {post} /v1/score/getSignDateThisMonth getSignDateThisMonth
     * @apiVersion 1.0.0
     * @apiName getSignDateThisMonth
     * @apiGroup Score
     * @apiDescription 查询当月已签到的日期（不返回年月）
     * @apiParam {Object}   data       入参数据
     * @apiParam {String}   data.memGuid   用户GUID
     * @apiSuccess (Success 100)  {Number}   code    状态码
     * @apiSuccess (Success 100)  {String}   msg 状态码说明
     * @apiSuccess (Success 100)  {Object}   data  返回数据
     * @apiSuccess (Success 100)  {String[]}   data.signDates  已签到的日期（不返回年月）
     * @apiError (Error 5xx) 500   未知错误
     * @apiError (Error 5xx) 501   入参数为空
     * @apiParamExample {json} getSignDateThisMonth-Example:
     * data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6"}
     * @apiSuccessExample {json} Success-Response:
     * {
     * "code": 100,
     * "msg": "success",
     * "data": {
     * "signDates": [
     * "28",
     * "29",
     * "26",
     * "27",
     * "25",
     * "24",
     * "30",
     * "21",
     * "23"
     * ]
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
     * 查询当月已签到的日期（不返回年月）
     */
    @Override
    @POST
    @Path("/getSignDateThisMonth")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getSignDateThisMonth(@FormParam("data") String data) {
        return getResponse(scoreService.getSignDateThisMonth(getMemGuid(data)));
    }

    @Override
    @POST
    @Path("/kafkaMessage")
    @Produces({MediaType.APPLICATION_JSON})
    public Response kafkaMessage(@FormParam("data") String data) {
        return getResponse(scoreService.kafkaMessage(data));
    }

    @Override
    @POST
    @Path("/scoreExchangeVoucher")
    @Produces({MediaType.APPLICATION_JSON})
    public Response scoreExchangeVoucher(@FormParam("data") String data) {
        return getResponse(scoreService.scoreExchangeVoucher(data));
    }


    @Override
    @POST
    @Path("/clearData")
    @Produces({MediaType.APPLICATION_JSON})
    public Response clearData() {
        return getResponse(scoreService.clearData());

    }

    /**
     * 退货回收积分查询 按商品纬度查询
     */
    @Override
    @POST
    @Path("/getRecoveryScore")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRecoveryScore(@FormParam("data") String data) {
        return getResponse(scoreService.getRecoveryScore(getMemGuid(data), data));
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


}
