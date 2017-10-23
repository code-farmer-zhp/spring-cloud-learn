package com.zhp.springbootstreamdemo.server;

import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(SendChannel.class)
public class SendServerConfig {

}
