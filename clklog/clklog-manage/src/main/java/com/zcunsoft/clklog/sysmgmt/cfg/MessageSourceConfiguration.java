package com.zcunsoft.clklog.sysmgmt.cfg;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 返回代码说明配置
 */
@Configuration
public class MessageSourceConfiguration {
    @Bean(name = "codeMessageSource")
    public MessageSource codeMessageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages.code");

        return source;
    }
}
