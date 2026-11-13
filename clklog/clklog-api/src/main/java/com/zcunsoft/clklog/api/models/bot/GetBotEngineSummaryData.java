package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "搜索引擎抓取汇总数据")
@Data
public class GetBotEngineSummaryData {

    @Schema(description = "引擎汇总行（page_count 为时间范围内去重URL数）")
    private List<BotEngineSummaryItem> list = new ArrayList<>();
}
