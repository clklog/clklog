package com.zcunsoft.clklog.api.models.area;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "地域信息")
@Data
public class AreaDetail{
    @Schema(description = "国家")
    private String country;

    @Schema(description = "省份")
    private String province;
    
    @Schema(description = "城市")
    private String city;

    @Schema(description = "浏览量(PV)")
    private int pv;
    @Schema(description = "浏览量占比")
    private float pvRate;

    @Schema(description = "访问次数")
    private int visitCount;

    @Schema(description = "新访客数")
    private int newUv;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;

    @Schema(description = "平均访问页数")
    private float avgPv;

    @Schema(description = "平均访问时长")
    private int avgVisitTime;

    @Schema(description = "跳出率")
    private float bounceRate;
    
    @Schema(description = "新访客比率")
    private float newUvRate;
}
