package com.zcunsoft.model;

import lombok.Data;

@Data
public class BodyCriteria {

    private String data_list;

    private String data;

    private String gzip = "0";

    private String crc;

}
