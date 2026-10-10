package com.zcunsoft.clklog.manage.models.apikey;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * API密钥模型
 */
@Data
public class ApiKeyModel {

    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "所属用户ID")
    private String userId;

    @Schema(description = "所属用户名")
    private String username;

    @Schema(description = "API密钥")
    private String apiKey;

    @Schema(description = "密钥显示名称")
    private String displayName;

    @Schema(description = "状态: 启用/禁用")
    private String status;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp expiresAt;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatedAt;
}
