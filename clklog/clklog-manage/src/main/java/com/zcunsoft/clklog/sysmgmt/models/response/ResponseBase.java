package com.zcunsoft.clklog.sysmgmt.models.response;

import com.zcunsoft.clklog.sysmgmt.models.enums.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 响应基类
 */
@Schema(description = "响应基类")
public class ResponseBase<T> {
    /**
     * 结果编码
     */
    @Schema(description = "结果编码", required = true, example = "0")
    private ErrorCode code;
    /**
     * 结果信息
     */
    @Schema(description = "结果信息", required = true, example = "成功")
    private String msg;

    /**
     * 结果数据
     */
    @Schema(description = "结果数据", required = true)
    private T data;

    public ResponseBase(ErrorCode code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ErrorCode getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
