package com.zcunsoft.daemon;

import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.model.RawMessage;
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

    volatile boolean running = false;

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
            BlockingQueue<RawMessage> queueForLog = constsDataHolder.getLogQueue();

            RawMessage log;
            try {
                log = queueForLog.take();
            } catch (InterruptedException e) {
                return;
            }

            List<RawMessage> logList = new ArrayList<RawMessage>();
            logList.add(log);
            queueForLog.drainTo(logList, 999);
            long retryDelay = 1000;
            while (true) {
                try {
                    handle(logList);
                    break;
                } catch (Exception ex) {
                    logger.error("Batch processing failed; {} messages remain unconfirmed", logList.size(), ex);
                    if (!running || Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    retryDelay = Math.min(retryDelay * 2, 30000);
                }
            }
        }
    }

    private void handle(List<RawMessage> logList) {
        if (serverSettings.isEnableSimpleVersion()) {
            ireceiveService.saveToClickHouse(logList);
        } else {
            ireceiveService.enqueueKafka(logList);
        }
    }


    @PreDestroy
    public void stop() {
        running = false;
        int queued = constsDataHolder.getLogQueue().size();
        if (queued > 0) {
            logger.warn("Stopping with {} messages still in the in-memory queue", queued);
        }
        long deadline = System.currentTimeMillis() + 30000;
        for (Thread thread : threadList) {
            try {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining > 0 && thread.getState() != Thread.State.WAITING) {
                    thread.join(remaining);
                }
                if (thread.isAlive()) {
                    thread.interrupt();
                    thread.join(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("thread join interrupted", e);
            }

            if (logger.isInfoEnabled()) {
                logger.info(thread.getName() + " stopping...");
            }
        }
    }
}
