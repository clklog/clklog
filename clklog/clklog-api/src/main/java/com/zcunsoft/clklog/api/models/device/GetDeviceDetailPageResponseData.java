package com.zcunsoft.clklog.api.models.device;

import com.zcunsoft.clklog.api.models.trend.FlowDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "分页获取设备详情的响应")
@Data
public class GetDeviceDetailPageResponseData {

    private int total;

    private List<FlowDetail> rows;
}