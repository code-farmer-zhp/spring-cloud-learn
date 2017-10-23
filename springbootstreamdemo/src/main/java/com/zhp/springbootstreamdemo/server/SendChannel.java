package com.zhp.springbootstreamdemo.server;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SendChannel {

    String SCORE_OUPUT = "mqScoreOutput";

    @Output(SendChannel.SCORE_OUPUT)
    @Qualifier("mqScoreOutput")
    MessageChannel scoreOutput();
}
