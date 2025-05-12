package com.zcunsoft.daemon;

import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.model.QueryCriteria;
import com.zcunsoft.services.IReceiveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
public class LogReceiveProcessBoss {
    private final Logger logger = LogManager.getLogger(this.getClass());
    ;

    @Resource
    private ConstsDataHolder constsDataHolder;

    @Resource
    private ReceiverSetting serverSettings;

    @Resource
    private IReceiveService ireceiveService;

    List<Thread> threadList = null;

    boolean running = false;

    @PostConstruct
    public void start() {
        threadList = new ArrayList<Thread>();
        if (serverSettings.getThreadCount() > 0) {
            running = true;
            for (int i = 0; i < serverSettings.getThreadCount(); i++) {
                int threadId = i;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        work(threadId);
                    }
                }, "LogReceiveProcess-" + String.valueOf(i));

                thread.start();
                threadList.add(thread);
            }
        }
    }

    private void work(int threadId) {
        while (running) {
            BlockingQueue<QueryCriteria> queueForLog = constsDataHolder.getLogQueue();

            QueryCriteria log;
            try {
                log = queueForLog.take();
            } catch (InterruptedException e) {
                return;
            }

            int count = 0;
            List<QueryCriteria> logList = new ArrayList<QueryCriteria>();
            logList.add(log);
            count++;

            while (running) {
                try {
                    log = queueForLog.poll();
                    if (log != null) {
                        logList.add(log);
                        count++;
                    } else {
                        break;
                    }
                    if (count >= 1000) {
                        handle(logList);
                        count = 0;
                        logList.clear();
                    }
                } catch (Exception ex) {
                    count = 0;
                    logList.clear();
                    logger.error("handle err ", ex);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            }
            if (count > 0) {
                try {
                    handle(logList);
                    logList.clear();
                } catch (Exception ex) {
                    logger.error("handle err ", ex);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private void handle(List<QueryCriteria> logList) {
        if (serverSettings.isEnableSimpleVersion()) {
            ireceiveService.saveToClickHouse(logList);
        } else {
            ireceiveService.enqueueKafka(logList);
        }
    }


    @PreDestroy
    public void stop() {
        running = false;
        for (Thread thread : threadList) {
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
}
