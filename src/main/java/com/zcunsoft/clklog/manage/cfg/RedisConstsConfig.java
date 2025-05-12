package com.zcunsoft.clklog.manage.cfg;

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
     * 城市中英文对照表 hash key.
     */
    private String cityEngChsMapKey = "CityEngChsMapKey";

    public String getProjectSettingKey() {
        return projectSettingKey;
    }

    public void setProjectSettingKey(String projectSettingKey) {
        this.projectSettingKey = projectSettingKey;
    }

    public String getCityEngChsMapKey() {
        return cityEngChsMapKey;
    }

    public void setCityEngChsMapKey(String cityEngChsMapKey) {
        this.cityEngChsMapKey = cityEngChsMapKey;
    }
}
