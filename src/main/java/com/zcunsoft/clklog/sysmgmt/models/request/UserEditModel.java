package com.zcunsoft.clklog.sysmgmt.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 编辑用户的请求
 */
@Schema(description = "编辑用户的请求")
@Data
public class UserEditModel {
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "72754fa3-040a-4177-8edb-2a2df81b7847")
    private String userId;

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
     * 旧密码
     */
    @Schema(description = "旧密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String oldpassword;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "管理员")
    private String displayName;

}
