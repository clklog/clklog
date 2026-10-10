package com.zcunsoft.clklog.api.poi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "下载的请求")
@Data
public class DownloadRequest {

    @Schema(description = "时间类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "hour,day,week,month")
    private String timeType;

    @Schema(description = "渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"安卓\",\"苹果\",\"网站\",\"微信小程序\"]")
    private List<String> channel = new ArrayList<>();

    @Schema(description = "国家或地区", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"美国\",\"全球\"]")
    private List<String> country;

    @Schema(description = "地域", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"上海\",\"北京\",\"其他\"]")
    private List<String> province;

    @Schema(description = "访客类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "新访客,老访客")
    private String visitorType;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-08")
    private String startTime;
    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-10")
    private String endTime;

    @Schema(description = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private String projectName;
    
    @Schema(description = "下载列", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"index\",\"pv\",\"pvRate\",\"visitCount\",\"visitCountRate\",\"uv\",\"uvRate\",\"newUv\",\"newUvRate\",\"ipCount\",\"ipCountRate\",\"bounceRate\",\"avgVisitTime\",\"visitTimeRate\",\"avgPv\",\"avgPvRate\",\"downPvCount\",\"entryCount\",\"exitCount\",\"exitRate\"]")
    private List<String> cols;
}
