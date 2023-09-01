package com.zcunsoft.clklog.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "受访页统计")
@Data
public class VisitUriStat {
    @Schema(description = "浏览量(PV)")
    private int pv;


    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "贡献下游浏览量")
    private int devoteCount;

    @Schema(description = "退出页次数")
    private int exitCount;

    @Schema(description = "平均停留时长")
    private int avgDuration;

    @Schema(description = "退出率")
    private float exitRate;
}
