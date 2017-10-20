package com.feiniu.score.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstantGrowth {


	/**
	 *  CACHE KEY START
	 */
	public static String CACHE_ORDER_GROWTH_KEY = "CACHE_ORDER_GROWTH_KEY_";


	public static String DEFAULT_STRING_VALUE = "_";

	// 订单金额 小于 10 元 不能获得成长值
	public static int ORDER_MIN_LIMIT = 10;
	
	// 单个订单最多可获得成长值500
	public static int ORDER_MAX_LIMIT = 500;

	public static BigDecimal ORDER_MAX_LIMIT_BIGD = new BigDecimal(500);
	// 订单中单个商品最大获得成长值 500 (例如 某个商品 买了 3 个 就是 1500)，规则已作废
	//public static int ORDER_ITEM_MAX_LIMIT = 500;


	/**
	 * 客人未进行收货确认时，到支付后的第10天赠送成长值（包含支付当日）
	 */
	public static int DAY_AGO_RECEIVE = -10 ;


	/**
	 * GROWTH_DETAIL.GROWTH_CHANNEL
	 */
	/**
	 *  登录
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_DL = 1;  // 登录
	/**
	 * 飞牛赠送
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_FNZS = 2;  // 飞牛赠送
	/**
	 * 购物
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_GW = 3;  // 购物
	/**
	 * 评论
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_PL = 4;  // 评论
	/**
	 * 评论置顶
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_PLZD = 5;  // 评论置顶
	/**
	 * 评论精华
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_PLJH = 6;  // 评论精华
	/**
	 *  退货回收(购物)
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_TH_GW = 7;  // 退货回收(购物)
	/**
	 * 退货回收(评论)
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_TH_PL = 8;  // 退货回收(评论)

	/**
	 * 权益取消
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_FNQX = 9;  // 权益取消
	/**
	 * 调整发放
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_FNTZFF = 10;  // 调整发放
	/**
	 * 调整取消
	 */
	public static Integer DETAIL_GROWTH_CHANNEL_FNTZQX = 11;  // 调整取消

	/********************************成长值变动类型描述*******************************************/
	public final static Map<Integer,String> GET_DETAIL_CHANNEL_DESC;
	static{
		HashMap<Integer,String> maps= new HashMap<>();
		maps.put(DETAIL_GROWTH_CHANNEL_DL, "登录获得");
		maps.put(DETAIL_GROWTH_CHANNEL_FNZS, "飞牛赠送");
		maps.put(DETAIL_GROWTH_CHANNEL_GW, "购物获得");
		maps.put(DETAIL_GROWTH_CHANNEL_PL, "评论获得");
		maps.put(DETAIL_GROWTH_CHANNEL_PLZD, "评论置顶");
		maps.put(DETAIL_GROWTH_CHANNEL_PLJH, "评论精华");
		maps.put(DETAIL_GROWTH_CHANNEL_TH_GW, "退货回收(购物)");
		maps.put(DETAIL_GROWTH_CHANNEL_TH_PL, "退货回收(评论)");

		maps.put(DETAIL_GROWTH_CHANNEL_FNQX, "飞牛回收");
		maps.put(DETAIL_GROWTH_CHANNEL_FNTZFF, "飞牛赠送");
		maps.put(DETAIL_GROWTH_CHANNEL_FNTZQX, "飞牛回收");
		GET_DETAIL_CHANNEL_DESC = Collections.unmodifiableMap(maps);
	}


	/**
	 * 回收成长值的都要增加到这里面
	 */
	public static Integer[] DETAIL_GROWTH_CHANNEL_OPERATE_LOSS = {DETAIL_GROWTH_CHANNEL_TH_GW , DETAIL_GROWTH_CHANNEL_TH_PL,DETAIL_GROWTH_CHANNEL_FNQX,DETAIL_GROWTH_CHANNEL_FNTZQX};

	/**
	 * GROWTH_DETAIL.DATA_FLAG
	 */
	/**
	 * 待生效
	 */
	public static Integer DATA_FLAG_DSX = 0;
	/**
	 * 有效
	 */
	public static Integer DATA_FLAG_YX = 1;


	/**
	 * GROWTH_ORDER_INFO.PAY_STATUS
	 */
	/**
	 * 已支付
	 */
	public static Integer ORDER_PAY_STATUS_YZF = 2;  // 已支付
	/**
	 * 已签收
	 */
	public static Integer ORDER_PAY_STATUS_YQS = 3;  // 已签收



	/********************************成长值获得量常数*******************************************/
	/**
	 * 登录获得5点成长值
	 */
	public static final Integer GROWTH_GAIN_LOGIN = 5;

	/**
	 * 评论获得20点成长值
	 */
	public static final Integer GROWTH_GAIN_COMMENT_PRODUCT = 20;
	/**
	 * 评论精华20点成长值
	 */
	public static final Integer GROWTH_GAIN_COMMENT_SET_ESSENCE  = 20;
	/**
	 * 评论置顶20点成长值
	 */
	public static final Integer GROWTH_GAIN_COMMENT_SET_TOP = 20;
	/**
	 * 评论晒图3张以上获得20点成长值
	 */
	public static final Integer GROWTH_GAIN_COMMENT_WITH_PIC = 20;


	/********************************成长值评论设置精华、置顶*******************************************/
	/**
	 * 精华
	 */
	public static final Integer GROWTH_COMTYPE_COMMENT_SET_ESSENCE = 1;
	/**
	 * 置顶
	 */
	public static final Integer GROWTH_COMTYPE_COMMENT_SET_TOP = 2;
	/**
	 * 既置顶又精华
	 */
	public static final Integer GROWTH_COMTYPE_COMMENT_BOTH_SET_ESSENCE_AND_TOP = 3;


	/********************************会员等级及所需成长值设置*******************************************/
	/**
	 * T0
	 */
	public static final String LEVEL_OF_0 = "T0";
	/**
	 * T1
	 */
	public static final String LEVEL_OF_1 = "T1";
	/**
	 * T2
	 */
	public static final String LEVEL_OF_2 = "T2";
	/**
	 * T3
	 */
	public static final String LEVEL_OF_3 = "T3";
	/**
	 * TP
	 */
	public static final String LEVEL_OF_PARTNER = "TP";
	/**
	 * TU
	 */
	public static final String LEVEL_OF_TRADE_UNIONIST = "TU";

	public static final String  DESC_OF_LEVEL_0="普通会员";
	public static final String  DESC_OF_LEVEL_1="银卡会员";
	public static final String  DESC_OF_LEVEL_2="金卡会员";
	public static final String  DESC_OF_LEVEL_3="白金卡会员";
	public static final String  DESC_OF_LEVEL_PARTNER="飞牛网合伙人";
	public static final String  DESC_OF_LEVEL_UNIONIST="工会会员";


	/**
	 * T1需1点成长值
	 */
	public static final Integer NEED_GROWTH_OF_T1 = 1;
	/**
	 * T2需1000点成长值
	 */
	public static final Integer NEED_GROWTH_OF_T2 = 1000;
	/**
	 * T3需3000点成长值
	 */
	public static final Integer NEED_GROWTH_OF_T3 = 3000;

	/**
	 * T0需0点成长值
	 */
	public static final Integer NEED_GROWTH_OF_T0 = 0;
	/********************************登录方式及其描述*******************************************/
	/**
	 *  1—PC端登录
	 */
	public static final Integer LOGIN_FROM_PC = 1;
	/**
	 *  2—APP端登录
	 */
	public static final Integer LOGIN_FROM_APP = 2;
	/**
	 * 3—触屏端登录
	 */
	public static final Integer LOGIN_FROM_TOUCH = 3;


	public final static Map<Integer, String> GET_LOGIN_FROM_DESC;
	static{
		HashMap<Integer,String> maps= new HashMap<>();
		maps.put(LOGIN_FROM_PC, "PC端登录");
		maps.put(LOGIN_FROM_APP, "APP端登录");
		maps.put(LOGIN_FROM_TOUCH, "触屏端登录");
		GET_LOGIN_FROM_DESC= Collections.unmodifiableMap(maps);
	}

	public final static int[] GET_LEVEL_VALUE_NEED_LIST ={NEED_GROWTH_OF_T0,NEED_GROWTH_OF_T1,NEED_GROWTH_OF_T2,NEED_GROWTH_OF_T3};
	
	public final static String[] GET_LEVEL_DESC_LIST ={DESC_OF_LEVEL_0,DESC_OF_LEVEL_1,DESC_OF_LEVEL_2,DESC_OF_LEVEL_3};
	
	public final static String[] GET_LEVEL_LIST ={LEVEL_OF_0,LEVEL_OF_1,LEVEL_OF_2,LEVEL_OF_3};
	
	public final static Map<String, String> GET_LEVEL_DESC;
	static{
		HashMap<String,String> maps= new HashMap<>();
		maps.put(LEVEL_OF_0, DESC_OF_LEVEL_0);
		maps.put(LEVEL_OF_1, DESC_OF_LEVEL_1);
		maps.put(LEVEL_OF_2, DESC_OF_LEVEL_2);
		maps.put(LEVEL_OF_3, DESC_OF_LEVEL_3);
		maps.put(LEVEL_OF_PARTNER, DESC_OF_LEVEL_PARTNER);
		maps.put(LEVEL_OF_TRADE_UNIONIST, DESC_OF_LEVEL_UNIONIST);
		GET_LEVEL_DESC = Collections.unmodifiableMap(maps);
	}
	
	public static List<Integer> GIFTCHANNELS= Arrays.asList(DETAIL_GROWTH_CHANNEL_FNZS,DETAIL_GROWTH_CHANNEL_FNTZFF);

	public static List<Integer> NOSHOWCHANNELS= Arrays.asList(DETAIL_GROWTH_CHANNEL_FNQX,DETAIL_GROWTH_CHANNEL_FNTZQX);

	public static List<Integer> OG_SM_CHANNELS= Arrays.asList(DETAIL_GROWTH_CHANNEL_GW,DETAIL_GROWTH_CHANNEL_TH_GW);

	public static List<Integer> OG_PL_CHANNELS= Arrays.asList(DETAIL_GROWTH_CHANNEL_PL,DETAIL_GROWTH_CHANNEL_PLZD,DETAIL_GROWTH_CHANNEL_PLJH,DETAIL_GROWTH_CHANNEL_TH_PL);
	//置顶加精只获得一次成长值
	public static List<Integer> ZDJJCHANNELS= Arrays.asList(DETAIL_GROWTH_CHANNEL_PLJH,DETAIL_GROWTH_CHANNEL_PLZD);

	/********************************评论是否晒图3张及以上*******************************************/
	/**
	 * 评论并晒图3张及以上
	 */
	public static final Integer COMMENT_WITH_PIC = 1;

	/**
	 * 抵用券计算成长值时按 抵扣金额除以0.2作为使用门槛
	 */
	public static final BigDecimal BONUS_DIV_COEFF_FOR_GROWTH_CAL = BigDecimal.valueOf(0.2) ;

	public static final String IS_PARTNER_KEY  = "isPartner";

	public static final String BE_PARTNER_TIME_KEY = "becomePartnerTime";


	public static Map<String, Object> getNextLevelInfo(int myGrowthValue) {
		Map<String, Object> data=new HashMap<>();
		if (myGrowthValue < ConstantGrowth.NEED_GROWTH_OF_T1) {
			data.put("nextLevel", ConstantGrowth.LEVEL_OF_1);
			data.put("nextLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_1));
			data.put("nextLevelNeed", ConstantGrowth.NEED_GROWTH_OF_T1 - myGrowthValue);
		} else if ((ConstantGrowth.NEED_GROWTH_OF_T1 <= myGrowthValue) && (myGrowthValue < ConstantGrowth.NEED_GROWTH_OF_T2)) {
			data.put("nextLevel", ConstantGrowth.LEVEL_OF_2);
			data.put("nextLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_2));
			data.put("nextLevelNeed", ConstantGrowth.NEED_GROWTH_OF_T2 - myGrowthValue);
		} else if ((ConstantGrowth.NEED_GROWTH_OF_T2 <= myGrowthValue) && (myGrowthValue < ConstantGrowth.NEED_GROWTH_OF_T3)) {
			data.put("nextLevel", ConstantGrowth.LEVEL_OF_3);
			data.put("nextLevelDesc", ConstantGrowth.GET_LEVEL_DESC.get(ConstantGrowth.LEVEL_OF_3));
			data.put("nextLevelNeed", ConstantGrowth.NEED_GROWTH_OF_T3 - myGrowthValue);
		}else if (myGrowthValue >= ConstantGrowth.NEED_GROWTH_OF_T3) {
			data.put("nextLevel", "");
			data.put("nextLevelDesc", "");
			data.put("nextLevelNeed", 0);
		}
		return data;
	}


	//会员等级列表，包括等级 等级描述和最大最小成长值
	public final static JSONArray GET_LEVEL_VALUE_NEED_DESC;
	static {
		JSONArray dataArr = new JSONArray();
		for (int i = 0; i < ConstantGrowth.GET_LEVEL_VALUE_NEED_LIST.length; i++) {
			JSONObject dataobj = new JSONObject();
			dataobj.put("levelThresholdMin",
					ConstantGrowth.GET_LEVEL_VALUE_NEED_LIST[i]);
			if (i == ConstantGrowth.GET_LEVEL_VALUE_NEED_LIST.length - 1) {
				dataobj.put("levelThresholdMax", null);
			} else {
				dataobj.put("levelThresholdMax",
						ConstantGrowth.GET_LEVEL_VALUE_NEED_LIST[i + 1] - 1);
			}
			dataobj.put("memLevel", ConstantGrowth.GET_LEVEL_LIST[i]);
			dataobj.put("memLevelDesc", ConstantGrowth.GET_LEVEL_DESC_LIST[i]);
			dataArr.add(dataobj);
		}
		GET_LEVEL_VALUE_NEED_DESC=dataArr;
	}
}
