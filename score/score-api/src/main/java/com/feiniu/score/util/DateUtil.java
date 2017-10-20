package com.feiniu.score.util;

import com.feiniu.score.common.ResultCode;
import com.feiniu.score.exception.ScoreException;
import com.feiniu.score.log.CustomLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * 日期工具类
 */
public class DateUtil {
    private static final CustomLog log = CustomLog.getLogger(DateUtil.class);


    /**
     * 获取当前日期
     */
    public static Date getNowDate() {
        return  getTimeOfZeroDiffToday(0);
    }

    /**
     * 获取当前时间
     */
    public static String getNow(String formatStr) {
        return  getFormatDate(new Date(), formatStr);
    }

    public static String getFormatDate(Date date, String formatStr) {
        String returnStr = null;
        if (date != null) {
            try {
                returnStr = FastDateFormat.getInstance(formatStr).format(date);
            } catch (Exception e) {
                log.error("日期格式化错误,date=" + date + ",formatStr=" + formatStr, "getFormatDate");
            }
        }
        return returnStr;
    }

    public static Date getDateFromStr(String dateStr, String formatStr) {
        Date returnStr = null;
        if (StringUtils.isNotBlank(dateStr)) {
            try {
                returnStr = FastDateFormat.getInstance(formatStr).parse(dateStr);
            } catch (Exception e) {
                log.error("日期格式化错误,date=" + dateStr + ",formatStr=" + formatStr, "getFormatDate");
            }
        }
        return returnStr;
    }

    public static String getFormatDateFromStr(String inStr, String inFormatStr, String outFormatStr) {
        String returnStr = null;
        if (StringUtils.isNotBlank(inStr)) {
            try {
                Date date = FastDateFormat.getInstance(inFormatStr).parse(inStr);
                returnStr = FastDateFormat.getInstance(outFormatStr).format(date);
            } catch (Exception e) {
                log.error("日期格式化错误,date=" + inStr + ",inFormatStr=" + inFormatStr + ",outFormatStr" + outFormatStr, "getFormatDate");
            }
        }
        return returnStr;
    }
    /*
    获取今天相隔i天的0点整
     */
    public static Date getTimeOfZeroDiffToday(long i) {
        long zero=(System.currentTimeMillis()+TimeZone.getDefault().getRawOffset())/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
        return new Date(zero+i*24*60*60*1000);
    }

    public static Date getTimeOfZeroDiffDate(Date date, long i) {
        long zero=(date.getTime()+TimeZone.getDefault().getRawOffset())/(1000*3600*24)*(1000*3600*24)-TimeZone.getDefault().getRawOffset();
        return new Date(zero+i*24*60*60*1000);
    }

    public static Date getTimeOf235959Date(Date date) {
        long zero=(date.getTime()+TimeZone.getDefault().getRawOffset())/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();
        long twelve=zero+24*60*60*1000-1000;//今天23点59分59秒的毫秒数
        return new Date(twelve);
    }

    public static Date getTimeAddSecond(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, i);
        return cal.getTime();
    }

    public static Long getSecondsUntilTomorrowZero() {
        Date now = new Date();
        Date TimeOf0 = getTimeOfZeroDiffToday(1);
        return (TimeOf0.getTime() - now.getTime()) / 1000;
    }

    /**
     * 获取当前日期
     */
    public static Date getNowDate2() {
        FastDateFormat onlyDateFormat = FastDateFormat.getInstance("yyyy-MM-dd");
        String format = onlyDateFormat.format(new Date());
        Date date;
        try {
            date = onlyDateFormat.parse(format);
        } catch (ParseException e) {
            throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "日期转换异常。");
        }
        return date;
    }

    public static Date getNowDate3() {
        SimpleDateFormat onlyDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = onlyDateFormat.format(new Date());
        Date date;
        try {
            date = onlyDateFormat.parse(format);
        } catch (ParseException e) {
            throw new ScoreException(ResultCode.RESULT_RUN_TIME_EXCEPTION, "日期转换异常。");
        }
        return date;
    }
    /*
    获取今天相隔i天的0点整
     */
    public static Date getTimeOfZeroDiffToday2(int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, i);
        return cal.getTime();
    }

    public static Date getTimeOfZeroDiffDate2(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, i);
        return cal.getTime();
    }

    public static Date getTimeOf235959Date2(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static void main(String[] args) {
        long beginTime=System.currentTimeMillis();
        Random random=new Random();
        /*for(int i=0;i<24;i++){
            for(int j=0;j<60;j=j+random.nextInt(6)) {
                for(int k=0;k<60;k=k+random.nextInt(6)) {
                    for(int l=0;l<1000;l=l+random.nextInt(6)) {
                        for(int m=0;m<10000;m=m+random.nextInt(20)) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            cal.set(Calendar.HOUR_OF_DAY, i);
                            cal.set(Calendar.MINUTE, j);
                            cal.set(Calendar.SECOND, k);
                            cal.set(Calendar.MILLISECOND, l);
                            cal.add(Calendar.DATE, m);
                            //System.out.println(cal.getTime());
                            Assert.isTrue(getNowDate().equals(getNowDate2()));
                            Assert.isTrue(getTimeOfZeroDiffToday(m).equals(getTimeOfZeroDiffToday2(m)));
                            Assert.isTrue(getTimeOfZeroDiffDate(cal.getTime(), m).equals(getTimeOfZeroDiffDate2(cal.getTime(), m)));
                            Assert.isTrue(getTimeOf235959Date(cal.getTime()).equals(getTimeOf235959Date2(cal.getTime())));
                        }
                    }
                }
            }
        }*/
        /*for(int l=0;l<10000000;l=l+random.nextInt(86400)) {
            for (int m = 0; m < 10000; m = m + random.nextInt(20)) {
                //System.out.println(cal.getTime());
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.SECOND, l);
                Assert.isTrue(getNowDate().equals(getNowDate2()));
                Assert.isTrue(getTimeOfZeroDiffToday(m).equals(getTimeOfZeroDiffToday2(m)));
                Assert.isTrue(getTimeOfZeroDiffDate(cal.getTime(), m).equals(getTimeOfZeroDiffDate2(cal.getTime(), m)));
                Assert.isTrue(getTimeOf235959Date(cal.getTime()).equals(getTimeOf235959Date2(cal.getTime())));
            }
        }*/
        for(int l=0;l<100000;l=l+1) {
            for (int m = 0; m < 1000; m = m +1) {
                getNowDate();
            }
        }
        System.out.println("getNowDate cost  "+(System.currentTimeMillis()-beginTime));
        beginTime=System.currentTimeMillis();
        for(int l=0;l<10000;l=l+1) {
            for (int m = 0; m < 1000; m = m +1) {
                getNowDate2();
            }
        }
        System.out.println("getNowDate2 cost  "+(System.currentTimeMillis()-beginTime));
        beginTime=System.currentTimeMillis();
        for(int l=0;l<10000;l=l+1) {
            for (int m = 0; m < 1000; m = m +1) {
                getNowDate3();
            }
        }
        System.out.println("getNowDate3 cost  "+(System.currentTimeMillis()-beginTime));
    }
}
