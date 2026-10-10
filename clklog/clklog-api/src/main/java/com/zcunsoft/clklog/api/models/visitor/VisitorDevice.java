package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访客设备统计")
@Data
public class VisitorDevice {

    @Schema(description = "设备")
    private String device;

    @Schema(description = "访问次数")
    private int visitCount;

}
