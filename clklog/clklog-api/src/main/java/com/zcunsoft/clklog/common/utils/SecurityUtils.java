package com.zcunsoft.clklog.common.utils;

import com.zcunsoft.clklog.common.exception.ServiceException;
import com.zcunsoft.clklog.common.model.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全服务工具类
 */
public class SecurityUtils {
    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public static String getUserId() {
        try {
            return getLoginUser().getUserId();
        } catch (Exception e) {
            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED.value());
        }
    }


    /**
     * 获取用户
     *
     * @return 用户信息
     */
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED.value());
        }
    }

    /**
     * 获取Authentication
     *
     * @return {@link Authentication }
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
