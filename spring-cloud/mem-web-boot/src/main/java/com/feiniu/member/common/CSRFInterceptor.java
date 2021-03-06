package com.feiniu.member.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class CSRFInterceptor  implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory
			.getLogger(CSRFInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
		if (!request.getMethod().equalsIgnoreCase(
				WebContentGenerator.METHOD_POST)) {
			// 忽略非POST请求
			return true;
		}
//		if ("POST".equalsIgnoreCase(request.getMethod())) {
//			String CsrfToken = CSRFTokenManager.getTokenFromRequest(request);
//			if (CsrfToken == null
//					|| !CsrfToken.equals(request.getSession().getAttribute(
//							CSRFTokenManager.CSRF_TOKEN_FOR_SESSION_ATTR_NAME))) {
//				return false;
//			}
//		}
		if ("POST".equalsIgnoreCase(request.getMethod())) {
		    logger.info("request的controller路径"+request.getServletPath());
			if(request.getServletPath()==null || request.getServletPath().equals("")|| request.getServletPath().contains("/refresh")){
				return true;
			}
		   if(!(request.getServletPath().contains("uploadPic"))){
			String CsrfToken = null;
			CsrfToken= CSRFTokenManager.getTokenFromRequest(request);
			if(CsrfToken==null){
				try {
					CsrfToken = CSRFTokenManager
							.getMulTokenFromRequest(request);
				} catch (Exception e) {
					logger.error("CsrfToken: "+CsrfToken+"  "+e);
				}
			}
			Cookie[] cookies = request.getCookies();
			String token = null;
			if(cookies==null||cookies.length<1){
				return false;
			}
				for (Cookie c : cookies) {
					if (c.getName().equals(CSRFTokenManager.CSRF_PARAM_NAME)) {
						token = c.getValue();
					}
				}

				if (CsrfToken == null || token == null
						|| !CsrfToken.equals(token)) {
					logger.error("CsrfToken: "+CsrfToken+"  token: "+token);
					return false;
				}
			}}
		return true;
	}

	private String getCurrentUrl(HttpServletRequest request) {
		String currentUrl = request.getRequestURL().toString();
		if (!StringUtils.isEmpty(request.getQueryString())) {
			currentUrl += "?" + request.getQueryString();
		}

		return currentUrl;
	}

	@Override
	public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
		if (modelAndView != null) {
			String token = CSRFTokenManager.getTokenForCookie(request);
			if (null == token) {
				token = UUID.randomUUID().toString();
			}
			Cookie tokenCookie = new Cookie(CSRFTokenManager.CSRF_PARAM_NAME,
					token);
			tokenCookie.setMaxAge(7200);
			tokenCookie.setPath("/");	
			response.addCookie(tokenCookie);
			request.setAttribute(CSRFTokenManager.CSRF_PARAM_NAME, token);
			//modelAndView.addObject(CSRFTokenManager.CSRF_PARAM_NAME, token);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
}