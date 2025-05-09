package com.zcunsoft.clklog.manage.models.global;

import lombok.Data;


/**
 * 全局项目配置.
 */
@Data
public class GlobalSettingDto {


    /**
     * 排除的IP地址
     */
    private String excludedIp;

    /**
     * 排除的用户代理列表
     */
    private String excludedUa;

    /**
     * 排除的URL参数列表
     */
    private String excludedUrlParams;

    /**
     * 站内搜索关键词参数
     */
    private String searchwordKey;

    /**
     * 站内搜索关键词参数分类
     */
    private String searchwordCategoryKey;

}
