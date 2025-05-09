package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.common.utils.SecurityUtils;
import com.zcunsoft.clklog.common.utils.StringUtils;
import com.zcunsoft.clklog.sysmgmt.assemblers.UserAssembler;
import com.zcunsoft.clklog.sysmgmt.domains.User;
import com.zcunsoft.clklog.sysmgmt.dto.*;
import com.zcunsoft.clklog.sysmgmt.models.enums.ErrorCode;
import com.zcunsoft.clklog.sysmgmt.models.request.UserAddModel;
import com.zcunsoft.clklog.sysmgmt.models.request.UserEditModel;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;
import com.zcunsoft.clklog.sysmgmt.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.*;


/**
 * 用户管理服务
 */
@Service
@Transactional("mysqlTransactionManager")
public class UserServiceImpl implements IUserService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ICodeService codeService;

    private final IUserRepository userRepository;

    public UserServiceImpl(ICodeService codeService, IUserRepository userRepository) {
        this.codeService = codeService;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseBase<UserDTO> get(String userId) {
        UserDTO userInfo = null;
        ErrorCode code = ErrorCode.Failed;
        try {
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent()) {
                User user = optUser.get();
                userInfo = new UserDTO();
                userInfo.setDisplayName(user.getDisplayName());
                userInfo.setUserId(user.getUserId());
                userInfo.setUserName(user.getUserName());

                code = ErrorCode.Success;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        ResponseBase<UserDTO> resp = new ResponseBase<UserDTO>(code, codeService.getMessage(code), userInfo);
        return resp;
    }

    @Override
    public User getByUserName(String userName) {
        User user = null;

        try {
            Specification<User> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("userName"), userName));
                Predicate[] pre = new Predicate[predicates.size()];
                return cb.and(predicates.toArray(pre));
            };
            Optional<User> optUser = userRepository.findOne(spec);
            if (optUser.isPresent()) {
                user = optUser.get();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return user;
    }

    @Override
    public ResponseBase<CommonIdDTO> add(UserAddModel userAddModel) {
        CommonIdDTO idDTO = new CommonIdDTO();
        User m_user = null;
        ErrorCode code = ErrorCode.Failed;
        try {
            Specification<User> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get("userName"), userAddModel.getUserName()));
                Predicate[] pre = new Predicate[predicates.size()];
                return cb.and(predicates.toArray(pre));
            };
            Optional<User> optUser = userRepository.findOne(spec);
            if (!optUser.isPresent()) {
                m_user = new User();
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                m_user.setCreatetime(ts);
                m_user.setUserId(UUID.randomUUID().toString());
                m_user.setDisplayName(userAddModel.getDisplayName());
                m_user.setUserName(userAddModel.getUserName());
                m_user.setPassword(SecurityUtils.encryptPassword(userAddModel.getPassword()));
                userRepository.save(m_user);
                idDTO.setId(m_user.getUserId());
                code = ErrorCode.Success;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        ResponseBase<CommonIdDTO> resp = new ResponseBase<CommonIdDTO>(code, codeService.getMessage(code), idDTO);
        return resp;
    }

    @Override
    public ResponseBase<Boolean> edit(UserEditModel userEditModel) {
        boolean isOk = false;

        ErrorCode code = ErrorCode.Failed;

        try {
            Optional<User> optUser = userRepository.findById(userEditModel.getUserId());

            if (optUser.isPresent()) {
                User user = optUser.get();
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                user.setUserId(userEditModel.getUserId());
                user.setModifytime(ts);
                if (userEditModel.getPassword() != null && !userEditModel.getPassword().isEmpty()) {
                    if (userEditModel.getOldpassword() == null || userEditModel.getOldpassword().isEmpty()) {
                        code = ErrorCode.WrongPassword;
                    } else {
                        if (SecurityUtils.matchesPassword(userEditModel.getOldpassword(), user.getPassword())) {
                            user.setPassword(SecurityUtils.encryptPassword(userEditModel.getPassword()));
                            user.setDisplayName(userEditModel.getDisplayName());
                            userRepository.save(user);
                            isOk = true;
                            code = ErrorCode.Success;
                        } else {
                            code = ErrorCode.WrongPassword;
                        }
                    }
                } else {
                    user.setDisplayName(userEditModel.getDisplayName());
                    userRepository.save(user);
                    isOk = true;
                    code = ErrorCode.Success;
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        ResponseBase<Boolean> resp = new ResponseBase<Boolean>(code, codeService.getMessage(code), isOk);
        return resp;
    }

    @Override
    public ResponseBase<Boolean> delete(String userId) {
        boolean isOk = false;
        ErrorCode code = ErrorCode.Failed;
        try {
            Optional<User> optUser = userRepository.findById(userId);
            Set<String> adminName = new HashSet<>(Arrays.asList("clklog", "admin"));
            if (optUser.isPresent() && !adminName.contains(optUser.get().getUserName())) {
                userRepository.delete(optUser.get());
                isOk = true;
                code = ErrorCode.Success;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        ResponseBase<Boolean> resp = new ResponseBase<Boolean>(code, codeService.getMessage(code), isOk);
        return resp;
    }

    @Override
    public ResponseBase<ListWithTotalCount<UserDTO>> getUserPageList(UserQueryDTO queryReq) {
        ErrorCode code = ErrorCode.Failed;
        ListWithTotalCount<UserDTO> list = null;
        try {
            Pageable pageable = PageRequest.of(queryReq.getPageIndex() - 1, queryReq.getPageSize(),
                    Sort.by(Direction.ASC, "userName"));
            Specification<User> spec = (root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (queryReq.getUserName() != null && !queryReq.getUserName().isEmpty()) {
                    predicates.add(cb.like(root.get("userName"), "%" + queryReq.getUserName() + "%"));
                }
                if (queryReq.getDisplayName() != null && !queryReq.getDisplayName().isEmpty()) {
                    predicates.add(cb.like(root.get("displayName"), "%" + queryReq.getDisplayName() + "%"));
                }
                if (!predicates.isEmpty()) {
                    return cb.and(predicates.toArray(new Predicate[0]));
                } else {
                    return null;
                }
            };
            Page<User> pageresult = userRepository.findAll(spec, pageable);
            List<UserDTO> dtoList = (new UserAssembler()).toDTOList(pageresult.getContent());
            list = new ListWithTotalCount<UserDTO>(dtoList, (int) pageresult.getTotalElements());
            code = ErrorCode.Success;
        } catch (Exception e) {
            logger.error("", e);
        }
        ResponseBase<ListWithTotalCount<UserDTO>> resp = new ResponseBase<ListWithTotalCount<UserDTO>>(code,
                codeService.getMessage(code), list);
        return resp;
    }

    @Override
    public ResponseBase<String> modifyPassword(UserModifyPasswordDTO userModifyPasswordDTO) {
        String msg = "修改密码失败";
        ErrorCode code = ErrorCode.Failed;
        if (!StringUtils.isPasswordStandard(userModifyPasswordDTO.getNewPassword())) {
            msg = "必须包含大小写字母、数字、特殊字符（例如：!@#%&*.）且长度不得小于12位且不超过18位";
            ResponseBase<String> resp = new ResponseBase<String>(code, msg, null);
            return resp;
        }
        try {
            Optional<User> optUser = userRepository.findById(userModifyPasswordDTO.getUserId());
            if (optUser.isPresent()) {
                User user = optUser.get();
                user.setPassword(SecurityUtils.encryptPassword(userModifyPasswordDTO.getNewPassword()));
                userRepository.save(user);
                msg = "修改密码成功";
                code = ErrorCode.Success;
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        //ResponseBase<String> resp = new ResponseBase<String>(code, codeService.getMessage(code), msg);
        ResponseBase<String> resp = new ResponseBase<String>(code, msg, null);
        return resp;
    }

    @Override
    public ResponseBase<String> modifyAuthPassword(String userId, AuthModifyPasswordDTO authModifyPasswordDTO) {
        String msg = "修改密码失败";
        ErrorCode code = ErrorCode.Failed;
        if (!StringUtils.isPasswordStandard(authModifyPasswordDTO.getNewPassword())) {
            msg = "必须包含大小写字母、数字、特殊字符（例如：!@#%&*.）且长度不得小于12位且不超过18位";
            ResponseBase<String> resp = new ResponseBase<String>(code, msg, null);
            return resp;
        }
        try {
            Optional<User> optUser = userRepository.findById(userId);
            if (optUser.isPresent()) {
                User user = optUser.get();
                if (SecurityUtils.matchesPassword(authModifyPasswordDTO.getOldPassword(), user.getPassword())) {
                    user.setPassword(SecurityUtils.encryptPassword(authModifyPasswordDTO.getNewPassword()));
                    userRepository.save(user);
                    msg = "修改密码成功";
                    code = ErrorCode.Success;
                } else {
                    msg = "原密码错误";
                    code = ErrorCode.Failed;
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        ResponseBase<String> resp = new ResponseBase<String>(code, msg, null);
        return resp;
    }
}
