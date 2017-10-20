package com.feiniu.member.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by xiaoyan.wu on 2016/3/14.
 */
public class BaseFunction {
    /*******************************************************************************************************
     * 获取地区信息
     *******************************************************************************************************/
    public static JSONObject areaJson(String areaCode) {

        JSONObject result = new JSONObject();

        String province = "";
        String city = "";
        String area = "";

        String split = "_";
        if (!areaCode.contains(split)) {
            split = "-";
        }
        String areaArray[] = areaCode.split(split);

        if (areaArray.length >= 1) {

            province = areaArray[0];

        }
        if (areaArray.length >= 2) {

            city = areaArray[1];

        }
        if (areaArray.length >= 3) {

            area = areaArray[2];

        }

        result.put("provinceCode",province);
        result.put("cityCode",city);
        result.put("areaCode",area);

        return result;

    }

    /*******************************************************************************************************
     * 判断JSONArray是否有值
     *******************************************************************************************************/
    public static Boolean jsonArrayHasData(JSONArray jsonArray) {

        Boolean result = false;

        //new JSONArray 的情况下size=0
        if (jsonArray != null && jsonArray.size() > 0) {
            result =true;
        }

        return result;

    }

    /*******************************************************************************************************
     * 判断JSONObject是否有值
     *******************************************************************************************************/
    public static Boolean jsonObjectHasData(JSONObject jsonObject) {

        Boolean result = false;

        //new JSONObject 的情况下size=0
        if (jsonObject != null && jsonObject.size() > 0) {
            result =true;
        }

        return result;

    }

    /*******************************************************************************************************
     * 从JSONObject中获取String类型的值
     *******************************************************************************************************/
    public static String jsonGetString(JSONObject jsonObject, String key) {

        String result = "";

        //key存在时付值，不存在时付null值
        if ( jsonObject != null) {

            result = jsonObject.getString(key);

        }

        return result;

    }

    /*******************************************************************************************************
     * 从JSONObject中获取Integer类型的值
     *******************************************************************************************************/
    public static Integer jsonGetInteger(JSONObject jsonObject, String key) {

        Integer result = 0;

        //key存在时付值，不存在时付0
        if ( jsonObject != null ) {

            Object value = jsonObject.get(key);

            if (value == null) {
                //null值的时候返回0
            } else if(value instanceof Integer) {
                result = (Integer)value;
            } else if (value instanceof Number) {
                result = Integer.valueOf(((Number)value).intValue());
            } else if (value instanceof String) {
                String strVal = (String)value;
                if (isNumeric(strVal)) {
                    Double dblData = Double.parseDouble(strVal);
                    result = dblData.intValue();
                } else {
                    //不能转换成数字的时候返回0
                }
            } else {
                //不是数字类型和文字类型的时候返回0
            }

        }

        return result;

    }

    /*******************************************************************************************************
     * 从JSONObject中获取JSONObject类型的值
     *******************************************************************************************************/
    public static JSONObject jsonGetJSONObject(JSONObject jsonObject, String key) {

        JSONObject result = null;

        //key存在时付值，不存在时付null值
        if ( jsonObject != null ) {

            Object value = jsonObject.get(key);

            if (value == null) {
                //null值的时候返回null
            } else if (value instanceof JSONObject) {
                result = (JSONObject)value;
            } else {
                //其他情况的时候返回null
            }

        }

        return result;

    }

    /*******************************************************************************************************
     * 从JSONObject中获取JSONArray类型的值
     *******************************************************************************************************/
    public static JSONArray jsonGetJSONArray(JSONObject jsonObject, String key) {

        JSONArray result = null;

        //key存在时付值，不存在时付null值
        if ( jsonObject != null ) {

            Object value = jsonObject.get(key);

            if (value == null) {
                //null值的时候返回null
            } else if (value instanceof JSONArray) {
                result = (JSONArray)value;
            } else {
                //其他情况的时候返回null
            }

        }

        return result;

    }

    /*******************************************************************************************************
     * 判断是否为数字
     *******************************************************************************************************/
    public static Boolean isNumeric(String str) {

        Boolean result = true;

        if (isEmptyNUllString(str)) {
            //null值的时候返回false
            result = false;
        } else {

            str = str.trim();

            int begin = 0;
            boolean once = false;

            if (str.startsWith("+") || str.startsWith("-")) {
                if (str.length() == 1) {
                    //+ =
                    result = false;
                } else {
                    begin = 1;
                }
            }

            for (int i = begin; i < str.length(); i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    if (str.charAt(i) == '.' && once == false) {
                        once = true;
                    } else {
                        result = false;
                        break;
                    }
                }
            }

            if (str.length() == (begin + 1) && once == true) {
                //".","+.","-."
                result = false;
            }

        }

        return result;

    }

    /*******************************************************************************************************
     * 判断是否为正整数
     *******************************************************************************************************/
    public static boolean isInteger(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /*******************************************************************************************************
     * 判断字符串是否为空
     *******************************************************************************************************/
    public static boolean isEmptyNUllString(String str) {

        boolean result = false;

        if ( str == null || str.trim().isEmpty() || str.trim().equals("null") ) {
            result = true;
        }

        return result;

    }

    /*******************************************************************************************************
     * 页数处理
     *******************************************************************************************************/
    public static String doPage(String pageNo) {

        String page = "1";

        if (isEmptyNUllString(pageNo) == false && isInteger(pageNo)) {

            page = pageNo;

        }

        return page;

    }

    /*******************************************************************************************************
     * 相除以后，向上舍入为最接近的整数
     *******************************************************************************************************/
    public static int ceil(Integer a, Integer b){
        int result;

        if (a == null || b == null || b == 0 || a == 0 ) {
            result = 0;
        } else {
            double dblA = Double.valueOf(a);
            double dblB = Double.valueOf(b);
            double value = dblA / dblB;
            result = (int) Math.ceil(value);
        }

        return result;
    }

    /*******************************************************************************************************
     * 格式化日期
     *******************************************************************************************************/
    public static String formatDate(Date date, String strFormat) {

        String ymd = "";

        try {

            DateFormat format = new SimpleDateFormat(strFormat);
            ymd = format.format(date);

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

        return ymd;

    }

    /*******************************************************************************************************
     * 某一天的昨天
     *******************************************************************************************************/
    public static Date getYesterday(Date date) {

        GregorianCalendar gc=new GregorianCalendar();
        gc.setTime(date);
        gc.add(5, -1);

        return gc.getTime();

    }

    /*******************************************************************************************************
     * 某一天加减周的第几天
     *******************************************************************************************************/
    public static Date getWeekDay(Date date, int week, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, week * 7);
        cal.set(Calendar.DAY_OF_WEEK, day);

        return cal.getTime();
    }

    /*******************************************************************************************************
     * 某一天是星期几
     *******************************************************************************************************/
    public static String getWeekName(Date date, String[] weekDays) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];

    }

    /*******************************************************************************************************
     * 某一天是星期几
     *******************************************************************************************************/
    public static Date getDateAdd(Date date, int day) {

        GregorianCalendar gc=new GregorianCalendar();
        gc.setTime(date);
        gc.add(5, day);

        return gc.getTime();

    }

    /*******************************************************************************************************
     * 转换成文字类型
     *******************************************************************************************************/
    public static Date stringToDate(String strDate) {

        Date returnDate = null;

        //2016-03-12 16:53:57.000
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");

        try {

            if (strDate.length() >= 10) {
                strDate = strDate.substring(0,10);
                returnDate = simpledateformat.parse(strDate);
            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

        return returnDate;

    }

    public static String picTransform(String picUrl, String type, String storeUrl, String imgInside){

        if (type.equals("0")) {
            //自营商品
            picUrl = picUrl.replace(".", "_200x200.");
            picUrl = imgInside + picUrl;
        } else if (type.equals("1")) {
            //商城商品
            Random random=new Random();

            picUrl = picUrl.replace(".jpg_200x200.jpg", "_200x200.jpg");
            picUrl = picUrl.replace("http://wh-image01.fn.com:80/", "http://imgsvr01.beta1.fn/");
            picUrl = picUrl.replace("http://wh-image01.fn.com/", "http://imgsvr01.beta1.fn/");
            picUrl = picUrl.replace("http://10.211.64.68", "http://imgsvr01.beta1.fn");
            picUrl = picUrl.replace("img10","img"+ Integer.toString(random.nextInt(3)+16));
            String[] split = picUrl.split("\\.");
            if(split[split.length-2].indexOf("_60x60")<0){
                picUrl = picUrl.replace("."+split[split.length-1],"_200x200."+split[split.length-1]);
            }
            if(picUrl.indexOf("http") < 0){
                String[] strings = picUrl.split("/");
                if(!"pic".equals(strings[1])) {
                    picUrl="/pic"+picUrl;
                }
                String[] storeUrlSplit = storeUrl.split(";");
                int l=  storeUrlSplit.length ;
                int x =   random.nextInt(l);
                picUrl=storeUrlSplit[x]+picUrl;
            }
        }

        return picUrl;
    }

    public static String picTransformTouch(String picUrl, String type, String storeUrl, String imgInside){

        if (type.equals("0")) {
            //自营商品
            picUrl = picUrl.replace(".", "_400x400_q75.");
            picUrl = imgInside + picUrl;
        } else if (type.equals("1")) {
            //商城商品
            Random random=new Random();

            picUrl = picUrl.replace(".jpg_200x200.jpg", "_400x400_q75.jpg");
            picUrl = picUrl.replace("http://wh-image01.fn.com:80/", "http://imgsvr01.beta1.fn/");
            picUrl = picUrl.replace("http://wh-image01.fn.com/", "http://imgsvr01.beta1.fn/");
            picUrl = picUrl.replace("http://10.211.64.68", "http://imgsvr01.beta1.fn");
            picUrl = picUrl.replace("img10","img"+ Integer.toString(random.nextInt(3)+16));
            String[] split = picUrl.split("\\.");
            if(split[split.length-2].indexOf("_60x60")<0){
                picUrl = picUrl.replace("."+split[split.length-1],"_400x400_q75."+split[split.length-1]);
            }
            if(picUrl.indexOf("http") < 0){
                String[] strings = picUrl.split("/");
                if(!"pic".equals(strings[1])) {
                    picUrl="/pic"+picUrl;
                }
                String[] storeUrlSplit = storeUrl.split(";");
                int l=  storeUrlSplit.length ;
                int x =   random.nextInt(l);
                picUrl=storeUrlSplit[x]+picUrl;
            }
        }

        return picUrl;
    }

    /*******************************************************************************************************
     * 判断字符串是否相等
     *******************************************************************************************************/
    public static boolean strEquals(String str, String compare) {

        boolean result;

        if (isEmptyNUllString(str) && isEmptyNUllString(compare)) {
            result = true;
        } else if (!isEmptyNUllString(str) && !isEmptyNUllString(compare)) {
            result = str.equals(compare);
        } else {
            result = false;
        }

        return result;

    }

    /*******************************************************************************************************
     * 判断商品类型名称
     * type:0：自营商品，1：商城商品
     * itemType:自营商品:1:借货 2:采购 3:转单  4:门店 ;商城商品:0:普通 1:跨境 2:虚拟
     * isFreshPord:是否是生鲜，1:-是，0-不是
     *******************************************************************************************************/
    public static String itemTypeName(Integer type, Integer itemType, Integer isFreshPord) {

        String result = "";

        if (type == 0) {
            //0：自营商品

            //1:借货 2:采购 3:转单  4:门店
            if (itemType == 3 && isFreshPord != 1) {
                result = "商家直送";
            } else {
                result = "自营";
            }

        } else if (type == 1) {
            //1：商城商品

            if (itemType == 0) {
                result = "商城";
            } else if (itemType == 1) {
                result = "环球购";
            }

        }

        return result;

    }

    /*******************************************************************************************************
     * 判断行销活动名称
     * type
     *******************************************************************************************************/
    public static String activityTypeName(String type) {

        String result = "";

        if (strEquals(type,"1")) {

            result = "满减";

        } else if (strEquals(type,"2") || strEquals(type,"6")) {

            result = "折扣";

        } else if (strEquals(type,"3") || strEquals(type,"7")) {

            result = "满赠";

        } else if (strEquals(type,"5") || strEquals(type,"8") || strEquals(type,"9") || strEquals(type,"10")) {

            result = "优惠";

        } else if (strEquals(type,"11")) {

            result = "换购";

        }

        return result;

    }

    /*******************************************************************************************************
     * 取得大区ｉｄ
     * type
     *******************************************************************************************************/
    public static String getPgSeq(String province) {

        String result = "";

        String[] CPG1 = new String[]{"CS000016","CS000017","CS000018","CS000019"};
        String[] CPG6 = new String[]{"CS000004","CS000020","CS000022","CS000023","CS000024","CS000025","CS000032"};
        String[] CPG2 = new String[]{"CS000026","CS000027","CS000028","CS000029","CS000030","CS000031"};
        String[] CPG7 = new String[]{"CS000001","CS000002","CS000003","CS000021"};
        String[] CPG8 = new String[]{"CS000005","CS000006","CS000007","CS000008","CS000009","CS000010","CS000011","CS000012","CS000013","CS000014","CS000015"};
        String[] CPG9 = new String[]{"CS000033"};

        int findKind = Arrays.binarySearch(CPG1, province);
        if (findKind >= 0) {
            result = "CPG1";
            return result;
        }
        findKind = Arrays.binarySearch(CPG6, province);
        if (findKind >= 0) {
            result = "CPG6";
            return result;
        }
        findKind = Arrays.binarySearch(CPG2, province);
        if (findKind >= 0) {
            result = "CPG2";
            return result;
        }
        findKind = Arrays.binarySearch(CPG7, province);
        if (findKind >= 0) {
            result = "CPG7";
            return result;
        }
        findKind = Arrays.binarySearch(CPG8, province);
        if (findKind >= 0) {
            result = "CPG8";
            return result;
        }
        findKind = Arrays.binarySearch(CPG9, province);
        if (findKind >= 0) {
            result = "CPG9";
            return result;
        }

        return result;

    }

}
