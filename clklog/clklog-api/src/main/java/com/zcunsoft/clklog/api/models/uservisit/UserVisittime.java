package com.zcunsoft.clklog.api.models.uservisit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访问时长")
@Data
public class UserVisittime {

	@Schema(description = "0-10秒")
    private int vt0_10Uv;

    @Schema(description = "10-30秒")
    private int vt10_30Uv;
    
    @Schema(description = "30-60秒")
    private int vt30_60Uv;
    
    @Schema(description = "60-120秒")
    private int vt60_120Uv;
    
    @Schema(description = "120-180秒")
    private int vt120_180Uv;
    
    @Schema(description = "180-240秒")
    private int vt180_240Uv;
    
    @Schema(description = "240-300秒")
    private int vt240_300Uv;
    
    @Schema(description = "300-600秒")
    private int vt300_600Uv;
    
    @Schema(description = "600-1800秒")
    private int vt600_1800Uv;
    
    @Schema(description = "1800-3600秒")
    private int vt1800_3600Uv;
    
    @Schema(description = "3600秒以上")
    private int vt3600Uv;
    
    
}