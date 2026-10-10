package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "获取访客详情的响应")
@Data
public class GetVisitorResponseData {

    @Schema(description = "访客总数")
    private long total;

    @Schema(description = "访客信息")
    private List<VisitorSummary> rows;
}
