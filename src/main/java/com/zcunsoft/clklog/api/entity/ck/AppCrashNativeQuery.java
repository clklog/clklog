package com.zcunsoft.clklog.api.entity.ck;

import lombok.Data;

/**
 * App崩溃分析Sql
 */
@Data
public class AppCrashNativeQuery {

    /**
     * 崩溃概览按日查询Sql
     */
    private String queryByDate;

    /**
     * 崩溃趋势查询sql
     */
    private String trendByDimension;

    /**
     * 按渠道与版本分组sql
     */
    private String groupedByLibAndVersion;

    /**
     * 按渠道版本型号分组计数sql
     */
    private String countStatByLibVersionAndModel;

    /**
     * 按渠道版本型号分页查询Sql
     */
    private String pageStatByLibVersionAndModel;
}
