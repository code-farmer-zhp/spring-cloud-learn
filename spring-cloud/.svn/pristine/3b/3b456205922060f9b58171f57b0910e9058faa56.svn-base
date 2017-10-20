package com.feiniu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@EnableBinding(OutputSource.class)
public class Sender{

//    @Bean
//    @InboundChannelAdapter(value = OutputSource.OUTPUT, poller = @Poller(fixedDelay = "10000", maxMessagesPerPoll = "1"))
//    public MessageSource<String> timerMessageSource() {
//        return new MessageSource<String>() {
//            public Message<String> receive() {
//                String message = "FromSource1";
//                System.out.println("******************");
//                System.out.println("From Source1");
//                System.out.println("******************");
//                System.out.println("Sending value: " + message);
//                return new GenericMessage(message);
//            }
//        };
//    }

    @StreamListener(OutputSource.INPUT)
    public void process(String message) {
        System.out.print("do service about message here: "+ (StringUtils.isEmpty(message)?"":message));
    }
}