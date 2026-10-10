package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "visitor_detail_byinfo")
@Data
public class VisitorDetailbyinfo {

	
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
    Timestamp updateTime;

    @Column
    String country;

    @Column
    String province;
    
    @Column
    String city;

    @Column
    String isFirstDay;
	
	@Column
    String distinctId;
	
	@Column
    String clientIp;
	
	@Column
    String manufacturer;
	
	@Column
	Timestamp latestTime;
	
	@Column
	Timestamp firstTime;
	
	@Column
	int visitTime;
	
	@Column
    Integer pv;

    @Column
    Integer visitCount;
	
}
