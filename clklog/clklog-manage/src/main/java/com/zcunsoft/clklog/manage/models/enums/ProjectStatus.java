package com.zcunsoft.clklog.manage.models.enums;


/**
 * 项目状态
 */
public enum ProjectStatus {
    Saved("已保存", 200),
    Disable("已停用", 300),
    Deleted("已删除", 400);

    /**
     * 枚举值.
     */
    private final int value;

    private final String name;

    /**
     * 初始化.
     *
     * @param value the value
     */

    ProjectStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据type,返回相应的枚举.
     *
     * @param type 枚举值
     * @return 枚举
     */
    public static ProjectStatus valueOf(int type) {
        for (ProjectStatus codeValue : values()) {
            if (codeValue.value == type) {
                return codeValue;
            }
        }
        throw new IllegalArgumentException(
                String.format("There is no value with type '%s' in Enum %s", type, ProjectStatus.class.getName()));
    }

    /**
     * 获取枚举值.
     *
     * @return 枚举值
     */
    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
