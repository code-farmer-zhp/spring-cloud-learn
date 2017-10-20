package com.feiniu.member.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.UUID;

public class HttpRequestUtils {
	
	private static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
	
	/*
	 * 生成请求号
	 */
	public static String generateRequestNo(HttpServletRequest request){
		String str = UUID.randomUUID().toString();
		threadLocal.set(str);
		
		return str;
	}
	
	/*
	 * 移除请求号
	 */
	public static void removeRequestNo(){
		threadLocal.remove();
	}
	
	/*
	 * 获取请求号
	 */
	public static String getRequestNo(){
		return threadLocal.get();
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
	
	/*
	 * 是否为ajax请求
	 */
	public static boolean isAjaxRequest(HttpServletRequest request){
		return StringUtils.isNotBlank(request.getHeader("X-Requested-With"));
	}
	
	public static String getParameterKeyValue(HttpServletRequest request) {
		StringBuffer sbf = new StringBuffer();
		Enumeration<String> paraNames = request.getParameterNames();
		for (Enumeration<String> e = paraNames; e.hasMoreElements();) {
			String thisName = e.nextElement().toString();
			sbf.append(thisName + "=" + request.getParameter(thisName) + "&");
		}
		return sbf.toString();
	}
	
	public static String getParaNoPassKeyValue(HttpServletRequest request) {
		StringBuffer sbf = new StringBuffer();
		Enumeration<String> paraNames = request.getParameterNames();
		for (Enumeration<String> e = paraNames; e.hasMoreElements();) {
			String thisName = e.nextElement().toString();
			if(!"pass".equals(thisName)){
				sbf.append(thisName + "=" + request.getParameter(thisName) + "&");
			}
		}
		return sbf.toString();
	}
	
}
