package com.feiniu.score.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.feiniu.score.util.HttpRequestUtils;

public class CommonFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 生成请求号
		HttpRequestUtils.generateRequestNo((HttpServletRequest) request);
		chain.doFilter(request, response);
		// 删除请求号
		HttpRequestUtils.removeRequestNo();
	}

	@Override
	public void destroy() {
		
	}

}
