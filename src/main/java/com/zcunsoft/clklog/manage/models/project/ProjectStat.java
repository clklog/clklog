package com.zcunsoft.clklog.manage.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 项目统计信息
 */
@Data
public class ProjectStat {
    /**
     * 项目编码
     */
    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private String projectName;

    /**
     * 日志数量
     */
    @Schema(description = "日志数量", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private Long logRecordCount;

    /**
     * 日志大小
     */
    @Schema(description = "日志大小", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private Long logSpaceSize;

    /**
     * 日志天数
     */
    @Schema(description = "日志天数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private Integer logDays;

    /**
     * 最新日志时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "最新日志时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private Timestamp logLatestTime;

    /**
     * 首次入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "首次入库时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private Timestamp dbFirstTime;

    /**
     * 最近入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "最近入库时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private Timestamp dbLatestTime;

    /**
     * 数据库表记录数
     */
    @Schema(description = "数据库表记录数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private Long dbRecordCount;

    /**
     * 数据库表占用空间大小
     */
    @Schema(description = "数据库表占用空间大小", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private Long dbSpaceSize;
}
