package com.zcunsoft.clklog.api.models.enums;

public enum LibType {

    Android("安卓","Android" ),

    IOS("苹果","iOS" ),
    Website("网站","js" ),
    All("全部","all"),
    MiniProgram("微信小程序","MiniProgram" );
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

    LibType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 根据name,返回相应的枚举.
     *
     * @param name 枚举值
     * @return 枚举
     */
    public static LibType parse(String name) {
        for (LibType codeValue : values()) {
            if (codeValue.value.equalsIgnoreCase(name) || codeValue.name.equalsIgnoreCase(name)) {
                return codeValue;
            }
        }
        return  null;
    }

    public static String getName(String name) {
    	LibType libType = parse(name);
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
