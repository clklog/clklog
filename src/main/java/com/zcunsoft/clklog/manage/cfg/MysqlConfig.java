package com.zcunsoft.clklog.manage.cfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Mysql配置.
 */
@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {"com.zcunsoft.clklog.manage.entity.mysql", "com.zcunsoft.clklog.sysmgmt.domains"})
@EnableJpaRepositories(
        basePackages = {"com.zcunsoft.clklog.manage.repository.mysql", "com.zcunsoft.clklog.sysmgmt.repository"},
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager")
public class MysqlConfig {
    @Autowired
    @Qualifier("mysqlDataSource")
    private DataSource mysqlDataSource;

    /**
     * 获取配置映射表.
     *
     * @return 配置映射表
     */
    protected Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        props.put("hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        return props;
    }

    /**
     * 获取mysql的EntityManagerFactory实例
     *
     * @param builder 构建EntityManagerFactory的构建器
     * @return mysql的EntityManagerFactory实例
     */
    @Bean(name = "mysqlEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(mysqlDataSource)
                .properties(jpaProperties())
                .packages("com.zcunsoft.clklog.manage.entity.mysql", "com.zcunsoft.clklog.sysmgmt.domains")
                .persistenceUnit("mysql")
                .build();
    }

    /**
     * 获取mysql的EntityManager实例
     *
     * @param builder 构建EntityManagerFactory的构建器
     * @return mysql的EntityManager实例
     */
    @Bean(name = "mysqlEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return mysqlEntityManagerFactory(builder).getObject().createEntityManager();
    }

    @Bean(name = "mysqlTransactionManager")
    PlatformTransactionManager mysqlTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(mysqlEntityManagerFactory(builder).getObject());
    }

}
