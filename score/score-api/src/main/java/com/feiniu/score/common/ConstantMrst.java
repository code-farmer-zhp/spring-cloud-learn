package com.feiniu.score.common;

import java.util.*;

public class ConstantMrst {
	public static final String PACKAGE_SELFDEFINE="PACKAGE_SELFDEFINE";
	public static final String MRST_UI_0 = "T0";
	public static final String MRST_UI_1 = "T1";
	public static final String MRST_UI_2 = "T2";
	public static final String MRST_UI_3 = "T3";
	public static final String MRST_UI_4 = "T4";
	public static final String MRST_UI_5 = "T5";
	public static final String MRST_UI_Z = "TZ";
	public static final String MRST_UI_6 = "T6";

	public static final String MRSTDESC_UI_0 = PACKAGE_SELFDEFINE;
	public static final String MRSTDESC_UI_1 = "生日礼包";
	public static final String MRSTDESC_UI_2 = "升级礼包";
	public static final String MRSTDESC_UI_3 = "神秘礼包";
	public static final String MRSTDESC_UI_4 = "周年惊喜礼包";
	public static final String MRSTDESC_UI_5 = "新人礼包";
	public static final String MRSTDESC_UI_Z = "会员福利礼包";
	public static final String MRSTDESC_UI_6 = "周年庆专享礼包";

	public static List<String> MRSTUIList= Arrays.asList(MRST_UI_0,MRST_UI_1,MRST_UI_2,MRST_UI_3,MRST_UI_4,MRST_UI_5,MRST_UI_Z,MRST_UI_6);

	public static final Map<String,String> GET_MRSTUI_DESC;
	static{
		HashMap<String,String> maps3= new HashMap<>();
		maps3.put(MRST_UI_0, MRSTDESC_UI_0);
		maps3.put(MRST_UI_1, MRSTDESC_UI_1);
		maps3.put(MRST_UI_2, MRSTDESC_UI_2);
		maps3.put(MRST_UI_3, MRSTDESC_UI_3);
		maps3.put(MRST_UI_4, MRSTDESC_UI_4);
		maps3.put(MRST_UI_5, MRSTDESC_UI_5);
		maps3.put(MRST_UI_Z, MRSTDESC_UI_Z);
		maps3.put(MRST_UI_6, MRSTDESC_UI_6);
		GET_MRSTUI_DESC = Collections.unmodifiableMap(maps3);
	}
	//回收礼包
	public static final String PKAD_TYPE_HS= "2";

	public static final String IS_T= "T";
	public static final String IS_F= "F";
	public static final Integer IS_T_DB= 1;
	public static final Integer IS_F_DB= 0;

	public static final String CARD_TYPE_DYQ= "1";
	public static final String CARD_TYPE_YHQ= "2";
	//商城-优惠券
	public static final String CARD_TYPE_SC_YHQ= "22";
	//商城-免邮券
	public static final String CARD_TYPE_SC_MYQ= "25";
	//商城-订单满额送券
	public static final String CARD_TYPE_SC_DDMEQ= "29";
	

	public static final String CARD_TYPE_ZY_MYQ= "5";

	public static final String CARD_TYPE_ZY_PP= "6";

	public static final String CARD_TYPE_GIFT= "7";

	public static final List<String> CARD_TYPE_LIST = Arrays.asList(CARD_TYPE_DYQ,CARD_TYPE_YHQ,CARD_TYPE_SC_YHQ,CARD_TYPE_SC_MYQ,CARD_TYPE_SC_DDMEQ,CARD_TYPE_ZY_MYQ,CARD_TYPE_ZY_PP);

	private static final String COUPON_TYPE_DYQ= "4";
	private static final String COUPON_TYPE_YHQ= "0";
	private static final String COUPON_TYPE_SC_YHQ= "11";
	private static final String COUPON_TYPE_SC_MYQ= "12";
	private static final String COUPON_TYPE_SC_DDMEQ= "";
	private static final String COUPON_TYPE_ZY_MYQ="1";
	private static final String COUPON_TYPE_ZY_PP= "3";
	private static final String COUPON_TYPE_GIFT= "5";


	public static final Map<String,String> GET_CARD_TYPE_BY_COUPON_TYPE;
	static{
		HashMap<String,String> maps3= new HashMap<>();
		maps3.put(COUPON_TYPE_DYQ, CARD_TYPE_DYQ);
		maps3.put(COUPON_TYPE_YHQ, CARD_TYPE_YHQ);
		maps3.put(COUPON_TYPE_SC_YHQ, CARD_TYPE_SC_YHQ);
		maps3.put(COUPON_TYPE_SC_MYQ, CARD_TYPE_SC_MYQ);
		maps3.put(COUPON_TYPE_ZY_MYQ, CARD_TYPE_ZY_MYQ);
		maps3.put(COUPON_TYPE_ZY_PP, CARD_TYPE_ZY_PP);
		maps3.put(COUPON_TYPE_GIFT, CARD_TYPE_GIFT);
		GET_CARD_TYPE_BY_COUPON_TYPE = Collections.unmodifiableMap(maps3);
	}

	public static final String MRDF_TYPE_C3 = "C3";

	public static final String KEY_OF_CARD_NO = "cardNo";
	public static final String KEY_OF_CARD_GET_TIME = "cardGetTime";

	public static final Map<String,String> GET_CARD_TYPE_DESC;
	static{
		HashMap<String,String> maps3= new HashMap<>();
		maps3.put(CARD_TYPE_DYQ, "抵用券");
		maps3.put(CARD_TYPE_YHQ, "优惠券");
		maps3.put("6", "优惠券");
		maps3.put(CARD_TYPE_SC_YHQ, "商城优惠券");
		maps3.put(CARD_TYPE_SC_MYQ, "免邮券");
		maps3.put(CARD_TYPE_ZY_MYQ, "免邮券");
		maps3.put(CARD_TYPE_ZY_PP, "品牌券");
		maps3.put(CARD_TYPE_GIFT, "礼品券");
		maps3.put("29", "商城优惠券");
		GET_CARD_TYPE_DESC = Collections.unmodifiableMap(maps3);
	}
}
