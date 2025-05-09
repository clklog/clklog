package com.zcunsoft.clklog.manage.models.project;

import lombok.Data;

import java.util.List;

/**
 * 分页获取项目列表的响应数据
 */
@Data
public class QueryProjectResponseData {
    /**
     * 项目总数
     */
    private long total;

    /**
     * 项目列表
     */
    private List<ProjectSlim> rows;
}
