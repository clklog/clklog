package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "AI爬虫抓取汇总行")
@Data
public class AiCrawlerSummaryItem {

    @Schema(description = "AI爬虫标识", example = "openai")
    private String source;

    @Schema(description = "AI爬虫中文名", example = "OpenAI")
    private String sourceName;

    @Schema(description = "抓取页面数（时间范围内去重URL）", example = "3")
    private Long pageCount;

    @Schema(description = "抓取次数", example = "5")
    private Long pv;

    @Schema(description = "机器人IP数", example = "2")
    private Long ipCount;

    @Schema(description = "判断规则正则", example = "(?i)gptbot|oai-searchbot|chatgpt-user")
    private String pattern;
}
