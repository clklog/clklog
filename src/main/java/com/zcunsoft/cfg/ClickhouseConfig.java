package com.zcunsoft.cfg;

import com.zcunsoft.repository.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EntityScan(basePackages = "com.zcunsoft.entity.clickhouse")
@EnableJpaRepositories(
        basePackages = "com.zcunsoft.repository.clickhouse",
        entityManagerFactoryRef = "clickhouseEntityManagerFactory",
        transactionManagerRef = "clickhouseTransactionManager",
        repositoryBaseClass = BaseRepositoryImpl.class)
//        enableDefaultTransactions = false)
@EnableTransactionManagement
public class ClickhouseConfig {
    @Autowired
    @Qualifier("clickhouseDataSource")
    private DataSource dataSource;


    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        return props;
    }


    @Bean(name = "clickhouseEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean clickhouseEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(jpaProperties())
                .packages("com.zcunsoft.entity.clickhouse")
                .persistenceUnit("clickhousePersistenceUnit")
                .build();

    }

    @Bean(name = "clickhouseEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return clickhouseEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "clickhouseTransactionManager")
    PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(clickhouseEntityManagerFactory(builder).getObject());
    }


    @Bean(name = "clickHouseJdbcTemplate")
    public JdbcTemplate clickHouseJdbcTemplate(
            @Qualifier("clickhouseDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
