//package com.feiniu;
//
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.messaging.Processor;
//import org.springframework.integration.annotation.Transformer;
//
///*
//*@author: Max
//*@mail:1069905071@qq.com
//*@time:2017/7/31 19:08
//*/
//@SpringBootApplication
//@EnableBinding(Processor.class)
//public class ProcessorApplication {
//
//    @Transformer
//    public String loggerSink(String payload) {
//        return payload.toUpperCase();
//    }
//}
