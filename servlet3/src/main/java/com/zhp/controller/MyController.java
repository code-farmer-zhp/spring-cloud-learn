package com.zhp.controller;

import com.zhp.bean.DynamicAsyncContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Callable;

@Controller
public class MyController {

    @Autowired
    private DynamicAsyncContext asyncContext;

    @RequestMapping("/book")
    @ResponseBody
    public void getBook(HttpServletRequest request, Long skuid, Integer cat1, Integer cat2) {
        asyncContext.submitFuture(request, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return "yes-zhp";
            }
        });
    }
}
