package com.zcunsoft.clklog.api.models.channel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 获取项目渠道的请求.
 */
@Schema(description = "获取项目渠道的请求")
@Data
public class GetChannelRequest {

    /**
     * 项目编码
     */
    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    @NotEmpty(message = "项目编码不能为空")
    private String projectName;
}
