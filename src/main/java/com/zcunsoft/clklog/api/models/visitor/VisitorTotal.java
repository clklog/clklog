package com.zcunsoft.clklog.api.models.visitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访客统计")
@Data
public class VisitorTotal {

	@Schema(description = "访客数(UV)")
	   private int uv;

	    @Schema(description = "新访客数")
	    private int newUv;
	    
	    @JsonInclude(JsonInclude.Include.NON_NULL)
	    @Schema(description = "访客占比")
	    private Float visitRate;

	    @Schema(description = "回流访客")
	    private int revisit;


	    @Schema(description = "沉默访客")
	    private int silent;

	    @Schema(description = "流失访客")
	    private int churn;

}
