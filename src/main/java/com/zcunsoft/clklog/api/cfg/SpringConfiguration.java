package com.zcunsoft.clklog.api.cfg;

import com.zcunsoft.clklog.api.handlers.ConstsDataHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({ ClklogApiSetting.class })
public class SpringConfiguration {

    @Bean
    public ConstsDataHolder constsDataHolder() {
        return new ConstsDataHolder();
    }
}
