package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访客详情")
@Data
public class VisitorDetail {
    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;


    @Schema(description = "平均访问时长")
    private float avgVisitTime;

    @Schema(description = "跳出率")
    private float bounceRate;
    
    @Schema(description = "访客类型")
    private String isFirstDay;
}
