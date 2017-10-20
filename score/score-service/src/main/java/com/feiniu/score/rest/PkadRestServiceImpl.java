package com.feiniu.score.rest;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dto.Result;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.service.C3PkadToKafkaService;
import com.feiniu.score.service.NotakenCouponIdsReportService;
import com.feiniu.score.service.PkadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Controller
@Path("/pkad")
public class PkadRestServiceImpl implements PkadRestService{
	@Autowired
	private PkadService pkadService;
	@Autowired
	private NotakenCouponIdsReportService notakenCouponIdsReportService;	@Autowired
	private C3PkadToKafkaService c3PkadToKafkaService;
	/**
	* @api {post} /pkad/takePkad takePkad
	* @apiVersion 1.0.0
	* @apiName takePkad
	* @apiGroup Pkad
	* @apiDescription 用户领取未取消未过期已生效的礼包
	* @apiParam {Object}   data       入参数据
	* @apiParam {String}   data.memGuid   用户GUID
	* @apiParam {String}   data.pkadSeq   礼包流水号
	* @apiParam {String}   [data.cardSeq]   多选一的礼包必传。即接口getPkadList中cardInfo的card_seq
	* @apiParam {String}   [data.cardType]   多选一的礼包必传。即接口getPkadList中cardInfo的card_type
	* @apiSuccess (Success 100)  {Number}   code    状态码
	* @apiSuccess (Success 100)  {String}   msg 状态码说明
	* @apiSuccess (Success 100)  {Object}   data 返回数据
	* @apiError (Error 5xx) 500   未知错误
	* @apiError (Error 5xx) 501   入参数为空
	* @apiError (Error 5xx) 502   该礼包已经过期
	* @apiError (Error 5xx) 503   卡券信息与礼包不对应 按传入的guid和pkadSeq查询到的礼包不包含cardSeq的卡券
	* @apiError (Error 5xx) 504   该礼包不存在，按传入的guid和pkadSeq未查询到礼包
	* @apiError (Error 5xx) 509   该礼包已经被领取
	* @apiError (Error 5xx) 510  领取的礼包本应停止发放
	* @apiError (Error 5xx) 511  领取的礼包是已停止发放的礼包，且过了兼容期
	* @apiError (Error 5xx) 513  礼包卡券信息为空
	* @apiParamExample 全领取:
	* data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6","pkadSeq":"43"}
	* @apiParamExample 三选一礼包领取:
	* data={"memGuid":"044A8094-7195-6620-AC07-BF75251F24B6","pkadSeq":"56" ,"cardSeq":"20150929C00019998","cardType":"1"}
	* @apiSuccessExample {json} Success-Response:
	* {
	* "code": 100
	* "msg": "领取礼包成功",
	* "data": null
	* }
	 * @apiErrorExample {json} Error-Response:
	* {
	* "code": 504,
	* "msg": "该礼包不存在",
	* "data": null
	* }
	*/
	/**
	* 用户领取未取消未过期已生效的礼包
	*/
	@Override
	@POST
	@Path("/takePkad")
	@Produces({MediaType.APPLICATION_JSON})
	public Response takePkad(@FormParam("data") String data){
		return getResponse(pkadService.takePkadByPkadSeqAndMembId(getMemGuid(data),data));
	}

	/* 入参isTake   isCancel  isExpire说明，*时不传，是传1，否传0
	isTake		isCancel	isExpire
	是否被领取	是否被取消	是否已过期	状态	前台
	是			*			*			已领取	显示在已领取
	否			是			*			已取消	不显示
	否			否			是			已过期	显示在已过期
	否			否			否			待领取	显示在未领取
	 */
	/**
	 * @api {post} /pkad/getPkadList getPkadList
	 * @apiVersion 1.0.0
	 * @apiName getPkadList
	 * @apiGroup Pkad
	 * @apiDescription 查询用户礼包信息
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiParam {Number}   data.pageNo   分页页码，第一页传1
	 * @apiParam {Number}   data.pageSize   分页每页的记录条数
	 * @apiParam {String}   data.isExpire   查询已领取时不传，已过期传1，未领取传0
	 * @apiParam {String}   data.isTake     是否已领取。查询已领取时为1，未领取和已过期为0。
	 * @apiParam {String}   data.isCancel   查询已领取时不传。查询未领取和已过期时传0
	 * @apiParam {String}   [data.order]   排序字段，不传则按数据表id排序。"d_take_f"：礼包生效时间"d_take": 领取时间"d_take_t": 礼包过期时间 。前台查询未领取传d_take_t，已领取传d_take 已过期传d_take_t
	 * @apiParam {String}   [data.sortType="desc"]   排序方式，"desc" 倒序/"asc" 升序 默认"desc"
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object}   data 返回数据
	 * @apiSuccess (Success 100)  {Number}   data.pageNo 当前页数
	 * @apiSuccess (Success 100)  {Number}   data.totalItems 记录总条数
	 * @apiSuccess (Success 100)  {Number}   data.totalPage 记录总页数
	 * @apiSuccess (Success 100)  {Object[]}   data.pkadList 礼包数据
	 * @apiSuccess (Success 100)  {Object[]}   data.pkadList.card_list 礼包中的卡券数据。多个表示可领多张券吧（多个权益）单个权益分为直接一张券，和三选一的券
	 * @apiSuccess (Success 100)  {String}   data.pkadList.card_list.card_id 卡券活动id，已隐藏
	 * @apiSuccess (Success 100)  {String}   data.pkadList.card_list.card_num 单个礼包卡券流水号（同个礼包中不重复）
	 * @apiSuccess (Success 100)  {String}   data.pkadList.card_list.card_seq 卡券流水号，有card_num时与card_num相同，没有时数据库中加密的card_seq
	 * @apiSuccess (Success 100)  {String}   data.pkadList.card_list.card_type 卡券类型  1.自营抵用券 2.自营优惠券 22.商城-优惠券 25.商城-免邮券 29.商城-订单满额送券 5.自营免邮券 6.自营品牌券 7.自营礼品券
	 * @apiSuccess (Success 100)  {String}   data.pkadList.card_list.memb_grade_f
	 * @apiSuccess (Success 100)  {String}   data.pkadList.card_list.memb_id  会员guid
	 * @apiSuccess (Success 100)  {String}   data.pkadList.card_list.mrdf_type 权益类型 B1 积分 C1券一张 C3券多张
	 * @apiSuccess (Success 100)  {String}   data.pkadList.card_list.mrst_id  权益id
	 * @apiSuccess (Success 100)  {String}   data.ddPkad 礼包发放日期
	 * @apiSuccess (Success 100)  {String}   data.ddTake 礼包领取时间
	 * @apiSuccess (Success 100)  {String}   data.ddTakeF 礼包生效时间
	 * @apiSuccess (Success 100)  {String}   data.ddTakeFShort 礼包生效时间缩写。"MM.dd"格式
	 * @apiSuccess (Success 100)  {String}   data.ddTakeT 礼包过期时间
	 * @apiSuccess (Success 100)  {String}   data.ddTakeTShort 礼包过期时间缩写。"MM.dd"格式
	 * @apiSuccess (Success 100)  {Number}   data.insTime 记录插入时间,long类型的时间戳
	 * @apiSuccess (Success 100)  {Number}   data.isCancel 是否被取消
	 * @apiSuccess (Success 100)  {Number}   data.isOpen 是否被打开。作废字段。无用
	 * @apiSuccess (Success 100)  {Number}   data.isRecharge 是否是直充型（直充型不需要在前台领取）
	 * @apiSuccess (Success 100)  {Number}   data.isTake 是否被领取
	 * @apiSuccess (Success 100)  {String}   data.membGradeF 去重用
	 * @apiSuccess (Success 100)  {String}   data.membId 会员guid
	 * @apiSuccess (Success 100)  {String}   data.mrstUi 礼包类型
	 * @apiSuccess (Success 100)  {String}   data.mrstUiDesc 礼包类型说明
	 * @apiSuccess (Success 100)  {String}   data.mrstUiName 自定义礼包名称
	 * @apiSuccess (Success 100)  {String}   data.pkadCnt 礼包所含权益数量（卡券权益数量加上积分权益数量，即cardInfo.size()+pointInfo.size()）
	 * @apiSuccess (Success 100)  {String}   data.pkadId 礼包id
	 * @apiSuccess (Success 100)  {String}   data.pkadSeq 礼包流水号
	 * @apiSuccess (Success 100)  {Object[]}   data.pointInfo 礼包中的积分数据
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pointInfo.d_eff_f 积分生效日期
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pointInfo.d_eff_t 积分失效日期
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pointInfo.memb_grade_f 去重用
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pointInfo.memb_id 会员guid
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pointInfo.mrdf_point 积分数
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pointInfo.mrdf_type 权益类型 B1 积分 C1券一张 C3券多张
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pointInfo.mrst_id  权益id
	 * @apiSuccess (Success 100)  {String}   data.updateTime 记录更新时间,long类型的时间戳
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 502   运行时错误
	 * @apiParamExample 未领取:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","order":"d_take_t","isTake":0,"pageNo":1,"isCancel":0,"sortType":"desc","pageSize":10,"isExpire":0}
	 * @apiParamExample 已领取:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","order":"d_take","isTake":1,"pageNo":1,"isCancel":"","sortType":"desc","pageSize":10,"isExpire":""}
	 * @apiParamExample 已过期:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","order":"d_take_t","isTake":0,"pageNo":1,"isCancel":0,"sortType":"desc","pageSize":10,"isExpire":1}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * 	"code": 100,
	 * 	"data": {
	 * 		"pageNo": 1,
	 * 		"pkadList": [
	 * 			{
	 * 			"cardInfo": [
	 * 			{
	 * 				"card_list": [
	 * 				{
	 * 				"card_id": "",
	 * 				"card_num": "vlvc_id",
	 * 				"card_seq": "vlvc_id",
	 * 				"card_type": "22"
	 * 				}
	 * 				],
	 * 				"memb_grade_f": "MRST000699+",
	 * 				"memb_id": "CDC35244-F926-F5AD-5EC7-1B693E8FCE9D",
	 * 				"mrdf_type": "C1",
	 * 				"mrst_id": "MRST000699"
	 * 				}
	 * 			],
	 * 			"ddPkad": "20170317",
	 * 			"ddTake": "",
	 * 			"ddTakeF": "20170317",
	 * 			"ddTakeFShort": "03.17",
	 * 			"ddTakeT": "20170320",
	 * 			"ddTakeTShort": "03.20",
	 * 			"insTime": 1489714123000,
	 * 			"isCancel": 0,
	 * 			"isOpen": "0",
	 * 			"isRecharge": 0,
	 * 			"isTake": 0,
	 * 			"membGradeF": "MRPK00491+",
	 * 			"membId": "CDC35244-F926-F5AD-5EC7-1B693E8FCE9D",
	 * 			"mrstUi": "T2",
	 * 			"mrstUiDesc": "升级礼包",
	 * 			"mrstUiName": "升级礼包",
	 * 			"pkadCnt": 2,
	 * 			"pkadId": "MRPK00491",
	 * 			"pkadSeq": 14322,
	 * 			"pkadType": "1",
	 * 			"pointInfo": [
	 * 			{
	 * 				"d_eff_f": "20170317",
	 * 				"d_eff_t": "null",
	 * 				"memb_grade_f": "MRST000698+",
	 * 				"memb_id": "CDC35244-F926-F5AD-5EC7-1B693E8FCE9D",
	 * 				"mrdf_point": "13",
	 * 				"mrdf_type": "B1",
	 * 				"mrst_id": "MRST000698"
	 * 			}
	 * 			],
	 * 			"updateTime": 1489714123000
	 * 			}
	 * 		],
	 * 		"totalItems": 1,
	 * 		"totalPage": 1
	 * 	},
	 * 	"msg": "success"
	 * }
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 504,
	 * "msg": "该礼包不存在",
	 * "data": null
	 * }
	 */
	/**
	 * 查询用户礼包信息
	 */
	@Override
	@POST
	@Path("/getPkadList")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPkadList(@FormParam("data") String data){
		return getResponse(pkadService.getPkadListBySel(getMemGuid(data), data));
	}

	/**
	 * @api {post} /pkad/getPkadListCount getPkadListCount
	 * @apiVersion 1.0.0
	 * @apiName getPkadListCount
	 * @apiGroup Pkad
	 * @apiDescription 按条件查询用户礼包数量
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiParam {String}   data.isExpire   查询已领取时不传，已过期传1，未领取传0
	 * @apiParam {String}   data.isTake     是否已领取。查询已领取时为1，未领取和已过期为0。
	 * @apiParam {String}   data.isCancel   查询已领取时不传。查询未领取和已过期时传0
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object}   data 返回数据
	 * @apiSuccess (Success 100)  {Number}   data.totalItems 记录总条数
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 502   运行时错误
	 * @apiParamExample 未领取:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","isTake":0,"isCancel":0,"isExpire":0}
	 * @apiParamExample 已领取:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","isTake":1,"isCancel":"","isExpire":""}
	 * @apiParamExample 已过期:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","isTake":0,"isCancel":0,"isExpire":1}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "code": 100,
	 * "data":{
	 * 		"totalItems": 1
	 * 	},
	 * 	"msg": "success"
	 * }
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 504,
	 * "msg": "该礼包不存在",
	 * "data": null
	 * }
	 */
	/**
	 * 按条件查询用户礼包数量
	 */
	@Override
	@POST
	@Path("/getPkadListCount")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPkadListCount(@FormParam("data") String data){
		return getResponse(pkadService.getPkadListBySelCount(getMemGuid(data), data));
	}

	/**
	 * @api {post} /pkad/getMrstUiList getMrstUiList
	 * @apiVersion 1.0.0
	 * @apiName getMrstUiList
	 * @apiGroup Pkad
	 * @apiDescription 查询会员已点亮的权益（合伙人、工会用户为空。其余会员，已点亮权益即发放过，已生效的礼包类型）
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object}   data 返回数据
	 * @apiSuccess (Success 100)  {String[]}   data.mrstUiList 已点亮的权益，T1生日礼包 T2升级礼包 T3神秘礼包 T4周年惊喜礼包 T5新人礼包 TZ会员福利礼包 T6周年庆专享礼包
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 502   运行时错误
	 * @apiParamExample 未领取:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","isTake":0,"isCancel":0,"isExpire":0}
	 * @apiParamExample 已领取:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","isTake":1,"isCancel":"","isExpire":""}
	 * @apiParamExample 已过期:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","isTake":0,"isCancel":0,"isExpire":1}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * 	"code":100,
	 * 	"data":{
	 * 		"mrstUiList":["T2","T3","T4"]
	 * 	}
	 *	,"msg":"success"
	 * 	}
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 501,
	 * "msg": "入参不能为空",
	 * "data": null
	 * }
	 */
	/**
	 * 查询会员已点亮的权益
	 */
	@Override
	@POST
	@Path("/getMrstUiList")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getMrstUiList(@FormParam("data") String data){
		return getResponse(pkadService.getMrstUiListBySel(getMemGuid(data), data));
	}

	/**
	 * @api {post} /pkad/getCardInfo getCardInfo
	 * @apiVersion 1.0.0
	 * @apiName getCardInfo
	 * @apiGroup Pkad
	 * @apiDescription 查询礼包包含的卡券
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiParam {String}   data.pkadSeq   用户GUID
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object[]}   data 返回数据
	 * @apiSuccess (Success 100)  {String}   data.cardSeq 卡券流水号
	 * @apiSuccess (Success 100)  {String}   data.cardType 卡券类型  1.抵用券 2.优惠券
	 * @apiSuccess (Success 100)  {String}   data.cardTypeDesc 卡券类型 描述
	 * @apiSuccess (Success 100)  {String}   data.discount 满price减discount price为0时为抵用金额
	 * @apiSuccess (Success 100)  {String}   data.endDate startDate不为空时为过期时间点，否则为领取后多少天过期
	 * @apiSuccess (Success 100)  {String}   data.name 卡券活动名称
	 * @apiSuccess (Success 100)  {String}   data.price 满price减discount，为0表示无门槛
	 * @apiSuccess (Success 100)  {String}   data.scopeDescription 卡券适用范围
	 * @apiSuccess (Success 100)  {String}   data.seq 卡券活动号，已隐藏
	 * @apiSuccess (Success 100)  {String}   data.startDate 卡券生效期
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 504   该礼包不存在/该礼包不包含卡券
	 * @apiParamExample Request-Example:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","pkadSeq":"16005"}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "code": 100,
	 * "data": [
	 * {
	 * 	"cardSeq": "vlvc_id",
	 * 	"cardType": "2",
	 * 	"cardTypeDesc": "优惠券",
	 * 	"discount": "30",
	 * 	"endDate": "2017/04/10",
	 * 	"name": "会员福利170328状态2",
	 * 	"price": "150",
	 * 	"scopeDescription": "自营/商家直送通用（部分商品除外）",
	 * 	"seq": "",
	 * 	"startDate": "2017/03/28"
	 * }
	 * ],
	 * "msg": "查询礼包卡券信息成功"
	 * }
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "code": 100,
	 * "data": [
	 * {
	 * 	"cardSeq": "fa6e48afb78591c04f8046ec6e05116a",
	 * 	"cardType": "1",
	 * 	"cardTypeDesc": "抵用券",
	 * 	"discount": "50",
	 * 	"endDate": "21",
	 * 	"name": "会员福利161026",
	 * 	"price": "",
	 * 	"scopeDescription": "自营全品类（部分商品除外）",
	 * 	"seq": "",
	 * 	"startDate": ""
	 * }
	 * ],
	 * "msg": "查询礼包卡券信息成功"
	 * }
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 501,
	 * "msg": "入参不能为空",
	 * "data": null
	 * }
	 */
	/**
	 * 查询礼包卡券活动信息
	 */
	@Override
	@POST
	@Path("/getCardInfo")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getCardInfo(@FormParam("data") String data){
		return getResponse(pkadService.getCardInfo(getMemGuid(data), data));
	}

	/**
	 * @api {post} /pkad/getCardInfoBatch getCardInfoBatch
	 * @apiVersion 1.0.0
	 * @apiName getCardInfoBatch
	 * @apiGroup Pkad
	 * @apiDescription 批量查询礼包包含的卡券
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiParam {String}   data.pkadSeqs   礼包流水号，用逗号间隔
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object}   data 返回数据，以pkadSeq为key的json对象
	 * @apiSuccess (Success 100)  {String}   data.cardSeqForBatch 卡券流水号，批量查询时做卡券匹配的唯一键
	 * @apiSuccess (Success 100)  {String}   data.cardSeq 卡券流水号
	 * @apiSuccess (Success 100)  {String}   data.cardType 卡券类型  1.抵用券 2.优惠券
	 * @apiSuccess (Success 100)  {String}   data.cardTypeDesc 卡券类型 描述
	 * @apiSuccess (Success 100)  {String}   data.discount 满price减discount price为0时为抵用金额
	 * @apiSuccess (Success 100)  {String}   data.endDate startDate不为空时为过期时间点，否则为领取后多少天过期
	 * @apiSuccess (Success 100)  {String}   data.name 卡券活动名称
	 * @apiSuccess (Success 100)  {String}   data.price 满price减discount，为0表示无门槛
	 * @apiSuccess (Success 100)  {String}   data.scopeDescription 卡券适用范围
	 * @apiSuccess (Success 100)  {String}   data.seq 卡券活动号，已隐藏
	 * @apiSuccess (Success 100)  {String}   data.startDate 卡券生效期
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 504   该礼包不存在/该礼包不包含卡券
	 * @apiParamExample Request-Example:
	 * data={"pkadSeqs":"11998,5838,5406,5648","memGuid":"3CEDA7BF-B213-10AB-2530-842517BBFA09"}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "code": 100,
	 * "data": {
	 * "11998": [
	 *	{
	 *		"cardSeq": "ba4f2d1ea627afe61fdb0856bd897f5e",
	 *		"cardSeqForBatch": "ba4f2d1ea627afe61fdb0856bd897f5e",
	 *		"cardType": "1",
	 *		"cardTypeDesc": "抵用券",
	 *		"discount": "50",
	 *		"endDate": "21",
	 *		"name": "会员福利161125",
	 *		"price": "",
	 *		"scopeDescription": "自营全品类（部分商品除外）",
	 *		"seq": "",
	 *		"startDate": ""
	 *	}
	 *	],
	 *	"5406": [
	 *	{
	 *		"cardSeq": "38d2c240e4b89338c73b344cb5de050d",
	 *		"cardSeqForBatch": "38d2c240e4b89338c73b344cb5de050d",
	 *		"cardType": "1",
	 *		"cardTypeDesc": "抵用券",
	 *		"discount": "50",
	 *		"endDate": "21",
	 *		"name": "会员福利1606-5",
	 *		"price": "",
	 *		"scopeDescription": "自营全品类（部分商品除外）",
	 *		"seq": "",
	 *		"startDate": ""
	 *	}
	 *	],
	 *	"5648": [
	 *	{
	 *		"cardSeq": "498f8213bbb8caa134e36ba8b0d98f37",
	 *		"cardSeqForBatch": "498f8213bbb8caa134e36ba8b0d98f37",
	 *		"cardType": "1",
	 *		"cardTypeDesc": "抵用券",
	 *		"discount": "50",
	 *		"endDate": "21",
	 *		"name": "会员福利1606-16",
	 *		"price": "",
	 *		"scopeDescription": "自营全品类（部分商品除外）",
	 *		"seq": "",
	 *		"startDate": ""
	 *	}
	 *	],
	 *	"5838": [
	 *	{
	 *		"cardSeq": "8454b8efc51acf3a254b3122fdef6f59",
	 *		"cardSeqForBatch": "8454b8efc51acf3a254b3122fdef6f59",
	 *		"cardType": "1",
	 *		"cardTypeDesc": "抵用券",
	 *		"discount": "50",
	 *		"endDate": "21",
	 *		"name": "会员福利1606-25",
	 *		"price": "",
	 *		"scopeDescription": "自营全品类（部分商品除外）",
	 *		"seq": "",
	 *		"startDate": ""
	 *	}
	 *	]
	 *},
	 *"msg": "批量查询礼包卡券信息成功"
	 *}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 *	"code": 100,
	 *	"data": {
	 *	"15169": [
	 *	{
	 *		"cardSeq": "vlvc_id",
	 *		"cardSeqForBatch": "a4d16ce623e410890e7a2779a8ec8abd",
	 *		"cardType": "2",
	 *		"cardTypeDesc": "优惠券",
	 *		"discount": "30",
	 *		"endDate": "2017/04/10",
	 *		"name": "会员福利170328状态2",
	 *		"price": "150",
	 *		"scopeDescription": "自营/商家直送通用（部分商品除外）",
	 *		"seq": "",
	 *		"startDate": "2017/03/28"
	 *	}
	 *	]
	 *	},
	 *	"msg": "批量查询礼包卡券信息成功"
	 *	}
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 501,
	 * "msg": "入参不能为空",
	 * "data": null
	 * }
	 */
	/**
	 * 查询礼包卡券活动信息，批量
	 */
	@Override
	@POST
	@Path("/getCardInfoBatch")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getCardInfoBatch(@FormParam("data") String data){
		return getResponse(pkadService.getCardInfoBatch(getMemGuid(data), data));
	}

	/**
	 * @api {post} /pkad/getTakenCardInfo getTakenCardInfo
	 * @apiVersion 1.0.0
	 * @apiName getTakenCardInfo
	 * @apiGroup Pkad
	 * @apiDescription 查询礼包已领取的卡券活动信息
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiParam {String}   data.pkadSeq   用户GUID
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object[]}   data 返回数据
	 * @apiSuccess (Success 100)  {String}   data.cardType 卡券类型  1.抵用券 2.优惠券
	 * @apiSuccess (Success 100)  {String}   data.cardTypeDesc 卡券类型 描述
	 * @apiSuccess (Success 100)  {String}   data.discount 满price减discount price为0时为抵用金额
	 * @apiSuccess (Success 100)  {String}   data.endDate startDate不为空时为过期时间点，否则为领取后多少天过期
	 * @apiSuccess (Success 100)  {String}   data.name 卡券活动名称
	 * @apiSuccess (Success 100)  {String}   data.price 满price减discount，为0表示无门槛
	 * @apiSuccess (Success 100)  {String}   data.scopeDescription 卡券适用范围
	 * @apiSuccess (Success 100)  {String}   data.seq 卡券活动号，已隐藏
	 * @apiSuccess (Success 100)  {String}   data.startDate 卡券生效期
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 504   该礼包不存在/该礼包不包含卡券
	 * @apiParamExample Request-Example:
	 * data={"memGuid":"CDC35244-F926-F5AD-5EC7-1B693E8FCE9D","pkadSeq":"16005"}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "code": 100,
	 * "data": [
	 * {
	 * 	"cardType": "1",
	 * 	"cardTypeDesc": "抵用券",
	 * 	"discount": "30",
	 * 	"endDate": "21",
	 * 	"name": "会员福利170212",
	 * 	"price": "",
	 * 	"scopeDescription": "自营全品类（部分商品除外）",
	 * 	"seq": "",
	 * 	"startDate": ""
	 * }
	 * ],
	 * "msg": "查询礼包已领取卡券信息成功"
	 * }
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 501,
	 * "msg": "入参不能为空",
	 * "data": null
	 * }
	 */
	/**
	 * 查询礼包已领取的卡券活动信息
	 */
	@Override
	@POST
	@Path("/getTakenCardInfo")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTakenCardInfo(@FormParam("data") String data){
		return getResponse(pkadService.getTakenCardInfo(getMemGuid(data), data));
	}

	/**
	 * @api {post} /pkad/getTakenCardInfoBatch getTakenCardInfoBatch
	 * @apiVersion 1.0.0
	 * @apiName getTakenCardInfoBatch
	 * @apiGroup Pkad
	 * @apiDescription 查询礼包已领取的卡券活动信息，批量
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiParam {String}   data.pkadSeqs   礼包流水号，用逗号间隔
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object}   data 返回数据，以pkadSeq为key的json对象
	 * @apiSuccess (Success 100)  {String}   data.cardType 卡券类型  1.抵用券 2.优惠券
	 * @apiSuccess (Success 100)  {String}   data.cardTypeDesc 卡券类型 描述
	 * @apiSuccess (Success 100)  {String}   data.discount 满price减discount price为0时为抵用金额
	 * @apiSuccess (Success 100)  {String}   data.endDate startDate不为空时为过期时间点，否则为领取后多少天过期
	 * @apiSuccess (Success 100)  {String}   data.name 卡券活动名称
	 * @apiSuccess (Success 100)  {String}   data.price 满price减discount，为0表示无门槛
	 * @apiSuccess (Success 100)  {String}   data.scopeDescription 卡券适用范围
	 * @apiSuccess (Success 100)  {String}   data.seq 卡券活动号，已隐藏
	 * @apiSuccess (Success 100)  {String}   data.startDate 卡券生效期
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 504   该礼包不存在/该礼包不包含卡券
	 * @apiParamExample Request-Example:
	 * data={"pkadSeqs":"8770,7053,11823,14054","memGuid":"3CEDA7BF-B213-10AB-2530-842517BBFA09"}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 *	"code": 100,
	 *	"data": {
	 *		"11823": [
	 *		{
	 *			"cardType": "2",
	 *			"cardTypeDesc": "优惠券",
	 *			"discount": "40",
	 *			"endDate": "2017/02/01",
	 *			"name": "T2神秘礼券",
	 *			"price": "199",
	 *			"scopeDescription": "自营/商家直送通用（部分商品除外）",
	 *			"seq": "",
	 *			"startDate": "2016/12/02
	 *		}
	 *		],
	 *		"14054": [
	 *		{
	 *			"cardType": "2",
	 *			"cardTypeDesc": "优惠券",
	 *			"discount": "40",
	 *			"endDate": "2017/04/09",
	 *			"name": "T2神秘礼券",
	 *			"price": "199",
	 *			"scopeDescription": "自营/商家直送通用\n部分商品除外）",
	 *			"seq": "",
	 *			"startDate": "2017/02/09"
	 *		}
	 *		],
	 *		"7053": [
	 *		{
	 *			"cardType": "1",
	 *			"cardTypeDesc": "抵用券",
	 *			"discount": "50",
	 *			"endDate": "21",
	 *			"name": "会员福利160809",
	 *			"price": "",
	 *			"scopeDescription": "自营全品类（部分商品除外）",
	 *			"seq": "",
	 *			"startDate": ""
	 *		}
	 *		],
	 *		"8479": [
	 *		{
	 *			"cardType": "1",
	 *			"cardTypeDesc": "抵用券",
	 *			"discount": "50",
	 *			"endDate": "30",
	 *			"name": "升级礼包",
	 *			"price": "",
	 *			"scopeDescription": "自营全品类（部分商品除外）",
	 *			"seq": "",
	 *			"startDate": ""
	 *		}
	 *		]
	 *	},
	 * "msg": "批量查询礼包已领取卡券信息成功"
	 *}
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 501,
	 * "msg": "入参不能为空",
	 * "data": null
	 * }
	 */
	/**
	 * 查询礼包已领取的卡券活动信息，批量
	 */
	@Override
	@POST
	@Path("/getTakenCardInfoBatch")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getTakenCardInfoBatch(@FormParam("data") String data){
		return getResponse(pkadService.getTakenCardInfoBatch(getMemGuid(data), data));
	}

	/**
	 * @api {post} /pkad/getPkadListForERP getPkadListForERP
	 * @apiVersion 1.0.0
	 * @apiName getPkadListForERP
	 * @apiGroup Pkad
	 * @apiDescription 查询礼包，给ERP使用
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiParam {String}   data.PageIndex   页码
	 * @apiParam {String}   data.RowCount   每页记录数
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object}   data 返回数据
	 * @apiSuccess (Success 100)  {Number}   data.PageIndex 当前页数
	 * @apiSuccess (Success 100)  {Number}   data.TotalItems 记录总条数
	 * @apiSuccess (Success 100)  {Number}   data.TotalPage 记录总页数
	 * @apiSuccess (Success 100)  {Object[]}   data.pkadList 礼包数据
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pkadDesc 礼包类型描述
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pkadGiveTime 礼包发放时间，精确到天
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pkadStatus 礼包状态
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pkadTakeTime 礼包领取时间
	 * @apiSuccess (Success 100)  {String}   data.pkadList.pkadValidTime 礼包有效期
	 * @apiSuccess (Success 100)  {String}   data.pkadList.takeCardSeq 礼包已领取的卡券
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 502	运行时错误
	 * @apiParamExample Request-Example:
	 * data={"memGuid":"3CEDA7BF-B213-10AB-2530-842517BBFA09","RowCount":"20","PageIndex":"1"}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 *	"code": 100,
	 *	"data": {
	 *		"PageIndex": 1,
	 *		"TotalItems": 5,
	 *		"TotalPage": 1,
	 *		"pkadList": [
	 *		{
	 *			"pkadDesc": "周年庆专享礼包",
	 *			"pkadGiveTime": "2016/09/28 00:00:00",
	 *			"pkadStatus": "已领取",
	 *			"pkadTakeTime": "2016/09/28 13:28:32",
	 *			"pkadValidTime": "2016/09/28 00:00:00-2016/10/11 23:59:59",
	 *			"takeCardSeq": {
	 *				"SC1fd54775-b360-4aec-a4c2-336a1812b926": "SCg9sds030V01KM00sS"
	 *			}
	 *		},
	 *		{
	 *			"pkadDesc": "会员福利礼包",
	 *			"pkadGiveTime": "2016/11/15 00:00:00",
	 *			"pkadStatus": "已过期",
	 *			"pkadTakeTime": "",
	 *			"pkadValidTime": "2016/11/15 00:00:00-2016/11/17 23:59:59"
	 *		},
	 *		{
	 *			"pkadDesc": "神秘礼包",
	 *			"pkadGiveTime": "2016/12/06 00:00:00",
	 *			"pkadStatus": "已领取",
	 *			"pkadTakeTime": "2016/12/06 18:30:17",
	 *			"pkadValidTime": "2016/12/06 00:00:00-2017/01/05 23:59:59",
	 *			"takeCardSeq": {
	 *				"ZY84Y3GC299WAOIY00003410": "C02139100006977"
	 *			}
	 *		},
	 *		{
	 *			"pkadDesc": "神秘礼包",
	 *			"pkadGiveTime": "2017/02/09 00:00:00",
	 *			"pkadStatus": "已领取",
	 *			"pkadTakeTime": "2017/02/09 16:52:51",
	 *			"pkadValidTime": "2017/02/09 00:00:00-2017/03/09 23:59:59",
	 *			"takeCardSeq": {
	 *				"ZYNJO3H1MADMSL1F00020838": "C02172300000630"
	 *			}
	 *		},
	 *		{
	 *			"pkadDesc": "神秘礼包",
	 *			"pkadGiveTime": "2017/03/29 00:00:00",
	 *			"pkadStatus": "已领取",
	 *			"pkadTakeTime": "2017/03/29 15:23:36",
	 *			"pkadValidTime": "2017/03/29 00:00:00-2017/04/28 23:59:59",
	 *			"takeCardSeq": {
	 *				"ZYBISOH3SAARZB5H00003628": "C02202800000888"
	 *			}
	 *		}
	 *		]
	 *	},
	 *	"msg": "success"
	 *}
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 501,
	 * "msg": "入参不能为空",
	 * "data": null
	 * }
	 */
	/**
	 * 查询礼包，给ERP使用
	 */
	@Override
	@POST
	@Path("/getPkadListForERP")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPkadListForERP(@FormParam("data") String data){
		return getResponse(pkadService.getPkadListBySelForERP(getMemGuid(data),data));
	}

	/**
	 * @api {post} /pkad/getPkadCountForERP getPkadCountForERP
	 * @apiVersion 1.0.0
	 * @apiName getPkadCountForERP
	 * @apiGroup Pkad
	 * @apiDescription 查询礼包，给ERP使用
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object}   data 返回数据
	 * @apiSuccess (Success 100)  {Number}   data.pageNo 当前页数
	 * @apiSuccess (Success 100)  {Number}   data.TotalItems 记录总条数
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 502	运行时错误
	 * @apiParamExample Request-Example:
	 * data={"memGuid":"3CEDA7BF-B213-10AB-2530-842517BBFA09"}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 *	"code": 100,
	 *	"data": {
	 *		"TotalItems": 5
	 *	},
	 *	"msg": "success"
	 *}
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * "code": 501,
	 * "msg": "入参不能为空",
	 * "data": null
	 * }
	 */
	/**
	 * 查询礼包数量
	 */
	@Override
	@POST
	@Path("/getPkadCountForERP")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getPkadCountForERP(@FormParam("data") String data){
		return getResponse(pkadService.getPkadCountBySelForERP(getMemGuid(data),data));
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


	/**
	 * @api {post} /pkad/getLastPkad getLastPkad
	 * @apiVersion 1.0.0
	 * @apiName getLastPkad
	 * @apiGroup Pkad
	 * @apiDescription 查询最新一条未领取的礼包
	 * @apiParam {Object}   data       入参数据
	 * @apiParam {String}   data.memGuid   用户GUID
	 * @apiSuccess (Success 100)  {Number}   code    状态码
	 * @apiSuccess (Success 100)  {String}   msg 状态码说明
	 * @apiSuccess (Success 100)  {Object}   data 返回数据
	 * @apiSuccess (Success 100)  {Number}   data.msg 礼包类型描述
	 * @apiSuccess (Success 100)  {Number}   data.type 礼包类型
	 * @apiError (Error 5xx) 500   未知错误
	 * @apiError (Error 5xx) 501   入参数为空
	 * @apiError (Error 5xx) 502	运行时错误
	 * @apiError (Error 5xx) 502	该用户没有未领取的礼包
	 * @apiParamExample Request-Example:
	 * data={"memGuid":"3CEDA7BF-B213-10AB-2530-842517BBFA09"}
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "code": 100,
	 * "data": {
	 *	"msg": "会员福利礼包",
	 *	"type": "TZ"
	 * },
	 * "msg": "查询礼包成功"
	 * }
	 * @apiErrorExample {json} Error-Response:
	 * {
	 * 	"code":601,
	 * 	"msg":"该用户没有未领取的礼包"
	 * 	}
	 */
	/**
	 *
	 * 查询最新一条未领取的礼包
	 * @return
     */
	@Override
	@POST
	@Path("/getLastPkad")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getLastPkad(@FormParam("data") String data){
		if (StringUtils.isEmpty(data)) {
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "入参不能为空");
		}
		String memGuid = getMemGuid(data);
		if(StringUtils.isEmpty(memGuid)){
			throw new ScoreException(ResultCode.RESULT_IN_PARA_NULL_EXCEPTION, "memGuid 不能为空");
		} else {
			return getResponse(pkadService.getLastPkad(memGuid));
		}

	}

	@Override
	@POST
	@Path("/c3ToSend")
	@Produces({MediaType.APPLICATION_JSON})
	public Response executeJob() {
		c3PkadToKafkaService.executeJob();
		return getResponse(new Result(ResultCode.SEND_MESSAGE_SUCCESS,"finished!"));
	}



	private Response getResponse(Result result) {
		return Response
				.ok((result == null
						? new Result(ResultCode.RESULT_STATUS_EXCEPTION, "服务器异常！")
						: result), MediaType.APPLICATION_JSON).build();
	}

	@Override
	@POST
	@Path("/noTakenCouponIdSearch")
	@Produces({MediaType.APPLICATION_JSON})
	public Response executeNoTakenCouponIdJob() {
		notakenCouponIdsReportService.executeJob();
		return getResponse(new Result(ResultCode.SEND_MESSAGE_SUCCESS,"finished!"));
	}



}
