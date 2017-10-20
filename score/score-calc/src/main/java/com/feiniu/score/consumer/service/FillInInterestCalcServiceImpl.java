package com.feiniu.score.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.feiniu.kafka.client.ConsumerClient;

@Service
public class FillInInterestCalcServiceImpl implements FillInInterestCalcService {
	 @Autowired
	    @Qualifier("interestConsumerClient")
	    private ConsumerClient consumerClient;
	    @Override
	    public void fillInInterest() {
	        consumerClient.consume();
	    }
}
