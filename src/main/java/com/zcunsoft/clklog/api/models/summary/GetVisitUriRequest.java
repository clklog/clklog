package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "获取Top10受访页面的请求")
@Data
public class GetVisitUriRequest extends BaseSummaryRequest {

}
