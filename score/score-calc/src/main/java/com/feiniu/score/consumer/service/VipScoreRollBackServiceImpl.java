package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class VipScoreRollBackServiceImpl implements VipScoreRollBackService {

    @Autowired
    @Qualifier("vipScoreRollBackClient")
    private ConsumerClient consumerClient;

    @Override
    public void calcScoreRollBack() {
        consumerClient.consume();
    }
}
