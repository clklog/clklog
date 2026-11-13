package com.zcunsoft.clklog.api.models.bot;

import com.zcunsoft.clklog.api.models.ResponseBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "获取机器人流量概览的响应")
@Data
@EqualsAndHashCode(callSuper = true)
public class GetBotOverviewResponse extends ResponseBase<GetBotOverviewData> {

}
