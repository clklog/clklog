package com.zcunsoft.clklog.manage.models.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 添加项目的响应数据
 */
@Data
public class AddProjectResponseData {

    /**
     * 项目ID
     */
    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "eb029af8-0e24-47d4-b992-3b5284ce4fc8")
    private String id;
}
