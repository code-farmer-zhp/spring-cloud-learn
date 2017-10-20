package com.feiniu.member.common;

import java.util.*;

public class ConstantGrowth {

	
	/**
	 *  CACHE KEY START 
	 */
	public static String CACHE_ORDER_GROWTH_KEY = "CACHE_ORDER_GROWTH_KEY_";
	
	
	public static String DEFAULT_STRING_VALUE = "_";
	
	// 订单金额 小于 10 元 不能获得成长值
	public static int ORDER_MIN_LIMIT = 10;
	
	// 订单中单个商品最大获得成长值 500 (例如 某个商品 买了 3 个 就是 1500)
	public static int ORDER_ITEM_MAX_LIMIT = 500;
	
	
	/**	
	 * 是否已经退货 ：没有退(init)
	 */
	public static int ORDER_RETURN_FLAG_NO = 0;
	/**	
	 * 是否已经退货 ：已经有退
	 */
	public static int ORDER_RETURN_FLAG_YES = 1;
	

	/**
	 * 客人未进行收货确认时，到支付后的第10天赠送成长值（包含支付当日）
	 */
	public static int DAY_AGO_RECEIVE = -9 ; 
	
	
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
	
	/********************************成长值变动类型描述*******************************************/

	public static final Map<Integer,String> DETAIL_CHANNEL_DESC;
	static {
		 Map<Integer,String> maps= new HashMap<Integer,String>();
		 maps.put(DETAIL_GROWTH_CHANNEL_DL, "登录获得");
		 maps.put(DETAIL_GROWTH_CHANNEL_FNZS, "飞牛赠送");
		 maps.put(DETAIL_GROWTH_CHANNEL_GW, "购物获得");
		 maps.put(DETAIL_GROWTH_CHANNEL_PL, "评论获得");
		 maps.put(DETAIL_GROWTH_CHANNEL_PLZD, "评论置顶");
		 maps.put(DETAIL_GROWTH_CHANNEL_PLJH, "评论精华");
		 maps.put(DETAIL_GROWTH_CHANNEL_TH_GW, "退货回收(购物)");
		 maps.put(DETAIL_GROWTH_CHANNEL_TH_PL, "退货回收(评论)");
		 DETAIL_CHANNEL_DESC = Collections.unmodifiableMap(maps);
	}

	/**
	 * 回收成长值的都要增加到这里面
	 */
	public static Integer[] DETAIL_GROWTH_CHANNEL_OPERATE_LOSS = new Integer[]{DETAIL_GROWTH_CHANNEL_TH_GW , DETAIL_GROWTH_CHANNEL_TH_PL};
	
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
	 * 无效
	 */
	public static Integer DATA_FLAG_WX = 2;
	
	
	/**
	 * GROWTH_ORDER_INFO.PAY_STATUS
	 */
	/**
	 * 未支付 
	 */
	public static Integer ORDER_PAY_STATUS_WZF = 1; // 未支付
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
	
	
	
	/********************************成长值评论设置精华、置顶*******************************************/
	/**
	 * 置顶
	 */ 
	public static final Integer GROWTH_COMTYPE_COMMENT_SET_ESSENCE = 1;
	/**
	 * 精华
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
	public static final String LEVEL_OF_P = "TP";
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
	
	public static final Map<Integer,String> LOGIN_FROM_DESC;
	static{
		 HashMap<Integer,String> maps1= new HashMap<Integer,String>();
		 maps1.put(LOGIN_FROM_PC, "PC端登录");
		 maps1.put(LOGIN_FROM_APP, "APP端登录");
		 maps1.put(LOGIN_FROM_TOUCH, "触屏端登录");
		 LOGIN_FROM_DESC = Collections.unmodifiableMap(maps1);
	}
	
	public static final Map<String, String> GET_LEVEL_DESC;
	static{
		 HashMap<String,String> maps2= new HashMap<String,String>();
		 maps2.put(LEVEL_OF_0, "普通会员");
		 maps2.put(LEVEL_OF_1, "银卡会员");
		 maps2.put(LEVEL_OF_2, "金卡会员");
		 maps2.put(LEVEL_OF_3, "白金卡会员");
		 maps2.put(LEVEL_OF_P, "飞牛网合伙人");
		 GET_LEVEL_DESC = Collections.unmodifiableMap(maps2);
	}
	
	 public static List<String> NOEXPIRYLEVEL= Arrays.asList(LEVEL_OF_0,LEVEL_OF_1);
	 
	 
	 public static String IS_T_STR= "1";
	 public static String IS_F_STR= "0";
	 public static Integer PKAD_DEFAULT_PAGE_SIZE=5;
	 public static Integer PKAD_DEFAULT_SHOW_SIZE=3;
	 public static Integer PKAD_MAX_PAGE_SIZE=600;

	public static String MRST_UI_0 = "T0";
	public static String MRST_UI_1 = "T1";
	public static String MRST_UI_2 = "T2";
	public static String MRST_UI_3 = "T3";
	public static String MRST_UI_4 = "T4";
	public static String MRST_UI_5 = "T5";
	public static String MRST_UI_Z = "TZ";
	public static String MRST_UI_6 = "T6";

	public static List<String> MRSTUIList= Arrays.asList(MRST_UI_1,MRST_UI_2,MRST_UI_3,MRST_UI_4,MRST_UI_5,MRST_UI_Z,MRST_UI_6);

	
	public static final Map<String,String> GET_UI_FOR_SHORT_DESC;
	static{
		 HashMap<String,String> maps4= new HashMap<String,String>();
		 maps4.put(MRST_UI_1, "bh");
		 maps4.put(MRST_UI_2, "ug");
		 maps4.put(MRST_UI_3, "ms");
		 maps4.put(MRST_UI_4, "bg");
		 maps4.put(MRST_UI_5, "nr");
		 maps4.put(MRST_UI_Z, "benefits");
		 maps4.put(MRST_UI_6, "benefits");
		 GET_UI_FOR_SHORT_DESC = Collections.unmodifiableMap(maps4);
	}
	
	public static final Map<String,String> GET_MRSTUI_FOR_BGIMG;
	static{
		 HashMap<String,String> maps4= new HashMap<String,String>();
		 maps4.put(MRST_UI_0, "lq1");
		 maps4.put(MRST_UI_1, "lq6");
		 maps4.put(MRST_UI_2, "lq3");
		 maps4.put(MRST_UI_3, "lq2");
		 maps4.put(MRST_UI_4, "lq5");
		 maps4.put(MRST_UI_5, "lq4");
		 maps4.put(MRST_UI_Z, "lq1");
		 maps4.put(MRST_UI_6, "lq1");
		 GET_MRSTUI_FOR_BGIMG = Collections.unmodifiableMap(maps4);
	}
	
	public static final Map<String,String> GET_MRSTUI_FOR_NOTAKEN_BGIMG;
	static{
		 HashMap<String,String> maps4= new HashMap<String,String>();
		 maps4.put(MRST_UI_0, "ct1");
		 maps4.put(MRST_UI_1, "ct6");
		 maps4.put(MRST_UI_2, "ct3");
		 maps4.put(MRST_UI_3, "ct2");
		 maps4.put(MRST_UI_4, "ct5");
		 maps4.put(MRST_UI_5, "ct4");
		 maps4.put(MRST_UI_Z, "ct1");
		 maps4.put(MRST_UI_6, "ct1");
		 GET_MRSTUI_FOR_NOTAKEN_BGIMG = Collections.unmodifiableMap(maps4);
	}
	
	
	public static final Map<String,String> GET_MRSTUI_FOR_BTN_DATE_TYPE;
	static{
		 HashMap<String,String> maps5= new HashMap<String,String>();
		 //生日惊喜  优惠券 g-layer-2
		 maps5.put(MRST_UI_1, "2"); 
		 //升级  积分 g-layer-1
		 maps5.put(MRST_UI_2, "1");
		 //神秘  优惠券3张  g-layer-3
		 maps5.put(MRST_UI_3, "3");
		 //周年   积分 g-layer-1
		 maps5.put(MRST_UI_4, "1");
		 //新人 积分 抵用券  g-layer-4
		 maps5.put(MRST_UI_5, "4");
		 maps5.put(MRST_UI_Z, "4");
		 maps5.put(MRST_UI_6, "4");
		 GET_MRSTUI_FOR_BTN_DATE_TYPE = Collections.unmodifiableMap(maps5);
	}
	
	public static final Map<String,String> GET_MRSTUI_FOR_DIALOG_DATE_TYPE;
	static{
		//已作废
		//1 积分 2优惠券 3抵用券 或者抵用券和积分
		 HashMap<String,String> maps5= new HashMap<String,String>();
		 //生日惊喜  优惠券
		 maps5.put(MRST_UI_1, "2"); 
		 //升级  抵用券（老的是积分）
		 maps5.put(MRST_UI_2, "3");
		 //神秘  优惠券3张 
		 maps5.put(MRST_UI_3, "2");
		 //周年   积分 
		 maps5.put(MRST_UI_4, "1");
		 //新人 抵用券
		 maps5.put(MRST_UI_5, "3");
		 maps5.put(MRST_UI_Z, "3");
		 maps5.put(MRST_UI_6, "3");
		 GET_MRSTUI_FOR_DIALOG_DATE_TYPE = Collections.unmodifiableMap(maps5);
	}
}
