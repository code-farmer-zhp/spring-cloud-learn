package com.feiniu.score.common;


public final class ResultCode {

	/**
	 * 正确
	 */
	public static final int RESULT_STATUS_SUCCESS = 100;

	/**
	 * 错误
	 */
	public static final int RESULT_STATUS_EXCEPTION = 500;

	/**
	 * 入参数为空
	 */
	public static final int RESULT_IN_PARA_NULL_EXCEPTION = 501;

	/**
	 * 运行时错误
	 */
	public static final int RESULT_RUN_TIME_EXCEPTION = 502;

	/**
	 * 入参不合法
	 */
	public static final int RESULT_IN_PARA_ILLEGAL_EXCEPTION = 503;	
	
	/**
	 * API不存在
	 */
	public static final int RESULT_API_NOT_FOUND_EXCEPTION = 504;	
	
	/**
	 * 积分不足
	 */
	public static final int RESULT_AVAILABLE_SCORE_NOT_ENOUGH = 505;

	/**
	 * 重复提交
	 */
	public static final int RESULT_REPEAT_SUBMIT = 506;

	/**
	 * 员工签到次数过多
	 */
	public static final int RESULT_SIGN_TO_MANY_BECAUSE_OF_EMPLPYEE = 507;
	
	/**
	 * 最近无订单，签到次数过多
	 */
	public static final int RESULT_SIGN_TO_MANY_WITHOUT_ORDER_IN_RECENT_DAYS = 508;
	/**
	 * 无订单的新用户，签到次数过多
	 */
	public static final int RESULT_SIGN_TO_MANY_WITHOUT_ANY_ORDER= 509;


	/************************提交订单异常*******************/

	/**
	 * 未找到订单消费信息，延迟处理
	 */
	public static final int RESULT_SCORE_SUBMIT_ORDER_BUT_NO_CONSUME_LOG = 509;




	/******************退货确认异常*****************/

	/**
	 * 退货确认，已有退货信息，属于二期数据
	 */
	public static final int RESULT_SCORE_RETURN_TWO_PHASE_DATA = 510;

	/**
	 * 未找到订单购买获得积分信息
	 */
	public static final int RESULT_SCORE_RETURN_BUT_NO_BUY_LOG = 511;

	/**
	 * 退货确认请求大于付款确认
	 */
	public static final int RESULT_SCORE_RETURN_BUT_NO_BUY_CONFIRM = 512;


	/******************取消商城订单*****************************/

	/**
	 * 取消商城订单，但未找到详细信息。
	 */
	public static final int RESULT_SCORE_CANCEL_ORDER_BUT_NOT_FIND_DETAIL = 513;
	
	/******************回滚兑换抵用券使用的积分*****************************/
	/**
	 * 积分兑换抵用券的记录
	 */
	public static final int RESULT_ROLL_BACK_BUT_NO_EXCHANGE_CONSUME_LOG = 514;


	/******************查询是否为合伙人出错*****************************/
	/**
	 * 查询是否为合伙人异常
	 */
	public static final int GET_IS_PARTNER_ERROR = 515;
	/******************查询是否为企业用户、工会会员出错****************************/
	/**
	 * 查询是否为企业用户、工会会员异常
	 */
	public static final int GET_IS_TRADE_UNIONIST_ERROR= 516;
	
	
	/**********************成长值异常**********************************/
	/**
	 * 成长值未找到订单消费信息，延迟处理
	 */
	public static final int RESULT_GROWTH_SUBMIT_ORDER_BUT_NO_CONSUME_LOG = 521;
	
	/**
	 * 查询成长值明细 detail表中无用户数据
	 */
	public static final int RESULT_SELECT_GROWTH_INFO_BUT_NO_DETAIL_RECORD = 522;
	
	/**
	 * 置顶或加精时未在growth_order_info表中找到对应的评论
	 */
	public static final int RESULT_GROWTH_Set_Essence_Or_Top_BUT_NO_COMMENT_RECORD = 523;

	/**********************礼包异常**********************************/
	/**
	 *
	 */
	public static final int TAKE_SUCCESS = 100;

	public static final int TAKE_PKAD_BUT_CANCEL = 501;

	public static final int TAKE_PKAD_BUT_EXPIRED=502;

	public static final int TAKE_PKAD_BUT_NOT_RELEVANT =503;

	public static final int TAKE_PKAD_BUT_NOT_EXSIT = 504;

	public static final int GET_LASTPKAD_NOT_EXSIT = 601;

	public static final int CARDARRAY_IS_EMPTY =513;

	/**
	 * 类型转换错误
	 */
	public static final int RESULT_TYPE_CONV_ERROR = 505;
	/**
	 * 直充时多个卡券
	 */
	public static final int PKAD_RECHARGE_BUT_TOO_MANY_CARD = 506;
	/**
	 * 领取礼包动作过于频繁
	 */
	public static final int TAKE_PKAD_BUT_TOO_OFEN = 507;
	/**
	 * 重复提交
	 */
	public static final int TAKE_PKA_REPEAT_SUBMIT = 508;
	
	public static final int TAKE_PKAD_BUT_HAS_TAKEN = 509;
	/**
	 * 领取的礼包本应停止发放
	 */
	public static final int TAKE_PKAD_BUT_BE_STOP_GRANT = 510;

	/**
	 * 领取的礼包是老礼包，且过了兼容期
	 */
	public static final int TAKE_PKAD_BUT_NO_SUPPORT_MRST = 511;

	/**
	 * 合伙人不能领取礼包。已作废！
	 */
	public static final int TAKE_PKAD_BUT_IS_PARTNER = 512;

	/**
	 * 前台礼包领取失败
     */
	public static final int PKAD_NOT_FULLY_TAKEN=513;

	public static final int TAKE_BONUS_BUT_IS_DOUBTABLE = 97;

	public static final int TAKE_BONUS_BUT_IS_EMP = 98;

	public static final int TAKE_PKAD_BUT_NO_BIND_PHONE = 99;
	/**
	 * 短信发送接口调用成功
	 */
	public static final int SEND_MESSAGE_SUCCESS = 100;
	/**
	 * 短信发送接口调用失败
	 */
	public static final int SEND_MESSAGE_UNSUCCESS = 101;
}
