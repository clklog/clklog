package com.zcunsoft.clklog.sysmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户修改密码请求类
 */
@Schema(description = "用户修改密码请求类")
@Data
public class UserModifyPasswordDTO {
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "72754fa3-040a-4177-8edb-2a2df81b7847")
    private String userId;

    /**
     * 新密码
     */
    @Schema(description = "新密码", required = true, example = "123456")
    private String newPassword;

}
