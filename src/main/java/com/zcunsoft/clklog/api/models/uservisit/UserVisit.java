package com.zcunsoft.clklog.api.models.uservisit;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访问频次")
@Data
public class UserVisit {

	@Schema(description = "1次")
    private int v1Uv;

    @Schema(description = "2次")
    private int v2Uv;
    
    @Schema(description = "3次")
    private int v3Uv;
    
    @Schema(description = "4次")
    private int v4Uv;
    
    @Schema(description = "5次")
    private int v5Uv;
    
    @Schema(description = "6次")
    private int v6Uv;
    
    @Schema(description = "7次")
    private int v7Uv;
    
    @Schema(description = "8次")
    private int v8Uv;
    
    @Schema(description = "9次")
    private int v9Uv;
    
    @Schema(description = "10次")
    private int v10Uv;
    
    @Schema(description = "11-15次")
    private int v11_15Uv;
    
    @Schema(description = "16-50次")
    private int v16_50Uv;
    
    @Schema(description = "51-100次")
    private int v51_100Uv;
    
    @Schema(description = "101-200次")
    private int v101_200Uv;
    
    @Schema(description = "201-300次")
    private int v201_300Uv;
    
    @Schema(description = "300次以上")
    private int v300Uv;
    
}
