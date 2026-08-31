package com.zcunsoft.clklog.sysmgmt.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zcunsoft.clklog.common.utils.ObjectMapperUtil;
import com.zcunsoft.clklog.common.utils.SecurityUtils;
import com.zcunsoft.clklog.sysmgmt.models.enums.ErrorCode;
import com.zcunsoft.clklog.sysmgmt.models.request.OperRecordAddModel;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ObjectMapperUtil objectMapper;

    private final static String el = "execution(* com.zcunsoft.clklog.sysmgmt.controllers..*(..)) and @annotation(org.springframework.web.bind.annotation.RequestMapping)";

    /**
     * 需要脱敏的敏感字段名（大小写不敏感，含密码类与令牌类）
     */
    private final static Pattern SENSITIVE_KEY_PATTERN = Pattern.compile(
            "\"((?i:password|passwd|pwd|newpassword|oldpassword|token|secret|apikey|api_key|authorization|cookie))\"\\s*:\\s*(\"(?:[^\"\\\\]|\\\\.)*\"|[^,\\}\\s]+)",
            Pattern.CASE_INSENSITIVE);

    /**
     * 脱敏后的占位值
     */
    private final static String MASKED = "\"***\"";

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
        String controllerName = signature.getDeclaringType().getSimpleName().replaceAll("Controller", "");

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        // 用户管理接口仅允许管理员访问：非管理员拦截，但仍记录审计日志
        ResponseBase<?> deniedResponse = ("User".equalsIgnoreCase(controllerName) && !SecurityUtils.isAdmin())
                ? new ResponseBase<>(ErrorCode.ResourceLock, "资源不可用", null)
                : null;

        String para = "";
        Object result = null;

        Object[] args = pjp.getArgs();
        // 对每个 @RequestBody 参数逐个序列化并脱敏敏感字段，避免明文记录密码/令牌
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter p = method.getParameters()[i];
            if (p.isAnnotationPresent(RequestBody.class)) {
                try {
                    para = maskSensitive(objectMapper.writeValueAsString(args[i]));
                } catch (JsonProcessingException e) {
                    logger.error("", e);
                }
            }
        }

        String resultContent = "";
        try {
            if (result == null) {
                if (deniedResponse != null) {
                    // 无权限：不执行业务逻辑，直接构造锁定响应（仍记录审计）
                    result = deniedResponse;
                } else {
                    result = pjp.proceed();
                }
                // 响应结果同样脱敏，避免含敏感字段的返回体被写入审计日志
                resultContent = maskSensitive(objectMapper.writeValueAsString(result));
            }
        } catch (Throwable e) {
            logger.error("", e);
        }

        long endTime = System.currentTimeMillis();
        Timestamp ts = new Timestamp(endTime);


        OperRecordAddModel operrecord = new OperRecordAddModel();
        operrecord.setOpertime(ts);
        // 记录真实操作人，避免硬编码掩盖审计轨迹
        String operator = "unknown";
        try {
            operator = SecurityUtils.getUsername();
        } catch (Exception e) {
            logger.warn("获取当前操作人失败，使用默认值 unknown.", e);
        }
        operrecord.setUser(operator);
        operrecord.setAction(String.format("访问:%s;参数:%s;结果:%s", request.getRequestURI(), para, resultContent));
        operRecordService.add(operrecord);

        return result;
    }

    /**
     * 对 JSON 字符串中的敏感字段值进行脱敏.
     * 匹配 password/passwd/pwd/newpassword/oldpassword/token 等字段名，
     * 将其值替换为 "***"，不修改其他字段，避免明文记录密码/令牌到审计日志。
     * 该方式与 modifyPassword 等接口“敏感字段只入不出”的原则一致，统一应用于所有接口。
     *
     * @param json 序列化后的请求体或响应体 JSON
     * @return 脱敏后的 JSON
     */
    private static String maskSensitive(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        Matcher matcher = SENSITIVE_KEY_PATTERN.matcher(json);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // 保留字段名，仅替换值部分（分组2）为掩码
            String replacement = matcher.group(1) != null
                    ? "\"" + matcher.group(1) + "\":" + MASKED
                    : matcher.group(0);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
