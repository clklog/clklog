package com.zcunsoft.daemons;

import com.zcunsoft.cfg.InitSetting;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.services.IInitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Map;

/**
 * 依次执行统计脚本.
 */
@Component
public class InOrderCalcProcessBoss {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IInitService initService;

    @Resource
    private InitSetting setting;

    /**
     * 缓存实例.
     */
    @Resource
    private ConstsDataHolder constsDataHolder;

    /**
     * The Thread.
     */
    Thread thread = null;

    /**
     * The Running.
     */
    boolean running = false;

    /**
     * Start.
     */
    @PostConstruct
    public void start() {

        running = true;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        }, "InOrderCalcProcessBoss");

        thread.start();
    }

    private void work() {
        while (running) {
            if (constsDataHolder.isDbInited()) {
                /* 依次执行统计脚本 */
                long start = System.currentTimeMillis();
                for (Map.Entry<String, String> item : setting.getQuartz().entrySet()) {
                    if (running) {
                        initService.calScript(item.getKey());
                        try {
                            Thread.sleep(5000);
                            Thread.sleep(setting.getSleepMillisecAfterOneScript());
                        } catch (InterruptedException e) {
                            logger.error("", e);
                        }
                    }
                }
                long left = setting.getSleepMillisecAfterOneRound() - (System.currentTimeMillis() - start);
                if (left > 0) {
                    logger.error("insert token left "+left);
                    try {
                        Thread.sleep(left);
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                }
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * Stop.
     */
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

