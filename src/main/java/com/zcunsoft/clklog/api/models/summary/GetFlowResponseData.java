package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取流量概览的响应")
@Data
public class GetFlowResponseData    {

    @Schema(description = "当前周期的信息")
    private FlowSummary current;
    @Schema(description = "前一周期的信息")
    private FlowSummary previous;

    @Schema(description = "当前周期的同期信息")
    private FlowSummary samePeriod;
}
