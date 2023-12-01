package com.zcunsoft.clklog.api.models.visitor;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "分页获取实时数据的请求")
@Data
public class GetLogAnalysisListPageRequest {

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum;

    @Schema(description = "页长", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private int pageSize;


    @Schema(description = "渠道", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"安卓\",\"苹果\",\"网站\",\"微信小程序\"]")
    private List<String> channel = new ArrayList<>();

    @Schema(description = "国家或地区", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"美国\",\"全球\"]")
    private List<String> country;

    @Schema(description = "地域", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"上海\",\"北京\",\"其他\"]")
    private List<String> province;

    @Schema(description = "访客类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "新访客")
    private String visitorType;
//
//    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-08")
//    private String startTime;
//    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-10")
//    private String endTime;

    @Schema(description = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "")
    private String projectName;
    
//    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8609475f862bd2cc")
//    private String distinctId;
//    
//    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "98816BD0-9E22-43DF-88CA-C29EFD910474")
//    private String eventSessionId;
}
