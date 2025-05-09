/**
 *
 */
package com.zcunsoft.clklog.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * 响应
 *
 */
public class ResponseBase<T> extends ResponseBaseSlim {

    /** 成功 */
    public static final int SUCCESS = 200;

    /** 失败 */
    public static final int FAIL = 500;

    /** 结果. */
    @Schema(description = "响应数据")
    private T data;

    public static <T> ResponseBase<T> ok() {
        return restResult(null, SUCCESS, null);
    }

    public static <T> ResponseBase<T> ok(T data) {
        return restResult(data, SUCCESS, null);
    }

    public static <T> ResponseBase<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> ResponseBase<T> fail() {
        return restResult(null, FAIL, null);
    }

    public static <T> ResponseBase<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> ResponseBase<T> fail(T data) {
        return restResult(data, FAIL, null);
    }

    public static <T> ResponseBase<T> fail(T data, String msg) {
        return restResult(data, FAIL, msg);
    }

    public static <T> ResponseBase<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    private static <T> ResponseBase<T> restResult(T data, int code, String msg) {
        ResponseBase<T> apiResult = new ResponseBase<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    /**
     * 初始化.
     */
    public ResponseBase() {

    }

    /**
     * 初始化.
     *
     * @param code 错误代码
     * @param msg  描述
     * @param data 响应结果
     */
    public ResponseBase(int code, String msg, T data) {
        super(code, msg);
        this.data = data;
    }

    /**
     * 获取 响应结果.
     *
     * @return 响应结果
     */
    public T getData() {
        return data;
    }

    /**
     * 设置 响应结果.
     *
     * @param returnData 响应结果
     */
    public void setData(T returnData) {
        this.data = returnData;
    }

}
