package com.zcunsoft.clklog.api.models.crash;

import com.zcunsoft.clklog.api.models.summary.GetCrashedResponseData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页获取按渠道版本型号统计的响应.
 */
@Schema(description = "分页获取按渠道版本型号统计的响应")
@Data
public class AppCrashedStatPageResponse {

    /**
     * 崩溃总数
     */
    private Long total;

    /**
     * 崩溃设备列表
     */
    private List<GetCrashedResponseData> rows;
}
