package com.zcunsoft.clklog.manage.models.apikey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 获取API密钥详情请求
 */
@Data
public class GetApiKeyRequest {

    @Schema(description = "密钥ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "密钥ID必填")
    private String id;
}