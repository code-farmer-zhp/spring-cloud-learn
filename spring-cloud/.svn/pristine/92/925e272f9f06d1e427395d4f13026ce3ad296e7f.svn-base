package com.feiniu;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/*
*@author: Max
*@mail:1069905071@qq.com 
*@time:2017/7/31 16:27 
*/
@RestController
public class SayHello {
    private OutputSource source;

    @Autowired
    public SayHello(OutputSource source) {
        this.source = source;
    }
    @RequestMapping(method = RequestMethod.GET, value = "sayHello/{message}")
    public void sayHello(@PathVariable("message")String message) {
        JSONObject returnObj=new JSONObject();
        returnObj.put("message",message);
        source.output().send(MessageBuilder.withPayload(returnObj.toJSONString()).setHeader("Content-Type","application/json;charset=UTF-8").build());
    }
}
