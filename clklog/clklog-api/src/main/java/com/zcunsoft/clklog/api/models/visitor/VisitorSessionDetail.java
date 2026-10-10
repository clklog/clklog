package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Schema(description = "用户会话detail")
@Data
public class VisitorSessionDetail {

    @Schema(description = "客户IP")
    private String clientIp;
    
    
    
    @Schema(description = "省份")
    private String province;
    
    @Schema(description = "浏览量(PV)")
    private int pv;
    
}
