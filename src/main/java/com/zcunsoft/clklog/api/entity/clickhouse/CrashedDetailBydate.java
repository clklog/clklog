package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * App崩溃详情表.
 */
@Entity(name = "crashed_detail_bydate")
@Data
//@EqualsAndHashCode(callSuper = true)
public class CrashedDetailBydate {

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
     * App版本.
     */
    @Column
    String appVersion;

    /**
     * 访问次数
     */
    @Column
    Long visitCount;

    /**
     * 总崩溃数
     */
    @Column
    Long crashedCount;

    /**
     * 访客数.
     */
    @Column
    Long uv;

    /**
     * 崩溃访客数
     */
    @Column
    Long crashedUv;

    /**
     * IP数.
     */
    @Column
    Long ipCount;

    /**
     * 总设备类型数
     */
    @Column
    Long modelCount;

    /**
     * 更新时间.
     */
    @Column
    Timestamp updateTime;

    /**
     * 周数
     */
    @Column
    String dimension;

    /**
     * 型号
     */
    @Column
    String model;

}
