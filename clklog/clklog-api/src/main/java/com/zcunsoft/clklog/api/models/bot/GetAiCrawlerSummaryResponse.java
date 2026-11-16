package com.zcunsoft.clklog.api.models.bot;

import com.zcunsoft.clklog.api.models.ResponseBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "获取AI爬虫抓取汇总的响应")
@Data
@EqualsAndHashCode(callSuper = true)
public class GetAiCrawlerSummaryResponse extends ResponseBase<GetAiCrawlerSummaryData> {

}
