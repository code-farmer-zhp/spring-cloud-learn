package com.feiniu;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by chao.zhang1 on 2017/7/31.
 */
public interface MyMQInterface {
    String INPUT="channel2";
    String OUTPUT="channel1";

    @Input(MyMQInterface.INPUT)
    SubscribableChannel inputChannel();
    @Input(MyMQInterface.OUTPUT)
    MessageChannel outputChannel();
}
