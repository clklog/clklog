package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访客地域统计")
@Data
public class VisitorArea {

    @Schema(description = "地域")
    private String country;
    
    @Schema(description = "城市")
    private String city;
    
    @Schema(description = "省份")
    private String province;

//    @Schema(description = "访问次数")
//    private int visitCount;

}
