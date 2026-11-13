package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "机器人分类汇总数据")
@Data
public class GetBotCategoryData {

    @Schema(description = "分类行（不含合计，占比=各分类IP数/机器人总IP数）")
    private List<BotCategoryItem> list = new ArrayList<>();

    @Schema(description = "机器人总IP数", example = "6")
    private Long totalIpCount;

    @Schema(description = "机器人总浏览量", example = "7")
    private Long totalPv;
}
