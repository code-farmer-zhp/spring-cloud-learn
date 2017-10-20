package com.feiniu.favorite.dto;

/**
 * 常量类
 *
 */
public class Code {

    /**
     * 正确
     */
    public static final int RESULT_STATUS_SUCCESS = 1;
    /**
     * 正确
     */
    public static final int RESULT_STATUS_SUCCESS_TWO = 2;
    /**
     * 参数错误
     */
    public static final int RESULT_STATUS_PARAMETER_ERROR = 10001;

    /**
     * 参数mem_guid为空
     */
    public static final int RESULT_STATUS_MEM_GUID_NOT_EXISTS = 10002;

    /**
     * 参数favorite_id为空
     */
    public static final int RESULT_STATUS_FAVORITE_ID_NOT_EXISTS = 10003;

    /**
     * 参数type为空或不正确
     */
    public static final int RESULT_STATUS_TYPE_NOT_EXISTS = 10004;

    /**
     * 重复收藏
     */
    public static final int RESULT_STATUS_DUPLICATE_FAVORITE = 10005;
    /**
     * 重复收藏
     */
    public static final int RESULT_STATUS_DUPLICATE_FAVORITE_ALL = 10015;

    /**
     * 内部异常
     */
    public static final int RESULT_STATUS_EXCEPTION = 10006;

    /**
     * 解析JSON异常
     */
    public static final int RESULT_STATUS_PARSE_EXCEPTION = 10007;

    /**
     * 调用API异常
     */
    public static final int RESULT_STATUS_CALL_API_EXCEPTION = 10008;

    /**
     * 参数area_code为空
     */
    public static final int RESULT_STATUS_AREACODE_NOT_EXISTS = 10009;

    /**
     * 每天添加到收藏夹的商品数超过限额
     */
    public static final int RESULT_STATUS_FAVORITE_COUNT_PERDAY_EXCEED = 10010;

    /**
     * 收藏夹商品数量超过限额
     */
    public static final int RESULT_STATUS_FAVORITE_COUNT_EXCEED = 10011;

    /**
     * 请求的API不存在
     */
    public static final int RESULT_STATUS_API_NOT_EXISTS = 10012;

    /**
     * 批量添加到收藏夹失败
     */
    public static final int RESULT_STATUS_FAVORITE_ALL_ERROR = 10013;

    /**
     * 批量添加到收藏夹
     */
    public static final int RESULT_STATUS_FAVORITE_ALL_INFO = 10014;

    /**
     * 促销分类编号
     */
    public static final String CATEGORY_ACTIVITY = "act0001";

    /**
     * 变价分类编号
     */
    public static final String CATEGORY_SPECIAL_PRICE = "SP0001";
}
