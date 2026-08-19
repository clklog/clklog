package com.zcunsoft.clklog.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Order(1)
public class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.user.name:}")
    private String actuatorUsername;

    @Value("${spring.security.user.password:}")
    private String actuatorPassword;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/actuator/**")
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .anyRequest().hasRole("ADMIN")
                .and()
                .httpBasic();
    }

    @Bean
    public BCryptPasswordEncoder actuatorPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(actuatorPasswordEncoder())
                .withUser(actuatorUsername)
                .password(actuatorPasswordEncoder().encode(actuatorPassword))
                .roles("ADMIN");
    }
}
