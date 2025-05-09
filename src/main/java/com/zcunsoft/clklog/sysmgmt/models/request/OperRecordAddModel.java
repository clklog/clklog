package com.zcunsoft.clklog.sysmgmt.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 添加操作记录的请求
 */
@Schema(description = "添加操作记录的请求")
@Data
public class OperRecordAddModel {
    /**
     * 操作时间
     */
    @Schema(description = "操作时间", example = "2021-01-01 00:00:00")
    private Timestamp opertime;

    /**
     * 操作用户
     */
    @Schema(description = "操作用户", example = "admin")
    private String user;

    /**
     * 操作行为
     */
    @Schema(description = "操作行为", example = "登录")
    private String action;
}
