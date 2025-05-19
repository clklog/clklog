package com.zcunsoft.clklog.api.entity.ck;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 崩溃日志
 */
@Entity
@Data
@DiscriminatorValue("AppCrashed")
@EqualsAndHashCode(callSuper = false)
public class LogAppCrashed extends LogAnalysis {

    /**
     * app版本
     */
    @Schema(description = "app版本")
    private String appVersion;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * 操作系统版本
     */
    @Column
    private String osVersion;

    /**
     * 设备型号
     */
    @Column
    private String model;

    /**
     * 崩溃原因
     */
    @Column
    private String appCrashedReason;

    /**
     * 会话ID
     */
    @Column
    private String eventSessionId;

}
