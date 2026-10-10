package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取App崩溃请求
 */
@Schema(description = "获取App崩溃请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class CrashSummaryRequest extends BaseSummaryRequest {

    /**
     * app版本
     */
    @Schema(description = "app版本", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1.0")
    private String version;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "HONGMI9")
    private String model;

}
