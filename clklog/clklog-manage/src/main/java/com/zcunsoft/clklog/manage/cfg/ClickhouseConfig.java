package com.zcunsoft.clklog.manage.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * ClickHouse配置.
 */
@Configuration
@EnableTransactionManagement
public class ClickhouseConfig {
    @Autowired
    @Qualifier("clickhouseDataSource")
    private DataSource dataSource;

    /**
     * 获取clickhouse的NamedParameterJdbcTemplate
     *
     * @param dataSource clickhouse数据源
     * @return clickhouse的NamedParameterJdbcTemplate
     */
    @Bean(name = "clickHouseJdbcTemplate")
    public NamedParameterJdbcTemplate clickHouseJdbcTemplate(
            @Qualifier("clickhouseDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
