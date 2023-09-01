package com.zcunsoft.clklog.api.models.area;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "分页获取来源网站详情的响应")
@Data
public class GetAreaDetailPageResponseData {

	private int total;
	
    private List<AreaDetail> rows;
}