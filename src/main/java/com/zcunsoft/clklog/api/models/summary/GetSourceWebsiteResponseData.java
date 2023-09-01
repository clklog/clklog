package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取Top10来源网站的响应")
@Data
public class GetSourceWebsiteResponseData {

    @Schema(description = "来源网站")
    private String website;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "占比")
    private float percent;

    @Schema(description = "渠道")
    private String channel;

}
