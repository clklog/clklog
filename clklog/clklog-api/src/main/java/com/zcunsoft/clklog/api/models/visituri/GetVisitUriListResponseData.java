package com.zcunsoft.clklog.api.models.visituri;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "获取访问的页面列表的响应")
@Data
public class GetVisitUriListResponseData {

    @Schema(description = "页面总数")
    private long total;

    @Schema(description = "页面信息")
    private List<VisitUri> rows;

}
