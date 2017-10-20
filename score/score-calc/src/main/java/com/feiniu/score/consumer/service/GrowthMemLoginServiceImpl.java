package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GrowthMemLoginServiceImpl implements GrowthMemLoginService {

    @Autowired
    @Qualifier("growthMemLoginServiceConsumerClient")
    private ConsumerClient consumer;

    @Override
    public void calcMemLogin() {
        consumer.consume(3);
    }
}
