package com.zcunsoft.clklog.api.models.area;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "地域信息")
@Data
public class AreaDetailCompare {
    @Schema(description = "国家")
    private String country;
    
    @Schema(description = "省份")
    private String province;

    @Schema(description = "对比数据")
    private List<AreaDetail> rows;
}
