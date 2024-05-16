package com.zcunsoft.daemon;

import com.zcunsoft.services.IReceiveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
public class LoadConstProcessBoss {
    private final Logger logger = LogManager.getLogger(this.getClass());;

    @Resource
    private IReceiveService ireceiveService;

    Thread thread = null;

    boolean running = false;

    @PostConstruct
    public void start()   {

        running = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        }, "LoadConstProcess");

        thread.start();
    }

    private void work() {
        try {
            ireceiveService.loadCountry();
            ireceiveService.loadProvince();
            ireceiveService.loadCity();
            ireceiveService.loadProjectSetting();
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            logger.error("", e);
        }

    }

    @PreDestroy
    public void stop() {
        running = false;

        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (logger.isInfoEnabled()) {
            logger.info(thread.getName() + " stopping...");
        }
    }
}