package com.zcunsoft.clklog.framework.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

/**
 * jackson配置
 */
@Configuration
public class ObjectMapperConfiguration {

    @Bean("objectMapper")
    public ObjectMapperUtil getObjectMapper() {
        ObjectMapperUtil objectMapperUtil = new ObjectMapperUtil();
        objectMapperUtil.setTimeZone(TimeZone.getDefault());
        objectMapperUtil.registerModule(new JavaTimeModule());
        return objectMapperUtil;
    }
}
