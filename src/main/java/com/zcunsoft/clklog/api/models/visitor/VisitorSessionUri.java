package com.zcunsoft.clklog.api.models.visitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Schema(description = "用户会话URI")
@Data
public class VisitorSessionUri {
	
	@Schema(description = "访客ID")
	private String distinctId;

    @Schema(description = "URI")
    private String uri;
    
    @Schema(description = "会话ID")
    private String eventSessionId;
    
    @Schema(description = "访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp logTime;
    
}
