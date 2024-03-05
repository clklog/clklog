
package com.zcunsoft.quartz;

import com.zcunsoft.cfg.InitSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MyStartupRunner implements CommandLineRunner {

	@Resource
	public InitSetting setting;

	@Autowired
	public CronSchedulerJob scheduleJobs;

	@Override
	public void run(String... args) throws Exception {
		if(setting.isQuartzEnabled()) {
			scheduleJobs.scheduleJobs();
		}
	}
}
