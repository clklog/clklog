package com.zcunsoft.clklog.analysis.bean;

import lombok.Data;

import java.util.List;

@Data
public class ProjectSetting {
    /**
     * 排除的IP地址.
     */
    private String excludedIp;

    /**
     * 排除的用户代理列表.
     */
    private String excludedUa;

    /**
     * 排除的URL参数列表.
     */
    private String excludedUrlParams;

    /**
     * 站内搜索关键词参数.
     */
    private String searchwordKey;

    /**
     * 站内搜索关键词参数分类.
     */
    private String searchwordCategoryKey;

    /**
     * url路径清洗规则.
     */
    private String uriPathRules;

    /**
     * url路径清洗规则列表.
     */
    private List<PathRule> pathRuleList;
}
