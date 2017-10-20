package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RegisteredConsumerServiceImpl implements RegisteredConsumerService {
    @Autowired
    @Qualifier("registeredServiceConsumerClient")
    private ConsumerClient consumerClient;
    @Override
    public void calcRegistered() {
        consumerClient.consume(2);
    }
}
