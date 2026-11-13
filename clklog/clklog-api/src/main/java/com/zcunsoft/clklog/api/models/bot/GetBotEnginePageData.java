package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "引擎抓取页面分页数据")
@Data
public class GetBotEnginePageData {

    @Schema(description = "总条数", example = "119")
    private Integer total;

    @Schema(description = "当前页数据")
    private List<BotEnginePageItem> list = new ArrayList<>();
}
