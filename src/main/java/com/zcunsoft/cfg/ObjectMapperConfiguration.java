package com.zcunsoft.cfg;

import com.zcunsoft.util.ReceiverObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class ObjectMapperConfiguration {

	@Bean("objectMapper")
	public ReceiverObjectMapper getReceiverObjectMapper() {
		ReceiverObjectMapper receiverObjectMapper = new ReceiverObjectMapper();
		receiverObjectMapper.setTimeZone(TimeZone.getDefault());

		return receiverObjectMapper;
	}
}
