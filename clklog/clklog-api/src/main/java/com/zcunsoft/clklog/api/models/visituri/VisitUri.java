package com.zcunsoft.clklog.api.models.visituri;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "访问页面信息")
@Data
public class VisitUri {

    @Schema(description = "页面地址")
    private String  url;
}
