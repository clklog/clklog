package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访客统计")
@Data
public class VisitorStat {
    @Schema(description = "浏览量(PV)")
    private int pv;


    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "新访客数")
    private int newUv;

    @Schema(description = "回流访客")
    private int revisit;


    @Schema(description = "沉默访客")
    private int silent;

    @Schema(description = "流失访客")
    private int churn;
}
