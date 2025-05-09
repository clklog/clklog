package com.zcunsoft.clklog.manage.models.global;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 获取全局项目统计的请求
 */
@Data
public class StatGlobalRequest {

    /**
     * 统计开始日期
     */
    @Schema(description = "统计开始日期", example = "2024-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp startDate;

    /**
     * 统计结束日期
     */
    @Schema(description = "统计结束日期", example = "2024-05-05")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp endDate;
}
