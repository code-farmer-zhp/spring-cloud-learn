package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MemberModifyPhoneCalcServiceImpl implements MemberModifyPhoneCalcService {
    @Autowired
    @Qualifier("modifyPhoneConsumerClient")
    private ConsumerClient consumerClient;

    @Override
    public void calcModifyPhone () {
        consumerClient.consume();
    }
}
