package com.zcunsoft.clklog.api.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.List;

@ConfigurationProperties("clklogapi")
public class ClklogApiSetting {

    private String[] accessControlAllowOriginPatterns;

    private String projectName;

    private List<String> projectHost;

    /**
     * 渠道对照表
     */
    private LinkedHashMap<String, String> libTypeMap;

    public String[] getAccessControlAllowOriginPatterns() {
        return accessControlAllowOriginPatterns;
    }

    public void setAccessControlAllowOriginPatterns(String[] accessControlAllowOriginPatterns) {
        this.accessControlAllowOriginPatterns = accessControlAllowOriginPatterns;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<String> getProjectHost() {
        return projectHost;
    }

    public void setProjectHost(List<String> projectHost) {
        this.projectHost = projectHost;
    }

    public LinkedHashMap<String, String> getLibTypeMap() {
        return libTypeMap;
    }

    public void setLibTypeMap(LinkedHashMap<String, String> libTypeMap) {
        this.libTypeMap = libTypeMap;
    }
}
