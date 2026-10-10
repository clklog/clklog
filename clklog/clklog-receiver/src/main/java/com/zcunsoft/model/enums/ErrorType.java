package com.zcunsoft.model.enums;

/**
 * 返回错误码枚举类
 */
public enum ErrorType {
    Success("success"), PARSE_DATA_FAILED("parse_data_failed"), RAW_MESSAGE_EMPTY("raw_message_empty"),
    PROJECT_NOT_EXIST("project_not_exist"), PROJECT_TOKEN_ERROR("project_token_error");
    private final String value;

    ErrorType(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}
