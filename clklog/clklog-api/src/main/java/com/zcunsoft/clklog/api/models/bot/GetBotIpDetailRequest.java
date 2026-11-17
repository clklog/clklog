package com.zcunsoft.clklog.api.models.bot;

import com.zcunsoft.clklog.api.models.summary.BaseSummaryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "分页获取机器人IP明细的请求")
@Data
public class GetBotIpDetailRequest extends BaseSummaryRequest {

    @Schema(description = "页码", example = "1")
    private int pageNum = 1;

    @Schema(description = "页长", example = "10")
    private int pageSize = 10;

    @Schema(description = "分类筛选（bot_category 键，空=全部）", example = "search_engine")
    private String botCategory;

    @Schema(description = "机器人IP模糊搜索（空=不过滤）", example = "220.181")
    private String ipKeyword;

    @Schema(description = "排序字段（pv/visitCount/avgPv/avgVisitTime/lastVisitTime，空=默认按浏览量）", example = "pv")
    private String sortField;

    @Schema(description = "排序方向（asc/desc，空=desc）", example = "desc")
    private String sortOrder;
}
