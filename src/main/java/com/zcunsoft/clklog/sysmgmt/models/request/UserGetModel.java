package com.zcunsoft.clklog.sysmgmt.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户获取请求
 */
@Schema(description = "用户获取请求")
@Data
public class UserGetModel {
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "72754fa3-040a-4177-8edb-2a2df81b7847")
    private String userId;
}
