package com.zcunsoft.clklog;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ManageApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ManageApp.class)
                .beanNameGenerator(new UniqueBeanNameGenerator())
                .run(args);
    }

    public static class UniqueBeanNameGenerator extends AnnotationBeanNameGenerator {
        /**
         * 如果自定义了beanName，就取自定义的，不然取默认的
         *
         * @param definition
         * @return
         */
        @Override
        protected String buildDefaultBeanName(BeanDefinition definition) {
            return definition.getBeanClassName();
        }
    }
}
