package com.zcunsoft.quartz;


import com.zcunsoft.cfg.InitSetting;
import org.quartz.*;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;


@Component
public class CronSchedulerJob {

    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

    @Resource
    private InitSetting setting;


    public void scheduleJobs() throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        for (Map.Entry<String, String> item : setting.getQuartz().entrySet()) {
            String jobName = item.getKey();

            JobDetail jobDetail = JobBuilder.newJob(CalcScriptsJob.class)
                    .withIdentity("job_" + jobName, "group_quartz")
                    .usingJobData("script_name", jobName)
                    .build();
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                    .cronSchedule(item.getValue()).withMisfireHandlingInstructionDoNothing();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("trigger_" + jobName, "group_quartz")
                    .withSchedule(scheduleBuilder).build();
            scheduler.scheduleJob(jobDetail, cronTrigger);
        }
    }
}