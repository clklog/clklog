package com.zcunsoft.clklog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ImportResource({"classpath:query/native-query.xml"})
public class App
{
    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);

    }
}
