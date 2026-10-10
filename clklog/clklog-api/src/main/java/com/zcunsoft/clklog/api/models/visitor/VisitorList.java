package com.zcunsoft.clklog.api.models.visitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Schema(description = "用户列表")
@Data
public class VisitorList {
	
	@Schema(description = "访客ID")
	private String distinctId;
	
    @Schema(description = "上次访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp latestTime;


    @Schema(description = "平均访问页数")
    private float avgPv;

    @Schema(description = "停留时长")
    private int visitTime;

    @Schema(description = "浏览量(PV)")
    private int pv;
    
    @Schema(description = "访客类型")
    private String visitorType;
    
    @Schema(description = "访问次数")
    private int visitCount;
}
