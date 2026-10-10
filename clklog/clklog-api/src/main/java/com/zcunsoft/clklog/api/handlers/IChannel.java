package com.zcunsoft.clklog.api.handlers;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Channel校验接口
 */
@Documented
@Constraint(validatedBy = ChannelValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IChannel {
    String message() default "请输入正确的渠道类型(安卓/苹果/网站/微信小程序/全部)等";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
