package com.zcunsoft.clklog.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "获取受访页面详情的响应")
@Data
public class GetVisitUriDetailResponseData {


    @Schema(description = "统计")
    private VisitUriStat total;

    @Schema(description = "详情")
    private List<VisitUriDetail> detail;
}
