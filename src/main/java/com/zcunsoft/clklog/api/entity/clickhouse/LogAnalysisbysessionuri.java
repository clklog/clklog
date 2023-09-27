package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity(name = "log_analysis")
@Data
public class LogAnalysisbysessionuri {

	
	@Column
    String distinctId;
	
	@Column
    String eventSessionId;
	
	@Column
    String uri;
	
	@Column
	Timestamp logTime;
	
	@Column
	String title;
	
}
