package com.feiniu.member.util;

import javax.net.ssl.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class HttpRequestUtil {

    private static String loginCookieName;
    private static String connectTimeout;
    private static String readTimeout;


    public static String getLoginCookieName() {
        return loginCookieName;
    }

    public static void setLoginCookieName(String loginCookieName) {
        HttpRequestUtil.loginCookieName = loginCookieName;
    }

    public static String getConnectTimeout() {
        return connectTimeout;
    }

    public static void setConnectTimeout(String connectTimeout) {
        HttpRequestUtil.connectTimeout = connectTimeout;
    }

    public static String getReadTimeout() {
        return readTimeout;
    }

    public static void setReadTimeout(String readTimeout) {
        HttpRequestUtil.readTimeout = readTimeout;
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param, String cookies) {
        String result = "";
        BufferedReader in = null;
        try {

            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            //System.out.println("GET请求的URL为：" + urlNameString);
            // 打开和URL之间的连接
            if(realUrl.openConnection() instanceof HttpsURLConnection){
                HttpsURLConnection connection =(HttpsURLConnection)realUrl.openConnection();
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                        new java.security.SecureRandom());
                //设置https相关属性
                connection.setSSLSocketFactory(sc.getSocketFactory());
                connection.setHostnameVerifier(new TrustAnyHostnameVerifier());
                setConnectProperty(connection,cookies);
                connection.connect();
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "UTF-8"));
            }else{
                HttpURLConnection connection =(HttpURLConnection)realUrl.openConnection();
                setConnectProperty(connection,cookies);
                connection.connect();
                // 定义 BufferedReader输入流来读取URL的响应
                in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "UTF-8"));
            }

            String line;
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            result = result.substring(0, result.length() - 1);
            //System.out.println("获取的结果为：" + result);
        } catch (Exception e) {
            //System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static void setConnectProperty(URLConnection connection, String cookies){
        connection.setDoOutput(true);
        connection.setReadTimeout(Integer.parseInt(readTimeout));
        connection.setConnectTimeout(Integer.parseInt(connectTimeout));
        // 设置通用的请求属性
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        connection.setRequestProperty("Cookie", cookies);
        // 建立实际的连接
           /*
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();

            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
           */
    }
    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            //System.out.println("Post请求的URL为：" + realUrl);
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setReadTimeout(Integer.parseInt(readTimeout));
            conn.setConnectTimeout(Integer.parseInt(connectTimeout));
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            //System.out.println("获取的结果为：" + result);
        } catch (Exception e) {
            //System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //System.out.println(result);
        return result;
    }


    /*
     * 获取ip地址
     */
    public static String getRemoteAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String assemblyJsonParam(Map<String, Object> params) {
        String paramStr = "";

        if (params.size() > 0) {
            // 获取参数列表组成参数字符串
            for (String key : params.keySet()) {
                paramStr += key + ":\"" + params.get(key) + "\",";
            }
            //去除最后一个","
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }
        paramStr = "data=" + "{" + paramStr + "}";
        return paramStr;
    }

    public static String assemblyFormParam(Map<String, Object> params) {
        String paramStr = "";

        if (params.size() > 0) {
            // 获取参数列表组成参数字符串
            for (String key : params.keySet()) {
                paramStr += key + "=" + params.get(key) + "&";
            }
            //去除最后一个","
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }
        return paramStr;
    }

    public static String assemblyJsonParam(Map<String, Object> params, String requestVal) {
        String paramStr = "";

        if (params.size() > 0) {
            // 获取参数列表组成参数字符串
            for (String key : params.keySet()) {
                paramStr += key + ":\"" + params.get(key) + "\",";
            }
            //去除最后一个","
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }
        paramStr = requestVal + "=" + "{" + paramStr + "}";
        return paramStr;
    }

    // 这是组装cookie
    public static String assemblyCookie(Cookie[] cookies) {
        if (cookies == null) {
            return "";
        }
        StringBuffer sbu = new StringBuffer();
        for (Cookie cookie : cookies) {
            sbu.append(cookie.getName()).append("=").append(cookie.getValue())
                    .append(";");
        }
        if (sbu.length() > 0)
            sbu.deleteCharAt(sbu.length() - 1);
        return sbu.toString();
    }

    public static String assemblyLoginCookie(Cookie[] cookies) {
        if (cookies == null) {
            return "";
        }
        StringBuffer sbu = new StringBuffer();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(loginCookieName)) {
                sbu.append(cookie.getName()).append("=").append(cookie.getValue())
                        .append(";");
            }
        }
        if (sbu.length() > 0)
            sbu.deleteCharAt(sbu.length() - 1);
        return sbu.toString();
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("memGuid", "00004A51-DA01-2C45-216B-309AEEAA6942");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.MONDAY, calendar.get(Calendar.MONDAY) - 1);
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.get(Calendar.DAY_OF_MONTH) + 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        params.put("startDate", sdf.format(calendar.getTime()));
        params.put("endDate", sdf.format(new Date()));
        HttpRequestUtil m = new HttpRequestUtil();
        //  String paramData="data="+URLEncoder.encode("{"+assemblyParam(params)+"}");
        //  System.out.println(paramData);
        String paramData = assemblyJsonParam(params);
        // System.out.println(paramData);
        // m.sendPost("http://10.202.185.165:8080/FeiniuScore/growth/mem/queryLevelCount", paramData,"");

        params.put("pageNo", 1);
        params.put("pageSize", 10);
        String getGrowthDetailList = HttpRequestUtil.sendPost("http://10.202.185.165:8080/FeiniuScore/growth/mem/queryLevel", HttpRequestUtil.assemblyJsonParam(params));

//              Map<String,Object> params2 = new HashMap<String, Object>();
//              params2.put("mem_guid", "00004A51-DA01-2C45-216B-309AEEAA6942");
//              System.out.println(assemblyFormParam(params2));
//              String vouchersInfo=HttpRequestUtil.sendPost("http://member-api.beta1.fn/voucher_api/get_bonus_vouchers_num", HttpRequestUtil.assemblyFormParam(params2),"");
//              JSONObject jo =JSONObject.parseObject(vouchersInfo);
//              System.out.println(jo.get("Body"));

    }
    

}