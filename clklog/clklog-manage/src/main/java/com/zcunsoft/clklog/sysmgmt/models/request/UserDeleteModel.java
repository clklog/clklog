package com.zcunsoft.clklog.sysmgmt.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 删除用户的请求
 */
@Schema(description = "删除用户的请求")
@Data
public class UserDeleteModel {
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "72754fa3-040a-4177-8edb-2a2df81b7847")
    private String userId;
}
