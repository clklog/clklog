package com.zcunsoft.clklog.sysmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录用户登修改密码请求类
 */
@Schema(description = "登录用户登修改密码请求类")
@Data
public class AuthModifyPasswordDTO {
    /**
     * 原密码
     */
    @Schema(description = "原密码", required = true, example = "123456")
    private String oldPassword;

    /**
     * 新密码
     */
    @Schema(description = "新密码", required = true, example = "654321")
    private String newPassword;

}
