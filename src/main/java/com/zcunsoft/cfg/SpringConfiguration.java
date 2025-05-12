package com.zcunsoft.cfg;

import com.zcunsoft.handlers.ConstsDataHolder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({ReceiverSetting.class, RedisConstsConfig.class})
public class SpringConfiguration {

    @Bean
    public ConstsDataHolder constsDataHolder() {
        return new ConstsDataHolder();
    }


    @Bean
    @ConfigurationProperties("spring.kafka")
    public KafkaSetting kafkaSetting() {
        return new KafkaSetting();
    }
}
