package com.feiniu.score.consumer.service;

import com.feiniu.kafka.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GrowthValueNumChangeConsumerServiceImpl implements GrowthValueNumChangeConsumerService {
	
	@Autowired
	@Qualifier("growthValueNumChangeConsumerClient")
	private ConsumerClient growthValueNumChangeConsumerClient;
	
	@Override
	public void growthValueNumChange() {
		growthValueNumChangeConsumerClient.consume(3);
	}
}
