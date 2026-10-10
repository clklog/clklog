package com.zcunsoft.clklog.api.models.uservisit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访问页数")
@Data
public class UserPv {

    @Schema(description = "1页")
    private int pv1Uv;

    @Schema(description = "2-5页")
    private int pv2_5Uv;
    
    @Schema(description = "6-10页")
    private int pv6_10Uv;
    
    @Schema(description = "11-20页")
    private int pv11_20Uv;
    
    @Schema(description = "21-30页")
    private int pv21_30Uv;
    
    @Schema(description = "31-40页")
    private int pv31_40Uv;
    
    @Schema(description = "41-50页")
    private int pv41_50Uv;
    
    @Schema(description = "51-100页")
    private int pv51_100Uv;
    
    @Schema(description = "101页以上")
    private int pv101Uv;
    
    
}
