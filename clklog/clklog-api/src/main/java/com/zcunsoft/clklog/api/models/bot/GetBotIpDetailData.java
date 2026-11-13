package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "机器人IP明细分页数据")
@Data
public class GetBotIpDetailData {

    @Schema(description = "总条数", example = "1199")
    private Integer total;

    @Schema(description = "当前页数据")
    private List<BotIpDetailItem> list = new ArrayList<>();
}
