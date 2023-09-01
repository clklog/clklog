package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "visitor_life_bydate")
@Data
public class VisitorLifebydate {

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
    Integer continuousActiveUv;

    @Column
    Integer newUv;

    @Column
    Integer revisitUv;

    @Column
    Integer silentUv;

    @Column
    Integer churnUv;


    @Column
    Timestamp updateTime;

    @Column
    String country;

    @Column
    String province;

}
