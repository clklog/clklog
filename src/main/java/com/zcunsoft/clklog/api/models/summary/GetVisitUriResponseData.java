package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取Top10受访页面的响应")
@Data
public class GetVisitUriResponseData {

    @Schema(description = "受访页面")
    private String uri;
    
    @Schema(description = "标题")
    private String title;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "占比")
    private float percent;

    @Schema(description = "渠道")
    private String channel;
}
