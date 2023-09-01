package com.zcunsoft.clklog.api.handlers;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ConstsDataHolder {

    private volatile Timestamp startStatDate;

    public Timestamp getStartStatDate() {
        return startStatDate;
    }

    public void setStartStatDate(Timestamp startStatDate) {
        this.startStatDate = startStatDate;
    }
}
