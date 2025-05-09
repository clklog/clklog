package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.sysmgmt.dto.UserInfoDTO;
import com.zcunsoft.clklog.sysmgmt.dto.UserLoginDTO;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;

import java.util.HashMap;

/**
 * 认证服务接口.
 */
public interface IAuthService {

    /**
     * 登录响应.
     *
     * @param usrLogin 用户登录信息
     * @return 登录响应
     */
    ResponseBase<UserInfoDTO> login(UserLoginDTO usrLogin);

    /**
     * 获取用户.
     *
     * @return 用户表
     */
    ResponseBase<HashMap<String, Object>> getUser();
}
