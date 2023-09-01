package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "user_pv_bydate")
@Data
public class UserPvbydate {
	
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
    private int pv1Uv;

    @Column
    private int pv2_5Uv;
    
    @Column
    private int pv6_10Uv;
    
    @Column
    private int pv11_20Uv;
    
    @Column
    private int pv21_30Uv;
    
    @Column
    private int pv31_40Uv;
    
    @Column
    private int pv41_50Uv;
    
    @Column
    private int pv51_100Uv;
    
    @Column
    private int pv101Uv;
    
    
}
