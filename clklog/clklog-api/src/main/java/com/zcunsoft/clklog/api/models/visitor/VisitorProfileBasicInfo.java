package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访客画像基本信息")
@Data
public class VisitorProfileBasicInfo {

    @Schema(description = "访客ID")
    private String visitorId;

    @Schema(description = "访客类型")
    private String visitorType;

    @Schema(description = "最近访客IP")
    private String clientIp;


}
