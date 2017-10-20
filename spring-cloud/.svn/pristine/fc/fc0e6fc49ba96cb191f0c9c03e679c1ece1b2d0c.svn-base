/**
 * Package name:com.feiniu.constant
 * File name:Constants.java
 * Date:2015年11月11日-下午4:51:22
 * feiniu.com Inc.Copyright (c) 2013-2015 All Rights Reserved.
 *
 */
package com.feiniu.constant;


/**
 * @ClassName Constants
 * @Description 常量类
 * @date 2015年11月11日 下午4:51:22
 * @author jun.wu
 * @version 1.0.0
 *
 */
public class Constants {
	
	public static String ORACLE_MODE_NAME;

	static {
		TIMEOT_TIME = Integer.parseInt(CustomerPropertyConfigurer.getProperty("timeout.time").toString());
		MD5_KEY = (String) CustomerPropertyConfigurer.getProperty("md5.key");
		MEMBERAPI_SECRETKEY = (String) CustomerPropertyConfigurer.getProperty("memberapi.secretkey");
		ORACLE_MODE_NAME = (String) CustomerPropertyConfigurer.getProperty("oracle.mode.name");
	}
	
	/**
	 * 新版图片验证码要使用
	 */
	public static final String CAPTCHA_PIC_TYPE = "member-fnapp";
	public static final String TMS_KEY = "FN02";
	public static final String TMS_TOKEN = "12b6c7d126f3a4ea";
	public static final String MEMCACHED_VOUCHERQTY = "memcached_voucherqty";
	
	/**
	 * 客服联系电话
	 */
	public static final String KEFU_NUMBER = "400-920-6565";

	public static final int SUPPLIER_TYPE_FEINIU = 1;

	public static final int SUPPLIER_TYPE_MALL = 2;

	public static final String ENV_DEV = "dev";

	public static final String ENV_DEVELOPMENT = "development";
//	public static final String ENV_PREVIEW = "preview";

	public static final String ENV_TESTING = "testing";//preview环境

	public static final String ENV_PRODUCTION = "production";//online环境

	public static final String NAME_TEST = "@test.fn";
	
	public static final int STATUS_FIRST_NO = 1;//预售单定金未支付  包裹状态显示待支付
	
	public static final int STATUS_FIRST_DONE = 2;//预售单定金已经支付，尾款未开始支付，包裹状态显示待支付
	
	public static final int STATUS_LAST_NO = 3;//尾款已经开始支付，但是未支付，包裹状态显示待支付
	
	public static final int STATUS_LAST_DONE = 4;//尾款已经支付 ，此时定金尾款均已经支付，包裹状态显示待发货
	
	public static final int STATUS_CANCEL_ALL_NO = 5;//预售单已取消，定金已经支付，尾款未支付，此时定金显示已支付，尾款显示未完成，包裹状态显示已取消
	
	public static final int STATUS_CANCEL_FISRT_DONE = 6;//预售单已取消，定金未支付，尾款未支付，此时定金和尾款均显示已取消，包裹状态显示已取消

	/** kafka topic 升级app送积分 */
	public static final String KAFKA_FN_TOPIC_SCORE_APP_UPGRADE = "fn_topic_score_app_upgrade";

	// 登录(图片)
	public static final int CAPTCHA_PIC_LOGIN = 1;

	// 注册(图片)
	public static final int CAPTCHA_PIC_REGIST = 2;

	// 注册(短信)
	public static final int CAPTCHA_SMS_REGIST = 3;

	// 忘记密码(图片)
	public static final int CAPTCHA_PIC_FORGET_PASSWORD = 4;

	// 忘记密码(短信)
	public static final int CAPTCHA_SMS_FORGET_PASSWORD = 5;

	// 忘记密码(邮箱)
	public static final int CAPTCHA_EMAIL_FORGET_PASSWORD = 6;

	// 绑定手机(短信)
	public static final int CAPTCHA_SMS_BINDPHONE = 7;

	// 修改支付密码(邮箱)
	public static final int CAPTCHA_EMAIL_MODIFY_PASSWORD = 8;

	// 修改支付密码(短信)
	public static final int CAPTCHA_SMS_MODIFY_PASSWORD = 9;

	// 购物卡充值(图片)
	public static final int CAPTCHA_PIC_CARD_POINT = 10;

	// 预约商品(短信)
	public static final int CAPTCHA_SMS_BOOKING_GOODS = 11;

	// 12:微信活动(图片)
	public static final int CAPTCHA_PIC_WEIXIN_ACTIVITY = 12;

	// 13:微信活动(短信)
	public static final int CAPTCHA_SMS_WEIXIN_ACTIVITY = 13;

	// 14:提交订单(图片)
	public static final int CAPTCHA_PIC_SUBMIT_ORDER = 14;
	
	// 15:快捷登录(短信)
	public static final int CAPTCHA_SMS_LOGIN = 15;
	
	// 16.注册(图片短信)
	public static final int CAPTCHA_PIC_SMS_REGIST = 16;
	
	// 17.微信活动(图片扭曲)
	public static final int CAPTCHA_PIC_WEIXIN_ACTIVITY_TWIST = 17;
	
	// 18.合约机(短信)
	public static final int CAPTCHA_SMS_CONTRACT = 18;

	// 19.账户异常登陆(短信)
	public static final int CAPTCHA_SMS_ABNORMAL_LOGIN = 19;
	
	// 20.账户异常登陆(邮箱)
	public static final int CAPTCHA_EMAIL_ABNORMAL_LOGIN = 20;
	
	// 21. APP短信登录(短信)
	public static final int CAPTCHA_SMS_LOGIN_REGIST = 21;
	
	// 22. 修改登录密码(短信)
	public static final int CAPTCHA_SMS_MODIFY_LOGIN_PASSWORD = 22;

	// 23. 卡券充值(短信)
	public static final int CAPTCHA_SMS_COUPONS_RECHARGE = 23;
	
	// 超时时间
	public static int TIMEOT_TIME;

	// MD5 KEY
	public static String MD5_KEY;

	// 加密解密收货人信息的KEY
	public static String MEMBERAPI_SECRETKEY;
	
	/** 抵用券 */
	public static final int COUPON_POINT = 1;

	/** 购物卡 (飞牛卡) */
	public static final int CARD_POINT = 2;

	/** 购物金 */
	public static final int CASH_POINT = 3;

	/** 优惠券(领取) */
	public static final int VOUCHER_POINT = 4;

	/** 店铺红包 */
	public static final int RED_ENVELOPE = 5;
	
	/** 用户积分 */
	public static final String MEMBER_POINT_KEY = "MEMBER_POINT_KEY";
	/** kafka topic vvip */
	public static final String MEMBER_VVIP_CHECKSTATE = "MEMBER_VVIP_CHECKSTATE";
	public static final String KAFKA_MEMBER_VVIP_STATUS_KEY = "check vvip status";

	// ***************
	// ** 购物车状态 **
	// ***************
	/**
	 * 加入购物车
	 */
	public static final int SALETYPE_ORIGINAL = 0;
	/**
	 * 立即预定
	 */
	public static final int SALETYPE_PRE = 1;
	/**
	 * 立即抢购
	 */
	public static final int SALETYPE_LIMIT = 2;
	/**
	 * 买立减
	 */
	public static final int SALETYPE_DISCOUNT = 3;
	/**
	 * 售完补货中
	 */
	public static final int SALETYPE_SELLOUT = 4;
	/**
	 * 即将开卖
	 */
	public static final int SALETYPE_READY = 5;
	/**
	 * 预购结束
	 */
	public static final int SALETYPE_CLOSE = 6;
	/**
	 * 抢购一空
	 */
	public static final int SALETYPE_ZERO = 7;
	/**
	 * 已下架
	 */
	public static final int SALETYPE_OFF = 8;
	/**
	 * 加入购物车（置灰）
	 */
	public static final int SALETYPE_ORIGINAL_GRAY = 8;
	/**
	 * 立即预约
	 */
	public static final int SALETYPE_RESERVATION = 9;
	public static final int SALETYPE_SUB_START = 9;
	/**
	 * 预约结束
	 */
	public static final int SALETYPE_RESERVATION_OVER = 10;
	public static final int SALETYPE_SUB_END = 10;
	/**预约结束(暂无售价 灰)*/
	public static final int SALETYPE_RESERVATION_OVER_NULL = 11;
    /**
     * 立即抢购(预约商品)
     */
	public static final int SALETYPE_PRE_LIMIT = 11;
	/**
	 * 手机套餐
	 */
	public static final int SALETYPE_PHONE = 12;

	/**
	 * 查看详情
	 */
	public static final int SALETYPE_SHOW_DETAIL = 13;
	
	// *****************
	// ** 活动时间状态 **
	// *****************
	/**
	 * 未到时间
	 */
	public static final int DT_BEFORE = 1;
	/**
	 * 正在进行
	 */
	public static final int DT_ON = 2;
	/**
	 * 时间已过
	 */
	public static final int DT_AFTER = 3;

	// *************
	// ** 商品类型 **
	// *************
	/**
	 * 商品类型为一般商品
	 */
	public static final int COMMODITY_TYPE_COMMON = 1;
	/**
	 * 商品类型为加购品
	 */
	public static final int COMMODITY_TYPE_PURCHASE = 2;
	/**
	 * 商品类型为赠品
	 */
	public static final int COMMODITY_TYPE_GIFT = 3;
	/**
	 * 商品类型为配件
	 */
	public static final int COMMODITY_TYPE_ACC = 4;
	
	//start-- FeiniuFavorite/rest/v1/favorite/IdList接口入参type.  type=0 返回自营商品卖场ID列表  type=1 返回店铺ID和商城商品ID列表  type=2返回店铺ID列表
	public static final int FAVORITE_TYPE_IDS_FEINIU = 0;
	public static final int FAVORITE_TYPE_IDS_MALL = 1;
	public static final int FAVORITE_TYPE_IDS_SHOP = 2;
	//end-- FeiniuFavorite/rest/v1/favorite/IdList接口入参type. 
	// *************
	// ** 页面类型 **
	// *************
	/**
	 * 曾经购买页
	 */
	public static final String PAGE_REBUY = "rebuylist";
	/**
	 * 秒杀列表页
	 */
	public static final String PAGE_SHAKE = "GetShakeItems";
	/**
	 * 收藏列表页
	 */
	public static final String PAGE_FAVORITE = "favoriteList";
	/**
	 * 频道页
	 */
	public static final String PAGE_CHANNEL = "GetMerchandiseChannel";
	
	public static final String EXPIRE_TIME = "2015-01-01 00:00:00";// 登录过期时间
	public static final String MODIFY_PWD_TIME = "2015-02-01 00:00:00";// 修改登录密码设置的过期时间

}
