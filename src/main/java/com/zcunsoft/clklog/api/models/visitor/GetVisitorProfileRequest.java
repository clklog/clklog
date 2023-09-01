package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取访客详情的请求")
@Data
public class GetVisitorProfileRequest {

    @Schema(description = "访客Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "4232432gfr12421432")
    private String visitorId;


}
