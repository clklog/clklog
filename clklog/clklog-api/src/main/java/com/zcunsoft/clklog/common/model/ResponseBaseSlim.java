package com.zcunsoft.clklog.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 基本响应
 */
public class ResponseBaseSlim {
    /**
     * 结果错误代码.
     */

    @Schema(description = "错误代码")
    private int code = 200;

    /**
     * 描述.
     */

    @Schema(description = "错误信息")
    private String msg = "操作成功";


    /**
     * 初始化.
     */
    public ResponseBaseSlim() {

    }

    /**
     * 初始化.
     *
     * @param code 错误代码
     * @param msg  错误信息
     */
    public ResponseBaseSlim(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 获取 错误代码.
     *
     * @return 错误代码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置 错误代码.
     *
     * @param code 错误代码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取 错误信息.
     *
     * @return 错误信息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置 错误信息.
     *
     * @param msg 错误信息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

}
