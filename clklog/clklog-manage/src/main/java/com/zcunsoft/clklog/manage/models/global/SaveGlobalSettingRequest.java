package com.zcunsoft.clklog.manage.models.global;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 全局项目配置的设置请求
 */
@Data
public class SaveGlobalSettingRequest {

    /**
     * 排除的全局的IP地址
     */
    @Schema(description = "排除的全局的IP地址", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入您要排除在Clklog统计之外的IP列表(每行一个)。您可以使用CIDR表示法,例如1.2.3.4/24,也可以使用通配符,例如1.2.3.*或1.2.*.*。")
    private String excludedIp;

    /**
     * 排除的全局的用户代理列表
     */
    @Schema(description = "排除的全局的用户代理列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入不需要加入Clklog统计的用户代理列表 (每行一个)，支持正则表达式。 如果访客的用户代理字串包含指定的字符，访问将不被 统计。")
    private String excludedUa;

    /**
     * 排除的全局的URL参数列表
     */
    @Schema(description = "排除的全局的URL参数列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入要排除的URL查询参数列表 (每行一个)，以便从受访页面分析报告中排除因无效参数产生的页面。支持普通参数、正则表达式。")
    private String excludedUrlParams;

    /**
     * 全局的站内搜索关键词参数
     */
    @Schema(description = "全局的站内搜索关键词参数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入用逗号分开的包含站内搜索关键字的所有搜索参数名称的列表。")
    private String searchwordKey;

    /**
     * 全局的站内搜索关键词分类参数
     */
    @Schema(description = "全局的站内搜索关键词分类参数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "输入用逗号分开的搜索分类参数列表来指定搜索分类。")
    private String searchwordCategoryKey;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    Timestamp updateTime;
}
