package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "访客的访问信息")
@Data
public class Visit {

    @Schema(description = "访问ID")
    private String  sessionId;

    @Schema(description = "访问Ip")
    private List<String> clientIpList=new ArrayList<>();

    @Schema(description = "首次访问时间")
    private String visitStart;
}
