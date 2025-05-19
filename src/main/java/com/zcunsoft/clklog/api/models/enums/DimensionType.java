package com.zcunsoft.clklog.api.models.enums;

/**
 * 时间维度类型
 */
public enum DimensionType {
    hour("按时", "hour"),
    day("按日", "day"),
    week("按周", "week"),
    month("按月", "month");
    /**
     * 枚举值.
     */
    private final String value;

    private final String name;

    /**
     * 初始化.
     *
     * @param value the value
     */

    DimensionType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据name,返回相应的枚举.
     *
     * @param name 枚举值
     * @return 枚举
     */
    public static DimensionType parse(String name) {
        for (DimensionType codeValue : values()) {
            if (codeValue.value.equalsIgnoreCase(name) || codeValue.name.equalsIgnoreCase(name)) {
                return codeValue;
            }
        }
        return null;
    }

    public static String getName(String name) {
        DimensionType libType = parse(name);
        return libType != null ? libType.getName() : name;
    }

    /**
     * 获取枚举值.
     *
     * @return 枚举值
     */
    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
