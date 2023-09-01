package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity(name = "visitor_summary_byvisitor")
@Data
public class VisitorSummarybyvisitor extends BaseDetailbydate {

  

    @Column
    String distinctId;


    @Column
    Timestamp latestTime;
}
