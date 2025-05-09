package com.zcunsoft.clklog.sysmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * ID主键视图类
 */
@Schema(description = "ID主键视图类")
@Data
public class CommonIdDTO {
    /**
     * ID主键
     */
    @Schema(description = "ID主键", example = "72754fa3-040a-4177-8edb-2a2df81b7847")
    private String id;

}
