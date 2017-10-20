package com.feiniu;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.util.StringUtils;

/*
*@author: Max
*@mail:1069905071@qq.com
*@time:2017/7/31 10:54
*/
@EnableBinding(MyMQInterface.class)
public class MyMQReceiver {
    @Autowired
    private MyMQInterface myMQInterface;

    @StreamListener(MyMQInterface.INPUT)
    public void process(String message) {
        System.out.print("do service about message here: "+ (StringUtils.isEmpty(message)?"":message));
//        JSONObject returnObj=new JSONObject();
//        returnObj.put("message",message);
//        return MessageBuilder.withPayload(returnObj.toJSONString()).setHeader("Content-Type","application/json;charset=UTF-8").build();
//        Message<String> msg = MessageBuilder.withPayload(returnObj.toJSONString()).build();
//        myMQInterface.outputChannel().send(msg);
    }
}
