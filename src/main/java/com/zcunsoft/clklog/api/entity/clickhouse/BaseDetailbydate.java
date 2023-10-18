package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
public class BaseDetailbydate {

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
    Integer pv;

    @Column
    Integer visitCount;

    @Column
    Integer uv;

    @Column
    Integer newUv;

    @Column
    Integer ipCount;

    @Column
    Integer visitTime;

    @Column
    Integer bounceCount;


    @Column
    Timestamp updateTime;

    @Column
    String country;

    @Column
    String province;
    
    @Column
    String city;

    @Column
    String isFirstDay;
}
