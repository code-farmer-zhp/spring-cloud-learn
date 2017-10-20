package com.feiniu.score.common;

import com.feiniu.score.util.MD5Util;

/*
*@author: Max
*@mail:1069905071@qq.com 
*@time:2017/1/18 12:58 
*/
public class MrstUtil {
    public static final String couponIdSalt="couponEncode";
    public static String  enCodeCouponSensitive(String couponSeq){
        return MD5Util.getMD5Code(default2String(couponSeq,"")+couponIdSalt);
    }

    public static String  default2String(String value,String defaultValue){
        if(value==null){
            return defaultValue;
        }else{
            return value;
        }

    }
}
