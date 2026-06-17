package com.zcunsoft.clklog.manage.models.apikey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 获取API密钥列表请求
 */
@Data
public class ListApiKeyRequest {

    @Schema(description = "状态筛选(可选，不传则查询所有)")
    private String status;
}
