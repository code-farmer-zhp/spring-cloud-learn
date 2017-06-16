package com.zhp.controller;

import com.zhp.bean.DynamicAsyncContext;
import com.zhp.bean.DynamicAsyncContext2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MyController {

    @Autowired
    private DynamicAsyncContext asyncContext;

    @Autowired
    private DynamicAsyncContext2 asyncContext2;

    @RequestMapping("/book")
    @ResponseBody
    public void getBook(HttpServletRequest request, Long skuid, Integer cat1, Integer cat2) {
        asyncContext2.submitFuture(request);
    }
}
