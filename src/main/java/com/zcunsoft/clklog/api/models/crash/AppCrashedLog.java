package com.zcunsoft.clklog.api.models.crash;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zcunsoft.clklog.api.entity.ck.LogAppCrashed;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * APP崩溃日志
 */
@Data
@NoArgsConstructor
public class AppCrashedLog {

    /**
     * app版本
     */
    @Schema(description = "app版本")
    private String appVersion;

    /**
     * 埋点平台
     */
    @Schema(description = "埋点平台")
    private String lib;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * 操作系统版本
     */
    @Schema(description = "操作系统版本")
    private String osVersion;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String model;

    /**
     * 崩溃时间
     */
    @Schema(description = "崩溃时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp logTime;

    /**
     * 崩溃原因
     */
    @Schema(description = "崩溃原因")
    private String appCrashedReason;

    /**
     * 会话id
     */
    @Schema(description = "会话id")
    private String eventSessionId;

    public AppCrashedLog(LogAppCrashed logAppCrashed) {
        this.appVersion = logAppCrashed.getAppVersion();
        this.lib = logAppCrashed.getLib();
        this.os = logAppCrashed.getOs();
        this.osVersion = logAppCrashed.getOsVersion();
        this.model = logAppCrashed.getModel();
        this.logTime = logAppCrashed.getLogTime();
        this.eventSessionId = logAppCrashed.getEventSessionId();
        this.appCrashedReason = logAppCrashed.getAppCrashedReason();
    }

}
