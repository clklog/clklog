package com.zcunsoft.clklog.api.models.visitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Schema(description = "用户基本信息")
@Data
public class VisitorDetailinfo {
	
	@Schema(description = "访客ID")
	private String distinctId;
	
	@Schema(description = "国家")
	private String country;

//	@Schema(description = "省")
//    String province;
	
	@Schema(description = "城市")
	private String city;
	
	@Schema(description = "客户端IP")
	private String client_ip;
    
	@Schema(description = "渠道")
	private String channel;
	
	@Schema(description = "访客类型")
    private String visitorType;
	
	@Schema(description = "设备")
	private String manufacturer;
    
	@Schema(description = "浏览量(PV)")
	private Integer pv;

	@Schema(description = "访问次数")
	private Integer visitCount;
 
	@Schema(description = "访问时长")
	private Integer visitTime;
    
    @Schema(description = "平均访问时长")
    private float avgVisitTime;
    
    @Schema(description = "平均访问页数")
    private float avgPv;
    
    @Schema(description = "最后访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp latestTime;
	
    @Schema(description = "首次访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp firstTime;
    
    @Schema(description = "访客地域统计")
    private List<VisitorArea> visitorAreaList;
}
