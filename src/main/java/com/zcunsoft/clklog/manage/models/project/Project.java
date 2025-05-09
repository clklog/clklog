/**
 *
 */
package com.zcunsoft.clklog.manage.models.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 项目信息
 */
@Data
public class Project {

    /**
     * 项目ID
     */
    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "eb029af8-0e24-47d4-b992-3b5284ce4fc8")
    private String id;

    /**
     * 项目编码
     */
    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "clklog")
    private String projectName;

    /**
     * 项目名称
     */
    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "clklog")
    private String projectDisplayName;

    /**
     * 排除的IP地址
     */
    @Schema(description = "排除的IP地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入您要排除在Clklog统计之外的IP列表(每行一个)。您可以使用CIDR表示法,例如1.2.3.4/24,也可以使用通配符,例如1.2.3.*或1.2.*.*。")
    private String excludedIp;

    /**
     * 排除的用户代理列表
     */
    @Schema(description = "排除的用户代理列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入不需要加入Clklog统计的用户代理列表 (每行一个)，支持正则表达式。 如果访客的用户代理字串包含指定的字符，访问将不被 统计。")
    private String excludedUa;

    /**
     * 排除的URL参数列表
     */
    @Schema(description = "排除的URL参数列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入要排除的URL查询参数列表 (每行一个)，以便从受访页面分析报告中排除因无效参数产生的页面。支持普通参数、正则表达式。")
    private String excludedUrlParams;

    /**
     * 站内搜索关键词参数
     */
    @Schema(description = "站内搜索关键词参数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入用逗号分开的包含站内搜索关键字的所有搜索参数名称的列表。")
    private String searchwordKey;

    /**
     * 站内搜索关键词分类参数
     */
    @Schema(description = "站内搜索关键词分类参数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入用逗号分开的搜索分类参数列表来指定搜索分类。")
    private String searchwordCategoryKey;

    /**
     * 项目状态
     */
    @Schema(description = "项目状态", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private String status;

    /**
     * 所埋网站的根网址
     */
    @Schema(description = "所埋网站的根网址", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "多个用逗号,隔开。例https://www.a.com,http://192.168.1.10")
    private String rootUrls;
}
