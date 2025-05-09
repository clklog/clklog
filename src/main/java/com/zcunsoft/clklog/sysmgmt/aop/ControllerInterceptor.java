package com.zcunsoft.clklog.sysmgmt.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import com.zcunsoft.clklog.sysmgmt.models.request.OperRecordAddModel;
import com.zcunsoft.clklog.sysmgmt.services.IAuthService;
import com.zcunsoft.clklog.sysmgmt.services.IOperRecordService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;

/**
 * 拦截器
 */
@Aspect
@Component
public class ControllerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    IOperRecordService operRecordService;

    @Resource
    IAuthService authService;

    @Resource
    private ObjectMapperUtil objectMapper;

    private final static String el = "execution(* com.zcunsoft.clklog.sysmgmt.controllers..*(..)) and @annotation(org.springframework.web.bind.annotation.RequestMapping)";

    /**
     * 定义拦截规则：拦截com.zcunsoft.clklog.sysmgmt.controllers包下面的所有类中，有@RequestMapping注解的方法。
     */
    @Pointcut(el)
    public void controllerMethodPointcut() {
    }

    /**
     * 拦截器具体实现
     *
     * @param pjp ProceedingJoinPoint
     * @return JsonResult（被拦截方法的执行结果，或需要登录的错误提示。）
     */
    @Around("controllerMethodPointcut()") // 指定拦截器规则；也可以直接把“execution(*
    // com.xjj.........)”写进这里
    public Object Interceptor(ProceedingJoinPoint pjp) {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod(); // 获取被拦截的方法
        String methodName = method.getName(); // 获取被拦截的方法名
        String controllerName = signature.getDeclaringType().getSimpleName().replaceAll("Controller", "");

        String para = "";
        Object result = null;

        Object[] args = pjp.getArgs();
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter p = method.getParameters()[i];
            if (p.isAnnotationPresent(RequestBody.class)) {
                try {
                    para = objectMapper.writeValueAsString(args[i]);
                } catch (JsonProcessingException e) {
                    logger.error("", e);
                }
            }
        }

        String resultContent = "";
        try {
            if (result == null) {
                result = pjp.proceed();

                resultContent = objectMapper.writeValueAsString(result);
            }
        } catch (Throwable e) {
            logger.error("", e);
        }

        long endTime = System.currentTimeMillis();
        Timestamp ts = new Timestamp(endTime);

        String desc = "";

        if (methodName.equalsIgnoreCase("add")) {
            desc += "增加";
        } else if (methodName.equalsIgnoreCase("edit")) {
            desc += "编辑";
        } else if (methodName.equalsIgnoreCase("delete")) {
            desc += "删除";
        } else if (methodName.equalsIgnoreCase("enable")) {
            desc += "启用";
        } else if (methodName.equalsIgnoreCase("audit")) {
            desc += "审核";
        } else if (methodName.equalsIgnoreCase("cancel")) {
            desc += "撤回";
        }
        if (!desc.isEmpty()) {
            if (controllerName.equalsIgnoreCase("User")) {
                desc += "用户";
            }
            OperRecordAddModel operrecord = new OperRecordAddModel();
            operrecord.setOpertime(ts);
            operrecord.setUser("admin");
            operrecord.setAction(String.format("%s;参数:%s;结果:%s", desc, para, resultContent));
            operRecordService.add(operrecord);
        }
        return result;
    }
}
