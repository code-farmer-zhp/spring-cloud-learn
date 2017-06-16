package com.zhp.bean;


import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class MyService extends HystrixCommand<Void> {

    private final AsyncContext asyncContext;

    public MyService(AsyncContext asyncContext) {
        super(HystrixCommandGroupKey.Factory.asKey("MyServiceGroup"));
        this.asyncContext = asyncContext;
    }

    @Override
    protected Void run() throws Exception {
        callback(asyncContext, "MyService", null, null);
        return null;
    }

    @Override
    protected Void getFallback() {
        doError(asyncContext);
        return null;
    }


    private void callback(AsyncContext asyncContext, Object result, String uri, Map<String, String[]> params) {
        HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
        try {
            write(response, JSONObject.toJSONString(result));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println(uri + ":" + params);
            e.printStackTrace();
        } finally {
            asyncContext.complete();
        }
    }

    private void write(HttpServletResponse response, String result) {
        try {
            PrintWriter writer = response.getWriter();
            writer.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void doError(AsyncContext asyncContext) {
        if (asyncContext != null) {
            try {
                ServletRequest request = asyncContext.getRequest();
                String uri = (String) request.getAttribute("uri");
                Map params = (Map) request.getAttribute("params");
                System.out.println(uri + ":" + params);
            } catch (Exception e) {
                //ignore
            }
            try {
                HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } finally {
                asyncContext.complete();
            }
        }
    }
}
