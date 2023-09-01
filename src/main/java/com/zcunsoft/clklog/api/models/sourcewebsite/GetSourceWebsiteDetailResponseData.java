package com.zcunsoft.clklog.api.models.sourcewebsite;

import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "获取来源网站详情的响应")
@Data
public class GetSourceWebsiteDetailResponseData {

    private FlowDetail total;

    private List<FlowDetail> detail;
}