package com.zcunsoft.clklog.api.models;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TimeFrame {

    private Timestamp startTime;

    private Timestamp endTime;
}
