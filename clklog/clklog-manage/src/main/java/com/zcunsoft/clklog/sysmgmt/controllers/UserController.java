package com.zcunsoft.clklog.sysmgmt.controllers;

import com.zcunsoft.clklog.sysmgmt.dto.*;
import com.zcunsoft.clklog.sysmgmt.models.request.UserAddModel;
import com.zcunsoft.clklog.sysmgmt.models.request.UserDeleteModel;
import com.zcunsoft.clklog.sysmgmt.models.request.UserEditModel;
import com.zcunsoft.clklog.sysmgmt.models.request.UserGetModel;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;
import com.zcunsoft.clklog.sysmgmt.services.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户管理.
 */
@RestController
@RequestMapping(path = "/user")
@Tag(name = "用户管理", description = "用户管理")
public class UserController {
    @Resource
    IUserService userService;

    @Operation(summary = "获取用户信息")
    @ResponseBody
    @RequestMapping(path = "/get", method = RequestMethod.POST)
    public ResponseBase<UserDTO> get(@RequestBody UserGetModel userGetModel) {
        return userService.get(userGetModel.getUserId());
    }

    @Operation(summary = "添加用户")
    @ResponseBody
    @RequestMapping(path = "/add", method = RequestMethod.POST)
    public ResponseBase<CommonIdDTO> add(@RequestBody UserAddModel userAddModel) {
        return userService.add(userAddModel);
    }

    @Operation(summary = "编辑用户")
    @ResponseBody
    @RequestMapping(path = "/edit", method = RequestMethod.POST)
    public ResponseBase<Boolean> edit(@RequestBody UserEditModel userEditModel) {
        return userService.edit(userEditModel);
    }

    @Operation(summary = "修改密码（管理员）")
    @ResponseBody
    @RequestMapping(path = "/modifyPassword", method = RequestMethod.POST)
    public ResponseBase<String> modifyPassword(@RequestBody UserModifyPasswordDTO userModifyPasswordDTO) {
        return userService.modifyPassword(userModifyPasswordDTO);
    }

    @Operation(summary = "删除用户")
    @ResponseBody
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public ResponseBase<Boolean> delete(@RequestBody UserDeleteModel userDeleteModel) {
        return userService.delete(userDeleteModel.getUserId());
    }

    @Operation(summary = "获取用户列表")
    @RequestMapping(path = "/getlistpage", method = RequestMethod.POST)
    public ResponseBase<ListWithTotalCount<UserDTO>> getList(@RequestBody UserQueryDTO queryReq) {
        return userService.getUserPageList(queryReq);
    }
}
