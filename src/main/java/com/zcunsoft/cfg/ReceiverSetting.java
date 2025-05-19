package com.zcunsoft.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("receiver")
public class ReceiverSetting {

    private int threadCount = 2;

    private boolean enableSimpleVersion;

    private String resourcePath = "";

    private String[] accessControlAllowOriginPatterns;

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
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

    public String[] getAccessControlAllowOriginPatterns() {
        return accessControlAllowOriginPatterns;
    }

    public void setAccessControlAllowOriginPatterns(String[] accessControlAllowOriginPatterns) {
        this.accessControlAllowOriginPatterns = accessControlAllowOriginPatterns;
    }
}
