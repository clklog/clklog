package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "引擎抓取页面明细行")
@Data
public class BotEnginePageItem {

    @Schema(description = "引擎标识", example = "baidu")
    private String engine;

    @Schema(description = "引擎中文名", example = "百度")
    private String engineName;

    @Schema(description = "抓取页面", example = "http://www.example.com/")
    private String url;

    @Schema(description = "页面标题", example = "网站首页")
    private String title;

    @Schema(description = "抓取次数", example = "1")
    private Long crawlCount;

    @Schema(description = "最后抓取时间", example = "2026-11-13 10:07:24")
    private String lastCrawlTime;
}
