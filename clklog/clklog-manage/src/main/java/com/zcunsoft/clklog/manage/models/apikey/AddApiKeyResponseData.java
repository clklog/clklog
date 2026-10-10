package com.zcunsoft.clklog.manage.models.apikey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 添加API密钥响应数据
 */
@Data
public class AddApiKeyResponseData {

    @Schema(description = "密钥ID")
    private String id;

    @Schema(description = "API密钥")
    private String apiKey;
}
