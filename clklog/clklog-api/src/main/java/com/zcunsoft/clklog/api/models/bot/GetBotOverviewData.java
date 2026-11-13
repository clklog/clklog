package com.zcunsoft.clklog.api.models.bot;

import com.zcunsoft.clklog.api.models.summary.FlowSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "机器人流量概览数据（机器人/真人同口径对比）")
@Data
public class GetBotOverviewData {

    @Schema(description = "机器人流量（log_analysis_bot）")
    private FlowSummary bot;

    @Schema(description = "真人流量（flow_trend_bydate，与机器人同时间范围同项目）")
    private FlowSummary human;
}
