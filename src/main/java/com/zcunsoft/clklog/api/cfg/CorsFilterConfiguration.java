package com.zcunsoft.clklog.api.cfg;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.Arrays;

@Configuration
public class CorsFilterConfiguration {
    @Resource
    ClklogApiSetting setting;

    @Bean
    public FilterRegistrationBean<Filter> corsFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<Filter>();
        filterRegistrationBean.setFilter(new CorsFilter(request -> {
            String origin = request.getHeader(HttpHeaders.ORIGIN);

            if (!StringUtils.hasText(origin)) {
                return null;
            }

            System.out.println(origin);
            CorsConfiguration configuration = new CorsConfiguration();
            if (setting.getAccessControlAllowOrigin().contains("*")) {
                configuration.addAllowedOrigin("*");
            }
            else  if (setting.getAccessControlAllowOrigin().contains(origin)) {
                configuration.addAllowedOrigin(origin);
            }
            configuration.addAllowedHeader("x-requested-with");
            configuration.addAllowedHeader("accept");
            configuration.addAllowedHeader("authorization");
            configuration.addAllowedHeader("content-type");
            configuration.addAllowedHeader("gpagent");
            configuration
                    .setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));

            return configuration;
        }));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Integer.MIN_VALUE); // Ensure first execution
        return filterRegistrationBean;
    }
}
