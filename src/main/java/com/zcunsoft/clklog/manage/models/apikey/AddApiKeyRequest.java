package com.zcunsoft.clklog.manage.models.apikey;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * 添加API密钥请求
 */
@Data
public class AddApiKeyRequest {

    @Schema(description = "密钥显示名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示名称必填")
    private String displayName;

    @Schema(description = "过期时间(可选，为空表示永不过期)")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp expiresAt;
}
