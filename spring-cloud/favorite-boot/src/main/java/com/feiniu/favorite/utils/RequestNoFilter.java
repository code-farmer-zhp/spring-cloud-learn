package com.feiniu.favorite.utils;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName="RequestNoFilter",urlPatterns="/*")
@Component
public class RequestNoFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        RequestNoGen.setNo();
        filterChain.doFilter(servletRequest, servletResponse);
        RequestNoGen.removeNo();
    }

    @Override
    public void destroy() {

    }
}
