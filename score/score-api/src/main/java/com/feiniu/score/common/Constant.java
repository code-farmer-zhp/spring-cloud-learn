package com.feiniu.score.common;

import java.util.*;


public class Constant {

    /**
     * 积分待生效天数
     */
    public static final Integer SCORE_EFFECT_DAY = 10;


    /************************消息队列标记处理类型**************/

    /**
     * CRM赠送或回收积分类型
     */
    public static final Integer CRM_ABOUT_SCORE = 9;

    /**
     * 提交订单
     */
    public static final Integer DIRECT_TYPE_SUBMIT_ORDER = 10;

    /**
     * 订单付款
     */
    public static final Integer DIRECT_TYPE_ORDER_BUY = 11;

    /**
     * 退货确认
     */
    public static final Integer DIRECT_TYPE_RETURN_PRODUCT = 12;

    /**
     * 取消商城订单
     */
    public static final Integer DIRECT_TYPE__CANCLE_MALL_ORDER = 13;

    /**
     * 评论商品
     */
    public static final Integer DIRECT_TYPE_COMMENT = 14;

    /**
     * 评论设定精华或置顶
     */
    public static final Integer DIRECT_TYPE_SETESSENCEORTOP = 15;


    /************************score_main_log status字段常量**************/

    /**
     * score_main_log 状态无效
     */
    public static final Integer SCORE_MAIN_LOG_STATUS_INVAILD = 0;


    /**
     * score_main_log 状态有效
     */
    public static final Integer SCORE_MAIN_LOG_STATUS_VAILD = 1;

    /******************
     * score_main_log channel字段常量
     *****************/
    public static final String[] DESCRIPTION = {"购物赠点", "退货回收", "购物折抵", "退货返点",
            "到期扣除", "购物赠点", "退货返点",
            "退货回收", "绑定赠点", "绑定赠点",
            "评论赠点", "加精赠点", "置顶赠点",
            "退货回收", "退货回收", "退货回收",
            "签到赠点", "客服赠送", "评论赠点",
            "加精赠点", "置顶赠点", "飞牛赠送", "飞牛回收",
            "积分抽奖", "积分抽奖", "积分兑换", "积分换券", "积分换券", "其他赠点", "会员专享", "升级APP"};


    public static final class ScoreStatus {
        public static final String getDes(Integer channel, Date limitTime) {
            switch (channel) {
                case 0:
                case 10:
                case 11:
                case 12:
                    if (limitTime == null) {
                        return "已生效";
                    } else if (limitTime.after(new Date())) {
                        return "待生效";
                    } else {
                        return "已生效";
                    }
                case 5:
                case 8:
                case 9:
                case 16:
                case 17:
                    return "已生效";
                case 4:
                    return "过期";
                default:
                    return "";
            }
        }

        public static int getStatus(Integer channel, Date limitTime) {
            switch (channel) {
                case 0:
                case 10:
                case 11:
                case 12:
                    if (limitTime == null) {
                        return 1;
                    } else if (limitTime.after(new Date())) {
                        return 0;
                    } else {
                        return 1;
                    }
                case 4:
                    return 2;
                default:
                    return 1;
            }
        }
    }

    /**
     * 订单购买（增加积分）
     */
    public static final Integer SCORE_CHANNEL_ORDER_BUY = 0;

    /**
     * 退货发放收回（回收积分）
     */
    public static final Integer SCORE_CHANNEL_RETURN_PRODUCT_REVOKE = 1;

    /**
     * 订单消费（抵扣积分）
     */
    public static final Integer SCORE_CHANNEL_ORDER_CONSUME = 2;

    /**
     * 退货时消费退回（抵扣积分退回）
     */
    public static final Integer SCORE_CHANNEL_RETURN_PRODUCT_ROCKBACK = 3;

    /**
     * 积分过期（减少可用积分）
     */
    public static final Integer SCORE_CHANNEL_SCORE_EXPIRED = 4;

    /**
     * 积分变成可用（增加可用积分）
     */
    public static final Integer SCORE_CHANNEL_SCORE_ADD_AVAILABLE = 5;
    /**
     * 订单取消。积分退回。
     */
    public static final Integer SCORE_CHANNEL_ORDER_CANCEL = 6;

    /**
     * 订单取消，发放收回。
     */
    public static final Integer SCORE_CHANNEL_ORDER_SUBMIT_CANCEL = 7;


    /**
     * 绑定手机获得积分
     */
    public static final Integer SCORE_CHANNEL_BIND_PHONE = 8;

    /**
     * 绑定邮箱获得积分
     */
    public static final Integer SCORE_CHANNEL_BIND_EMAIL = 9;

    /**
     * 评论商品获得积分
     */
    public static final Integer SCORE_CHANNEL_COMMENT_PRODUCT = 10;

    /**
     * 评论设定精华
     */
    public static final Integer SCORE_CHANNEL__COMMENT_SET_ESSENCE = 11;

    /**
     * 评论置顶
     */
    public static final Integer SCORE_CHANNEL__COMMENT_SET_TOP = 12;


    /**
     * 客人退货回收评论积分
     */
    public static final Integer SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_COMMENT = 13;

    /**
     * 客人退货回收评论设置精华积分
     */
    public static final Integer SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_ESSENCE = 14;

    /**
     * 客人退货回收评论置顶积分
     */
    public static final Integer SCORE_CHANNEL_RETURN_PRODUCT_RECOVER_TOP = 15;

    /**
     * 用户签到获得积分
     */
    public static final Integer SCORE_CHANNEL_PHONE_SIGN = 16;

    /**
     * 审核通过送积分
     */
    public static final Integer SCORE_CHANNEL_APPROVAL = 17;


    /**
     * 评论商品获得积分。扫描生效（加积分）
     */
    public static final Integer SCORE_CHANNEL_COMMENT_PRODUCT_AVAILABLE = 18;

    /**
     * 评论设定精华 扫描生效（加积分）
     */
    public static final Integer SCORE_CHANNEL__COMMENT_SET_ESSENCE_AVAILABLE = 19;

    /**
     * 评论置顶 扫描生效（加积分）
     */
    public static final Integer SCORE_CHANNEL__COMMENT_SET_TOP_AVAILABLE = 20;


    /**
     * CRM赠送
     */
    public static final Integer SCORE_CHANNEL_CRM_GIVE = 21;

    /**
     * CRM回收
     */
    public static final Integer SCORE_CHANNEL_CRM_RECOVER = 22;


    /**
     * 礼包赠送
     */
    public static final Integer SCORE_CHANNEL_PKAD_GIVE = 21;

    /**
     * 礼包回收
     */
    public static final Integer SCORE_CHANNEL_PKAD_RECOVER = 22;

    /**
     * 抽奖获得  23
     */
    public static final Integer SCORE_CHANNEL_RAFFLE_GIVE = 23;
    /**
     * 抽奖使用  24
     */
    public static final Integer SCORE_CHANNEL_RAFFLE_COST = 24;
    /**
     * 兑换商品消耗  25
     */
    public static final Integer SCORE_CHANNEL_EXCHANGE_GOODS_COST = 25;
    /**
     * 兑换异页券消耗   26
     */
    public static final Integer SCORE_CHANNEL_EXCHANGE_CARD_COST = 26;
    /**
     * 兑换抵用券消耗   27
     */
    public static final Integer SCORE_CHANNEL_EXCHANGE_VOUCHER_COST = 27;
    /**
     * 填写兴趣爱好赠点   28
     */
    public static final Integer SCORE_CHANNEL_FILL_IN_INTEREST = 28;
    /**
     * 会员专享商品消耗  29
     */
    public static final Integer SCORE_CHANNEL_EXCLUSIVE_COST = 29;

    /**
     * 用户升级App赠送积分
     */
    public static final Integer SCORE_CHANNEL_APP_UPGRADE_GIVE = 30;

    public static final String VOUCHER_EXCHANGE_REMARK_PREFIX = "voucherName:";


    public final static Set<Integer> TYPES_OF_CONSUME_AND_EXCHANGE;

    static {
        Set<Integer> sets = new HashSet<>();
        sets.add(SCORE_CHANNEL_ORDER_CONSUME);
        sets.add(SCORE_CHANNEL_EXCHANGE_GOODS_COST);
        sets.add(SCORE_CHANNEL_EXCLUSIVE_COST);
        TYPES_OF_CONSUME_AND_EXCHANGE = Collections.unmodifiableSet(sets);
    }

    /***************************score_order_detail type字段常量*******************************/

    /**
     * 购买
     */
    public static final Integer SCORE_ORDER_DETAIL_TYPE_BUY = 0;

    /**
     * 退货发放收回
     */
    public static final Integer SCORE_ORDER_DETAIL_TYPE_RETURN_PRODUCT = 1;

    /**
     * 订单消费
     */
    public static final Integer SCORE_ORDER_DETAIL_TYPE_ORDER_CONSUME = 2;


    /**
     * 退货时消费退回
     */
    public static final Integer SCORE_ORDER_DETAIL_TYPE_RETURN_PRODUCT_CONSUME_RETURN = 3;


    /**
     * 订单取消消费退回
     */
    public static final Integer SCORE_ORDER_DETAIL_TYPE_ORDER_CANCEL_CONSUME_RETURN = 4;

    /**
     * 订单取消发放收回
     */
    public static final Integer SCORE_ORDER_DETAIL_TYPE_ORDER_CANCEL_GRANT_DEDUCT = 5;


    /**********************score_comment_detail type字段常量****************************/
    /**
     * 评论获得积分
     */
    public static final Integer SCORE_COMMENT_DETAIL_TYPE_COMMENT_PRODUCT = 0;

    /**
     * 评论设定精华
     */
    public static final Integer SCORE_COMMENT_DETAIL_TYPE_COMMENT_SET_ESSENCE = 1;

    /**
     * 评论置顶
     */
    public static final Integer SCORE_COMMENT_DETAIL_TYPE_COMMENT_SET_TOP = 2;


    /**
     * 客人退货回收评论积分
     */
    public static final Integer SCORE_COMMENT_DETAIL_TYPE_RETURN_PRODUCT_RECOVER_COMMENT = 3;

    /**
     * 客人退货回收评论设置精华积分
     */
    public static final Integer SCORE_COMMENT_DETAIL_TYPE_RETURN_PRODUCT_RECOVER_ESSENCE = 4;

    /**
     * 客人退货回收评论置顶积分
     */
    public static final Integer SCORE_COMMENT_DETAIL_TYPE_RETURN_PRODUCT__RECOVER_TOP = 5;


    /*********************************评论设定类型*****************************************/
    /**
     * 评论设置精华
     */
    public static final Integer COMMENT_SET_ESSENCE = 1;

    /**
     * 评论置顶
     */
    public static final Integer COMMENT_SET_TOP = 2;


    /*******************************绑定签到获得积分*********************************/
    /**
     * 绑定手机获得20积分
     */
    public static final Integer BIND_PHONE_SCORE = 20;


    /**
     * 绑定邮箱获得20积分
     */
    public static final Integer BIND_EMAIL_SCORE = 20;

    /**
     * 手机签到获得5积分
     */
    public static final Integer PHONE_SIGN_SCORE = 5;

    /**
     * 连续签到满7天获得双倍积分
     */
    public static final Integer DUR_SIGN_SCORE = 10;


    /**
     * 填写兴趣爱好获得50积分
     */
    public static final Integer INTEREST_SCORE = 50;
    /***********************************失败job******************************/

    /**
     * 未处理
     */
    public static final Integer IS_DEAL_NOT = 0;


    /**
     * 默认job状态
     */
    public static final Integer JOB_STATUS_DEFAULT = 0;

    /**
     * job执行成功
     */
    public static final Integer JOB_STATUS_SUCCESSED = 1;


    /****************************按照日期对积分收支进行统计******************************/

    /**
     * 默认站点 飞牛网
     */
    public static final Integer LOAD_SCORE_DEFAULT_WEBSIT = 1;

    /**
     * 默认pageNo
     */
    public static final Integer LOAD_SCORE_DEFAULT_PAGENO = 0;

    /**
     * 默认pageSize
     */
    public static final Integer LOAD_SCORE_DEFAULT_PAGESIZE = 31;

    /*************************依据商品出货号获取商品 赚取/消费 积分********************************/

    /**
     * 赚取积分类型
     */
    public static final Integer LOADOL_SCORE_TYPE_GET = 1;


    /********************************积分来源类型*******************************************/

    /**
     * 购物
     */
    public static final Integer SRC_TYPE_BUY = 1;

    /**
     * 评论
     */
    public static final Integer SRC_TYPE_COMMENT = 2;

    /**
     * 绑定手机
     */
    public static final Integer SRC_TYPE_BIND_PHONE = 3;

    /**
     * 绑定邮箱
     */
    public static final Integer SRC_TYPE_BIND_EMAIL = 4;

    /**
     * 手机签到获得
     */
    public static final Integer SRC_TYPE_PHONE_SIGN = 5;

    /**
     * CRM赠送
     */
    public static final Integer SRC_TYPE_CRM_GIVE = 6;

    /**
     * CRM回收
     */
    public static final Integer SRC_TYPE_CRM_RECOVER = 7;

    /**
     * 积分抽奖
     */
    public static final Integer SRC_TYPE_RAFFLE = 8;

    /**
     * 其他
     */
    public static final Integer SRC_TYPE_OTHER = 9;
    /**
     * 兑换
     */
    public static final Integer SRC_TYPE_EXCHANGE = 10;
    /************************平台类型***********************************************/

    /**
     * 分销平台
     */
    public static final Integer DISTRIBUTION_PLATFORM = 5;

    public static final String ELECTRONIC_SCREEN = "17";

    /**
     * 积分兑换商品
     */
    public static final String BUY_MODE_OF_SCORE_EXCHANGE = "1";
    /**
     * 会员专享商品
     */
    public static final String BUY_MODE_OF_EXCLUSIVE = "2";
    /**
     * 爆款商品，商品促销等级为1
     */
    public static final String PROMOTION_GRADE_OF_1 = "1";

    /**
     * 秒杀商品，商品促销等级为3,
     */
    public static final String PROMOTION_GRADE_OF_3 = "3";
    /**
     * 团购商品，商品促销等级为3。商详页查询接口用
     */
    public static final String PROMOTION_GRADE_OF_4 = "4";
    /**
     * 团购类型
     */
    public static final String GROUP_TYPE = "5";

    /**
     * 每页查询的条数
     */
    public static final Integer DEFAULT_PAGE_SIZE = 500;

    /*************************************用户类型********************************/

    /**
     * 企业用户标识
     */
    public static final Integer COMPANY_USER = 1;

    /**************************支付状态******************************************/

    /**
     * 支付状态
     */
    public static final String IS_PAY = "1";

    /**********************************积分平台类型****************************/

    /**
     * 商城
     */
    public static final Integer MALLTYPE = 2;

    /**
     * 自营
     */
    public static final Integer SELFTYPE = 1;

    /**********************************
     * 商城 自营获得积分类型
     ****************************/

    public static final Integer SELF_SCORE_TYPE = 0;

    /**
     * 订单购买积分
     */
    public static final Integer MALL_SCORE_TYPE_ORDER_BUY = 2;


    /**********************************CRM积分类型****************************/

    /**
     * 赠送积分
     */
    public static final Integer CRM_SCORE_GIVE = 1;

    /**
     * 回收积分
     */
    public static final Integer CRM_SCORE_RECOVER = 2;


    /**********************************score 失败type类型*****************/

    /**
     * 未找到订单消费信息，延迟处理
     */
    public static final Integer SCORE_UNSUCESS_TYPE_FOURTEEN = 14;


    /**
     * 退货确认请求早于订单付款请求
     */
    public static final Integer SCORE_UNSUCESS_TYPE_TWO = 2;


    /**
     * 未找到订单购买获得积分信息，延迟处理
     */
    public static final Integer SCORE_UNSUCESS_TYPE_FIFTEEN = 15;

    /**
     * 退货确认请求大于付款确认，延迟处理
     */
    public static final Integer SCORE_UNSUCESS_TYPE_SIXTEEN = 16;


    /**
     * 取消商城订单。但是未找到详细信息
     */
    public static final Integer SCORE_UNSUCESS_TYPE_SEVENTEEN = 17;


    /**
     * 消费积分解冻kafka消息，并发送微信
     */
    public static final Integer SCORE_UNSUCESS_UNLOCK_MSG_FOR_WX = 19;

    /**
     * 签到失败
     */
    public static final Integer SCORE_UNSUCESS_SIGN = 20;

    /**
     * 评论相关失败
     */
    public static final Integer SCORE_UNSUCESS_COMMENT = 21;

    /**
     * 提交订单
     */
    public static final Integer GROWTH_ORDER_SUBMIT = 50;
    /**
     * 订单支付
     */
    public static final Integer GROWTH_ORDER_PAY = 51;

    /**
     * 确认收货
     */
    public static final Integer GROWTH_ORDER_RECEIVE = 52;
    /**
     * 订单退货
     */
    public static final Integer GROWTH_ORDER_RETURN = 53;

    /**
     * 成长值消息消费
     */
    public static final Integer GROWTH_COSUMER_GROWTH_RECEIVE = 54;
    /**
     * 等级消息消费
     */
    public static final Integer GROWTH_COSUMER_GRADE_RECEIVE = 55;
    /**
     * 成长值通知CRM
     */
    public static final Integer GROWTH_PRODUCE_GROWTH = 56;

    /**
     * 成长值统计变动
     */
    public static final Integer GROWTH_VALUE_NUM_CHANGE = 57;
    /**********************************growth 失败type类型*****************/

    /**
     * 评论未找到订单消费信息，延迟处理
     */
    public static final Integer GROWTH_COMMENT_UNSUCESS_TYPE_NO_ORDER = 62;

    /**
     * 评论置顶或加精未找到订单消费信息，延迟处理
     */
    public static final Integer GROWTH_SET_ESSENCE_OR_TOP_UNSUCESS_TYPE_NO_ORDER = 63;


    /**
     * 礼包消息消费
     */
    public static final Integer PKAD_CONSUME_UNSUCCESS = 71;

    /**
     * 礼包领取通知CRM
     */
    public static final Integer PKAD_TAKEN_KAFKA = 72;
    /**
     * 礼包领取失败
     */
    public static Integer PKAD_TAKEN_UNSUCCESS = 73;

    /**
     * 工会会员注册赠送抵用券
     */
    public static Integer UNIONIST_REGISTER_SEND_BONUS = 74;
    /**
     * 工会会员绑定赠送抵用券
     */
    public static Integer UNIONIST_BIND_SEND_BONUS = 75;

    /*****************
     * 评论是否是商城的
     *************************/
    public static final String IS_MALL = "1";

    /********************************
     * 是跨境订单
     ************************/
    public static final int IS_OVERSEA = 1;


    /********************************
     * 积分立即生效接口type类型
     ************************/
    public static final Integer CHANGE_SCORE_IMMEDIATELY_TYPE_OF_RAFFLE_GIVE = 1;

    public static final Integer CHANGE_SCORE_IMMEDIATELY_TYPE_OF_RAFFLE_COST = 2;

    public static final Integer CHANGE_SCORE_IMMEDIATELY_TYPE_EXCHANGE_CARD_COST = 3;

    public final static Map<Integer, Integer> CHANGE_SCORE_TYPE_TO_CHANNEL;

    static {
        HashMap<Integer, Integer> maps = new HashMap<>();
        maps.put(CHANGE_SCORE_IMMEDIATELY_TYPE_OF_RAFFLE_GIVE, SCORE_CHANNEL_RAFFLE_GIVE);
        maps.put(CHANGE_SCORE_IMMEDIATELY_TYPE_OF_RAFFLE_COST, SCORE_CHANNEL_RAFFLE_COST);
        maps.put(CHANGE_SCORE_IMMEDIATELY_TYPE_EXCHANGE_CARD_COST, SCORE_CHANNEL_EXCHANGE_CARD_COST);
        CHANGE_SCORE_TYPE_TO_CHANNEL = Collections.unmodifiableMap(maps);
    }

    public final static Map<Integer, String> CHANGE_SCORE_TYPE_TO_REMARK;

    static {
        HashMap<Integer, String> maps = new HashMap<>();
        maps.put(CHANGE_SCORE_IMMEDIATELY_TYPE_OF_RAFFLE_GIVE, "抽奖获得积分");
        maps.put(CHANGE_SCORE_IMMEDIATELY_TYPE_OF_RAFFLE_COST, "抽奖使用积分");
        CHANGE_SCORE_TYPE_TO_REMARK = Collections.unmodifiableMap(maps);
    }


    /********************************
     * 30天内无订单、员工账号最多签到多少次
     ************************/
    public static final int SIGN_COUNT_LIMIT_EMP = 7;

    public static final int SIGN_COUNT_LIMIT1_WITHOUT_RECENT_ORDER = 0;

    public static final int RECENT_DAYS_WITH_ORDER = 30;

    /********************************抽奖获得/使用积分,兑换抵用券使用积分************************/
    /**
     * 获得积分
     */
    public static final String TYPE_OF_SCORE_GIVE = "0";
    /**
     * 使用积分
     */
    public static final String TYPE_OF_SCORE_COST = "1";
    /**
     * 回滚积分
     */
    public static final String TYPE_OF_SCORE_ROOLBACK = "2";

    /********************************使用获得积分立即生效的途径************************/
    /**
     * 抽奖使用/获得积分
     */
    public static final String CHANNEL_OF_RAFFLE = "1";
    /**
     * 兑换抵用券使用积分/兑换抵用券失败回滚积分
     */
    public static final String CHANNEL_OF_EXCHANGE_VOUCHER = "2";

    /********************************
     * 晒图多少张赠送额外积分和成长值
     ************************/
    public static final Integer PIC_COUNT = 3;

    /*****************************报表积分类型**************************/
    /**
     * 购物
     */
    public static final String REPORT_SHOPPING = "1";

    /**
     * 绑定手机
     */
    public static final String REPORT_BIND_PHONE = "2";

    /**
     * 绑定邮箱
     */
    public static final String REPORT_BIND_EMAIL = "3";

    /**
     * 签到
     */
    public static final String REPORT_SIGN = "4";

    /**
     * 评论
     */
    public static final String COMMENT = "5";

    /**
     * 飞牛
     */
    public static final String FEI_NIU = "6";

    /**
     * 抽奖
     */
    public static final String CHOU_JANG = "2";

    /**
     * 兑换
     */
    public static final String DUI_HUAN = "3";

    /*****************************商品类型**************************/
    /**
     * 一般商品
     */
    public static final String IT_TYPE_OF_YBSP = "0";

    public static final String EDFAULT_DB_EMPTY = "_";

    public static final String TP_LOGIN_TYPE_OF_LABOUR = "10";

    public static final String DELIMITER = ";";
}
