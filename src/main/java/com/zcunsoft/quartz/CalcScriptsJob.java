package com.zcunsoft.quartz;

import com.zcunsoft.services.IInitService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class CalcScriptsJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IInitService initService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long l = System.currentTimeMillis();

        JobDataMap jobMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String scriptName = jobMap.getString("script_name");
        initService.calScript(scriptName);

        logger.info(scriptName + " token " + (System.currentTimeMillis() - l));
    }
}
