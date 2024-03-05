package com.zcunsoft.cfg;

import com.zcunsoft.util.ObjectMapperUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class ObjectMapperConfiguration {

	@Bean("objectMapper")
	public ObjectMapperUtil getReceiverObjectMapper() {
		ObjectMapperUtil objectMapperUtil = new ObjectMapperUtil();
		objectMapperUtil.setTimeZone(TimeZone.getDefault());

		return objectMapperUtil;
	}
}
