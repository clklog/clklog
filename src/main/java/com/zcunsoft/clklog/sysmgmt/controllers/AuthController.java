package com.zcunsoft.clklog.sysmgmt.controllers;

import com.zcunsoft.clklog.common.utils.SecurityUtils;
import com.zcunsoft.clklog.sysmgmt.dto.AuthModifyPasswordDTO;
import com.zcunsoft.clklog.sysmgmt.dto.UserInfoDTO;
import com.zcunsoft.clklog.sysmgmt.dto.UserLoginDTO;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;
import com.zcunsoft.clklog.sysmgmt.services.IAuthService;
import com.zcunsoft.clklog.sysmgmt.services.IUserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * 权限管理.
 */
@RestController
@RequestMapping(path = "/auth")
@Tag(name = "权限管理", description = "权限管理")
public class AuthController {
    @Resource
    IAuthService authService;

    @Resource
    IUserService userService;

    @Operation(summary = "用户登录")
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseBase<UserInfoDTO> login(@RequestBody UserLoginDTO usrLogin) {
        return authService.login(usrLogin);
    }

    @Hidden
    @Operation(summary = "获取用户信息")
    @RequestMapping(path = "/getUser", method = RequestMethod.POST)
    public ResponseBase<HashMap<String, Object>> getUser() {
        return authService.getUser();
    }

    @Operation(summary = "修改密码（登录用户）")
    @RequestMapping(path = "/modifyPassword", method = RequestMethod.POST)
    public ResponseBase<String> modifyPassword(@RequestBody AuthModifyPasswordDTO authModifyPasswordDTO) {
        return userService.modifyAuthPassword(SecurityUtils.getUserId(), authModifyPasswordDTO);
    }
}
