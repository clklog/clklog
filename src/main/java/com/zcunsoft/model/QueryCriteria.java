package com.zcunsoft.model;

import lombok.Data;

@Data
public class QueryCriteria {

    private String data_list;

    private String project;

    private String token;

    private String data;

    private String gzip = "0";

    private String crc;

    private String clientIp;

    private String ua;
}
