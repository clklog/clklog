package com.zcunsoft.clklog.api.models.trend;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Schema(description = "流量信息")
@Data
public class FlowDetail {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "统计时间")
    private String statTime;

    @Schema(description = "浏览量(PV)")
    private int pv;

    @Schema(description = "访问次数")
    private int visitCount;

    @Schema(description = "新访客数")
    private int newUv;

    @Schema(description = "访客数(UV)")
    private int uv;

    @Schema(description = "IP数")
    private int ipCount;

    @Schema(description = "平均访问页数")
    private float avgPv;

    @Schema(description = "访问时长")
    private int visitTime;

    @Schema(description = "平均访问时长")
    private float avgVisitTime;

    @Schema(description = "跳出率")
    private float bounceRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "渠道")
    private String channel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "浏览量占比")
    private Float pvRate = 0.0f;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访问次数占比")
    private Float visitCountRate = 0.0f;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访问时长占比")
    private Float visitTimeRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "新访客数占比")
    private Float newUvRate = 0.0f;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访客数占比")
    private Float uvRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "IP数占比")
    private Float ipCountRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "设备")
    private String device;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "来源网站")
    private String sourcesite;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访客类型")
    private String visitorType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "搜索词")
    private String searchword;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "省份")
    private String province;
    
    @Schema(description = "贡献下游流量")
    private int downPvCount;
    
    @Schema(description = "退出页次数")
    private int exitCount;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "退出率")
    private Float exitRate;

    @Schema(description = "入口页次数")
    private int entryCount;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "页面URL")
    private String uri;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "访客ID")
	private String distinctId;
	
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "上次访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp latestTime;
    
    @Schema(description = "回流访客")
    private int revisit;


    @Schema(description = "沉默访客")
    private int silent;

    @Schema(description = "流失访客")
    private int churn;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "标题")
	private String title;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "路径")
	private String uriPath;
}
