package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "visitor_detail_byinfo")
@Data
public class UserLatesttimebydate {
	
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
    private int lt0Uv;
    
    @Column
    private int lt1Uv;
    
    @Column
    private int lt2Uv;

    @Column
    private int lt3_7Uv;
    
    @Column
    private int lt8_15Uv;
    
    @Column
    private int lt16_30Uv;
    
    @Column
    private int lt31_90Uv;
    
    @Column
    private int lt90Uv;
    
    
}
