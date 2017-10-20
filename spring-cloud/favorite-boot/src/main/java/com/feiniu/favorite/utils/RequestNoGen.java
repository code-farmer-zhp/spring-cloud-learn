package com.feiniu.favorite.utils;

import java.util.UUID;

public class RequestNoGen {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setNo() {
        UUID uuid = UUID.randomUUID();
        threadLocal.set(uuid.toString());
    }
    public static void setNo(String requestNo) {
        threadLocal.set(requestNo);
    }
    public static String getNo(){
        return threadLocal.get();
    }

    public static void removeNo(){
        threadLocal.remove();
    }
}
