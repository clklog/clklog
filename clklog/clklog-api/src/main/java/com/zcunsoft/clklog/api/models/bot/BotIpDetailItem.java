package com.zcunsoft.clklog.api.models.bot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "机器人IP明细行")
@Data
public class BotIpDetailItem {

    @Schema(description = "机器人请求IP", example = "220.181.108.75")
    private String ip;

    @Schema(description = "国家", example = "中国")
    private String country;

    @Schema(description = "省份/州", example = "北京")
    private String province;

    @Schema(description = "分类键", example = "search_engine")
    private String botCategory;

    @Schema(description = "分类中文名", example = "搜索引擎")
    private String categoryName;

    @Schema(description = "UA归属（bot_subcategory）", example = "baidu")
    private String uaOwner;

    @Schema(description = "UA归属中文名", example = "百度")
    private String uaOwnerName;

    @Schema(description = "命中原因（中文，逗号分隔）", example = "搜索引擎UA")
    private String hitReasons;

    @Schema(description = "浏览量", example = "2")
    private Long pv;

    @Schema(description = "访问次数", example = "1")
    private Long visitCount;

    @Schema(description = "平均访问页数", example = "2.0")
    private Float avgPv;

    @Schema(description = "平均访问时长(秒)", example = "0.0")
    private Float avgVisitTime;

    @Schema(description = "最后访问时间", example = "2026-11-13 10:07:24")
    private String lastVisitTime;
}
