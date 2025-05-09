package com.zcunsoft.clklog.manage.models.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 项目信息
 */
@Data
public class ProjectSlim {

    /**
     * 项目ID
     */
    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "eb029af8-0e24-47d4-b992-3b5284ce4fc8")
    private String id;

    /**
     * 项目编码
     */
    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "clklog")
    private String projectName;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "clklog")
    private String projectDisplayName;

    /**
     * 项目凭据
     */
    private String token;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp updateTime;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp createTime;
    /**
     * 统计信息
     */
    private ProjectStat stat;


}
