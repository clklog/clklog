package com.zcunsoft.clklog.api.models.crash;

import com.zcunsoft.clklog.api.models.summary.CrashSummaryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页获取崩溃设备统计的请求
 */
@Schema(description = "分页获取崩溃设备统计的请求")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppCrashedStatPageRequest extends CrashSummaryRequest {

    /**
     * 页码
     */
    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum;

    /**
     * 页长
     */
    @Schema(description = "页长", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private int pageSize;

}
