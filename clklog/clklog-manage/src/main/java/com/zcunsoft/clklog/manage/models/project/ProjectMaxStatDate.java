package com.zcunsoft.clklog.manage.models.project;


import java.sql.Timestamp;


/**
 * 项目最新统计日期信息
 */
public interface ProjectMaxStatDate {
    /**
     * 获取项目编码
     *
     * @return {@link String }
     */
    String getProjectName();

    /**
     * 获取最新统计日期
     *
     * @return {@link Timestamp }
     */
    Timestamp getStatDate();
}
