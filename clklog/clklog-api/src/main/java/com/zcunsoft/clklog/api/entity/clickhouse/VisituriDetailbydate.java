package com.zcunsoft.clklog.api.entity.clickhouse;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "visituri_detail_bydate")
@Data
public class VisituriDetailbydate extends BaseDetailbydate {

   

    @Column
    String uri;


    @Column
    Integer exitCount;
    
    @Column
    Integer entryCount;
    
    @Column
    Integer downPvCount;
    
    @Column 
    String title;
    
    @Column 
    String uriPath;

    @Column
    String host;
}
