package com.zcunsoft.clklog.manage.entity.mysql;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * 项目统计信息.
 */
@Entity(name = "tbl_project_stat")
@Data
public class TblProjectStat {

    /**
     * 项目编码
     */
    @Id
    @Column(length = 40)
    private String projectName;

    /**
     * 日志记录数
     */
    @Column
    private Long logRecordCount;

    /**
     * 日志容量
     */
    @Column
    private Long logSpaceSize;

    /**
     * 日志天数
     */
    @Column
    private Integer logDays;

    /**
     * 最新日志时间
     */
    @Column
    private Timestamp logLatestTime;

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
     * 数据库表占用空间
     */
    @Column
    private Long dbSpaceSize;

    /**
     * 更新时间
     */
    @Column
    private Timestamp updateTime;
}
