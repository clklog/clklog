package com.zcunsoft.model;

import com.zcunsoft.clklog.core.bean.LogMessage;
import lombok.Data;

@Data
public class RawMessage implements LogMessage {

    private String data_list;

    private String project;

    private String token;

    private String data;

    private String gzip = "0";

    private String crc;

    private String clientIp;

    private String ua;

    private String ext;

    private String receiveTime;
}
