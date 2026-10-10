package com.zcunsoft.clklog.api.handlers;

import com.zcunsoft.clklog.api.models.ProjectSetting;
import lombok.Data;

import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class ConstsDataHolder {

    private volatile Timestamp startStatDate;

    public Timestamp getStartStatDate() {
        return startStatDate;
    }

    public void setStartStatDate(Timestamp startStatDate) {
        this.startStatDate = startStatDate;
    }

    /**
     * 项目配置hash表
     */
    private final ConcurrentMap<String, ProjectSetting> htProjectSetting = new ConcurrentHashMap<String, ProjectSetting>();


    public ConcurrentMap<String, ProjectSetting> getHtProjectSetting() {
        return htProjectSetting;
    }
}
