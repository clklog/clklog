package com.zcunsoft.clklog.sysmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户信息
 */
@Schema(description = "用户信息")
@Data
public class UserInfoDTO {
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String userName;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称", example = "管理员")
    private String displayName;

    /**
     * Token
     */
    @Schema(description = "Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private String token;

}
