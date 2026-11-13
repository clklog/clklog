package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "机器人分类汇总行")
@Data
public class BotCategoryItem {

    @Schema(description = "分类键", example = "search_engine")
    private String botCategory;

    @Schema(description = "分类中文名", example = "搜索引擎")
    private String categoryName;

    @Schema(description = "请求IP数", example = "3")
    private Long ipCount;

    @Schema(description = "请求IP占比(%)", example = "50.0")
    private Double ipRatio;

    @Schema(description = "浏览量", example = "4")
    private Long pv;
}
