package com.zhp.springbootstreamdemo.server;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ReceiverChannel2 {
    String INPUT = "mq1Input";

    @Input(INPUT)
    SubscribableChannel input();
}
