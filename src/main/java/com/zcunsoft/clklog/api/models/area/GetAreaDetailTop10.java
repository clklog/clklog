package com.zcunsoft.clklog.api.models.area;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "地域信息")
@Data
public class GetAreaDetailTop10 {

    @Schema(description = "国家")
    private String country;

	@Schema(description = "省份")
    private String province;
	
	@Schema(description = "访问次数")
    private int visitCount;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访问次数占比")
    private Float visitCountRate;
}
