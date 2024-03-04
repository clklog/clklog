package com.zcunsoft.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


@ConfigurationProperties("receiver")
public class ReceiverSetting {

    private int threadCount = 2;

    private List<String> appList;

    private boolean enableSimpleVersion;

    private String resourcePath = "";

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public List<String> getAppList() {
        return appList;
    }

    public void setAppList(List<String> appList) {
        this.appList = appList;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public boolean isEnableSimpleVersion() {
        return enableSimpleVersion;
    }

    public void setEnableSimpleVersion(boolean enableSimpleVersion) {
        this.enableSimpleVersion = enableSimpleVersion;
    }
}
