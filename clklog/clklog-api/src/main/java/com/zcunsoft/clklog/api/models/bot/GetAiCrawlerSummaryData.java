package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "AI爬虫抓取汇总数据")
@Data
public class GetAiCrawlerSummaryData {

    @Schema(description = "AI爬虫汇总行（page_count 为时间范围内去重URL数）")
    private List<AiCrawlerSummaryItem> list = new ArrayList<>();
}
