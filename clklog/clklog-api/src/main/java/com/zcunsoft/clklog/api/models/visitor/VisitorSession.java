package com.zcunsoft.clklog.api.models.visitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Schema(description = "用户会话")
@Data
public class VisitorSession {
	
	@Schema(description = "访客ID")
	private String distinctId;
	
    @Schema(description = "访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp firstTime;

    @Schema(description = "访问时长")
    private int visitTime;

//    @Schema(description = "客户IP")
//    private String clientIp;
    
    @Schema(description = "会话ID")
    private String eventSessionId;
    
    
//    @Schema(description = "省份")
//    private String province;
    
    @Schema(description = "浏览量(PV)")
    private int pv;
    
    @Schema(description = "来源网站")
    private String sourcesite;
    
    @Schema(description = "搜索词")
    private String searchword;
    
    private List<VisitorSessionDetail> rows;
}
