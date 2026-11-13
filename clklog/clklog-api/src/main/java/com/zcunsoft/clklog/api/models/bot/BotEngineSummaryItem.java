package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "搜索引擎抓取汇总行")
@Data
public class BotEngineSummaryItem {

    @Schema(description = "引擎标识", example = "baidu")
    private String engine;

    @Schema(description = "引擎中文名", example = "百度")
    private String engineName;

    @Schema(description = "抓取页面数（时间范围内去重URL）", example = "2")
    private Long pageCount;

    @Schema(description = "抓取次数", example = "2")
    private Long pv;

    @Schema(description = "机器人IP数", example = "1")
    private Long ipCount;
}
