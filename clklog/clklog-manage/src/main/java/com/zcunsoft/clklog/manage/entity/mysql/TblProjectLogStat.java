package com.zcunsoft.clklog.manage.entity.mysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * 项目日志按天统计信息.
 */
@Entity(name = "tbl_project_log_stat")
@Data
public class TblProjectLogStat {

    /**
     * 统计信息ID
     */
    @Id
    @Column(length = 36)
    private String id;

    /**
     * 项目编码
     */
    @Column(length = 40)
    private String projectName;

    /**
     * 日志记录数
     */
    @Column
    private Long logRecordCount;

    /**
     * 日志占用空间量
     */
    @Column
    private Long logSpaceSize;

    /**
     * 最新日志时间
     */
    @Column
    private Timestamp logLatestTime;

    /**
     * 统计日期
     */
    @Column
    private Timestamp statDate;

    /**
     * 首次入库时间
     */
    @Column
    private Timestamp dbFirstTime;

    /**
     * 最近入库时间
     */
    @Column
    private Timestamp dbLatestTime;

    /**
     * 数据库表记录数
     */
    @Column
    private Long dbRecordCount;

    /**
     * 更新时间
     */
    @Column
    private Timestamp updateTime;
}
