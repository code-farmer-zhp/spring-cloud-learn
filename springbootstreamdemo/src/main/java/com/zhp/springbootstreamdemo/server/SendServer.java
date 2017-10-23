package com.zhp.springbootstreamdemo.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Service
public class SendServer {

    @Autowired
    private SendChannel sendChannel;

    @Autowired
    @Qualifier("mqScoreOutput")
    private MessageChannel mqScoreOutput;

    public void send1() {
        User u = new User();
        u.setId(1L);
        u.setName("fffffsend1");
        Message<User> fffff = MessageBuilder.withPayload(u).build();
        sendChannel.scoreOutput().send(fffff);
        System.out.println("发送消息send1");
    }

    public void send2() {
        User u = new User();
        u.setId(2L);
        u.setName("fffffsend2");
        Message<User> fffff = MessageBuilder.withPayload(u).build();
        mqScoreOutput.send(fffff);
        System.out.println("发送消息send2");
    }
}
