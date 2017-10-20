package com.feiniu.member.util;

import com.feiniu.member.log.CustomLog;

import java.util.Calendar;
import java.util.Date;


public class DateUtil {

    public static final CustomLog logger = CustomLog.getLogger(DateUtil.class);


    /**
     * 得到两日期相差几个月
     */
    public static long getMonth(Date startDate1, Date endDate1) {
        long monthday;
        try {

            Calendar starCal = Calendar.getInstance();
            starCal.setTime(startDate1);

            int sYear = starCal.get(Calendar.YEAR);
            int sMonth = starCal.get(Calendar.MONTH);
            int sDay = starCal.get(Calendar.DATE);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(endDate1);
            int eYear = endCal.get(Calendar.YEAR);
            int eMonth = endCal.get(Calendar.MONTH);
            int eDay = endCal.get(Calendar.DATE);

            monthday = ((eYear - sYear) * 12 + (eMonth - sMonth));

            if (sDay < eDay) {
                monthday = monthday + 1;
            }
            return monthday;
        } catch (Exception e) {
            logger.error("获取相差月数失败");
            monthday = 0;
        }
        return monthday;
    }
}

