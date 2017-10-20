package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service
public class CancelMallOrderServiceImpl implements CancelMallOrderService{

    @Autowired
    @Qualifier("cancelMallOrderConsumerClient")
    private ConsumerClient consumerClient;
    @Override
    public void calcCancelMallOrder() {
        consumerClient.consume();
    }
}
