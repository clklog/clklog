package com.zcunsoft.clklog.sysmgmt.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 返回错误码枚举类
 */
public enum ErrorCode {
    Success(200), Forbidden(403), UserLoginFailed(501), WrongPassword(502),

    Failed(500);

    private final int value;

    ErrorCode(int value) {
        this.value = value;
    }

    public static ErrorCode valueOf(int type) {
        for (ErrorCode codeValue : values()) {
            if (codeValue.value == type) {
                return codeValue;
            }
        }
        throw new IllegalArgumentException(
                String.format("There is no value with type '%s' in Enum %s", type, ErrorCode.class.getName()));
    }

    @JsonValue
    public int getValue() {
        return value;
    }
}
