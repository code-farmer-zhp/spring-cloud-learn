//package com.feiniu;
//
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.stream.annotation.EnableBinding;
//import org.springframework.cloud.stream.messaging.Sink;
//import org.springframework.integration.annotation.ServiceActivator;
//
//@SpringBootApplication
//@EnableBinding(Sink.class)
//public class SinkApplication {
//
//    @ServiceActivator(inputChannel=Sink.INPUT)
//    public void loggerSink(Object payload) {
//    }
//}