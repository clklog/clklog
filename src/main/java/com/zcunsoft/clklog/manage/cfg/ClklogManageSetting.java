package com.zcunsoft.clklog.manage.cfg;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 管理服务配置.
 */
@ConfigurationProperties("clklog-manage")
@Data
public class ClklogManageSetting {

    /**
     * 接收服务的日志存放路径
     */
    private String logStorePath;
}
