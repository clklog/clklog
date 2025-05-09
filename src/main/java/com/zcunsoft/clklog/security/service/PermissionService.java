package com.zcunsoft.clklog.security.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zcunsoft.clklog.common.constant.Constants;
import com.zcunsoft.clklog.common.model.LoginUser;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import com.zcunsoft.clklog.common.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 权限验证服务.
 */
@Component
public class PermissionService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private StringRedisTemplate queueRedisTemplate;

    @Resource
    private ObjectMapperUtil objectMapper;

    /**
     * 验证是否有访问controllerName/methodName权限.
     *
     * @param args                   参数
     * @param controllerName         控制器名
     * @param methodName             接口名
     * @param checkProjectPermission 是否验证项目权限
     * @return true:有权限,false:无权限
     */
    public boolean checkPermission(Object[] args, String controllerName, String methodName,
                                   boolean checkProjectPermission) {
        boolean isValid = false;
        try {
            if (args != null && args.length >= 1 && args[0] != null) {
                if (checkProjectPermission) {
                    JsonNode jn = objectMapper.readTree(objectMapper.writeValueAsString(args[0]));
                    JsonNode objProject = jn.get("projectName");
                    if (objProject != null) {
                        String projectName = objProject.asText();
                        isValid = checkPermission(projectName, controllerName, methodName);
                    }
                } else {
                    isValid = true;
                }
            }
        } catch (Exception ex) {
            logger.error("checkPermission error ", ex);
        }
        return isValid;
    }

    /**
     * 验证是否项目有访问controllerName/methodName权限.
     *
     * @param projectName    项目编码
     * @param controllerName 控制器名
     * @param methodName     接口名
     * @return true:有权限,false:无权限
     */
    public boolean checkPermission(String projectName, String controllerName, String methodName) {
        boolean isValid = false;
        try {
            if (StringUtils.isNotBlank(projectName)) {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null) {
                    List<String> topUserList = new ArrayList<>(Arrays.asList("clklog", "admin"));
                    if (topUserList.contains(loginUser.getUsername())) {
                        isValid = true;
                    } else {
                        if (loginUser.getPerms().containsKey(projectName)) {
                            Set<String> permissionSet = loginUser.getPerms().get(projectName);
                            String path = ("/" + controllerName + "/" + methodName).toLowerCase(Locale.ROOT);
                            Object permission = queueRedisTemplate.opsForHash().get(Constants.METHOD_PERMISSION_HASH,
                                    path);
                            if (permission != null) {
                                String[] permissionArr = permission.toString().split(",", -1);
                                permissionSet.retainAll(Arrays.asList(permissionArr));
                                if (permissionSet.size() > 0) {
                                    isValid = true;
                                }
                            } else {
                                isValid = true;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("checkPermission error ", ex);
        }
        return isValid;
    }
}
