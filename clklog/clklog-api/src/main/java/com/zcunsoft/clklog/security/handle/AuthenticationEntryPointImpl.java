package com.zcunsoft.clklog.security.handle;


import com.zcunsoft.clklog.common.model.ResponseBase;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import com.zcunsoft.clklog.common.utils.ServletUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Resource
    private ObjectMapperUtil objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        String msg = String.format("请求访问：%s，认证失败，无法访问系统资源", request.getRequestURI());
        ResponseBase<String> resp = new ResponseBase<String>(403, msg, "");
        String strResp = objectMapper.writeValueAsString(resp);
        ServletUtils.renderString(response, strResp);
    }
}
