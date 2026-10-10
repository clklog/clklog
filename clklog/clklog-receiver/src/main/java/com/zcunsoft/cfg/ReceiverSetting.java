package com.zcunsoft.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("receiver")
public class ReceiverSetting {

    private int threadCount = 2;

    private boolean enableSimpleVersion;

    private String resourcePath = "";

    private String[] accessControlAllowOriginPatterns;

    /**
     * 校验项目token的开关,true为校验.
     */
    private boolean enableCheckProjectToken = false;

    /** 单个请求上报数据（压缩后/原始数据）的最大字节数限制，防止超大请求打垮进程 */
    private int maxRequestSize = 2 * 1024 * 1024;

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

    public int getMaxRequestSize() {
        return maxRequestSize;
    }

    public void setMaxRequestSize(int maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public boolean isEnableCheckProjectToken() {
        return enableCheckProjectToken;
    }

    public void setEnableCheckProjectToken(boolean enableCheckProjectToken) {
        this.enableCheckProjectToken = enableCheckProjectToken;
    }
}
