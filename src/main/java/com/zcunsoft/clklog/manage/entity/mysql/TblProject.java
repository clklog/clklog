package com.zcunsoft.clklog.manage.entity.mysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * 项目信息
 */
@Entity(name = "tbl_project")
@Data
public class TblProject {

    /**
     * 项目ID.
     */
    @Id
    @Column(length = 36)
    private String id;

    /**
     * 项目编码.
     */
    @Column(length = 40)
    private String projectName;

    /**
     * 项目编码.
     */
    @Column(length = 40)
    private String projectDisplayName;

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
     * 项目状态.
     */
    @Column(length = 16)
    private String status;

    /**
     * 项目凭据.
     */
    @Column(length = 36)
    private String token;

    /**
     * 更新时间.
     */
    @Column
    Timestamp updateTime;

    /**
     * 创建时间.
     */
    @Column
    Timestamp createTime;

    /**
     * 所埋网站的根网址
     */
    @Column(columnDefinition = "TEXT")
    private String rootUrls;
}
