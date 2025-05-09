package com.zcunsoft.clklog.sysmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户视图类
 */
@Schema(description = "用户视图类")
@Data
public class UserDTO {
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "72754fa3-040a-4177-8edb-2a2df81b7847")
    private String userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "clklog")
    private String userName;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称", example = "clklog")
    private String displayName;

    /**
     * 创建用户
     */
    @Schema(description = "创建用户", example = "clklog")
    private String createuser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Timestamp createtime;

    /**
     * 修改用户
     */
    @Schema(description = "修改用户", example = "clklog")
    private String modifyuser;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Timestamp modifytime;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private Timestamp lastlogintime;

}
