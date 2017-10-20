package com.feiniu.member.common;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * A manager for the CSRF token for a given session. The
 * {@link #getTokenForSession(HttpSession)} should used to obtain the token
 * value for the current session (and this should be the only way to obtain the
 * token value).
 * ***/

public final class CSRFTokenManager {

	/**
	 * The token parameter name
	 */
	public static final String CSRF_PARAM_NAME = "CSRF_TOKEN";

	/**
	 * The location on the session which stores the token
	 */
	public static final String CSRF_TOKEN_FOR_SESSION_ATTR_NAME = CSRFTokenManager.class
			.getName() + ".tokenval";

	public static String getTokenForSession(HttpSession session) {
		String token = null;

		// I cannot allow more than one token on a session - in the case of two
		// requests trying to
		// init the token concurrently
		synchronized (session) {
			token = (String) session
					.getAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME);
			if (null == token) {
				token = UUID.randomUUID().toString();
				session.setAttribute(CSRF_TOKEN_FOR_SESSION_ATTR_NAME, token);
			}
		}
		return token;
	}

	public static String getTokenForCookie(HttpServletRequest request) {
		String token = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie c : cookies) {
				if (c.getName().equals(CSRF_PARAM_NAME)) {
					token = c.getValue();
				}
			}
		}
		return token;
	}
	
	/**
	 * Extracts the token value from the session
	 * 
	 * @param request
	 * @return
	 */
	public static String getTokenFromRequest(HttpServletRequest request) {
		return request.getParameter(CSRF_PARAM_NAME);
	}
	
	public static String getMulTokenFromRequest(HttpServletRequest request) {
		  boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		  if(isMultipart){
			  MultipartHttpServletRequest multipartRequest   =   (MultipartHttpServletRequest)   request;
			  return multipartRequest.getParameter(CSRF_PARAM_NAME);   
		  }
		  else{
			  return request.getParameter(CSRF_PARAM_NAME);
		  }
	 }
	
	private CSRFTokenManager() {
	};

}