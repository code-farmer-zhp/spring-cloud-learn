package com.zhp.bean;


import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class DynamicAsyncContext implements InitializingBean {

    @Value("${asyncTimeoutInSeconds}")
    private long asyncTimeoutInSeconds;

    @Value("${poolSize}")
    private String poolSize;

    @Value("${keepAliveTimeInSeconds}")
    private int keepAliveTimeInSeconds;

    @Value("${queueCapacity}")
    private int queueCapacity;

    private ThreadPoolExecutor executor;

    private BlockingQueue<Runnable> blockingQueue;

    private AsyncListener asyncListener;

    public void afterPropertiesSet() throws Exception {
        String[] split = poolSize.split("-");
        int corePoolSize = Integer.valueOf(split[0]);
        int maxNumPoolSize = Integer.valueOf(split[1]);

        blockingQueue = new LinkedBlockingDeque<>(queueCapacity);

        executor = new ThreadPoolExecutor(corePoolSize, maxNumPoolSize,
                keepAliveTimeInSeconds, TimeUnit.SECONDS, blockingQueue);
        executor.allowCoreThreadTimeOut(true);
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (r instanceof CanceledCallable) {
                    CanceledCallable cc = (CanceledCallable) r;
                    AsyncContext asyncContext = cc.getAsyncContext();
                    doError(asyncContext);
                }
            }
        });

        asyncListener = new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent asyncEvent) throws IOException {
                System.out.println("完成");
            }

            @Override
            public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                AsyncContext asyncContext = asyncEvent.getAsyncContext();
                doError(asyncContext);
            }

            @Override
            public void onError(AsyncEvent asyncEvent) throws IOException {
                AsyncContext asyncContext = asyncEvent.getAsyncContext();
                doError(asyncContext);
            }

            @Override
            public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                System.out.println("开始");
            }
        };
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


    public void submitFuture(HttpServletRequest request, final Callable<Object> task) {
        final String uri = request.getRequestURI();
        final Map<String, String[]> params = request.getParameterMap();
        final AsyncContext asyncContext = request.startAsync();

        asyncContext.getRequest().setAttribute("uri", uri);
        asyncContext.getRequest().setAttribute("params", params);
        asyncContext.setTimeout(asyncTimeoutInSeconds * 1000);
        if (asyncListener != null) {
            asyncContext.addListener(asyncListener);
        }

        executor.execute(new CanceledCallable(asyncContext) {
            @Override
            public void run() {
                try {
                    Object call = task.call();
                    callback(asyncContext, call, uri, params);
                } catch (Exception e) {
                    doError(asyncContext);
                }
            }
        });
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


}
