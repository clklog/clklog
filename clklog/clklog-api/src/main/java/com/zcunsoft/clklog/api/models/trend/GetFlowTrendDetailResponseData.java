package com.zcunsoft.clklog.api.models.trend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "获取流量趋势详情的响应")
@Data
public class GetFlowTrendDetailResponseData  {

    private FlowDetail total;

    private List<FlowDetail> detail;
}
