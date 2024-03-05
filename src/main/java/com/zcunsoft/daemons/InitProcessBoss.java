package com.zcunsoft.daemons;

import com.zcunsoft.services.IInitService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
public class InitProcessBoss {
    private final Logger logger = LogManager.getLogger(this.getClass());
    ;

    @Resource
    private IInitService initService;

    Thread thread = null;

    boolean running = false;

    @PostConstruct
    public void start() {

        running = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        }, "InitProcessBoss");

        thread.start();
    }

    private void work() {
        while (running) {
            try {
                Thread.sleep(10000);
                if (initService.initDb()) {
                    running = false;
                }
            } catch (InterruptedException e) {
                logger.error("", e);
            }
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