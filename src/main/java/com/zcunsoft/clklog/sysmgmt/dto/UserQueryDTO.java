package com.zcunsoft.clklog.sysmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询类
 */
@Schema(description = "用户查询类")
@Data
public class UserQueryDTO {
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
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String userName;

    /**
     * 用户显示名称
     */
    @Schema(description = "用户显示名称", example = "管理员")
    private String displayName;
}
