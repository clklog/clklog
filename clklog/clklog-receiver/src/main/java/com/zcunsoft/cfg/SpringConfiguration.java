package com.zcunsoft.cfg;

import com.zcunsoft.handlers.ConstsDataHolder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;


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

    /**
     * 注册请求体大小限制过滤器，只拦截上报接口 /api/gp.
     *
     * @param receiverSetting 接收服务配置
     * @return 过滤器注册信息
     */
    @Bean
    public FilterRegistrationBean<RequestSizeLimitFilter> requestSizeLimitFilter(ReceiverSetting receiverSetting) {
        FilterRegistrationBean<RequestSizeLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RequestSizeLimitFilter(receiverSetting));
        registration.addUrlPatterns("/api/gp");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
