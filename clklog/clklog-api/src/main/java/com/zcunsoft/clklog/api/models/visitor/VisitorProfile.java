package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "访客画像")
@Data
public class VisitorProfile {

    @Schema(description = "访客信息")
    private VisitorProfileBasicInfo baseInfo;

    @Schema(description = "访客信息")
    private VisitorProfileSummary summary;

    @Schema(description = "访客设备信息")
    private List<VisitorDevice> deviceList;

    @Schema(description = "访客地域信息")
    private List<VisitorArea> areaList;

}
