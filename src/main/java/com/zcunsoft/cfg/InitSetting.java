package com.zcunsoft.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;


@ConfigurationProperties("init")
public class InitSetting {

    private String resourcePath = "";

    private boolean quartzEnabled = true;

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
}
