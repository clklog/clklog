package com.zcunsoft.clklog.api.models.summary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Schema(description = "获取地域的请求")
@Data
public class GetAreaRequest extends BaseSummaryRequest {

	@Schema(description = "访客类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "新访客")
    private String visitorType;
	
}
