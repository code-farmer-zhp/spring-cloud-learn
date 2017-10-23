package com.zhp.springbootstreamdemo.server;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.ServiceActivator;

@EnableBinding({ReceiverChannel2.class})
public class ReceiverServerConfig2 {


    @ServiceActivator(inputChannel = ReceiverChannel2.INPUT)
    public void receive(Object object) {
        System.out.println("原生接收消息：" + object);
    }
}
