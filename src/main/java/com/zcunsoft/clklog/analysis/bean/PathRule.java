package com.zcunsoft.clklog.analysis.bean;

import lombok.Data;

/**
 * 路径清洗规则.
 */
@Data
public class PathRule {
    /**
     * 正则表达式
     */
    private String value;
    /**
     * 替换字符串
     */
    private String target;
}
