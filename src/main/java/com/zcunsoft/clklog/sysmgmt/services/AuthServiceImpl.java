package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.common.model.LoginUser;
import com.zcunsoft.clklog.common.model.UserInfo;
import com.zcunsoft.clklog.common.utils.SecurityUtils;
import com.zcunsoft.clklog.security.service.TokenService;
import com.zcunsoft.clklog.sysmgmt.domains.UserLogin;
import com.zcunsoft.clklog.sysmgmt.dto.UserInfoDTO;
import com.zcunsoft.clklog.sysmgmt.dto.UserLoginDTO;
import com.zcunsoft.clklog.sysmgmt.models.enums.ErrorCode;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;
import com.zcunsoft.clklog.sysmgmt.repository.IUserLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * 认证服务.
 */
@Service
@Transactional("mysqlTransactionManager")
public class AuthServiceImpl implements IAuthService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TokenService tokenService;

    private final ICodeService codeService;

    private final IUserLoginRepository userLoginRepository;

    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(ICodeService codeService, IUserLoginRepository userLoginRepository,
                           AuthenticationManager authenticationManager, TokenService tokenService) {
        this.codeService = codeService;
        this.userLoginRepository = userLoginRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public ResponseBase<UserInfoDTO> login(UserLoginDTO usrLogin) {
        UserInfoDTO userInfo = null;
        ErrorCode code = ErrorCode.UserLoginFailed;
        try {
            // 用户验证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usrLogin.getUsername(), usrLogin.getPassword()));
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            String token = tokenService.createToken(loginUser);
            userInfo = new UserInfoDTO();
            userInfo.setToken(token);
            userInfo.setDisplayName(loginUser.getUser().getDisplayName());
            userInfo.setUserName(loginUser.getUsername());
            UserLogin userLogin = new UserLogin();
            userLogin.setUserName(usrLogin.getUsername());
            userLogin.setCreateTime(new Timestamp(System.currentTimeMillis()));
            userLogin.setToken(token);
            userLoginRepository.save(userLogin);

            code = ErrorCode.Success;
        } catch (Exception e) {
            logger.error("", e);
            if (e instanceof BadCredentialsException) {
                code = ErrorCode.WrongPassword;
            }
        }
        ResponseBase<UserInfoDTO> resp = new ResponseBase<UserInfoDTO>(code, codeService.getMessage(code), userInfo);
        return resp;
    }

    @Override
    public ResponseBase<HashMap<String, Object>> getUser() {
        UserInfoDTO userInfo = null;
        ErrorCode code = ErrorCode.UserLoginFailed;
        HashMap<String, Object> map = new HashMap<>();
        try {
            UserInfo user = SecurityUtils.getLoginUser().getUser();

            userInfo = new UserInfoDTO();
            userInfo.setDisplayName(user.getDisplayName());
            userInfo.setUserName(user.getUserName());

            map = new HashMap<>();
            map.put("user", userInfo);

            code = ErrorCode.Success;
        } catch (Exception e) {
            logger.error("", e);
            if (e instanceof BadCredentialsException) {
                code = ErrorCode.WrongPassword;
            }
        }
        ResponseBase<HashMap<String, Object>> resp = new ResponseBase<>(code, codeService.getMessage(code), map);
        return resp;
    }

}
