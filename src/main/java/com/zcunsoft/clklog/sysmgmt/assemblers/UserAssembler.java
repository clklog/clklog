package com.zcunsoft.clklog.sysmgmt.assemblers;

import com.zcunsoft.clklog.sysmgmt.domains.User;
import com.zcunsoft.clklog.sysmgmt.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * User装配工具
 */
public class UserAssembler {
    public UserDTO toUserDTO(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setDisplayName(user.getDisplayName());
        userDto.setUserId(user.getUserId());
        userDto.setUserName(user.getUserName());
        userDto.setCreateuser(user.getCreateuser());
        userDto.setCreatetime(user.getCreatetime());
        userDto.setModifyuser(user.getModifyuser());
        userDto.setModifytime(user.getModifytime());
        userDto.setLastlogintime(user.getLastlogintime());
        return userDto;
    }

    public List<UserDTO> toDTOList(List<User> activities) {
        final List<UserDTO> dtolList = new ArrayList<UserDTO>(activities.size());
        for (User user : activities) {
            dtolList.add(toUserDTO(user));
        }
        return dtolList;
    }
}
