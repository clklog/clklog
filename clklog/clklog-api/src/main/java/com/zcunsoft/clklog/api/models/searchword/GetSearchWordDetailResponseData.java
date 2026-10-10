package com.zcunsoft.clklog.api.models.searchword;

import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "获取搜索词详情的响应")
@Data
public class GetSearchWordDetailResponseData {

    @Schema(description = "搜索词总数")
    private long total;

    @Schema(description = "搜索词信息")
    private List<FlowDetail> rows;
}
