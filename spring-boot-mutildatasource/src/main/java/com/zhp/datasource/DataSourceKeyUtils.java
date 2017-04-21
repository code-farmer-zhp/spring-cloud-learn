package com.zhp.datasource;


public class DataSourceKeyUtils {

    private final static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void set(String dataSourceName) {
        threadLocal.set(dataSourceName);
    }

    public static String get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
