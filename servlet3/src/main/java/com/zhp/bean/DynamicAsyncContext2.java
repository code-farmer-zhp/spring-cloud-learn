package com.zhp.bean;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class DynamicAsyncContext2 {

    @Value("${asyncTimeoutInSeconds}")
    private long asyncTimeoutInSeconds;

    @Value("${poolSize}")
    private String poolSize;

    @Value("${keepAliveTimeInSeconds}")
    private int keepAliveTimeInSeconds;

    @Value("${queueCapacity}")
    private int queueCapacity;


    public void submitFuture(HttpServletRequest request) {
        final String uri = request.getRequestURI();
        final Map<String, String[]> params = request.getParameterMap();
        final AsyncContext asyncContext = request.startAsync();

        asyncContext.getRequest().setAttribute("uri", uri);
        asyncContext.getRequest().setAttribute("params", params);
        asyncContext.setTimeout(asyncTimeoutInSeconds * 1000);

        new MyService(asyncContext).queue();

    }


}
