package com.zcunsoft.clklog.api.entity.ck;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 崩溃日志
 */
@Entity(name = "log_analysis")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event", discriminatorType = DiscriminatorType.STRING)
@Data
public class LogAnalysis {

    /**
     * 用户ID
     */
    @Id
    private String distinctId;

    /**
     * 渠道
     */
    private String lib;

    /**
     * 日志时间
     */
    private Timestamp logTime;

    /**
     * 项目编码
     */
    private String projectName;

    /**
     * 统计日期
     */
    private String statDate;

    /**
     * 统计时刻
     */
    private String statHour;

}
