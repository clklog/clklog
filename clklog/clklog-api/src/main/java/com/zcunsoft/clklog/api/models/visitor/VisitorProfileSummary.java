package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访客画像总计")
@Data
public class VisitorProfileSummary {

    @Schema(description = "访问次数")
    private int visitCount;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "停留时长")
    private int duration;

    @Schema(description = "访客IP")
    private String clientIp;

    @Schema(description = "首次访问时间")
    private String visitStart;

    @Schema(description = "最后访问时间")
    private String visitEnd;

}
