package com.zhp.bean;


import javax.servlet.AsyncContext;

public abstract class CanceledCallable implements Runnable {

    private AsyncContext asyncContext;

    public CanceledCallable(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
    }

    public AsyncContext getAsyncContext() {
        return asyncContext;
    }
}
