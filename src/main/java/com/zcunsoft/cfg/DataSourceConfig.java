package com.zcunsoft.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfig {
    @Bean(name = "clickhouseDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public DataSource clickhouseDataSourceProperties() {
        return DataSourceBuilder.create().build();
    }

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private HibernateProperties hibernateProperties;


    @Bean(name = "vendorClickhouseProperties")
    public Map<String, Object> vendorClickhouseProperties() {
        Map<String, String> properties = jpaProperties.getProperties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        return hibernateProperties.determineHibernateProperties(properties, new HibernateSettings());
    }
}
