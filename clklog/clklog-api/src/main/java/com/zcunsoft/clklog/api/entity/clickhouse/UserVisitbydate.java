package com.zcunsoft.clklog.api.entity.clickhouse;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "user_visit_bydate")
@Data
public class UserVisitbydate {

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
    String country;

    @Column
    String province;

    @Column
    String isFirstDay;
    
    @Column
    Timestamp updateTime;
	
    @Column
    private int v1Uv;

    @Column
    private int v2Uv;
    
    @Column
    private int v3Uv;
    
    @Column
    private int v4Uv;
    
    @Column
    private int v5Uv;
    
    @Column
    private int v6Uv;
    
    @Column
    private int v7Uv;
    
    @Column
    private int v8Uv;
    
    @Column
    private int v9Uv;
    
    @Column
    private int v10Uv;
    
    @Column
    private int v11_15Uv;
    
    @Column
    private int v16_50Uv;
    
    @Column
    private int v51_100Uv;
    
    @Column
    private int v101_200Uv;
    
    @Column
    private int v201_300Uv;
    
    @Column
    private int v300Uv;
    
}
