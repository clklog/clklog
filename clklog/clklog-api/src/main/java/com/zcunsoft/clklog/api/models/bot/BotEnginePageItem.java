package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "引擎抓取页面明细行（按页面聚合）")
@Data
public class BotEnginePageItem {

    @Schema(description = "抓取页面", example = "http://www.example.com/")
    private String url;

    @Schema(description = "页面标题", example = "网站首页")
    private String title;

    @Schema(description = "抓取过该页面的搜索引擎中文名列表（可多个）")
    private List<String> engines = new ArrayList<>();

    @Schema(description = "总抓取次数（各引擎合计）", example = "5")
    private Long crawlCount;

    @Schema(description = "最后抓取时间", example = "2026-11-13 10:07:24")
    private String lastCrawlTime;
}
