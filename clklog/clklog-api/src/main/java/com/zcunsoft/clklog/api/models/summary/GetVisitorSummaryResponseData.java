package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取访客的响应")
@Data
public class GetVisitorSummaryResponseData {

    @Schema(description = "新访客")
    private FlowSummary newVisitor;
    @Schema(description = "老访客")
    private FlowSummary oldVisitor;
}
