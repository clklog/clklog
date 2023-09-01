package com.zcunsoft.clklog.api.models.summary;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取地域的响应")
@Data
public class GetAreaResponseData {

    @Schema(description = "国家")
    private String country;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访问次数")
    private int visitCount;

    @Schema(description = "访客数(UV)")
    private int uv;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "渠道")
    private String channel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "浏览量占比")
    private Float pvRate = 0.0f;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访问次数占比")
    private Float visitCountRate = 0.0f;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访客数占比")
    private Float uvRate;


}
