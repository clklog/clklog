package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取访客的访问列表的请求")
@Data
public class GetVisitListRequest {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum;

    @Schema(description = "页长", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private int pageSize;

    @Schema(description = "访客ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "45hgre45364364gtrg4345")
    private String visitorId;
}
