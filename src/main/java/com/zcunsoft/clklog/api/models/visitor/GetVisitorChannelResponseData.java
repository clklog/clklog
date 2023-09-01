package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "获取访客详情的响应")
@Data
public class GetVisitorChannelResponseData {

    @Schema(description = "访客渠道")
    private String channel;

    @Schema(description = "指标信息")
    private VisitorChannel visitorChannel;

}
