package com.zcunsoft.cfg;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class KafkaConfig {

	@Resource
	private ReceiverSetting receiverSetting;

	@Bean
	public NewTopic batchTopic() {
		return new NewTopic(receiverSetting.getTopicName(), 6, (short) 1);
	}
}
