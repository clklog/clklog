package com.zcunsoft.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;


@ConfigurationProperties("init")
public class InitSetting {

    private String logDb = "clklog";

    private String resourcePath = "";

    private boolean quartzEnabled = true;


    private int sleepMillisecAfterOneRound = 300000;


    private int sleepMillisecAfterOneScript = 2000;

    private HashMap<String, String> quartz;

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public boolean isQuartzEnabled() {
        return quartzEnabled;
    }

    public void setQuartzEnabled(boolean quartzEnabled) {
        this.quartzEnabled = quartzEnabled;
    }

    public HashMap<String, String> getQuartz() {
        return quartz;
    }

    public void setQuartz(HashMap<String, String> quartz) {
        this.quartz = quartz;
    }

    public String getLogDb() {
        return logDb;
    }

    public void setLogDb(String logDb) {
        this.logDb = logDb;
    }

    public int getSleepMillisecAfterOneScript() {
        return sleepMillisecAfterOneScript;
    }

    public void setSleepMillisecAfterOneScript(int sleepMillisecAfterOneScript) {
        this.sleepMillisecAfterOneScript = sleepMillisecAfterOneScript;
    }

    public int getSleepMillisecAfterOneRound() {
        return sleepMillisecAfterOneRound;
    }

    public void setSleepMillisecAfterOneRound(int sleepMillisecAfterOneRound) {
        this.sleepMillisecAfterOneRound = sleepMillisecAfterOneRound;
    }
}
