package com.zcunsoft.clklog.api.models.summary;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "流量概览信息")
@Data
public class FlowSummary {

    @Schema(description = "统计时间")
    private String statTime;
    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访问次数")
    private int visitCount;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;

    @Schema(description = "平均访问页数")
    private float avgPv;

    @Schema(description = "平均访问时长")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = ".02f")
    private float avgVisitTime;

    @Schema(description = "跳出率")
    private float bounceRate;

    @Schema(description = "渠道")
    private String channel;
}
