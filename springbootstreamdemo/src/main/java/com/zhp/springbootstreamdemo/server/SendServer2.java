package com.zhp.springbootstreamdemo.server;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


@EnableBinding(value = {SendServer2.SendChannel2.class})
public class SendServer2 {

   /* @Bean
    @InboundChannelAdapter(value = SendChannel2.OUTPUT, poller = @Poller(fixedDelay = "2000"))
    public MessageSource<String> time() {
        return new MessageSource<String>() {
            @Override
            public Message<String> receive() {
                return new GenericMessage<>("good");
            }
        };
    }*/


    public interface SendChannel2 {
        String OUTPUT = "mq1Output";

        @Output(OUTPUT)
        MessageChannel output();
    }
}
