package com.zcunsoft.cfg;

import com.clickhouse.client.config.ClickHouseDefaults;
import com.clickhouse.jdbc.ClickHouseDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class ClickhouseConfig {
    /**
     * 注入 Clickhouse 配置项
     *
     * @return Clickhouse 配置项
     */
    @Bean(name = "clickhouseDataSource")
    @Primary
    public DataSource clickhouseDataSourceProperties(@Qualifier("clickhouseDbProperties") Properties clickhouseDbProperties) throws SQLException, SQLException {
        clickhouseDbProperties.setProperty(ClickHouseDefaults.USER.getKey(), clickhouseDbProperties.getProperty("username"));
        return new ClickHouseDataSource(clickhouseDbProperties.getProperty("jdbc-url"), clickhouseDbProperties);
    }


    /**
     * 读取clickhouse配置项
     *
     * @return {@link Properties }
     */
    @Bean
    @ConfigurationProperties("spring.datasource.clickhouse")
    public Properties clickhouseDbProperties() {
        return new Properties();
    }


    @Bean(name = "clickHouseJdbcTemplate")
    public JdbcTemplate clickHouseJdbcTemplate(
            @Qualifier("clickhouseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
