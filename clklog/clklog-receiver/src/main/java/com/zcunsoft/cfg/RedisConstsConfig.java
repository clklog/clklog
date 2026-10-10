package com.zcunsoft.cfg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * redis常量配置类.
 */
@ConfigurationProperties("clklog.redisconsts")
@Data
public class RedisConstsConfig {

    /**
     * Ip信息Hashkey.
     */
    private String clientIpRegionHashKey = "ClientIpRegionHash";


    /**
     * 项目的配置 key.
     */
    private String projectSettingKey = "ProjectSettingKey";

    /**
     * 城市中英文对照表 hash key.
     */
    private String cityEngChsMapKey = "CityEngChsMapKey";
}
