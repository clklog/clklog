package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "sourcesite_detail_bydate")
@Data
public class SourcesiteDetailbydate extends BaseDetailbydate {

    @Column
    String sourcesite;
}
