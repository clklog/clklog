package com.zcunsoft.clklog.api.models.bot;

import com.zcunsoft.clklog.api.models.summary.BaseSummaryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "分页获取AI爬虫抓取页面的请求")
@Data
public class GetAiCrawlerPageRequest extends BaseSummaryRequest {

    @Schema(description = "页码", example = "1")
    private int pageNum = 1;

    @Schema(description = "页长", example = "10")
    private int pageSize = 10;

    @Schema(description = "AI爬虫筛选（bot_subcategory 键，空=全部）", example = "openai")
    private String source;

    @Schema(description = "页面地址模糊搜索（空=不过滤）", example = "example.com")
    private String urlKeyword;
}
