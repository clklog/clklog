package com.zcunsoft.cfg;


import com.zcunsoft.handlers.ConstsDataHolder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@EnableConfigurationProperties({InitSetting.class})
public class SpringConfiguration {

    @Bean
    public ConstsDataHolder constsDataHolder() {
        return new ConstsDataHolder();
    }


}
