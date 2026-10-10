package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "searchword_summary_bydate")
@Data
public class SourcesiteSummarybydate {

    @Id
    @Column
    Timestamp statDate;

    @Id
    @Column
    String lib;

    @Id
    @Column
    String projectName;

    @Column
    String sourcesite;


    @Column
    Integer pv;



    @Column
    Timestamp updateTime;
}
