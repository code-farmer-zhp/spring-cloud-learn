package com.feiniu.score.log;

import javax.annotation.PostConstruct;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.stereotype.Component;

//@Component
public class JerseyLoggingBridge {    
    //@PostConstruct
    private void init() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }    
}