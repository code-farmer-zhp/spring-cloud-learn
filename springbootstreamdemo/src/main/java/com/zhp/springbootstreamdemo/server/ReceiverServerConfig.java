package com.zhp.springbootstreamdemo.server;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

@EnableBinding({ReceiverChannel.class})
public class ReceiverServerConfig {

    @StreamListener(ReceiverChannel.SCORE_INPUT)
    @SendTo(SendServer2.SendChannel2.OUTPUT)
    public Object receive(User user) {
        user.setId(5555L);
        return user;
    }
}
