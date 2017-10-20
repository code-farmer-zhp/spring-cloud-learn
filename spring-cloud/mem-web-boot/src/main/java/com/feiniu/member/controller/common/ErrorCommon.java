package com.feiniu.member.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.log.CustomLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

@Controller
public class ErrorCommon {
	private CustomLog log= CustomLog.getLogger(ErrorCommon.class);
	@Value("${error.url}")
	private String errorUrl;

	@Value("${m.error.url}")
	private String mErrorUrl;

	@Value("${m.my.server}")
	private String mMyServer;

	@RequestMapping(value="/error",method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request){
		logErrInfo(request);

		String serverName = request.getServerName();
		log.error("serverName="+serverName);
		//触屏报错调到触屏404页面
		if (serverName.equals(mMyServer)) {
			return new ModelAndView("redirect:" + mErrorUrl);
		}else {
			String[] errorUrlArray = errorUrl.split(",");
			Random random = new Random();
			int n = random.nextInt(errorUrlArray.length);
			return new ModelAndView("redirect:" + errorUrlArray[n]);
		}
	}

	/*
     * 打印错误日志信息
     */
	private void logErrInfo(HttpServletRequest request){
		JSONObject jsonObj = new JSONObject();
		// 该属性给出状态码(如404, 500, 505等)，状态码可被存储，并在存储为 java.lang.Integer 数据类型后可被分析。
		jsonObj.put("status_code", request.getAttribute("javax.servlet.error.status_code"));
		// 该属性给出确切错误消息的信息，信息可被存储，并在存储为 java.lang.String 数据类型后可被分析。
		jsonObj.put("message", request.getAttribute("javax.servlet.error.message"));
		// 该属性给出有关 URL 调用 Servlet 的信息，信息可被存储，并在存储为 java.lang.String 数据类型后可被分析。
		jsonObj.put("request_uri", request.getAttribute("javax.servlet.error.request_uri"));
		// 该属性给出 Servlet 的名称，名称可被存储，并在存储为 java.lang.String 数据类型后可被分析。
		jsonObj.put("servlet_name", request.getAttribute("javax.servlet.error.servlet_name"));

		log.error( jsonObj.toJSONString());
		// 打印错误堆栈信息
		log.error(jsonObj.toJSONString(), (Exception)request.getAttribute("javax.servlet.error.exception"));
	}
}
