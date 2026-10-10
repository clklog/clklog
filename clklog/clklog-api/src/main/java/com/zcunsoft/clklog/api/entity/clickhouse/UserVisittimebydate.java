package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "user_visittime_bydate")
@Data
public class UserVisittimebydate {

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
    private int vt0_10Uv;

    @Column
    private int vt10_30Uv;
    
    @Column
    private int vt30_60Uv;
    
    @Column
    private int vt60_120Uv;
    
    @Column
    private int vt120_180Uv;
    
    @Column
    private int vt180_240Uv;
    
    @Column
    private int vt240_300Uv;
    
    @Column
    private int vt300_600Uv;
    
    @Column
    private int vt600_1800Uv;
    
    @Column
    private int vt1800_3600Uv;
    
    @Column
    private int vt3600Uv;
    
    
}
