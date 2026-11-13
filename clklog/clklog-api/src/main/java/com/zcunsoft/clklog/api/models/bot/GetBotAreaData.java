package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "机器人地域分布数据")
@Data
public class GetBotAreaData {

    @Schema(description = "省级分布行（国内/海外由 region 字段分组）")
    private List<BotAreaItem> list = new ArrayList<>();
}
