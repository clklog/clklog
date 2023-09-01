package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "device_detail_bydate")
@Data
public class DeviceDetailbydate extends  BaseDetailbydate {

    @Column
    String device;
}
