package com.zcunsoft.daemon;

import com.zcunsoft.cfg.ReceiverSetting;
import com.zcunsoft.dto.QueryCriteria;
import com.zcunsoft.handlers.ConstsDataHolder;
import com.zcunsoft.services.IReceiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@Component
public class LogReceiveProcessBoss {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ConstsDataHolder constsDataHolder;

    @Resource
    private ReceiverSetting serverSettings;

    @Resource
    private IReceiveService ireceiveService;

    List<Thread> threadList = null;

    boolean running = false;

    @PostConstruct
    public void start() throws Exception {
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
                log = queueForLog.poll();
                if (log != null) {
                    logList.add(log);
                    count++;
                } else {
                    break;
                }
                if (count >= 20) {
                    handle(logList);
                    count = 0;
                    logList.clear();
                }
            }
            if (count > 0) {
                handle(logList);
                count = 0;
                logList.clear();
            }
        }
    }

    private void handle(List<QueryCriteria> logList) {
        ireceiveService.enqueueKafka(logList);
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