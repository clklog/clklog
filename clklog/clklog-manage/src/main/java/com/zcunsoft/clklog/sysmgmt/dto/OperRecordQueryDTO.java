package com.zcunsoft.clklog.sysmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 操作记录查询类
 */
@Schema(description = "操作记录查询类")
@Data
public class OperRecordQueryDTO {
    /**
     * 页长
     */
    @Schema(description = "页长", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private int pageSize;

    /**
     * 页码
     */
    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageIndex;

    /**
     * 用户
     */
    @Schema(description = "用户", example = "admin")
    private String user;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2024-05-21 10:57:00")
    private String startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", example = "2024-05-21 10:57:00")
    private String endTime;
}
