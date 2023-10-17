package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "area_detail_bydate")
@Data
public class AreaDetailbycompare extends  BaseDetailbydate {

	@Column
    Integer comparePv;

    @Column
    Integer compareVisitCount;

    @Column
    Integer compareUv;

    @Column
    Integer compareNewUv;

    @Column
    Integer compareIpCount;

    @Column
    Integer compareVisitTime;

    @Column
    Integer compareBounceCount;

    @Column
    String compareCountry;

    @Column
    String compareProvince;
	
}
