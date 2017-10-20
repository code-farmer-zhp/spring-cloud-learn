package com.feiniu.score.log;


import org.apache.log4j.Logger;


public final class CustomLog {

    private Logger log;

    private Class clazz;

    private CustomLog(Logger log, Class clazz) {
        this.log = log;
        this.clazz = clazz;
    }

    public static CustomLog getLogger(Class clazz) {

        return new CustomLog(Logger.getLogger(clazz), clazz);
    }

    public void info(String message, String method) {
        log.info(method + ":" + message);
    }

    public void info(String message) {
        log.info(message);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, String method) {
        log.error(method + ":" + message);
    }

    public void error(String message, String method, Exception e) {
        log.error(method + ":" + message, e);
    }

    public void error(String message, Exception e) {
        log.error(message, e);
    }
}
