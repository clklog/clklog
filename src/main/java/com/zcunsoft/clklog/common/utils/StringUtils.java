package com.zcunsoft.clklog.common.utils;

/**
 * 字符串工具类
 */
public class StringUtils {
    /**
     * 密码格式必须包含大小写字母、数字、特殊字符,长度不得小于12位且不超过18位；
     *
     * @param password 密码
     * @return 是否符合密码规范
     */
    public static boolean isPasswordStandard(String password) {
        if (password.length() < 12 || password.length() > 18) {
            return false;
        }
        boolean hasDigit = false;
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasSpecialChar = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else {
                hasSpecialChar = true;
            }
        }
        return hasDigit && hasLowerCase && hasUpperCase && hasSpecialChar;
    }
}
