package com.zcunsoft.clklog.api.models.uservisit;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "忠诚度分析")
@Data
public class BaseUserVisit {

	@Schema(description = "key")
    private String key;

    @Schema(description = "value")
    private int value;
    
    @Schema(description = "占比")
    private float rate;
}
