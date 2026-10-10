package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "visitor_detail_bysession")
@Data
public class VisitorDetailbysession {

	
	@Id
    @Column
    Timestamp statDate;
	
	@Id
    @Column
    String projectName;
	
	@Column
    String distinctId;
	
	@Column
    String eventSessionId;
	
	@Column
	Timestamp firstTime;
	
	@Column
	Timestamp latestTime;
	
	@Column
	int visitTime;
	
	@Column
    String clientIp;
	
	@Column
	Timestamp updateTime;
	
	@Column
	int pv;
	
	@Column
	int visitCount;
	
	@Column
	String province;
	
	@Column
	String sourcesite;
	
	@Column
	String searchword;
}
