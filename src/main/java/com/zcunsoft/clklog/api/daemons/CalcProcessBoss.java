package com.zcunsoft.clklog.api.daemons;

import com.zcunsoft.clklog.api.handlers.ConstsDataHolder;
import com.zcunsoft.clklog.api.services.IReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 */
@Component
public class CalcProcessBoss {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    Thread thread = null;

    @Resource
    IReportService reportService;

    @Resource
    private ConstsDataHolder constsDataHolder;

    boolean running = false;

    private static final int LOOP_SPAN = 5000;

    @PostConstruct
    public void start() throws Exception {
        running = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        }, "CalcProcessBoss");

        thread.start();
    }

    private void work() {
        running = true;
        Calendar loopShoudEndTime = Calendar.getInstance();

        while (running) {
            try {

                Timestamp startStatDate = reportService.getProjectNameStartStatDate();
                constsDataHolder.setStartStatDate(startStatDate);

            } catch (Exception ex) {
                if (logger.isErrorEnabled()) {
                    logger.error("work failed", ex);
                }
            }

            Calendar loopEndTime = Calendar.getInstance();
            if (loopEndTime.compareTo(loopShoudEndTime) <= 0) {
                long sleepTime = loopShoudEndTime.getTimeInMillis() - loopEndTime.getTimeInMillis() + 1;
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    running = false;
                    break;
                }
                loopShoudEndTime.add(Calendar.MILLISECOND, LOOP_SPAN);
            } else {
                if (logger.isErrorEnabled()) {
                    logger.error("Last round work finished after expected time point, cost more {}ms.",
                            loopEndTime.getTimeInMillis() - loopShoudEndTime.getTimeInMillis());
                }
                loopShoudEndTime.add(Calendar.MILLISECOND,
                        (int) (LOOP_SPAN + (loopEndTime.getTimeInMillis() - loopShoudEndTime.getTimeInMillis())));
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