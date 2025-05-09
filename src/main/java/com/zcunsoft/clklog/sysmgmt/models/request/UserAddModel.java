package com.zcunsoft.clklog.sysmgmt.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 添加用户的请求
 */
@Schema(description = "添加用户的请求")
@Data
public class UserAddModel {
    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String userName;

    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    private String displayName;


}
