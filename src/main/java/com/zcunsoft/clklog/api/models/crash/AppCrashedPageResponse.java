package com.zcunsoft.clklog.api.models.crash;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 分页获取App崩溃详情的响应
 */
@Schema(description = "分页获取App崩溃详情的响应")
@Data
public class AppCrashedPageResponse {

    /**
     * 崩溃总数
     */
    private Long total;

    /**
     * 崩溃详情列表
     */
    private List<AppCrashedLog> rows;
}
