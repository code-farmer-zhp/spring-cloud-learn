package com.feiniu.member.log;


import com.feiniu.member.util.HttpRequestUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.log4j.Logger;


public final class CustomLog {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private Logger log;

    private Class clazz;

    private CustomLog(Logger log, Class clazz) {
        this.log = log;
        this.clazz = clazz;
    }

    public static CustomLog getLogger(Class clazz) {
        return new CustomLog(Logger.getLogger(clazz), clazz);
    }

    public void info(String message) {
        log.info("<" + HttpRequestUtils.getRequestNo() + ">    " + message);
    }

    public void error(String message) {
        log.error("<" + HttpRequestUtils.getRequestNo() + ">    " + message);
    }

    public void error(String message, Throwable e) {
        log.error("<" + HttpRequestUtils.getRequestNo() + ">    " + message, e);
    }
}
