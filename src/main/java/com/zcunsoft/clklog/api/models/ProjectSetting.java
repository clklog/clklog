package com.zcunsoft.clklog.api.models;

import lombok.Data;

/**
 * 项目配置
 */
@Data
public class ProjectSetting {
    /**
     * 排除的URL参数
     */
    private String excludedUrlParams;

    /**
     * 搜索词关键字
     */
    private String searchwordKey;

    /**
     * 项目地址
     */
    private String rootUrls;
}
