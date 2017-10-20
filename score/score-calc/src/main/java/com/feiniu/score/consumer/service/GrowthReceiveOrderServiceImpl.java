package com.feiniu.score.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.feiniu.kafka.client.ConsumerClient;

@Service
public class GrowthReceiveOrderServiceImpl implements GrowthReceiveOrderService {

	 	@Autowired
	    @Qualifier("growthReceiveOrderConsumerClient")
	    private ConsumerClient consumerClient;
	 	
	    @Override
	    public void calcGrowthReceiveOrder() {
	        consumerClient.consume();
	    }
}
