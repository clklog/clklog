package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "flow_summary_byhour")
@Data
public class FlowSummarybyhour {

    @Id
    @Column
    Timestamp statDate;

    @Id
    @Column
    String statHour;

    @Id
    @Column
    String lib;

    @Id
    @Column
    String projectName;

    @Column
    Integer pv;

    @Column
    Integer visitCount;

    @Column
    Integer uv;

    @Column
    Integer ipCount;

    @Column
    Integer visitTime;

    @Column
    Integer bounceCount;


    @Column
    Timestamp updateTime;
}
