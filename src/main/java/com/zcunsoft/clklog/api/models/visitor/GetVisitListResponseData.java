package com.zcunsoft.clklog.api.models.visitor;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "获取访问列表的响应")
@Data
public class GetVisitListResponseData {

    @Schema(description = "访问总数")
    private long total;

    @Schema(description = "访问信息")
    private List<Visit> rows;

}
