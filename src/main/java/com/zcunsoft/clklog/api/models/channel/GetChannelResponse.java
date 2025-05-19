package com.zcunsoft.clklog.api.models.channel;

import com.zcunsoft.clklog.api.models.ResponseBase;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 获取项目渠道的响应
 */
@Schema(description = "获取项目渠道的响应")
public class GetChannelResponse extends ResponseBase<List<Channel>> {


}
