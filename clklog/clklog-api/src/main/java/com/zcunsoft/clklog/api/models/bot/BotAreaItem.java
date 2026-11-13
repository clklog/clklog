package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "机器人地域分布行（省级）")
@Data
public class BotAreaItem {

    @Schema(description = "国家", example = "中国")
    private String country;

    @Schema(description = "省份/州", example = "北京")
    private String province;

    @Schema(description = "国内/海外分组", example = "国内")
    private String region;

    @Schema(description = "机器人IP数", example = "3")
    private Long ipCount;

    @Schema(description = "机器人浏览量", example = "4")
    private Long pv;

    @Schema(description = "机器人访问次数", example = "3")
    private Long visitCount;
}
