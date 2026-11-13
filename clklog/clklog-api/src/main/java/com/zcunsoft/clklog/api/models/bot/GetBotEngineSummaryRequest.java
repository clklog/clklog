package com.zcunsoft.clklog.api.models.bot;

import com.zcunsoft.clklog.api.models.summary.BaseSummaryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取搜索引擎抓取汇总的请求")
@Data
public class GetBotEngineSummaryRequest extends BaseSummaryRequest {

}
