package com.zcunsoft.clklog.api.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * redis常量配置类.
 */
@ConfigurationProperties("clklog.redisconsts")
public class RedisConstsConfig {

    /**
     * 项目的配置 key.
     */
    private String projectSettingKey = "ProjectSettingKey";

    /**
     * 项目的渠道的最新缓存时间戳.
     */
    private String latestTimeOfCacheChannelKey = "LatestTimeOfCacheChannel";


    /**
     * 项目的渠道 hash key.
     */
    private String projectChannelHashKey = "ProjectChannelHashKey";


    public String getProjectSettingKey() {
        return projectSettingKey;
    }

    public void setProjectSettingKey(String projectSettingKey) {
        this.projectSettingKey = projectSettingKey;
    }

    public String getLatestTimeOfCacheChannelKey() {
        return latestTimeOfCacheChannelKey;
    }

    public void setLatestTimeOfCacheChannelKey(String latestTimeOfCacheChannelKey) {
        this.latestTimeOfCacheChannelKey = latestTimeOfCacheChannelKey;
    }

    public String getProjectChannelHashKey() {
        return projectChannelHashKey;
    }

    public void setProjectChannelHashKey(String projectChannelHashKey) {
        this.projectChannelHashKey = projectChannelHashKey;
    }
}
