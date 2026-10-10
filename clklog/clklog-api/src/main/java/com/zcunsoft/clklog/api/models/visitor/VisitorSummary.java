package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "单个访客统计概览")
@Data
public class VisitorSummary {

    @Schema(description = "访客ID")
    private String visitorId;
    @Schema(description = "访客类型")
    private String visitorType;

    @Schema(description = "访问次数")
    private int visitCount;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "停留时长")
    private int duration;

    @Schema(description = "访客IP")
    private String clientIp;


    @Schema(description = "访问设备")
    private String  device;

    @Schema(description = "访问区域")
    private String area;
}
