package com.zcunsoft.clklog.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "受访页面详情")
@Data
public class VisitUriDetail {
    @Schema(description = "页面")
    private String uri;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;

    @Schema(description = "退出页次数")
    private int exitCount;

//    @Schema(description = "平均停留时长")
//    private float avgDuration;

    @Schema(description = "退出率")
    private float exitRate;

    @Schema(description = "入口页次数")
    private int entryCount;
    
    @Schema(description = "平均访问时长")
    private float avgVisitTime;
    
    @Schema(description = "贡献下游流量")
    private int downPvCount;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "路径")
    private String uriPath;
}
