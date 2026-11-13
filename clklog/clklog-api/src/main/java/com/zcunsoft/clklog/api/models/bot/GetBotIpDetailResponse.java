package com.zcunsoft.clklog.api.models.bot;

import com.zcunsoft.clklog.api.models.ResponseBase;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "分页获取机器人IP明细的响应")
@Data
@EqualsAndHashCode(callSuper = true)
public class GetBotIpDetailResponse extends ResponseBase<GetBotIpDetailData> {

}
