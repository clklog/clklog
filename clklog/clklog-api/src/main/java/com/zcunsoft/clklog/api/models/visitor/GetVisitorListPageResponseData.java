package com.zcunsoft.clklog.api.models.visitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Schema(description = "分页获取用户列表的响应")
@Data
public class GetVisitorListPageResponseData {

	private int total;
	
    private List<VisitorList> rows;
}