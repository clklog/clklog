package com.zcunsoft.clklog.api.models.enums;

public enum VisitorType {

    New("新访客","true" ),

    Old("老访客", "false"),
    All("全部","all") ;
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

    VisitorType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据name,返回相应的枚举.
     *
     * @param name 枚举值
     * @return 枚举
     */
    public static VisitorType parse(String name) {
        for (VisitorType codeValue : values()) {
            if (codeValue.value.equalsIgnoreCase(name) || codeValue.name.equalsIgnoreCase(name)) {
                return codeValue;
            }
        }
        return  null;
    }

    public static String getName(String name) {
    	VisitorType libType = parse(name);
        return  libType != null ? libType.getName() : name;
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
