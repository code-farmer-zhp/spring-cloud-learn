package com.zhp.springbootstreamdemo.server;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface ReceiverChannel {

    String SCORE_INPUT = "mqScoreInput";

    @Input(ReceiverChannel.SCORE_INPUT)
    SubscribableChannel scoreInput();
}
