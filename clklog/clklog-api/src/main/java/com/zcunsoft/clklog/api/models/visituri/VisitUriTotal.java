package com.zcunsoft.clklog.api.models.visituri;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "受访页面统计")
@Data
public class VisitUriTotal {

	@Schema(description = "浏览量(PV)")
    private int pv;
	
	@Schema(description = "访客数(UV)")
	   private int uv;

	    @Schema(description = "贡献下游流量")
	    private int downPvCount;
	    
	    @Schema(description = "退出页次数")
	    private int exitCount;
	    
	    @Schema(description = "平均访问时长")
	    private float avgVisitTime;
	    
	    @JsonInclude(JsonInclude.Include.NON_NULL)
	    @Schema(description = "跳出率")
	    private Float bounceRate;
	    
	    

}
