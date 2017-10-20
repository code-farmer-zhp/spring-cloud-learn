package com.feiniu.member.common;

import com.feiniu.member.util.HttpRequestUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@WebFilter("CommonFilter")
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
