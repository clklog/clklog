package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Schema(description = "各搜索引擎对该页面的抓取次数（engineName=引擎中文名，crawlCount=次数，按次数降序）")
    private List<Map<String, Object>> engineCounts = new ArrayList<>();

    @Schema(description = "最后抓取时间", example = "2026-11-13 10:07:24")
    private String lastCrawlTime;
}
