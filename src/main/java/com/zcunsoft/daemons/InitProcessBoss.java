package com.zcunsoft.daemons;

import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.services.IInitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
public class InitProcessBoss {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IInitService initService;

    @Resource
    private ConstsDataHolder constsDataHolder;

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
                /* 初始化一次 */
                if (initService.initDb()) {
                    constsDataHolder.setDbInited(true);
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
