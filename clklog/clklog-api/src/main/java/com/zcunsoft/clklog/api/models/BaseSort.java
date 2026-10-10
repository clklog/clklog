/**
 * 
 */
package com.zcunsoft.clklog.api.models;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "排序")
@Data
public class BaseSort  {
	 @Schema(description = "属性", requiredMode = Schema.RequiredMode.REQUIRED, example = "pv,visitCount,newUv,uv,ipCount,exitCount,entryCount,downPvCount,latestTime,bounceCount,searchword,avgVisitTime,avgPv,bounceRate,exitRate,visitTime")
	 private String sortName;
	 
	 @Schema(description = "顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "desc,asc")
	 private String sortOrder;
}
