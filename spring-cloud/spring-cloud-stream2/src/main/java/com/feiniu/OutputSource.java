package com.feiniu;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/*
*@author: Max
*@mail:1069905071@qq.com 
*@time:2017/7/31 14:46 
*/
public interface OutputSource {
    String OUTPUT = "channel2";
    @Output(OUTPUT)
    MessageChannel output();
    String INPUT = "channel1";
    @Output(INPUT)
    SubscribableChannel input();
}