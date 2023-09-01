package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取流量概览的请求")
@Data
public class GetFlowRequest extends BaseSummaryRequest {

}

