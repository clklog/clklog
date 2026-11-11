package com.zcunsoft.clklog.traffic;

import java.util.Map;

/**
 * 将 Redis Hash 中已解码的 JSON 记录转换为搜索引擎 IP 网段。
 *
 * <p>Hash 字段名必须与内容中的 engine 和 cidr 一致，格式为 {@code engine|cidr}；
 * 可信来源、时间范围及 CIDR 的进一步校验由 {@link SearchEngineIpEntry} 完成。</p>
 */
public final class SearchEngineIpRecordParser {
    private SearchEngineIpRecordParser() { }

    /**
     * 校验字段名和记录内容并构造网段，时间字段必须是毫秒时间戳数值。
     *
     * @param field Redis Hash 字段名
     * @param value JSON 解码后的记录内容
     * @return 已校验的 IP 网段记录
     * @throws IllegalArgumentException 字段缺失、类型错误或记录内容无效
     */
    public static SearchEngineIpEntry parse(String field, Map<String, Object> value) {
        String engine = string(value, "engine");
        String cidr = string(value, "cidr");
        if (!field.equals(engine + "|" + cidr)) {
            throw new IllegalArgumentException("IP library key does not match value");
        }
        return new SearchEngineIpEntry(engine, cidr, string(value, "source"),
                number(value, "verified_at"), number(value, "expires_at"));
    }

    private static String string(Map<String, Object> value, String key) {
        Object item = value.get(key);
        if (!(item instanceof String)) {
            throw new IllegalArgumentException("missing " + key);
        }
        return (String) item;
    }

    private static long number(Map<String, Object> value, String key) {
        Object item = value.get(key);
        if (!(item instanceof Number)) {
            throw new IllegalArgumentException("missing " + key);
        }
        return ((Number) item).longValue();
    }
}
