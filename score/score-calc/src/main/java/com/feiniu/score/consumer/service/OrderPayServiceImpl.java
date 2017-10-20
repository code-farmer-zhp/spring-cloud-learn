package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderPayServiceImpl implements OrderPayService {
    @Autowired
    @Qualifier("orderPayServiceConsumerClient")
    private ConsumerClient consumerClient;
    @Override
    public void calcOrderPay() {
        consumerClient.consume();
    }
}
