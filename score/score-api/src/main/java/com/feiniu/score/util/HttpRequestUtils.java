package com.feiniu.score.util;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public class HttpRequestUtils {

    private static ThreadLocal<String> threadLocal = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return UUID.randomUUID().toString();
        }
    };

    /*
     * 生成请求号
     */
    public static String generateRequestNo(HttpServletRequest request) {
        String str = UUID.randomUUID().toString();
        threadLocal.set(str);

        return str;
    }

    /*
     * 移除请求号
     */
    public static void removeRequestNo() {
        threadLocal.remove();
    }

    /*
     * 获取请求号
     */
    public static String getRequestNo() {
        return threadLocal.get();
    }

    public static void set(String requestNo) {
        threadLocal.set(requestNo);
    }
}
