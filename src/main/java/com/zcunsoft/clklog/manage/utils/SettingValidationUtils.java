package com.zcunsoft.clklog.manage.utils;

import com.zcunsoft.clklog.common.exception.ServiceException;
import inet.ipaddr.IPAddressString;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 设置项输入校验工具.
 * 全局设置与项目设置的排除策略（IP、UA、URL参数）会序列化为 JSON 写入 Redis，
 * 下游采集服务直接消费。为防止非法/超大/恶意输入经 Redis 投毒污染下游，
 * 所有写入前须经此处统一校验。
 */
public class SettingValidationUtils {

    /**
     * 单个排除策略字段最大长度，防止超大对象写入 Redis 造成内存耗尽
     */
    private static final int MAX_FIELD_LEN = 10000;

    /**
     * 单行排除规则最大长度，防止超长正则引发 ReDoS 放大
     */
    private static final int MAX_LINE_LEN = 256;

    /**
     * 站内搜索关键词参数：仅允许英文字母、数字、下划线、中杠
     */
    private static final Pattern SEARCHWORD_KEY_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    /**
     * 网址（URL）：可选 http(s)://，域名或IP（含localhost），可选端口与路径
     */
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?" // 可选协议
                    + "(([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,63}" // 域名
                    + "|localhost" // 本地域名
                    + "|\\[?[0-9a-fA-F:]+\\]?" // IPv6
                    + "|\\d{1,3}(\\.\\d{1,3}){3})" // IPv4
                    + "(:[0-9]{1,5})?" // 可选端口
                    + "(/[\\w./?%&=#-]*)?$"); // 可选路径/查询

    /**
     * 校验单行 IP 规则.
     * 纯 IPv4/IPv6 及 CIDR 交由 ipaddress 库严格校验（含掩码 0-32 / 0-128）；
     * 通配符仅支持结尾形式，先转成对应的 CIDR 掩码后再校验：
     * ".*.*.*" -> ".0.0.0/8"、"*.*.*.*" -> "0.0.0.0/0"、
     * ".*.*"   -> ".0.0/16"、"*.*"     -> "0.0.0/16"、
     * ".*"     -> ".0/24"、  "*"       -> "0.0.0/24"。
     *
     * @param line 单行规则
     * @return 是否合法
     */
    private static boolean isValidIpLine(String line) {
        // 通配符形式：仅处理结尾的 .*.*.* / .*.* / .*，转成 CIDR 掩码后再校验
        if (line.indexOf('*') >= 0) {
            String cidr = line;
            if (cidr.endsWith(".*.*.*")) {
                cidr = cidr.substring(0, cidr.length() - ".*.*.*".length()) + ".0.0.0/8";
            } else if (cidr.endsWith(".*.*")) {
                cidr = cidr.substring(0, cidr.length() - ".*.*".length()) + ".0.0/16";
            } else if (cidr.endsWith(".*")) {
                cidr = cidr.substring(0, cidr.length() - ".*".length()) + ".0/24";
            } else {
                // 整段全通配符（无前置点）：* / *.* / *.*.* 等
                if (cidr.equals("*")) {
                    cidr = "0.0.0.0/0";
                } else if (cidr.equals("*.*")) {
                    cidr = "0.0.0.0/16";
                } else if (cidr.equals("*.*.*")) {
                    cidr = "0.0.0.0/24";
                } else {
                    return false; // 仅支持结尾通配符，其他形式非法
                }
            }
            return isIpAddress(cidr);
        }
        // 纯 IP 或 CIDR：交由 ipaddress 库校验（自动校验地址族与掩码范围）
        return isIpAddress(line);
    }

    /**
     * 判断是否为合法 IP 地址（IPv4/IPv6）或 CIDR.
     *
     * @param value 待校验字符串
     * @return 是否合法
     */
    private static boolean isIpAddress(String value) {
        try {
            return new IPAddressString(value).isValid();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验排除策略三个字段.
     *
     * @param excludedIp        排除的IP列表（每行一个，支持CIDR/通配符）
     * @param excludedUa        排除的UA列表（每行一个，支持正则）
     * @param excludedUrlParams 排除的URL参数列表（每行一个，支持正则）
     */
    public static void validateExcludedStrategy(String excludedIp, String excludedUa, String excludedUrlParams) {
        validateExcludedIp(excludedIp);
        validateExcludedUa(excludedUa);
        validateExcludedUrlParams(excludedUrlParams);
    }

    /**
     * 校验排除IP字段：每行一个，支持 IPv4/IPv6/CIDR/结尾通配符.
     *
     * @param excludedIp 排除IP列表，允许为空
     */
    public static void validateExcludedIp(String excludedIp) {
        checkIpLines(excludedIp, "排除IP");
    }

    /**
     * 校验排除UA字段：每行一个，作为正则表达式使用.
     *
     * @param excludedUa 排除UA列表，允许为空
     */
    public static void validateExcludedUa(String excludedUa) {
        checkRegexLines(excludedUa, "排除UA");
    }

    /**
     * 校验排除URL参数字段：每行一个，作为正则表达式使用.
     *
     * @param excludedUrlParams 排除URL参数列表，允许为空
     */
    public static void validateExcludedUrlParams(String excludedUrlParams) {
        checkRegexLines(excludedUrlParams, "排除URL参数");
    }

    /**
     * 校验站内搜索关键词参数.
     * 多个以逗号分隔，每个参数只允许英文字母、数字、下划线、中杠.
     *
     * @param searchwordKey         站内搜索关键词参数
     * @param searchwordCategoryKey 站内搜索关键词分类参数
     */
    public static void validateSearchwordKey(String searchwordKey, String searchwordCategoryKey) {
        checkCommaSeparated(searchwordKey, "站内搜索关键词参数", SEARCHWORD_KEY_PATTERN);
        checkCommaSeparated(searchwordCategoryKey, "站内搜索关键词分类参数", SEARCHWORD_KEY_PATTERN);
    }

    /**
     * 校验项目访问网址（根网址）.
     * 多个以逗号分隔，每项须为合法网址（可选 http(s)://，域名或IP，可选端口与路径）.
     *
     * @param rootUrls 项目访问网址列表，逗号分隔
     */
    public static void validateRootUrls(String rootUrls) {
        if (rootUrls == null || rootUrls.isEmpty()) {
            return;
        }
        if (rootUrls.length() > MAX_FIELD_LEN) {
            throw new ServiceException("访问网址内容过长", 500);
        }
        // 禁止控制字符（如 NUL），防止 JSON/解析注入
        for (int i = 0; i < rootUrls.length(); i++) {
            char c = rootUrls.charAt(i);
            if (c == '\0' || (c < 0x20 && c != ',' && c != ' ')) {
                throw new ServiceException("访问网址包含非法字符", 500);
            }
        }
        String[] items = rootUrls.split(",");
        for (String raw : items) {
            String item = raw.trim();
            if (item.isEmpty()) {
                continue;
            }
            if (item.length() > MAX_LINE_LEN) {
                throw new ServiceException("访问网址单项过长", 500);
            }
            if (!URL_PATTERN.matcher(item).matches()) {
                throw new ServiceException(String.format("访问网址包含非法项：%s", item), 500);
            }
        }
    }

    /**
     * 字段级校验：逗号分隔，每项须匹配给定正则.
     *
     * @param value     字段值，允许为空
     * @param fieldName 字段中文名，用于错误提示
     * @param pattern   单项匹配正则
     */
    private static void checkCommaSeparated(String value, String fieldName, Pattern pattern) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (value.length() > MAX_FIELD_LEN) {
            throw new ServiceException(String.format("%s内容过长，最多 %d 个字符", fieldName, MAX_FIELD_LEN), 500);
        }
        // 禁止 NUL 及不可打印控制字符（逗号/空格除外），防止 JSON/解析注入
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\0' || (c < 0x20 && c != ',' && c != ' ')) {
                throw new ServiceException(String.format("%s包含非法控制字符", fieldName), 500);
            }
        }
        String[] items = value.split(",");
        for (String raw : items) {
            String item = raw.trim();
            if (item.isEmpty()) {
                continue;
            }
            if (item.length() > MAX_LINE_LEN) {
                throw new ServiceException(String.format("%s单项过长，最多 %d 个字符", fieldName, MAX_LINE_LEN), 500);
            }
            if (!pattern.matcher(item).matches()) {
                throw new ServiceException(
                        String.format("%s包含非法项（仅允许英文、数字、下划线、中杠）：%s", fieldName, item), 500);
            }
        }
    }


    /**
     * 排除策略字段前置校验：空判断 + 长度上限 + 控制字符.
     * 供 IP 行校验与正则行校验复用。
     *
     * @param value     字段值，允许为空
     * @param fieldName 字段中文名，用于错误提示
     */
    private static void checkFieldBasics(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (value.length() > MAX_FIELD_LEN) {
            throw new ServiceException(String.format("%s内容过长，单次最多 %d 个字符", fieldName, MAX_FIELD_LEN), 500);
        }
        // 禁止 NUL 及不可打印控制字符（换行/回车除外），防止 JSON/解析注入
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\0' || (c < 0x20 && c != '\n' && c != '\r')) {
                throw new ServiceException(String.format("%s包含非法控制字符", fieldName), 500);
            }
        }
    }

    /**
     * 排除IP字段行级校验：每行一个，逐行校验 IPv4/IPv6/CIDR/结尾通配符.
     *
     * @param value     字段值，允许为空
     * @param fieldName 字段中文名，用于错误提示
     */
    private static void checkIpLines(String value, String fieldName) {
        checkFieldBasics(value, fieldName);
        if (value == null || value.isEmpty()) {
            return;
        }
        String[] lines = value.split("\\r?\\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.length() > MAX_LINE_LEN) {
                throw new ServiceException(String.format("%s单行规则过长，最多 %d 个字符", fieldName, MAX_LINE_LEN), 500);
            }
            if (!isValidIpLine(line)) {
                throw new ServiceException(String.format("%s包含非法IP/CIDR/通配符规则：%s", fieldName, line), 500);
            }
        }
    }

    /**
     * 排除UA/URL参数字段行级校验：每行一个，逐行预编译为正则.
     *
     * @param value     字段值，允许为空
     * @param fieldName 字段中文名，用于错误提示
     */
    private static void checkRegexLines(String value, String fieldName) {
        checkFieldBasics(value, fieldName);
        if (value == null || value.isEmpty()) {
            return;
        }
        String[] lines = value.split("\\r?\\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.length() > MAX_LINE_LEN) {
                throw new ServiceException(String.format("%s单行规则过长，最多 %d 个字符", fieldName, MAX_LINE_LEN), 500);
            }
            // 按行作为正则使用，预编译校验语法合法性，防止恶意/非法正则进入 Redis
            try {
                Pattern.compile(line);
            } catch (PatternSyntaxException e) {
                throw new ServiceException(String.format("%s包含非法正则表达式：%s", fieldName, line), 500);
            }
        }
    }
}
