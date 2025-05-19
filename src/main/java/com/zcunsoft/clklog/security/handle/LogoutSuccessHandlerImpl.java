package com.zcunsoft.clklog.security.handle;

import com.zcunsoft.clklog.common.model.LoginUser;
import com.zcunsoft.clklog.common.model.ResponseBase;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import com.zcunsoft.clklog.common.utils.ServletUtils;
import com.zcunsoft.clklog.security.service.TokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义退出处理类 返回成功
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Resource
    private TokenService tokenService;

    @Resource
    private ObjectMapperUtil objectMapper;

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null) {
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());

        }
        String msg = "退出成功";
        ResponseBase<String> resp = new ResponseBase<String>(200, msg, "");
        String strResp = objectMapper.writeValueAsString(resp);

        ServletUtils.renderString(response, strResp);
    }
}
