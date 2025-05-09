package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.common.exception.ServiceException;
import com.zcunsoft.clklog.common.model.LoginUser;
import com.zcunsoft.clklog.common.model.UserInfo;
import com.zcunsoft.clklog.sysmgmt.domains.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户验证处理
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Resource
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUserName(username);
        if (user == null) {
            logger.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        }

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(User user) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        return new LoginUser(user.getUserId(), userInfo);
    }
}
