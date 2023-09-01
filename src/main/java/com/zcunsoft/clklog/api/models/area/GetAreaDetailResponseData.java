package com.zcunsoft.clklog.api.models.area;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "获取地域详情的响应")
@Data
public class GetAreaDetailResponseData {

    @Schema(description = "合计")
    private AreaDetail total;
    @Schema(description = "详情")
    private List<AreaDetail> detail;
}
