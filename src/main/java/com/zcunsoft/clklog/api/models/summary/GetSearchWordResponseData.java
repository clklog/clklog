package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取Top10搜索词的响应")
@Data
public class GetSearchWordResponseData {

    @Schema(description = "时间")
    private String word;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "占比")
    private float percent;

    @Schema(description = "渠道")
    private String channel;

}
