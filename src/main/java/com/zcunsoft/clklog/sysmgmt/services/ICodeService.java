package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.sysmgmt.models.enums.ErrorCode;

/**
 * 错误码的说明服务接口
 */
public interface ICodeService {
    /**
     * 获取指定错误码的说明.
     *
     * @param code 错误码
     * @return 说明
     */
    String getMessage(ErrorCode code);

    /**
     * 获取指定错误码的说明.
     *
     * @param code 错误码
     * @param args 参数
     * @return 说明
     */
    String getMessage(ErrorCode code, Object[] args);
}
