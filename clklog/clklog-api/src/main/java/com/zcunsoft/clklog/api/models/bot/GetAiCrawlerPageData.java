package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "AI爬虫抓取页面分页数据")
@Data
public class GetAiCrawlerPageData {

    @Schema(description = "总条数", example = "36")
    private Integer total;

    @Schema(description = "当前页数据")
    private List<AiCrawlerPageItem> list = new ArrayList<>();
}
