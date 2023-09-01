/**
 * 
 */
package com.zcunsoft.clklog.api.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author mersey
 * @param <T>
 *
 */
public class ResponseBase<T> extends ResponseBaseSlim {
	/** 结果. */
	@Schema(description = "响应数据")
	private T data;

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
