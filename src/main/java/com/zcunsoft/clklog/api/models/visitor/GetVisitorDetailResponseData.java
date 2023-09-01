package com.zcunsoft.clklog.api.models.visitor;

import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "获取访客详情的响应")
@Data
public class GetVisitorDetailResponseData {

    @Schema(description = "访客类型列表")
    private List<FlowDetail> visitorDetail;
   


}
