package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.sysmgmt.domains.User;
import com.zcunsoft.clklog.sysmgmt.dto.*;
import com.zcunsoft.clklog.sysmgmt.models.request.UserAddModel;
import com.zcunsoft.clklog.sysmgmt.models.request.UserEditModel;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;

/**
 * 用户管理服务接口.
 */
public interface IUserService {

    /**
     * 通过用户ID获取用户详情.
     *
     * @param userId 用户ID
     * @return 获取用户详情的响应
     */
    ResponseBase<UserDTO> get(String userId);

    /**
     * 通过用户名获取用户详情.
     *
     * @param userName 用户名
     * @return 用户详情
     */
    User getByUserName(String userName);

    /**
     * 添加用户.
     *
     * @param userAddModel 用户信息
     * @return 添加用户的响应
     */
    ResponseBase<CommonIdDTO> add(UserAddModel userAddModel);

    /**
     * 编辑用户.
     *
     * @param userEditModel 用户信息
     * @return 编辑用户的响应
     */
    ResponseBase<Boolean> edit(UserEditModel userEditModel);

    /**
     * 删除用户信息.
     *
     * @param userId 用户ID
     * @return 删除用户信息的响应
     */
    ResponseBase<Boolean> delete(String userId);

    /**
     * 分页获取用户列表.
     *
     * @param queryReq 分页获取用户列表的请求
     * @return 分页获取用户列表的响应
     */
    ResponseBase<ListWithTotalCount<UserDTO>> getUserPageList(UserQueryDTO queryReq);

    /**
     * 修改用户密码.
     *
     * @param userModifyPasswordDTO 用户密码信息
     * @return 修改用户密码的响应
     */
    ResponseBase<String> modifyPassword(UserModifyPasswordDTO userModifyPasswordDTO);

    /**
     * 修改用户密码e.
     *
     * @param userId                用户ID
     * @param userModifyPasswordDTO 用户密码信息
     * @return 修改用户密码的响应
     */
    ResponseBase<String> modifyAuthPassword(String userId, AuthModifyPasswordDTO userModifyPasswordDTO);

}
