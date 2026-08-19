package com.zcunsoft.cfg;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class ClickhouseConfig {

    @Bean(name = "clickHouseJdbcTemplate")
    public JdbcTemplate clickHouseJdbcTemplate(
            @Qualifier("clickhouseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
