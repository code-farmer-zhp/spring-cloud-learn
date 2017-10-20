package com.feiniu.score.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.feiniu.kafka.client.ConsumerClient;

@Service
public class GrowthConsumerServiceImpl implements GrowthConsumerService {
	
	@Autowired
	@Qualifier("growthConsumerClient")
	private ConsumerClient consumerClient;
	
	@Override
	public void calcGrowthScore() {
		consumerClient.consume();
	}
}
