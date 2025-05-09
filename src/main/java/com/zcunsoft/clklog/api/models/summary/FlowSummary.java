package com.zcunsoft.clklog.api.models.summary;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流量概览信息
 */
@Schema(description = "流量概览信息")
@Data
public class FlowSummary {

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private String statTime;

    /**
     * 浏览量(PV)
     */
    @Schema(description = "浏览量(PV)")
    private Long pv;

    /**
     * 访问次数
     */
    @Schema(description = "访问次数")
    private Long visitCount;

    /**
     * 访客数(UV)
     */
    @Schema(description = "访客数(UV)")
    private Long uv;

    /**
     * IP数
     */
    @Schema(description = "IP数")
    private Long ipCount;

    /**
     * 平均访问页数
     */
    @Schema(description = "平均访问页数")
    private Float avgPv;

    /**
     * 平均访问时长
     */
    @Schema(description = "平均访问时长")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT, pattern = ".02f")
    private Float avgVisitTime;

    /**
     * 跳出率
     */
    @Schema(description = "跳出率")
    private Float bounceRate;

    /**
     * 渠道
     */
    @Schema(description = "渠道")
    private String channel;
}
