package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * 浏览概要情况.
 */
@Entity(name = "flow_summary_bydate")
@Data
public class FlowSummarybydate {

    /**
     * 统计日期.
     */
    @Id
    @Column
    Timestamp statDate;

    /**
     * 渠道.
     */
    @Id
    @Column
    String lib;

    /**
     * 项目编码.
     */
    @Id
    @Column
    String projectName;

    /**
     * 浏览量.
     */
    @Column
    Long pv;

    /**
     * 访问次数.
     */
    @Column
    Long visitCount;

    /**
     * 访客数.
     */
    @Column
    Long uv;

    /**
     * Ip数.
     */
    @Column
    Long ipCount;

    /**
     * 访问时长
     */
    @Column
    Long visitTime;

    /**
     * 跳出次数.
     */
    @Column
    Long bounceCount;

    /**
     * 更新时间.
     */
    @Column
    Timestamp updateTime;
}
