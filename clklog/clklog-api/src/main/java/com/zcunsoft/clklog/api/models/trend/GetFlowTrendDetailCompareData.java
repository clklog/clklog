package com.zcunsoft.clklog.api.models.trend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@Schema(description = "获取流量趋势详情的响应")
@Data
public class GetFlowTrendDetailCompareData  {

    private String statTime;

    private List<FlowDetail> detail;
}
