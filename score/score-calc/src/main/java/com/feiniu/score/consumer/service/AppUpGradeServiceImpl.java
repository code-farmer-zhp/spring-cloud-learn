package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AppUpGradeServiceImpl implements AppUpGradeService {
    @Autowired
    @Qualifier("appUpGradeConsumerClient")
    private ConsumerClient consumerClient;

    @Override
    public void calcAppUpGrade() {
        consumerClient.consume();
    }
}
