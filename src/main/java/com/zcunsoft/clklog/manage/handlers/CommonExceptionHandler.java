package com.zcunsoft.clklog.manage.handlers;

import com.zcunsoft.clklog.common.exception.ServiceException;
import com.zcunsoft.clklog.manage.models.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理.
 * 统一捕获业务异常并返回前端友好的结构化响应（R：code/msg/data），
 * 避免参数校验失败等异常直接暴露为 500 错误页。
 * 仅作用于 manage 包下的控制器。
 */
@RestControllerAdvice
public class CommonExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);

    /**
     * 处理业务异常，向前端返回具体的错误信息.
     *
     * @param e 业务异常
     * @return 结构化错误响应
     */
    @ExceptionHandler(ServiceException.class)
    public R<?> handleServiceException(ServiceException e) {
        int code = e.getCode() != null ? e.getCode() : 500;
        logger.warn("业务异常：{}", e.getMessage());
        return R.fail(code, e.getMessage());
    }

    /**
     * 兜底处理未预期的异常.
     *
     * @param e 异常
     * @return 结构化错误响应
     */
    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        logger.error("系统异常：", e);
        return R.fail(500, "系统异常，请联系管理员");
    }
}
