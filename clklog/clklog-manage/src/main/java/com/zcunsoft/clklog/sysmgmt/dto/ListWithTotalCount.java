package com.zcunsoft.clklog.sysmgmt.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 带总数的列表
 */
@Schema(description = "带总数的列表")
public class ListWithTotalCount<T> {
    /**
     * 总数
     */
    @Schema(description = "总数", example = "10")
    private final int total;

    /**
     * 数据
     */
    @Schema(description = "数据")
    private final List<T> rows;

    public ListWithTotalCount(List<T> rows, int total) {
        this.rows = rows;
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getRows() {
        return rows;
    }
}
