package com.zcunsoft.clklog.manage.entity.mysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * 全局项目配置
 */
@Entity(name = "tbl_global_setting")
@Data
public class TblGlobalSetting {
    /**
     * 项目ID.
     */
    @Id
    @Column(length = 36)
    private String id;

    /**
     * 排除的IP地址.
     */
    @Column(columnDefinition = "TEXT")
    private String excludedIp;

    /**
     * 排除的用户代理列表.
     */
    @Column(columnDefinition = "TEXT")
    private String excludedUa;

    /**
     * 排除的URL参数列表.
     */
    @Column(columnDefinition = "TEXT")
    private String excludedUrlParams;

    /**
     * 站内搜索关键词参数.
     */
    @Column(columnDefinition = "TEXT")
    private String searchwordKey;

    /**
     * 站内搜索关键词参数分类.
     */
    @Column(columnDefinition = "TEXT")
    private String searchwordCategoryKey;

    /**
     * 更新时间.
     */
    @Column
    Timestamp updateTime;

}
