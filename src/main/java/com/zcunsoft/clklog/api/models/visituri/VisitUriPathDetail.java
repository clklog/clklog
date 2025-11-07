package com.zcunsoft.clklog.api.models.visituri;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "受访页面路径详情")
@Data
public class VisitUriPathDetail {
    @JsonIgnore
    @Schema(description = "域名")
    private String host;
    @JsonIgnore
    @Schema(description = "页面路径")
    private String uri;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;

    @Schema(description = "退出页次数")
    private int exitCount;

    @Schema(description = "退出率")
    private float exitRate;

    @Schema(description = "入口页次数")
    private int entryCount;

    @Schema(description = "页面浏览时长")
    private float visitTime;

    @Schema(description = "平均浏览时长")
    private float avgVisitTime;

    @Schema(description = "贡献下游流量")
    private int downPvCount;


    @JsonIgnore
    private List<String> pathList;

    @JsonIgnore
    private int pathLength;
}
