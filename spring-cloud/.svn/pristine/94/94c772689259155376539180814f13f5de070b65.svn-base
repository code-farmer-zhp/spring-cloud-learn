package com.feiniu.member.service.favorite;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 全部优惠券分2种运营方式：自营、商城
 * 商城又分成：平台券、商家券（包括普通商家券、代运营商家券，不存在商城券概念）
 * 
 * 严格意义上来说  环球购商家券也算商家券，但是为了业务区分，所以有了22这个独立的类型
 * @author huan.liu
 *
 */
public class CouponUtils {
	
	/**红色卡券色值（自营券）：飞牛券、购物金、抵用券、免邮券、以旧换新券、礼品券、购物卡*/
	private static final String COUPON_COLOR_RED = "#FF3354";
	/**黄色卡券色值（商城券）：商家店铺券、商家跨店铺券、代运营商家券、平台券*/
	private static final String COUPON_COLOR_YELLOW = "#FEB039";
	/**紫色卡券色值（环球购券）：店铺环球购券、跨店铺环球购券*/
	private static final String COUPON_COLOR_PURPLE = "#9748E0";
	/**领券中心看一看背景色-红色*/
	private static final String COUPON_COLOR_RED_LOOK_BG = "#e43434";
	/**领券中心看一看背景色-黄色*/
	private static final String COUPON_COLOR_YELLOW_LOOK_BG = "#ff791f";
	/**领券中心看一看背景色-紫色*/
	private static final String COUPON_COLOR_PURPLE_LOOK_BG = "#7b1fd0";
	
	/**飞牛购物卡*/
	public static final String COUPON_TYPE_SHOPPING_CARD = "0";
	
	/**自营飞牛券*/
	public static final String COUPON_TYPE_FEINIU = "10";
	/**自营免邮券*/
	public static final String COUPON_TYPE_SELFFREIGHTFREE = "11";
	/**自营购物金*/
	public static final String COUPON_TYPE_CASH = "12";
	/**自营品牌券*/
	public static final String COUPON_TYPE_BRAND = "13";
	/**自营抵用券*/
	public static final String COUPON_TYPE_REPAY = "14";
	/**自营礼品券*/
	public static final String COUPON_TYPE_GIFT = "15";
	/**自营以旧换新券*/
	public static final String COUPON_TYPE_EXNEW = "16";
	
	
	/**商家店铺券*/
	public static final String COUPON_TYPE_JAMALL = "20";
	/**平台优惠券*/
	public static final String COUPON_TYPE_JAMALLMAIN = "21";
	/**跨店铺券*/
	public static final String COUPON_TYPE_ACROSSMERCHANT = "22";
	/**商城免邮券--此券未上线，暂时不考虑*/
	public static final String COUPON_TYPE_JAMALLFREE = "23";
	
	//----------虚拟的API券类型，只适用API-------------
	/**商家券-代运营商家券*/
	public static final String COUPON_TYPE_JAMALL_PROXY = "27";
	/**商家券-店铺券-环球购*/
	public static final String COUPON_TYPE_JAMALL_GLOBAL = "28";
	/**商家券-跨店铺券-环球购*/
	public static final String COUPON_TYPE_ACROSSMERCHANT_GLOBAL = "29";

	//--------------武汉-张路路那边提供的规则----------------
	/**普通商家*/
	public static final String MERCHANT_TYPE_NORMAL = "0";
	/**跨境商家*/
	public static final String MERCHANT_TYPE_ACROSS = "1";
	/**转单商家*/
	public static final String MERCHANT_TYPE_TRANSFE = "3";
	/**代运营商家*/
	public static final String MERCHANT_TYPE_PROXY = "4";
	/**
	 * 领券提示语   
	 * 
	 * 目前只同步所有自营券和商城券的领取提示语
	 * 
	 * @param couponType
	 *            0自营券 1商城券
	 * @param errorCode
	 *            领券错误码
	 * @param source来源 
	 * @return
	 */
	public static Object getCounponPrompt(int couponType, String errorCode) {
		String prompt = "";
		if (couponType == 0) {// 自营优惠券
			switch (errorCode) {
			//注释的提示语：为原武汉接口返回码对应的错误提示
			case "0":
				prompt = "领取成功";//券领取成功！
				break;
			case "1":
				prompt = "未到领取时间，稍后再来哦";//还未到活动领取开始时间,领取失败！
				break;
			case "2":
				prompt = "已过领取时间，下次趁早哦";//活动已过领取有效期,领取失败！
				break;
			case "3":// 活动已停止,领取失败！
			case "4":
				prompt = "您已经领过了哦";//您已达到该券领取上限，赶紧去使用吧！
				break;
			case "5":
				prompt = "今天抢光了，明天趁早哦";//当日系统所发券已经领完,改日再领！
				break;
			case "6":
				prompt = "抢光了，下次趁早哦";//该券已经领完！
				break;
			case "7":
				prompt = "绑定手机才能领取哦";//用户未绑定手机！
				break;
			case "9"://新用户不能领取！
				prompt = "本券适用老用户，试试别的优惠吧";
				break;
			case "10"://老用户不能领取！
				prompt = "本券适用新用户，试试别的优惠吧";
				break;
			case "11":
				prompt = "优惠券序列号不存在！";//优惠券序列号不存在！
				break;
			case "12":
				prompt = "已过领取时间，下次趁早哦";//活动已过期,领取失败！
				break;
			case "13":
				prompt = "用户ID不存在";//用户ID不存在！
				break;
			case "14":
				prompt = "优惠券领取不支持行销发券";//优惠券领取不支持行销发券
				break;
			default:
				prompt = "系统异常，请稍后再试";
				break;
			}
		} else if (couponType == 1) {// 商城券
			switch (errorCode) {
			case "0":
				prompt = "领取成功！";//券领取成功！
				break;
			case "1":
				prompt = "未到领取时间，稍后再来哦";//活动未开始,领取失败！
				break;
			case "2":
				prompt = "已过领取时间，下次趁早哦";// 活动已过领取有效期,领取失败！
				break;
			case "3":
				prompt = "已过领取时间，下次趁早哦";//活动已停止,领取失败！
				break;
			case "4":
				prompt = "您已经领过了哦";//用户已达到领取限额,领取失败！
				break;
			case "5":
				prompt = "今天抢光了，明天趁早哦";// 当日系统所发券已经领完,改日再领！
				break;
			case "6":
				prompt = "抢光了，下次趁早哦";//该券已经领完！
				break;
			case "7":
				prompt = "绑定手机才能领取哦";// 用户未绑定手机！
				break;
			case "8":
				prompt = "绑定失败";// 绑定失败！
				break;
			case "9"://新用户不能领取！
				prompt = "本券适用老用户，试试别的优惠吧";
				break;
			case "10"://老用户不能领取！
				prompt = "本券适用新用户，试试别的优惠吧";
				break;
			case "11":
				prompt = "优惠券序列号不存在！";//优惠券序列号不存在！
				break;
			case "12":
				prompt = "用户ID不存在";//用户ID不存在！
				break;
			default:
				prompt = "系统异常，请稍后再试";
				break;
			}
		}
		return prompt;
	}

	/**
	 * 507以前的全颜色处理
	 * 
	 * 券颜色 
	 * 
	 * @param couponType
	 * 
	 * 商祥要用couponType 标识券类型  对应商祥createPlatform字段,isMall = true标识商城券
	 * 
	 * @param source
	 *            1:领券中心  2: 购物车领券  3:商详   4:我的账户优惠券   5:摇一摇    6:店铺收藏领券    7:结算页
	 *            
	 * @param isMall
	 *            是否是商城券
	 *            
	 * @return colorValue 1自营 2商城 3以旧换新  4礼品券
	 */
	public static int getCouponColor(String couponType, int source) {
		int colorValue = 0;
		if (source == 1) {// source == 1领券中心   10、自营飞牛券11、自营免邮券13、自营品牌券20、商家店铺券21、平台优惠券 22、跨店铺券
			switch (couponType) {
			case "10":
			case "11":
			case "13":
				colorValue = 1;
				break;
			case "20":
			case "21":
				colorValue = 2;
				break;
			default:
				break;
			}
		} else if (source == 2) {// source == 2购物车 0-商家券 1-品牌券 2-飞牛券 3-平台券 4-自营免邮券
			switch (couponType) {
			case "1":
			case "2":
			case "4":
				colorValue = 1;
				break;
			case "0":
			case "3":
				colorValue = 2;
				break;
			default:
				break;
			}
		} else if (source == 4) {// source == 4我的账户-优惠券
			switch (couponType) {
			case "10"://10、自营飞牛券
			case "11"://11、自营免邮券
			case "12"://12、自营购物金
			case "13"://13、自营品牌券
			case "14"://14、自营抵用券
				colorValue = 1;
				break;
			case "15"://15、自营礼品券；
				colorValue = 4;
				break;
			case "16":
				colorValue = 3;//16、自营以旧换新券；
				break;
			case "20":
			case "21":
			case "22":
			case "23":
				colorValue = 2;
				break;
			default:
				break;
			}
		} else if (source == 5) {// 5摇一摇
		} else if (source == 6) {// 6店铺收藏-领券
		} else if (source == 7) {// 7 结算页
			switch (couponType) {
			case "feiniuVoucher":// 飞牛券
			case "cashVoucher":// 购物金
			case "repayVoucher":// 抵用券
			case "brandVoucher":// 品牌券
			case "selfFreightFreeVoucher"://自营免邮券
				colorValue = 1;
				break;
			case "jaMallVoucher":// 商家券
			case "jaMallMainVoucher":// 平台券
			case "jaMallFreeVoucher":// 商城免邮券
			case "acrossMerchantVoucher"://跨店铺商家券
				colorValue = 2;
				break;
			case "exnewVoucher":// 以旧换新券
				colorValue = 3;
				break;
			default:
				break;
			}
		}

		return colorValue;
	}
	
	/**
	 * 507及其以后的券颜色处理
	 * 
	 * 获取不同券类型的色值
	 * 包括购物卡，购物卡类型为0
	 * @param couponType 券类型
	 * @param source 券来源
	 * @return String
	 * 2016年9月14日
	 * @author huan.liu
	 * @since
	 */
	public static String getCouponColorVal(String couponType) {
		String colorValue = "";
		
		switch (couponType) {
			case COUPON_TYPE_SHOPPING_CARD://0、飞牛购物卡
			case COUPON_TYPE_FEINIU://10、自营飞牛券
			case COUPON_TYPE_SELFFREIGHTFREE://11、自营免邮券
			case COUPON_TYPE_CASH://12、自营购物金
			case COUPON_TYPE_BRAND://13、自营品牌券
			case COUPON_TYPE_REPAY://14、自营抵用券
			case COUPON_TYPE_GIFT://15、自营礼品券；
			case COUPON_TYPE_EXNEW://16、自营以旧换新券；
				colorValue = COUPON_COLOR_RED;
				break;
			case COUPON_TYPE_JAMALL:
			case COUPON_TYPE_JAMALLMAIN:
			case COUPON_TYPE_ACROSSMERCHANT:
			case COUPON_TYPE_JAMALLFREE:
			case COUPON_TYPE_JAMALL_PROXY:
				colorValue = COUPON_COLOR_YELLOW;
				break;
			case COUPON_TYPE_JAMALL_GLOBAL:
			case COUPON_TYPE_ACROSSMERCHANT_GLOBAL:
				colorValue = COUPON_COLOR_PURPLE;
				break;
			default:
				break;
		}

		return colorValue;
	}
	
	/**
	 * 获取领券中心看一看的展示颜色
	 * @param couponType
	 * @param source
	 * @return String
	 * 2016年9月20日
	 * @author huan.liu
	 * @since
	 */
	public static String getCouponColorLookVal(String couponType) {
		String colorValue = "";
		switch (couponType) {
			case COUPON_TYPE_FEINIU://10、自营飞牛券
			case COUPON_TYPE_SELFFREIGHTFREE://11、自营免邮券
			case COUPON_TYPE_CASH://12、自营购物金
			case COUPON_TYPE_BRAND://13、自营品牌券
			case COUPON_TYPE_REPAY://14、自营抵用券
			case COUPON_TYPE_GIFT://15、自营礼品券；
			case COUPON_TYPE_EXNEW://16、自营以旧换新券；
				colorValue = COUPON_COLOR_RED_LOOK_BG;
				break;
			case COUPON_TYPE_JAMALL:
			case COUPON_TYPE_JAMALLMAIN:
			case COUPON_TYPE_ACROSSMERCHANT:
			case COUPON_TYPE_JAMALLFREE:
			case COUPON_TYPE_JAMALL_PROXY:
				colorValue = COUPON_COLOR_YELLOW_LOOK_BG;
				break;
			case COUPON_TYPE_JAMALL_GLOBAL:
			case COUPON_TYPE_ACROSSMERCHANT_GLOBAL:
				colorValue = COUPON_COLOR_PURPLE_LOOK_BG;
				break;
			default:
				break;
		}

		return colorValue;
	}
	
	public static JSONObject getCouponTypes() {
		JSONObject type_list = new JSONObject();
		type_list.put("feiniuVoucher", 1); // 飞牛券
		type_list.put("cashVoucher", 2);// 购物金
		type_list.put("repayVoucher", 3);// 抵用券
		type_list.put("jaMallVoucher", 4);// 商家券
		type_list.put("agentMerchantVoucher", 4);// 代理商家券
		type_list.put("overseasMerchantVoucher", 4);// 环球购券
		type_list.put("jaMallMainVoucher", 5);// 商城平台券
		type_list.put("selfFreightFreeVoucher", 6);// 自营免邮券
		type_list.put("brandVoucher", 7);// 品牌券
		type_list.put("jaMallFreeVoucher", 8);// 商城免邮券
		type_list.put("exnewVoucher", 9);// 以旧换新
		type_list.put("acrossMerchantVoucher", 10);// 跨店铺商家券
		return type_list;
	}
	
	/**
	 * 根据券类型和适用商品描述处理得到展示的卡券名称
	 * @param couponType
	 * @return String
	 * 2016年9月14日
	 * @author huan.liu
	 * @since
	 */
	public static String getViewCouponName(String couponType) {
		String retType = "";
		switch (couponType) {
		case COUPON_TYPE_FEINIU://自营飞牛券
		case COUPON_TYPE_CASH://自营购物金
		case COUPON_TYPE_BRAND://自营品牌券
			retType = "自营／商家直送";
			break;
		case COUPON_TYPE_SELFFREIGHTFREE://自营免邮券
			retType = "免邮券";
			break;
		case COUPON_TYPE_REPAY://自营抵用券
			retType = "抵用券";
			break;
		case COUPON_TYPE_GIFT://自营礼品券
			retType = "礼品券";
			break;
		case COUPON_TYPE_EXNEW://自营以旧换新券
			retType = "以旧换新券";
			break;
		case COUPON_TYPE_JAMALL://商家店铺券
			retType = "商城";
			break;
		case COUPON_TYPE_JAMALLMAIN://平台优惠券
		case COUPON_TYPE_ACROSSMERCHANT://跨店铺券
			retType = "商城／商家直送";
			break;
		case COUPON_TYPE_JAMALLFREE://商城免邮券---此券未上线，暂时不考虑
			retType = "商城免邮券";
			break;
		case COUPON_TYPE_JAMALL_PROXY://商家券-代运营商家券
			retType = "商家直送";
			break;
		case COUPON_TYPE_JAMALL_GLOBAL://商家券-店铺券-环球购
		case COUPON_TYPE_ACROSSMERCHANT_GLOBAL://商家券-跨店铺券-环球购
			retType = "环球购";
			break;
		default:
			break;
		}
		return retType;
	}
	
	/**
	 * 获取使用门槛信息
	 * @param couponType
	 * @param threshold
	 * @return String
	 * 2016年9月14日
	 * @author huan.liu
	 * @since
	 */
	public static String getThreshold(String couponType, String man, int version) {
		String thresholdDesc = "";
		switch (couponType) {
		case COUPON_TYPE_BRAND:
		case COUPON_TYPE_FEINIU:
		case COUPON_TYPE_ACROSSMERCHANT:
		case COUPON_TYPE_JAMALL:
		case COUPON_TYPE_JAMALL_PROXY:
		case COUPON_TYPE_JAMALL_GLOBAL:
		case COUPON_TYPE_ACROSSMERCHANT_GLOBAL:
		case COUPON_TYPE_JAMALLMAIN:
			thresholdDesc = StringUtils.isNotBlank(man) && Double.valueOf(man) > 0d ? "满" + parsePriceInt(man, version) + "可用" : "无门槛";
			break;
		case COUPON_TYPE_CASH:
		case COUPON_TYPE_EXNEW:
			thresholdDesc = "无门槛";
			break;
		case COUPON_TYPE_REPAY:
			thresholdDesc = "抵扣20%";
			break;
		case COUPON_TYPE_SELFFREIGHTFREE:
		case COUPON_TYPE_JAMALLFREE:
			thresholdDesc = StringUtils.isNotBlank(man) && Double.valueOf(man) > 0d ? "满" + parsePriceInt(man, version) + "免邮" : "无门槛";
			break;
			
		default:
			break;
		}
		
		return thresholdDesc;
	}
	
	
	/**
	 * 获取优惠券使用条件说明
	 * @param couponType
	 * @return String
	 * 2016年9月18日
	 * @author huan.liu
	 * @since
	 */
	public static String getCondition(String couponType) {
		if (COUPON_TYPE_GIFT.equals(couponType)) {
			//针对礼品券不提供使用条件信息
			return "";
		}
		String condition = "";
		if (COUPON_TYPE_REPAY.equals(couponType)) {
			condition = "使用条件：抵扣订单金额（扣除运费）20%，只能使用一次，不找零";//抵用券使用条件
		} else if (COUPON_TYPE_CASH.equals(couponType)) {
			condition = "使用条件：不抵扣运费，只能使用一次，不找零";//购物金使用条件
		} else {
			condition = "";
		}
		return condition;
	}
	
	/**
	 * 针对结算页的优惠券类型，转换成规范的优惠券类型进行处理
	 * @param saCouponType
	 * @return String
	 * 2016年9月18日
	 * @author huan.liu
	 * @since
	 */
	public static String transformSettleAccountsCouponType(String saCouponType) {
		switch (saCouponType) {
		case "brandVoucher":
			return COUPON_TYPE_BRAND;
		case "feiniuVoucher":
			return COUPON_TYPE_FEINIU;
		case "cashVoucher":
			return COUPON_TYPE_CASH;
		case "repayVoucher":
			return COUPON_TYPE_REPAY;
		case "selfFreightFreeVoucher":
			return COUPON_TYPE_SELFFREIGHTFREE;
		case "acrossMerchantVoucher":
			return COUPON_TYPE_ACROSSMERCHANT;
		case "overseasMerchantVoucher":
			return COUPON_TYPE_ACROSSMERCHANT_GLOBAL;
		case "jaMallVoucher":
			return COUPON_TYPE_JAMALL;
		case "jaMallMainVoucher":
			return COUPON_TYPE_JAMALLMAIN;
		case "exnewVoucher":
			return COUPON_TYPE_EXNEW;
		case "jaMallFreeVoucher":
			return COUPON_TYPE_JAMALLFREE;
		case "agentMerchantVoucher":
			return COUPON_TYPE_JAMALL_PROXY;

		default:
			return "";
		}
	}
	
	/**
	 * 针对购物车卡券类型的转换
	 * @param cartCouponType
	 * @return String
	 * 2016年9月18日
	 * @author huan.liu
	 * @since
	 */
	public static String transformCartCouponType(String cartCouponType, boolean isGlobal) {
		switch (cartCouponType) {
		case "0":
			if (isGlobal)
				return COUPON_TYPE_JAMALL_GLOBAL;
			return COUPON_TYPE_JAMALL;
		case "1":
			return COUPON_TYPE_BRAND;
		case "2":
			return COUPON_TYPE_FEINIU;
		case "3":
			return COUPON_TYPE_JAMALLMAIN;
		case "4":
			return COUPON_TYPE_SELFFREIGHTFREE;
		case "5":
			return COUPON_TYPE_JAMALL_PROXY;

		default:
			return "";
		}
	}
	
	/**
	 * 针对商详页券类型转换
	 * @param goodsDetailCouponType
	 * @return String
	 * 2016年9月18日
	 * @author huan.liu
	 * @since
	 */
	public static String transformGoodsDetailCouponType(String goodsDetailCouponType, String createPlatForm, boolean isGlobal, boolean isProxy) {
		//商家券
		if ("0".endsWith(createPlatForm)) {
			if (isGlobal)
				return COUPON_TYPE_JAMALL_GLOBAL;
			if (isProxy)
				return COUPON_TYPE_JAMALL_PROXY;
			return COUPON_TYPE_JAMALL;
		}
		//平台券
		if ("1".endsWith(createPlatForm)) {
			return COUPON_TYPE_JAMALLMAIN;
		}
		//自营券
		if("2".equals(createPlatForm)){
			switch (goodsDetailCouponType) {
			case "0":
				return COUPON_TYPE_FEINIU;
			case "1":
				return COUPON_TYPE_SELFFREIGHTFREE;
			case "2":
				return COUPON_TYPE_CASH;
			case "3":
				return COUPON_TYPE_BRAND;
			case "4":
				return COUPON_TYPE_REPAY;
			case "5":
				return COUPON_TYPE_GIFT;
			case "6":
				return COUPON_TYPE_EXNEW;
	
			default:
				return "";
			}
		}
		
		return "";
	}
	
	/**
	 * 转化卡券类型，处理全球购和代理券的分类转换
	 */
	public static String transformCenterCouponType(String centerCouponType, boolean isGlobal, boolean isProxy) {
		switch (centerCouponType) {
		case COUPON_TYPE_JAMALL:
			if (isGlobal)
				return COUPON_TYPE_JAMALL_GLOBAL;
			if (isProxy)
				return COUPON_TYPE_JAMALL_PROXY;
			
		case COUPON_TYPE_ACROSSMERCHANT:
			if (isGlobal)
				return COUPON_TYPE_ACROSSMERCHANT_GLOBAL;
			
		default:
			break;
		}
		return centerCouponType;
	}
	
	/**
	 * 商详页自营券类型转化
	 * @param couponType
	 * @return String
	 * 2016年9月22日
	 * @author huan.liu
	 * @since
	 */
	public static String transformGoodsDetailZYCouponType(int couponType) {
		if(couponType == 4){
			return COUPON_TYPE_BRAND;
		}else if(couponType == 1){
			return COUPON_TYPE_FEINIU;
		}else if(couponType == 2){
			return COUPON_TYPE_SELFFREIGHTFREE;
		}else if(couponType == 3){
			return COUPON_TYPE_CASH;
		}
		return "";
	}
	
	// 新接口vaPostalType对应到老接口vaPostalType(不知道对不对，就将就这样了)
	public static int exNewTypeToOldType(String vaPostalType){
		int oldType = -1;
		switch (vaPostalType) {
		case COUPON_TYPE_FEINIU:
			oldType = 1;
			break;
		case COUPON_TYPE_SELFFREIGHTFREE:
			oldType = 2;
			break;
		case COUPON_TYPE_CASH:
			oldType = 3;
			break;
		case COUPON_TYPE_BRAND:
			oldType = 4;
			break;
		case COUPON_TYPE_REPAY:
			oldType = 5;
			break;
		case COUPON_TYPE_EXNEW:
			oldType = 6;
			break;
		default:
			break;
		}
		return oldType;
	}
	
	/**
	 * 针对我的优惠券进行排序处理
	 * 只是服务于商家直送的情况，（内部接口要求分开两次查询，导致内部接口排序失效，坑爹啊，内部接口）
	 * @param couponList void
	 * @param status 1:未使用 2：已使用 3：已失效
	 * 2016年11月15日
	 * @author huan.liu
	 * @since
	 */
	public static void sortMyCoupons(JSONArray couponList, String status) {
		switch (status) {
		//未使用，以旧换新券＞“新到”券＞“快过期”券＞其他券的顺序排序，以旧换新券（无论状态是“新到”或“快过期”）排序置顶（多张时按照领取时间由近到远排序）
		case "1":
			//以旧换新券
			List<JSONObject> exnewList = new ArrayList<>();
			//新到券
			List<JSONObject> newList = new ArrayList<>();
			//快过期券
			List<JSONObject> expiredList = new ArrayList<>();
			//其他券
			List<JSONObject> othersList = new ArrayList<>();
			JSONObject couponInfo;
			for (Object obj : couponList) {
				couponInfo = (JSONObject) obj;
				if (COUPON_TYPE_EXNEW.equals(couponInfo.getString("vaPostalType"))) {
					//依旧换新券
					exnewList.add(couponInfo);
					continue;
				}
				if (1 == couponInfo.getIntValue("isNewCoupon")) {
					//新到券
					newList.add(couponInfo);
					continue;
				}
				if (1 == couponInfo.getIntValue("isSoonExpired")) {
					//快过期券
					expiredList.add(couponInfo);
					continue;
				}
				//其他券
				othersList.add(couponInfo);
			}
			//按照领取时间排序的排序比较
			Comparator<Object> receiveComp = new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					if (null != o1 && null != o2 && o1 instanceof JSONObject && o2 instanceof JSONObject) {
						JSONObject obj1 = (JSONObject) o1;
						JSONObject obj2 = (JSONObject) o2;
						if (obj2.getLongValue("receiveTime") == obj1.getLongValue("receiveTime")) {
							return 0;
						} else if (obj2.getLongValue("receiveTime") > obj1.getLongValue("receiveTime")) {
							return 1;
						} else {
							return -1;
						}
					}
					return 0;
				}
			};
			Collections.sort(exnewList, receiveComp);
			Collections.sort(newList, receiveComp);
			Collections.sort(expiredList, receiveComp);
			Collections.sort(othersList, receiveComp);
			couponList.clear();
			couponList.addAll(exnewList);
			couponList.addAll(newList);
			couponList.addAll(expiredList);
			couponList.addAll(othersList);
			break;
		//按照使用时间由近到远排序
		case "2":
			//按照使用时间排序的排序比较
			Comparator<Object> userTimeComp = new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					if (null != o1 && null != o2 && o1 instanceof JSONObject && o2 instanceof JSONObject) {
						JSONObject obj1 = (JSONObject) o1;
						JSONObject obj2 = (JSONObject) o2;
						if (obj2.getLongValue("userTime") == obj1.getLongValue("userTime")) {
							return 0;
						} else if (obj2.getLongValue("userTime") > obj1.getLongValue("userTime")) {
							return 1;
						} else {
							return -1;
						}
					}
					return 0;
				}
			};
			Collections.sort(couponList, userTimeComp);
			break;
		//按照失效时间由近到远排序
		case "3":
			//按照失效时间排序的排序比较
			Comparator<Object> endTimeComp = new Comparator<Object>() {
				@Override
				public int compare(Object o1, Object o2) {
					if (null != o1 && null != o2 && o1 instanceof JSONObject && o2 instanceof JSONObject) {
						JSONObject obj1 = (JSONObject) o1;
						JSONObject obj2 = (JSONObject) o2;
						if (obj2.getLongValue("vcsEndDate") == obj1.getLongValue("vcsEndDate")) {
							return 0;
						} else if (obj2.getLongValue("vcsEndDate") > obj1.getLongValue("vcsEndDate")) {
							return 1;
						} else {
							return -1;
						}
					}
					return 0;
				}
			};
			Collections.sort(couponList, endTimeComp);
			break;

		default:
			break;
		}
	}
	
	/**
	 * 针对优惠券充值和验证的返回码进行判断处理
	 * 	ONE("0001","1","memGuid不能为空！"),
	TWO("0001","2","充值码不能为空！"),
	THREE("0003","3","充值类型不能为空！"),
	FOUR("0004","4","充值类型错误！"),
	FIVE("0005","5","您输入的卡号无效，请重新确认！"),
	SIX("0006","6","您输入的卡号还未到兑换期！"),
	SEVEN("0007","7","您输入的卡号已过兑换期限！"),
	EIGHT("0008","8","memGuid不存在！"),
	TEN("0010","10","您输入的卡号剩余数量不足！"),
	ELEVEN("0011","11","您的用户类型不允许使用该卡号！"),
	TWELVE("0012","12","您输入的卡号活动尚未生效！"),
	THIRTEEN("0013","13","您已兑换过该卡号！"),
	FOURTEEN("0014","14","您的兑换次数已超过该活动的次数上限！"),
	SEVENTEEN("0017","17","活动编号不能为空！"),
	EIGHTEEN("0018","18","活动编号不存在！"),
	TWENTY("0020","20","已充值卡券张数达到活动上限！"),
	TWENTY_ONE("0021","21","您输入的充值码不存在或已被充值，请重新确认！"),
	TWENTY_TWO("0022","22","您输入的充值码已被充值，请重新确认！"),
	TWENTY_THREE("0023","23","没有找到合法的数据，请重新确认！"),
	TWENTY_FOUR("0024","24","您的用户需要绑定手机号才能充值！"),
	TWENTY_SIX("0026","26","您输入的充值码已充值且活动已绑定充值用户！"),
	TWENTY_EIGHT("0028","28","该充值码仅供线上使用！"),
	TWENTY_NINE("0029","29","您输入的充值码已过期或作废！"),
	THIRTY("0030","30","操作太频繁！"),
	THIRTY_TWO("0032","32","该活动今天卡券领取张数已达上限"),
	THIRTY_THREE("0033","33","卖场编号不能为空！"),
	THIRTY_FOUR("0034","34","卡券类型不能为空！"),
	THIRTY_FIVE("0035","35","必选参数为空！"),
	THIRTY_SIX("0036","36","接口内部异常"),
	THIRTY_SEVEN("0037","37","新用户不能领取！"),
	THIRTY_EIGHT("0038","38","老用户不能领取！"),
	THIRTY_NINE("0001","5","页数需为数值且大于0！"),
	FORTY("0001","6","每页记录条数需为数值且大于0！"),
	FORTY_ONE("0041","41","排序策略需为数值且大于0！"),
	FORTY_TWO("0001","30","您输入的卡号为仓储券，不能充值！"),
	FORTY_THREE("0043","43","卖场编号不能为空！"),
	FORTY_FOUR("0044","44","卡卷类型错误，只能查询优惠卷、购物金、品牌卷！"),
	FORTY_FIVE("0045","45","卡券流水号"),
	FORTY_SIX("0046","46","根据活动编号获取充值码失败!"),
	NINE("0001","9","内部员工不能充值抵用券!"),
	FORTY_EIGHT("0048","2","卡券流水号不能为空!"),
	FORTY_NINE("0049","49","memGuid与bnmSeq不匹配!"),
	FIFTY("0001","px000004","查询用户信息失败!"),
	FIFTY_ONE("0001","35","获取合伙人信息失败!"),
	FIFTY_TWO("0001","36","该券限指定会员充值!"),
	FIFTY_THREE("0053","53","该活动无效！"),
	FIFTY_FOUR("0001","33","当前对应卡号类型不对！"),
	FIFTY_FIVE("0001","5","您输入的卡号或充值码无效，请重新确认！"),
	FIFTY_SIX("0001","6","您输入的卡号还未到兑换期！"),
	FIFTY_SEVEN("0001","7","您输入的卡号已过兑换期限！"),
	FIFTY_EIGHT("0001","12","您输入的卡号活动尚未生效！"),
	FIFTY_NINE("0001","8","memGuid不存在！"),
	SIXTY("0001","11","您的用户类型不允许使用该卡号！"),
	SIXTY_ONE("0001","13","您已兑换过该卡号！"),
	SIXTY_TWO("0001","14","您的兑换次数已超过该活动的次数上限！"),
	SEVENTY_ONE("0001","1","memGuid不能为空！"),
	SEVENTY_TWO("0001","2","充值码不能为空！"),
	SEVENTY_THREE("0001","3","充值类型不能为空！"),
	SEVENTY_FOUR("0001","4","充值类型错误！"),
	SEVENTY_FIVE("0001","3","memGuid与bnmSeq不匹配！"),
	//参数错误
	NINE_NINE_NINE("0001","-6","THE PARAMETER ERROR"),
	TWO_HUNDRED("0200","0","成功返回参数！"),
	//可疑会员
	SUSPICIOUS_GUID_RESULT("0001","102","尊敬的客户您好，您的账户存在异常，无法充值领券，如有疑问请致电客服！"),
	SEVENTY_SIX("0001","5","您的充值码已被使用！"),
	SEVENTY_SEVEN("0001","2","status不能为空！"),
	SEVENTY_EIGHT("0078","78","该礼品已经被抢完啦，正在紧急补货，请稍后再试"),
	SEVENTY_NINE("0079","79","该礼品已经被抢完啦，正在紧急补货，请稍后再试"),
	EIGHTY("0080","80","您输入的卡号已停止领取"),
	EIGHTY_ONE("0081","81","该券仅限指定用户可充值"),
	EIGHTY_TWO("0082","82","抵用券充值只支持行销发券类型");
	
	
	你已充值该活动抵用券 code=14. sn=0001
	购物车已满 code=22 sn =0022
	
	EIGHTY_FIVE("0085","85","本券适用老用户，试试别的优惠吧"),
EIGHTY_SIX("0086","86","本券适用新用户，试试别的优惠吧")
	 * @param code
	 * @return String
	 * 2016年11月15日
	 * @author huan.liu
	 * @since
	 */
	public static String getReturnMessageForCoupon(String sn, String code) {
		if (null == code || null == sn) {
			return null;
		}
		switch (sn+"_"+code) {
		/*默认返回null的情况就是充值码错误，此处不明确返回，方便调用方针对null的情况去调用购物卡接口
		 * case "0005_5":
		case "0021_21":
		case "0023_23":
			return "您输入的充值码有误，请重新输入";*/
			
		case "0001_5":
		case "0013_13":
		case "0001_13":
		case "0022_22":
			return "您的充值码已被使用";
			
		case "0011_11"://针对卡券充值验证接口出现：guid为空（未注册），但是优惠券是“限老会员充值”的情况
		case "0085_85":
			return "限老会员充值";
			
		case "0086_86":
			return "限新会员充值";
			
		case "0001_7":
		case "0007_7":
		case "0029_29":
			return "您的充值码已过期";
			
		case "0006_6":
		case "0001_6":
			return "该充值码时间未到";
			
		case "0001_9":
			return "员工不能充值抵用券";
			
		case "0001_14":
			return "您已充值过该活动抵用券";
			
		case "0014_14":
		case "0020_20":
		case "0032_32":
			return "您已超出了可充值的最大次数";
			
		case "0001_30":
			return "该充值码无效，如有疑问请致电4009206565";
			
		/* 卡券充值验证的时候不会出现，购物车已满的验证，这个验证只会在卡券充值的时候发生
		 * case "0022_22":
			return "购物车商品已满，请先去购物车删除商品";*/
			
		default:
			break;
		}
		
		//活动被终止的情况 按照内部接口，只需要判断code就行，sn有好几种
		if ("87".equals(code)) {
			return "该充值码已失效，详情请咨询客服400-920-6565";
		}
		
		return null;
	}
	
	/**
	 * 购物卡充值码根据给定code返回消息
-6	参数不能被解析	参数不能被解析
0	充值码有效	充值码有效且未被使用
px000004	查询会员信息失败	查询会员信息失败
23	该卡还未到兑换期	该卡还未到兑换期
24	改卡已过兑换期限	改卡已过兑换期限
35	获取合伙人信息失败");	获取合伙人信息失败");
36	您的账号类型不能使用该券");	您的账号类型不能使用该券");
51	memGuid不能为空	memGuid不能为空
52	充值码不能为空	充值码不能为空
53	充值类型不能为空	充值类型不能为空
54	充值类型错误	充值类型错误
55	充值码无效，请重新确认	充值码无效，请重新确认
58	memGuid不存在	memGuid不存在
59	员工账号不能使用该券	员工账号不能使用该券
60	该卡余量不足	该卡余量不足
61	您的账号类型不能使用该券	您的账号类型不能使用该券
62	该卡活动尚未生效	该卡活动尚未生效
63	该卡已被您兑换过	该卡已被您兑换过
64	您兑换该卡的张数超过上限	您兑换该卡的张数超过上限
65	该卡不能被激活	该卡不能被激活
93	该券为仓储券，不能充值	该券为仓储券，不能充值
97	该券为无效券	该券为无效券
	 * @param code
	 * @return String
	 * 2016年11月15日
	 * @author huan.liu
	 * @since
	 */
	public static String getReturnMessageForShoppingCard(String code) {
		if (null == code) {
			return null;
		}
		switch (code) {
		
		case "63":
			return "您的充值码已被使用";
			
		case "24":
			return "您的充值码已过期";
			
		case "23":
			return "该充值码时间未到";
			
		case "64":
			return "您已超出了可充值的最大次数";
			
		case "93":
			return "该充值码无效，如有疑问请致电4009206565";
			
		default:
			break;
		}
		
		return null;
	}
	
	/**
         * 格式化金额  parsePriceInt 小数点后面去0
         * 
         * @param s
         * @return String
         * @exception
         * @since 1.0.0
         */
        public static String parsePriceInt(String s, int version){
                if (StringUtils.isEmpty(s) || 0== Double.parseDouble(s)) {
                        if(version<504){
                                return "0.00";
                        }else{
                                return "0";
                        }
                }
                
                String str = parsePrice(s, 2);
                if(version<504){
                        return str;
                }
                
                if(str.indexOf(".") > 0){
                        //正则表达
                        str = str.replaceAll("0+?$", "");//去掉后面无用的零
                        str = str.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
                }
                return str;
        }
        
        /**
         * 格式化金额  parsePrice
         * 
         * @param s
         * @param len
         * @return String
         * @exception
         * @since 1.0.0
         */
        public static String parsePrice(String s, int len) {
                if (StringUtils.isEmpty(s) || 0== Double.parseDouble(s)) {
                        return "0.00";
                }
                NumberFormat formater = null;
                double num = Double.parseDouble(s);
                if (len == 0) {
                        formater = new DecimalFormat("#################.00");

                } else {
                        StringBuffer buff = new StringBuffer();
                        buff.append("################0.");
                        for (int i = 0; i < len; i++) {
                                buff.append("0");
                        }
                        formater = new DecimalFormat(buff.toString());
                }
                String result = formater.format(num);
                return result;
        }
	
}
