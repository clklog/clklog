package com.zcunsoft.clklog.api.models.bot;

import com.zcunsoft.clklog.api.models.ResponseBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "分页获取AI爬虫抓取页面的响应")
@Data
@EqualsAndHashCode(callSuper = true)
public class GetAiCrawlerPageResponse extends ResponseBase<GetAiCrawlerPageData> {

}
