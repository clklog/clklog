package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "os_detail_bydate")
@Data
public class OsDetailbydate extends  BaseDetailbydate {

    @Column
    String os;
}
