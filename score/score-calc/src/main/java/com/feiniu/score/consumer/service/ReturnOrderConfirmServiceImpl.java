package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ReturnOrderConfirmServiceImpl implements ReturnOrderConfirmService {

    @Autowired
    @Qualifier("returnOrderConfirmConsumerClient")
    private ConsumerClient consumerClient;
    @Override
    public void calcReturnOrderConfirm() {
        consumerClient.consume();
    }
}
